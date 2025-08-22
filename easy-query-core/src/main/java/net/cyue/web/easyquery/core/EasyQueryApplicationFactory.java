package net.cyue.web.easyquery.core;

import net.cyue.web.easyquery.core.config.ConfigException;

import java.io.IOException;

/**
 * EasyQueryApplication 工厂类
 */
public class EasyQueryApplicationFactory {

    /**
     * 创建 EasyQueryApplication
     * @param serverContext 原始服务上下文
     * @return EasyQueryApplication
     * @param <TContext> 原始服务上下文类型
     * @throws ConfigException 配置异常
     * @throws IOException IO 异常
     */
    public static <TContext> EasyQueryApplication<TContext> create(TContext serverContext)
        throws ConfigException, IOException
    {
        EasyQueryContext<TContext> context = new EasyQueryContext<>(serverContext);
        return new EasyQueryApplication<>(context);
    }
}
