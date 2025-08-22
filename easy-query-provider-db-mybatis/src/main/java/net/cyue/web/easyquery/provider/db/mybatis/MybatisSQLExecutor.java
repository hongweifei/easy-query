package net.cyue.web.easyquery.provider.db.mybatis;

import net.cyue.util.ResourceUtil;
import net.cyue.util.StringUtil;
import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.db.api.ISQLExecutor;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MybatisSQLExecutor implements ISQLExecutor {

    private static final String MYBATIS_PROPERTIES = "mybatis.properties";
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SqlSessionFactory sqlSessionFactory;
    private final SqlSession sqlSession;
    private final QueryMapper queryMapper;

    public MybatisSQLExecutor(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.sqlSession = sqlSessionFactory.openSession();
        this.queryMapper = this.sqlSession.getMapper(QueryMapper.class);
    }
    public MybatisSQLExecutor() throws IOException, ConfigException {
        Properties properties = new Properties();
        try (InputStream inputStream = ResourceUtil.getResourceAsStream(MYBATIS_PROPERTIES)) {
            if (inputStream == null) {
                throw new ConfigException("mybatis.properties未找到");
            }
            properties.load(inputStream);
        }
        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        if (StringUtil.isBlank(driver)) {
            driver = properties.getProperty("driverClassName", DEFAULT_DRIVER);
        }
        if (StringUtil.isBlank(username)) {
            username = properties.getProperty("user");
        }

        if (url == null || username == null) {
            this.logger.warn("mybatis.properties未正确配置。");
        }
        if (StringUtil.isBlank(url)) {
            throw new ConfigException("url为空");
        }
        if (StringUtil.isBlank(username)) {
            throw new ConfigException("username为空");
        }
        if (StringUtil.isBlank(password)) {
            password = "";
        }

        Configuration configuration = getConfiguration(driver, url, username, password);
        configuration.addMapper(QueryMapper.class); // 注册 Mapper 接口

        // 5. 创建 SqlSessionFactory
        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        this.sqlSession = this.sqlSessionFactory.openSession();
        this.queryMapper = this.sqlSession.getMapper(QueryMapper.class);
    }

    private static Configuration getConfiguration(
        String driver,
        String url,
        String username,
        String password
    ) {
        DataSource dataSource = new PooledDataSource(
            driver,
            url,
            username,
            password
        );

        // 2. 构建事务工厂
        TransactionFactory transactionFactory = new JdbcTransactionFactory();

        // 3. 配置环境
        Environment environment = new Environment(
            "easy-query-mybatis",
            transactionFactory,
            dataSource
        );

        // 4. 构建 Configuration 对象
        return new Configuration(environment);
    }


    @Override
    public List<Map<String, Object>> query(String sql, Map<String, Object> params) {
        params.put("dynamic_sql_for_query", sql);
        return this.queryMapper.dynamicQuery(params);
    }

}
