package net.cyue.web.easyquery.core.config;


/**
 * 配置项
 */
public class ConfigItem {

    /**
     * 配置项所属组
     */
    private ConfigGroup group;

    /**
     * 配置项名称
     */
    private String name;
    private String description;

    @Override
    public String toString() {
        return this.getFullName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigItem)) {
            return false;
        }
        ConfigItem item = (ConfigItem) obj;
        return this.getFullName().equalsIgnoreCase(item.getFullName());
    }

    /**
     * 判断配置项是否一样
     * @param configItemKey 配置项全名
     * @return 结果
     */
    public boolean equals(String configItemKey) {
        if (configItemKey == null) {
            return false;
        }
        return configItemKey.equalsIgnoreCase(this.getFullName());
    }

    /**
     * 获取配置项所属组
     * @return 配置项所属组
     */
    public ConfigGroup getGroup() {
        return this.group;
    }

    /**
     * 获取配置项名称
     * @return 配置项名称
     */
    public String getFullName() {
        return this.group.getFullName() + "." + this.name;
    }

    /**
     * 获取配置项名称
     * @return 配置项名称
     */
    public String getSimpleName() {
        return this.name;
    }

    /**
     * 获取配置项描述
     * @return 配置项描述
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 创建配置项
     * @param group 配置组
     * @param name 配置项名称
     * @return 配置项
     */
    public static ConfigItem create(
        ConfigGroup group,
        String name
    ) {
        return ConfigItem.create(group, name, "");
    }

    /**
     * 创建配置项
     * @param group 配置组
     * @param name 配置项名称
     * @param description 配置项描述
     * @return 配置项
     */
    public static ConfigItem create(
        ConfigGroup group,
        String name,
        String description
    ) {
        ConfigItem item = new ConfigItem();
        item.group = group;
        item.name = name;
        item.description = description;
        return item;
    }

}
