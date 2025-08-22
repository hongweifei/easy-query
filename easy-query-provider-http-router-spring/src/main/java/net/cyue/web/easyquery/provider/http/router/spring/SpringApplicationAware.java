package net.cyue.web.easyquery.provider.http.router.spring;

import net.cyue.web.easyquery.core.util.TaskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationAware implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.logger.info("设置 WebApplicationContext...");
        TaskUtil.runTask(SpringProviderTaskType.SET_CONTEXT.getName(), applicationContext);
    }
}
