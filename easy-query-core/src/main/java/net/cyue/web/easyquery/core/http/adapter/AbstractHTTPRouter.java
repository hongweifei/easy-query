package net.cyue.web.easyquery.core.http.adapter;

import net.cyue.web.easyquery.core.http.api.IHTTPRouter;
import net.cyue.web.easyquery.core.service.api.IService;

/**
 * HTTP 路由适配器抽象类
 * @param <TContext> 原始服务上下文类型
 */
public abstract class AbstractHTTPRouter<TContext> implements IHTTPRouter, IService<TContext> {
    /**
     * 原始服务上下文
     */
    protected final TContext context;

    /**
     * 构造函数
     * @param context 原始服务上下文实例
     */
    public AbstractHTTPRouter(TContext context)
    {
        this.context = context;
    }

    /**
     * 获取原始服务上下文实例
     * @return 原始服务上下文实例
     */
    @Override
    public TContext getPrototype() {
        return this.context;
    }
}
