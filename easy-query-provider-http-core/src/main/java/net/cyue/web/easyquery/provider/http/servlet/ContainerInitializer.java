package net.cyue.web.easyquery.provider.http.servlet;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import net.cyue.web.easyquery.core.util.TaskUtil;
import net.cyue.web.easyquery.provider.http.HTTPProviderTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ContainerInitializer implements ServletContainerInitializer  {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        this.logger.info("Servlet容器初始化...");
        servletContext.setRequestCharacterEncoding("UTF-8");
        servletContext.setResponseCharacterEncoding("UTF-8");
        TaskUtil.runTask(HTTPProviderTaskType.INIT.getName());
    }
}
