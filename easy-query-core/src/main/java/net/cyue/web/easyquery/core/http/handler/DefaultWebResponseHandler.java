package net.cyue.web.easyquery.core.http.handler;

import net.cyue.web.easyquery.core.http.api.IHTTPResponse;
import net.cyue.web.easyquery.core.http.handler.api.IWebResponseHandler;

/**
 * 默认的响应处理器
 */
public class DefaultWebResponseHandler implements IWebResponseHandler {
    @Override
    public void handle(IHTTPResponse response, Object result) {
        response.send(result);
    }
}
