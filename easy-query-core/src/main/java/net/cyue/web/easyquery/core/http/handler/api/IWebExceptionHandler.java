package net.cyue.web.easyquery.core.http.handler.api;

import net.cyue.web.easyquery.core.http.api.IHTTPResponse;

/**
 * Web异常处理器接口
 */
public interface IWebExceptionHandler {
    /**
     * 处理 Web 异常
     * @param e 异常
     * @param response HTTP 响应对象
     * @return 返回 true 表示异常已被处理，不需要再抛出 RuntimeException；返回 false 表示异常未被完全处理，应继续抛出 RuntimeException
     */
    boolean handle(Exception e, IHTTPResponse response);
}
