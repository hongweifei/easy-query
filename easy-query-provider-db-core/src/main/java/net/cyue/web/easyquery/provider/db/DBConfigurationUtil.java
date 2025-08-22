package net.cyue.web.easyquery.provider.db;

import net.cyue.util.ResourceUtil;
import net.cyue.util.StringUtil;
import net.cyue.web.easyquery.core.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConfigurationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBConfigurationUtil.class);

    public static String getDBType() {
        return "jdbc";
    }

    public static String getMySQLDriverName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    /**
     * 加载resources中的配置文件
     *
     * @param fileName 配置文件名
     * @return properties
     */
    public static DBConfiguration loadProperties(String fileName)
        throws IOException, ConfigException
    {
        Properties properties = new Properties();
        try (InputStream inputStream = ResourceUtil.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new ConfigException(fileName + " 未找到");
            }
            properties.load(inputStream);
        }

        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        if (StringUtil.isBlank(driver)) {
            driver = properties.getProperty("driverName");
        }
        if (StringUtil.isBlank(driver)) {
            driver = properties.getProperty("driverClassName", DBConfigurationUtil.getMySQLDriverName());
        }
        if (StringUtil.isBlank(username)) {
            username = properties.getProperty("user");
        }

        if (url == null || username == null) {
            LOGGER.warn("{} 未正确配置。", fileName);
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

        return DBConfiguration
            .builder()
            .driverName(driver)
            .url(url)
            .username(username)
            .password(password)
            .build();
    }
}
