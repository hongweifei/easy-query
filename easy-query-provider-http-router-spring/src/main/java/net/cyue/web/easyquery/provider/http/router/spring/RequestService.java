package net.cyue.web.easyquery.provider.http.router.spring;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import net.cyue.web.easyquery.core.http.handler.api.IWebRequestHandler;
import net.cyue.web.easyquery.provider.http.servlet.ServletHTTPRequest;
import net.cyue.web.easyquery.provider.http.servlet.ServletHTTPResponse;

import java.io.IOException;

public class RequestService {

    private final IWebRequestHandler handler;

    public RequestService(IWebRequestHandler handler) {
        this.handler = handler;
    }

    // 处理请求的方法
    public void service(ServletRequest servletRequest, ServletResponse servletResponse)
        throws IOException
    {
        servletRequest.setCharacterEncoding("UTF-8");
        servletResponse.setCharacterEncoding("UTF-8");
        this.handler.handle(
            new ServletHTTPRequest(servletRequest),
            new ServletHTTPResponse(servletResponse)
        );
    }
}
