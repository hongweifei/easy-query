package net.cyue.web.easyquery.provider.db.jdbc;

import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.db.api.ISQLExecutor;
import net.cyue.web.easyquery.core.util.SQLUtil;
import net.cyue.web.easyquery.provider.db.DBConfiguration;
import net.cyue.web.easyquery.provider.db.DBConfigurationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class JDBCSQLExecutor implements ISQLExecutor {

    private static final String JDBC_PROPERTIES = "jdbc.properties";
    private final Logger logger = LoggerFactory.getLogger(this.toString());
    private final Connection connection;

    public JDBCSQLExecutor() throws IOException, ConfigException {
        DBConfiguration configuration = DBConfigurationUtil.loadProperties(JDBC_PROPERTIES);

        try {
            this.connection = DriverManager.getConnection(
                configuration.getUrl(),
                configuration.getUsername(),
                configuration.getPassword()
            );
        } catch (SQLException e) {
            this.logger.warn("获取数据库连接失败，请检查数据库驱动是否注册");
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Object> getResultMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        ResultSetMetaData md = rs.getMetaData();
        int count = md.getColumnCount(); // 获取列的数量
        for (int i = 1; i <= count; i++) {
            String key = md.getColumnLabel(i);
            Object value = rs.getObject(i);
            map.put(key, value);
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> query(String sql, Map<String, Object> params) throws SQLException {
        String newSql = SQLUtil.fillParameters(sql, params);
        List<Map<String, Object>> list = new ArrayList<>();

        if (!params.isEmpty() && newSql.equals(sql)) {
            this.logger.warn("SQL参数未替换成功");
            return null;
        }

        this.logger.info("\n收到 SQL：{}\n执行 SQL：{}", sql, newSql);
        try (PreparedStatement sm = this.connection.prepareStatement(newSql)) {
            ResultSet rs = sm.executeQuery();
            while(rs.next()) {
                list.add(getResultMap(rs));
            }
            rs.close();
            return list;
        }
    }
}
