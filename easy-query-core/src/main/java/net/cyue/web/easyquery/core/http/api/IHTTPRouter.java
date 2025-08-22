package net.cyue.web.easyquery.core.http.api;

import net.cyue.web.easyquery.core.http.HTTPRequestMethod;
import net.cyue.web.easyquery.core.http.handler.api.IWebRequestHandler;

/**
 * HTTP路由器接口
 */
public interface IHTTPRouter {
    /**
     * 添加路由
     * @param path api 路径
     * @param handler 请求处理器实例
     * @param methods 接受的请求方法
     * @return 添加结果
     */
    boolean addRoute(String path, IWebRequestHandler handler, HTTPRequestMethod[] methods);
}
