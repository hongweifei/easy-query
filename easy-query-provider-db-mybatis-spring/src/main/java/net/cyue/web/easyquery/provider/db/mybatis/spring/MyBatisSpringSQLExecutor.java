package net.cyue.web.easyquery.provider.db.mybatis.spring;

import jakarta.servlet.ServletContext;
import net.cyue.web.easyquery.core.EasyQueryApplication;
import net.cyue.web.easyquery.core.EasyQueryApplicationTaskType;
import net.cyue.web.easyquery.core.db.api.ISQLExecutor;
import net.cyue.web.easyquery.core.util.TaskUtil;
import net.cyue.web.easyquery.provider.db.DBConfiguration;
import net.cyue.web.easyquery.provider.db.DBConfigurationUtil;
import net.cyue.web.easyquery.provider.db.mybatis.DynamicQueryMapper;
import net.cyue.web.easyquery.provider.db.mybatis.MyBatisQueryMapper;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.List;
import java.util.Map;

public class MyBatisSpringSQLExecutor implements ISQLExecutor {
    private static final String MYBATIS_PROPERTIES = "mybatis.properties";
    private final Logger logger = LoggerFactory.getLogger(this.toString());
    private SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;
    private DynamicQueryMapper queryMapper;

    public MyBatisSpringSQLExecutor(DriverManagerDataSource dataSource) throws Exception {
        this.initSqlSession(dataSource);
    }
    public MyBatisSpringSQLExecutor(SqlSessionFactory sqlSessionFactory) {
        this.initSqlSession(sqlSessionFactory);
    }
    public MyBatisSpringSQLExecutor() {
        TaskUtil.Task<EasyQueryApplication<?>> task = (app) -> {
            if (this.sqlSession != null) {
                return;
            }
            if (app.getServerContext() == null) {
                this.logger.warn("ServerContext 为空");
                return;
            }
            WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext((ServletContext) app.getServerContext());
            if (context == null) {
                this.logger.warn("WebApplicationContext 为空");
                return;
            }
            this.initSqlSession(context);
        };
        TaskUtil.addTask(EasyQueryApplicationTaskType.INIT, task);
        TaskUtil.addTask(MyBatisSpringProviderTaskType.SET_CONTEXT, this::initSqlSession);
    }

    private void initSqlSession(ApplicationContext context)
        throws Exception
    {
        try {
            this.logger.info("获取 SqlSessionFactory Bean");
            SqlSessionFactory factory = context.getBean(SqlSessionFactory.class);
            this.initSqlSession(factory);
        } catch (BeansException be1) {
            // no factory
            this.logger.warn("获取 SqlSessionFactory Bean 失败");
            this.logger.info("获取 DriverManagerDataSource Bean");
            try {
                DriverManagerDataSource dataSource = context.getBean(DriverManagerDataSource.class);
                this.initSqlSession(dataSource);
            } catch (Exception be2) {
                // no dataSource
                this.logger.warn("获取 DriverManagerDataSource Bean 失败");
                this.logger.info("自行创建 DriverManagerDataSource");
                DBConfiguration config = DBConfigurationUtil.loadProperties(MYBATIS_PROPERTIES);
                DriverManagerDataSource dataSource = new DriverManagerDataSource(
                    config.getUrl(),
                    config.getUsername(),
                    config.getPassword()
                );
                dataSource.setDriverClassName(config.getDriverName());
                this.initSqlSession(dataSource);
            }
        }
    }

    private void initSqlSession(DriverManagerDataSource dataSource) throws Exception {
        this.logger.info("初始化 SqlSessionFactory");
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setDataSource(dataSource);
        // factoryBean.setTypeAliasesPackage(EasyQueryApplication.MAIN_PACKAGE_NAME);
        factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/**/*.xml"));
        this.sqlSessionFactory = factoryBean.getObject();
        this.sqlSession = sqlSessionFactory.openSession();
        try {
            this.queryMapper = this.sqlSession.getMapper(MyBatisSpringQueryMapper.class);
        } catch (BindingException e) {
            this.logger.error(e.getMessage());
            this.queryMapper = this.sqlSession.getMapper(MyBatisQueryMapper.class);
        }
    }
    private void initSqlSession(SqlSessionFactory factory) {
        this.logger.info("获取 SqlSession");
        this.sqlSessionFactory = factory;
        this.sqlSession = sqlSessionFactory.openSession();
        try {
            this.queryMapper = this.sqlSession.getMapper(MyBatisSpringQueryMapper.class);
        } catch (BindingException e) {
            this.logger.error(e.getMessage());
            this.queryMapper = this.sqlSession.getMapper(MyBatisQueryMapper.class);
        }
    }

    @Override
    public List<Map<String, Object>> query(String sql, Map<String, Object> params) {
        params.put("dynamic_sql_for_query", sql);
        return this.queryMapper.dynamicQuery(params);
    }
}
