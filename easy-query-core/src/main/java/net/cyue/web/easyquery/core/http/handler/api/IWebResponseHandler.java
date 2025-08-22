package net.cyue.web.easyquery.core.http.handler.api;

import net.cyue.web.easyquery.core.http.api.IHTTPResponse;

/**
 * Web响应处理器接口
 */
public interface IWebResponseHandler {
    /**
     * 使用 result.getClass() 获取相关类型
     * @param response HTTP响应
     * @param result 经过 WebResult处理器 的 result
     */
    void handle(IHTTPResponse response, Object result);
}
