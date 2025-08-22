package net.cyue.web.easyquery.provider.db.mybatis;

import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.db.api.ISQLExecutor;
import net.cyue.web.easyquery.provider.db.DBConfiguration;
import net.cyue.web.easyquery.provider.db.DBConfigurationUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MybatisSQLExecutor implements ISQLExecutor {

    private static final String MYBATIS_PROPERTIES = "mybatis.properties";
    private final Logger logger = LoggerFactory.getLogger(this.toString());
    private final SqlSessionFactory sqlSessionFactory;
    private final SqlSession sqlSession;
    private final DynamicQueryMapper queryMapper;

    public MybatisSQLExecutor(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.sqlSession = sqlSessionFactory.openSession();
        this.queryMapper = this.sqlSession.getMapper(MyBatisQueryMapper.class);
    }
    public MybatisSQLExecutor() throws IOException, ConfigException {
        DBConfiguration dbConfiguration = DBConfigurationUtil.loadProperties(MYBATIS_PROPERTIES);
        Configuration mybatisConfiguration = MyBatisUtil.getMyBatisConfiguration(dbConfiguration);
        mybatisConfiguration.addMapper(MyBatisQueryMapper.class); // 注册 Mapper 接口

        // 创建 SqlSessionFactory
        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(mybatisConfiguration);
        this.sqlSession = this.sqlSessionFactory.openSession();
        this.queryMapper = this.sqlSession.getMapper(MyBatisQueryMapper.class);
    }


    @Override
    public List<Map<String, Object>> query(String sql, Map<String, Object> params) {
        params.put("dynamic_sql_for_query", sql);
        return this.queryMapper.dynamicQuery(params);
    }

}
