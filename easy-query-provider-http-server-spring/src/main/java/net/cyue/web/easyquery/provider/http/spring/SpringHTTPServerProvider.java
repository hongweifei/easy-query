package net.cyue.web.easyquery.provider.http.spring;

import jakarta.servlet.ServletContext;
import net.cyue.web.easyquery.core.provider.AbstractServiceProvider;

public class SpringHTTPServerProvider extends AbstractServiceProvider<ServletContext, SpringHTTPServer> {

    private final SpringHTTPServer server;

    public SpringHTTPServerProvider(ServletContext applicationContext) {
        super(applicationContext);
        this.server = new SpringHTTPServer(applicationContext);
    }

    @Override
    protected SpringHTTPServer initService() {
        return server;
    }
}
