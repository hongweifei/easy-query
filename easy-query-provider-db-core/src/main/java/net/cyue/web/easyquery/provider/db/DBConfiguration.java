package net.cyue.web.easyquery.provider.db;

import net.cyue.util.StringUtil;
import net.cyue.web.easyquery.core.config.ConfigException;

public class DBConfiguration {
    private final String driverName;
    private final String url;
    private final String username;
    private final String password;

    public DBConfiguration(
        String driverName,
        String url,
        String username
    ) {
        this(driverName, url, username, "");
    }
    public DBConfiguration(
        String driverName,
        String url,
        String username,
        String password
    ) {
        this.driverName = driverName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getDriverName() {
        return this.driverName;
    }
    public String getUrl() {
        return this.url;
    }
    public String getUsername() {
        return this.username;
    }
    public String getPassword() {
        return this.password;
    }

    public static class Builder {
        private String driverName;
        private String url;
        private String username;
        private String password;

        private Builder() {}

        public Builder driverName(String driverName) {
            this.driverName = driverName;
            return this;
        }
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        public DBConfiguration build() throws ConfigException {
            if (this.driverName == null || StringUtil.isBlank(this.driverName)) {
                this.driverName = DBConfigurationUtil.getMySQLDriverName();
            }
            if (this.url == null || StringUtil.isBlank(url)) {
                throw new ConfigException("url为空");
            }
            if (this.username == null || StringUtil.isBlank(username)) {
                throw new ConfigException("username为空");
            }
            return new DBConfiguration(driverName, url, username, password);
        }
    }
    public static Builder builder() {
        return new Builder();
    }
}
