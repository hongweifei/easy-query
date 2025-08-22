package net.cyue.web.easyquery.core.config;

/**
 * 配置组
 */
public class ConfigGroup {
    /**
     * server 配置组
     */
    public static final ConfigGroup SERVER = ConfigGroup.create("server");
    /**
     * application 配置组
     */
    public static final ConfigGroup APPLICATION = ConfigGroup.create("application");
    /**
     * application.context 配置组
     */
    public static final ConfigGroup APPLICATION_CONTEXT = ConfigGroup.create(APPLICATION, "context");
    /**
     * database 配置组
     */
    public static final ConfigGroup DATABASE = ConfigGroup.create("database");

    private ConfigGroup parent;
    private String name;
    private String description;

    @Override
    public String toString() {
        return this.getFullName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigGroup)) {
            return false;
        }
        ConfigGroup group = (ConfigGroup) obj;
        return this.getFullName().equalsIgnoreCase(group.getFullName());
    }

    /**
     * 判断配置组组名
     * @param name 组名
     * @return 组名是否一致
     */
    public boolean equals(String name) {
        return this.getFullName().equalsIgnoreCase(name);
    }

    /**
     * 获取父级组
     * @return 父级组
     */
    public ConfigGroup getParent() {
        return this.parent;
    }

    /**
     * 获取组名
     * @return 组名
     */
    public String getFullName() {
        if (this.parent == null) {
            return this.name;
        }
        return this.parent.getFullName() + "." + this.name;
    }

    /**
     * 获取组名
     * @return 组名
     */
    public String getSimpleName() {
        return this.name;
    }

    /**
     * 获取配置组描述
     * @return 组描述
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 创建配置组
     * @param name 组名
     * @return 配置组
     */
    public static ConfigGroup create(String name) {
        return create(name, "");
    }

    /**
     * 创建配置组
     * @param name 组名
     * @param description 组描述
     * @return 配置组
     */
    public static ConfigGroup create(String name, String description) {
        ConfigGroup group = new ConfigGroup();
        group.parent = null;
        group.name = name;
        group.description = description;
        return group;
    }

    /**
     * 创建配置组
     * @param parent 父级组
     * @param name 组名
     * @return 配置组
     */
    public static ConfigGroup create(ConfigGroup parent, String name) {
        return create(parent, name, "");
    }

    /**
     * 创建配置组
     * @param parent 父级组
     * @param name 组名
     * @param description 组描述
     * @return 配置组
     */
    public static ConfigGroup create(ConfigGroup parent, String name, String description) {
        ConfigGroup group = new ConfigGroup();
        group.parent = parent;
        group.name = name;
        group.description = description;
        return group;
    }
}
