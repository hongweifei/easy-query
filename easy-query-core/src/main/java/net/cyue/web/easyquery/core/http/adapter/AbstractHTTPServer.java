package net.cyue.web.easyquery.core.http.adapter;

import net.cyue.web.easyquery.core.http.HTTPRequestMethod;
import net.cyue.web.easyquery.core.http.api.IHTTPRouter;
import net.cyue.web.easyquery.core.http.api.IHTTPServer;
import net.cyue.web.easyquery.core.http.handler.api.IWebRequestHandler;
import net.cyue.web.easyquery.core.service.api.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP 服务适配器抽象类
 * @param <TContext> 原始服务上下文类型
 */
public abstract class AbstractHTTPServer<TContext> implements IHTTPServer, IService<TContext> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 原始服务上下文
     */
    protected final TContext context;
    /**
     * 路由器
     */
    protected IHTTPRouter router;

    /**
     * 构造函数
     * @param context 原始服务上下文实例
     */
    public AbstractHTTPServer(TContext context) {
        this(context, null);
    }
    /**
     * 构造函数
     * @param context 原始服务上下文实例
     * @param router 路由器实现实例
     */
    public AbstractHTTPServer(TContext context, IHTTPRouter router) {
        this.context = context;
        this.router = router;
    }

    /**
     * 设置路由器
     * @param router 路由器实例
     */
    public void setRouter(IHTTPRouter router) {
        this.router = router;
    }

    /**
     * 添加路由
     * @param path api 路径
     * @param handler 请求处理器实例
     * @param methods 接受的请求方法
     * @return 添加结果
     */
    protected boolean addRoute(String path, IWebRequestHandler handler, HTTPRequestMethod[] methods) {
        if (this.router == null) {
            this.logger.warn("Router is not set");
            return false;
        }
        return this.router.addRoute(path, handler, methods);
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
