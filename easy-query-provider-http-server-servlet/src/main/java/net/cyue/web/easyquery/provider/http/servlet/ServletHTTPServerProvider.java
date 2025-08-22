package net.cyue.web.easyquery.provider.http.servlet;

import jakarta.servlet.ServletContext;
import net.cyue.web.easyquery.core.provider.AbstractServiceProvider;

public class ServletHTTPServerProvider extends AbstractServiceProvider<ServletContext, ServletHTTPServer> {

    public ServletHTTPServerProvider(ServletContext context) {
        super(context);
    }
    @Override
    protected ServletHTTPServer initService() {
        return new ServletHTTPServer(this.prototype);
    }
}
