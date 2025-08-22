package net.cyue.web.easyquery.provider.http.router.spring;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import net.cyue.web.easyquery.core.EasyQueryApplication;
import net.cyue.web.easyquery.core.EasyQueryApplicationTaskType;
import net.cyue.web.easyquery.core.http.HTTPRequestMethod;
import net.cyue.web.easyquery.core.http.adapter.AbstractHTTPRouter;
import net.cyue.web.easyquery.core.http.handler.api.IWebRequestHandler;
import net.cyue.web.easyquery.core.util.TaskUtil;
import net.cyue.web.easyquery.provider.http.HTTPProviderTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Arrays;

public class SpringHTTPRouter extends AbstractHTTPRouter<ServletContext> {

    private final Logger logger = LoggerFactory.getLogger(this.toString());
    // Spring MVC的请求映射处理器
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public SpringHTTPRouter(ServletContext context) {
        super(context);
        TaskUtil.Task<WebApplicationContext> setContextTask = (webApplicationContext) -> {
            this.setRequestMappingHandler(webApplicationContext);
            this.runAddRouteTask();
        };
        TaskUtil.Task<EasyQueryApplication<?>> appInitTask = (app) -> {
            ServletContext servletContext = (ServletContext) app.getServerContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            setContextTask.run(webApplicationContext);
        };
        TaskUtil.addTask(EasyQueryApplicationTaskType.INIT, appInitTask);
        TaskUtil.addTask(SpringProviderTaskType.SET_CONTEXT, setContextTask);
    }

    public void setRequestMappingHandler(WebApplicationContext webApplicationContext) {
        if (webApplicationContext == null) {
            this.logger.warn("WebApplicationContext 为空");
            return;
        }
        try {
            this.logger.info("获取 RequestMappingHandlerMapping");
            this.requestMappingHandlerMapping = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
        } catch (BeansException e) {
            this.logger.error(e.getMessage());
            this.logger.info("自行创建 RequestMappingHandlerMapping");
            RequestMappingHandlerMapping mappingHandler = new RequestMappingHandlerMapping();
            mappingHandler.setApplicationContext(webApplicationContext);
            this.requestMappingHandlerMapping = mappingHandler;
            TaskUtil.addTask(SpringProviderTaskType.SET_BEAN_FACTORY, (beanFactory) -> {
                DefaultListableBeanFactory factory =  (DefaultListableBeanFactory) beanFactory;
                factory.registerSingleton("requestMappingHandlerMapping", this.requestMappingHandlerMapping);
            });
        }
    }
    public void setRequestMappingHandler(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    /**
     * 运行添加路由任务
     */
    private void runAddRouteTask() {
        TaskUtil.runTask(HTTPProviderTaskType.ADD_ROUTE, this.context);
    }

    @Override
    public boolean addRoute(String path, IWebRequestHandler handler, HTTPRequestMethod[] methods) {
        // 支持 spring-boot
        if (this.requestMappingHandlerMapping == null) {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(this.context);
            if (webApplicationContext != null) {
                this.setRequestMappingHandler(webApplicationContext);
            }
        }

        TaskUtil.Task<ServletContext> task = (_context) -> {
            RequestMethod[] springMethods =
                Arrays
                    .stream(methods)
                    .map(m -> RequestMethod.valueOf(m.name()))
                    .toList()
                    .toArray(new RequestMethod[0]);
            // 创建请求映射信息
            RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(path)
                .methods(springMethods)
                .build();

            try {
                RequestService requestService = new RequestService(handler);
                Method serviceMethod = RequestService.class.getMethod("service", ServletRequest.class, ServletResponse.class);
                this.requestMappingHandlerMapping.registerMapping(
                    requestMappingInfo,
                    requestService,
                    serviceMethod
                );
                this.logger.info(
                    "\n添加路由：\n\t路由: {}\n\t请求方法: {}",
                    path,
                    Arrays.toString(methods)
                );
            } catch (NoSuchMethodException e) {
                this.logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        };

        TaskUtil.addTask(HTTPProviderTaskType.ADD_ROUTE, task);
        if (this.requestMappingHandlerMapping == null) {
            return true;
        }
        this.runAddRouteTask();
        return true;
    }
}
