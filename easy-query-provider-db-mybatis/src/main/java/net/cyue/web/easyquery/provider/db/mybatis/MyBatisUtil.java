package net.cyue.web.easyquery.provider.db.mybatis;

import net.cyue.web.easyquery.provider.db.DBConfiguration;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

public class MyBatisUtil {

    public static Configuration getMyBatisConfiguration(DBConfiguration dbConfiguration) {
        return getMyBatisConfiguration(
            dbConfiguration.getDriverName(),
            dbConfiguration.getUrl(),
            dbConfiguration.getUsername(),
            dbConfiguration.getPassword()
        );
    }

    public static Configuration getMyBatisConfiguration(
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
}
