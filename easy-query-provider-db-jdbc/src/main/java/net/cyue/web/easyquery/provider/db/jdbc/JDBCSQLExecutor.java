package net.cyue.web.easyquery.provider.db.jdbc;

import net.cyue.util.ResourceUtil;
import net.cyue.util.StringUtil;
import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.db.api.ISQLExecutor;
import net.cyue.web.easyquery.core.util.SQLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class JDBCSQLExecutor implements ISQLExecutor {

    private static final String JDBC_PROPERTIES = "jdbc.properties";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Connection connection;

    public JDBCSQLExecutor() throws IOException, ConfigException {
        Properties properties = new Properties();
        try (InputStream inputStream = ResourceUtil.getResourceAsStream(JDBC_PROPERTIES)) {
            if (inputStream == null) {
                throw new ConfigException("jdbc.properties未找到");
            }
            properties.load(inputStream);
        }

        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        if (StringUtil.isBlank(username)) {
            username = properties.getProperty("user");
        }

        if (url == null || username == null) {
            this.logger.warn("jdbc.properties未正确配置。");
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

        try {
            this.connection = DriverManager.getConnection(
                url,
                username,
                password
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
