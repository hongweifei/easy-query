package net.cyue.web.easyquery.provider.http.servlet;

import jakarta.servlet.ServletContext;
import net.cyue.web.easyquery.core.http.HTTPRequestMethod;
import net.cyue.web.easyquery.core.http.adapter.AbstractHTTPServer;
import net.cyue.web.easyquery.core.http.handler.api.IWebRequestHandler;
import net.cyue.web.easyquery.core.util.TaskUtil;
import net.cyue.web.easyquery.provider.http.HTTPProviderTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServletHTTPServer extends AbstractHTTPServer<ServletContext> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServletHTTPServer(ServletContext context) {
        super(context);
        TaskUtil.addTask(HTTPProviderTaskType.INIT.getName(), (_arg) -> {
            this.logger.info("初始化 HTTPServer@{}", this.getClass().getSimpleName());
            TaskUtil.runTask(HTTPProviderTaskType.ADD_ROUTE.getName());
        });
    }


    @Override
    public void use(IWebRequestHandler handler) {
        this.addRoute("/", handler, null);
    }

    @Override
    public void use(String path, IWebRequestHandler handler) {
        this.addRoute(path, handler, null);
    }

    @Override
    public void get(IWebRequestHandler handler) {
        this.addRoute("/", handler, new HTTPRequestMethod[]{HTTPRequestMethod.GET});
    }

    @Override
    public void get(String path, IWebRequestHandler handler) {
        this.addRoute(path, handler, new HTTPRequestMethod[]{HTTPRequestMethod.GET});
    }

    @Override
    public void post(IWebRequestHandler handler) {
        this.addRoute("/", handler, new HTTPRequestMethod[]{HTTPRequestMethod.POST});
    }

    @Override
    public void post(String path, IWebRequestHandler handler) {
        this.addRoute(path, handler, new HTTPRequestMethod[]{HTTPRequestMethod.POST});
    }

    @Override
    public void head(IWebRequestHandler handler) {
        this.addRoute("/", handler, new HTTPRequestMethod[]{HTTPRequestMethod.HEAD});
    }

    @Override
    public void head(String path, IWebRequestHandler handler) {
        this.addRoute(path, handler, new HTTPRequestMethod[]{HTTPRequestMethod.HEAD});
    }

    @Override
    public void delete(IWebRequestHandler handler) {
        this.addRoute("/", handler, new HTTPRequestMethod[]{HTTPRequestMethod.DELETE});
    }

    @Override
    public void delete(String path, IWebRequestHandler handler) {
        this.addRoute(path, handler, new HTTPRequestMethod[]{HTTPRequestMethod.DELETE});
    }

    @Override
    public void put(IWebRequestHandler handler) {
        this.addRoute("/", handler, new HTTPRequestMethod[]{HTTPRequestMethod.PUT});
    }

    @Override
    public void put(String path, IWebRequestHandler handler) {
        this.addRoute(path, handler, new HTTPRequestMethod[]{HTTPRequestMethod.PUT});
    }

    @Override
    public void patch(IWebRequestHandler handler) {
        this.addRoute("/", handler, new HTTPRequestMethod[]{HTTPRequestMethod.PATCH});
    }

    @Override
    public void patch(String path, IWebRequestHandler handler) {
        this.addRoute(path, handler, new HTTPRequestMethod[]{HTTPRequestMethod.PATCH});
    }

    @Override
    public void options(IWebRequestHandler handler) {
        this.addRoute("/", handler, new HTTPRequestMethod[]{HTTPRequestMethod.OPTIONS});
    }

    @Override
    public void options(String path, IWebRequestHandler handler) {
        this.addRoute(path, handler, new HTTPRequestMethod[]{HTTPRequestMethod.OPTIONS});
    }

    @Override
    public void all(IWebRequestHandler handler) {
        this.addRoute("/", handler, null);
    }

    @Override
    public void all(String path, IWebRequestHandler handler) {
        this.addRoute(path, handler, null);
    }
}
