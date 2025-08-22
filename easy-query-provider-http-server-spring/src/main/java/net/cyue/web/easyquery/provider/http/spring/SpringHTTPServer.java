package net.cyue.web.easyquery.provider.http.spring;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import net.cyue.web.easyquery.core.http.HTTPRequestMethod;
import net.cyue.web.easyquery.core.http.adapter.AbstractHTTPServer;
import net.cyue.web.easyquery.core.http.handler.api.IWebRequestHandler;
import net.cyue.web.easyquery.core.util.TaskUtil;
import net.cyue.web.easyquery.provider.http.HTTPProviderTaskType;
import net.cyue.web.easyquery.provider.http.spring.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


public class SpringHTTPServer extends AbstractHTTPServer<ServletContext> {

    private static final String DISPATCHER_SERVLET_NAME = "dispatcherServlet";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean isInit = false;

    public SpringHTTPServer(ServletContext context) {
        super(context);
        TaskUtil.addTask(HTTPProviderTaskType.INIT.getName(), (_arg) -> {
            if (!SpringContextUtil.hasApplicationContext()) {
                this.initSpringApplication();
            }
        });
    }

    private void initSpringApplication() {
        if (this.isInit) {
            return;
        }
        this.isInit = true;
        this.logger.info("创建默认 WebApplicationContext...");
        // 创建 WebApplicationContext
        AnnotationConfigWebApplicationContext webApplicationContext = SpringContextUtil.createConfigWebApplicationContext();
        // 初始化 context
        webApplicationContext.setServletContext(this.context);
        // 将 SpringHTTPServer 注册到 BeanFactory
        webApplicationContext.addApplicationListener((ApplicationListener<ContextRefreshedEvent>) event -> {
            this.logger.info("Spring 上下文刷新完成，执行初始化任务...");
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) webApplicationContext.getBeanFactory();
            beanFactory.registerSingleton(this.getClass().getSimpleName(), this);
        });
        // 初始化 SpringMVC
        ServletRegistration.Dynamic dynamic = this.context.addServlet(
            DISPATCHER_SERVLET_NAME,
            new DispatcherServlet(webApplicationContext)
        );
        dynamic.addMapping("/*");
        // dynamic.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
        // dynamic.setInitParameter("contextConfigLocation", DefaultSpringConfig.class.getName());
        dynamic.setLoadOnStartup(1);
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
        this.addRoute(
            "/",
            handler,
            new HTTPRequestMethod[]{
                HTTPRequestMethod.GET,
                HTTPRequestMethod.POST,
                HTTPRequestMethod.HEAD,
                HTTPRequestMethod.DELETE,
                HTTPRequestMethod.PUT,
                HTTPRequestMethod.PATCH,
                HTTPRequestMethod.OPTIONS
            }
        );
    }

    @Override
    public void all(String path, IWebRequestHandler handler) {
        this.addRoute(
            path,
            handler,
            new HTTPRequestMethod[]{
                HTTPRequestMethod.GET,
                HTTPRequestMethod.POST,
                HTTPRequestMethod.HEAD,
                HTTPRequestMethod.DELETE,
                HTTPRequestMethod.PUT,
                HTTPRequestMethod.PATCH,
                HTTPRequestMethod.OPTIONS
            }
        );
    }

}
