package net.cyue.web.easyquery.provider.http.router.servlet;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import net.cyue.web.easyquery.core.http.HTTPRequestMethod;
import net.cyue.web.easyquery.core.http.adapter.AbstractHTTPRouter;
import net.cyue.web.easyquery.core.http.handler.api.IWebRequestHandler;
import net.cyue.web.easyquery.core.util.TaskUtil;
import net.cyue.web.easyquery.provider.http.HTTPProviderTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ServletHTTPRouter extends AbstractHTTPRouter<ServletContext> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServletHTTPRouter(ServletContext context) {
        super(context);
    }

    private String getNewServletName(String path) {
        // return "Servlet" + (++TomcatAdapter.SERVLET_COUNT);
        String[] parts = path.split("/");
        return "servlet" + String.join("-", parts);
    }

    // 转换为默认映射（例如：/users/{id} -> /users/*）
    private String convertToDefaultMapping(String path) {
        return path.replaceAll("\\{.*?}", "*");
    }

    @Override
    public boolean addRoute(String path, IWebRequestHandler handler, HTTPRequestMethod[] methods) {
        TaskUtil.addTask(HTTPProviderTaskType.ADD_ROUTE.getName(), (_arg) -> {
            final String servletName = this.getNewServletName(path);
            final Servlet servlet = new CustomServlet(handler, methods);

            ServletRegistration.Dynamic dynamic = this.context.addServlet(servletName, servlet);
            dynamic.addMapping(convertToDefaultMapping(path));

            this.logger.info(
                "\n添加路由：\n\tservlet: {}\n\t路由: {}\n\t请求方法: {}",
                servletName,
                path,
                Arrays.toString(methods)
            );
        });
        return true;
    }
}
