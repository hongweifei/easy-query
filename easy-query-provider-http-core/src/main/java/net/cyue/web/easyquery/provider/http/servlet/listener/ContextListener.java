package net.cyue.web.easyquery.provider.http.servlet.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import net.cyue.web.easyquery.core.util.TaskUtil;
import net.cyue.web.easyquery.provider.http.HTTPProviderTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ContextListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void contextInitialized(ServletContextEvent sce) {
        this.logger.info("Servlet容器初始化完成");
        // 添加路由
        TaskUtil.runTask(HTTPProviderTaskType.ADD_ROUTE.getName());
    }

}
