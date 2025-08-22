package net.cyue.web.easyquery.core.http.handler.api;

import net.cyue.web.easyquery.core.http.api.IHTTPRequest;
import net.cyue.web.easyquery.core.http.api.IHTTPResponse;

import java.io.IOException;

/**
 * Web请求处理器接口
 */
public interface IWebRequestHandler {

    /**
     * 处理Web请求
     * @param request HTTP请求
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void handle(
        IHTTPRequest request,
        IHTTPResponse response
    ) throws IOException;
}

