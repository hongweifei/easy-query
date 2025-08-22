package net.cyue.web.easyquery.core.http.api;

import net.cyue.web.easyquery.core.http.handler.api.IWebRequestHandler;

/**
 * HTTP服务接口
 */
public interface IHTTPServer {

    /**
     * 添加通用路由
     * @param handler 处理器
     */
    default void use(IWebRequestHandler handler) {
        all(handler);
    }

    /**
     * 添加通用路由
     * @param path 路径
     * @param handler 处理器
     */
    default void use(String path, IWebRequestHandler handler) {
        all(path, handler);
    }

    /**
     * 添加 get 方法路由
     * @param handler 处理器
     */
    void get(IWebRequestHandler handler);

    /**
     * 添加 get 方法路由
     * @param path 路径
     * @param handler 处理器
     */
    void get(String path, IWebRequestHandler handler);

    /**
     * 添加 post 方法路由
     * @param handler 处理器
     */
    void post(IWebRequestHandler handler);

    /**
     * 添加 post 方法路由
     * @param path 路径
     * @param handler 处理器
     */
    void post(String path, IWebRequestHandler handler);

    /**
     * 添加 head 方法路由
     * @param handler 处理器
     */
    void head(IWebRequestHandler handler);

    /**
     * 添加 head 方法路由
     * @param path 路径
     * @param handler 处理器
     */
    void head(String path, IWebRequestHandler handler);

    /**
     * 添加 delete 方法路由
     * @param handler 处理器
     */
    void delete(IWebRequestHandler handler);

    /**
     * 添加 delete 方法路由
     * @param path 路径
     * @param handler 处理器
     */
    void delete(String path, IWebRequestHandler handler);

    /**
     * 添加 put 方法路由
     * @param handler 处理器
     */
    void put(IWebRequestHandler handler);

    /**
     * 添加 put 方法路由
     * @param path 路径
     * @param handler 处理器
     */
    void put(String path, IWebRequestHandler handler);

    /**
     * 添加 patch 方法路由
     * @param handler 处理器
     */
    void patch(IWebRequestHandler handler);

    /**
     * 添加 patch 方法路由
     * @param path 路径
     * @param handler 处理器
     */
    void patch(String path, IWebRequestHandler handler);

    /**
     * 添加 options 方法路由
     * @param handler 处理器
     */
    void options(IWebRequestHandler handler);

    /**
     * 添加 options 方法路由
     * @param path 路径
     * @param handler 处理器
     */
    void options(String path, IWebRequestHandler handler);

    /**
     * 添加通用路由
     * @param handler 处理器
     */
    void all(IWebRequestHandler handler);

    /**
     * 添加通用路由
     * @param path 路径
     * @param handler 处理器
     */
    void all(String path, IWebRequestHandler handler);
}
