package net.cyue.web.easyquery.core.config;

import net.cyue.web.easyquery.core.EasyQueryContext;

/**
 * 配置项处理器接口
 */
public interface IConfigItemHandler {

    /**
     * 处理配置项
     *
     * @param context EasyQueryContext
     * @param configItem 配置项
     * @param value 配置项值
     * @throws ConfigException 配置异常
     */
    void handle(
        EasyQueryContext<?> context,
        ConfigItem configItem,
        String value
    ) throws ConfigException;
}
