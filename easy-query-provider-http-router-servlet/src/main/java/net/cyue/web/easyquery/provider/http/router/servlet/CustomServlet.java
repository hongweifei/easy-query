package net.cyue.web.easyquery.provider.http.router.servlet;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import net.cyue.web.easyquery.core.http.HTTPRequestMethod;
import net.cyue.web.easyquery.core.http.handler.api.IWebRequestHandler;
import net.cyue.web.easyquery.provider.http.servlet.ServletHTTPRequest;
import net.cyue.web.easyquery.provider.http.servlet.ServletHTTPResponse;

import java.io.IOException;

@MultipartConfig
public class CustomServlet extends HttpServlet {
    private IWebRequestHandler handler;
    private HTTPRequestMethod[] allowMethods;

    public CustomServlet(IWebRequestHandler handler) {
        this(handler, new HTTPRequestMethod[]{HTTPRequestMethod.ALL});
    }

    public CustomServlet(IWebRequestHandler handler, HTTPRequestMethod[] allowMethods) {
        this.handler = handler;
        this.allowMethods = allowMethods;
    }

    @Override
    public void service(
        ServletRequest servletRequest,
        ServletResponse servletResponse
    ) throws IOException {
        servletRequest.setCharacterEncoding("UTF-8");
        servletResponse.setCharacterEncoding("UTF-8");

        if (
            this.allowMethods == null ||
            this.allowMethods[0].equals(HTTPRequestMethod.ALL)
        ) {
            this.handler.handle(
                new ServletHTTPRequest(servletRequest),
                new ServletHTTPResponse(servletResponse)
            );
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HTTPRequestMethod method = HTTPRequestMethod.valueOf(request.getMethod().toUpperCase());
        for (HTTPRequestMethod m : this.allowMethods) {
            if (m.equals(method)) {
                this.handler.handle(
                    new ServletHTTPRequest(servletRequest),
                    new ServletHTTPResponse(servletResponse)
                );
                break;
            }
        }
    }

    @Override
    public void destroy() {
        this.handler = null;
        this.allowMethods = null;
    }
}
