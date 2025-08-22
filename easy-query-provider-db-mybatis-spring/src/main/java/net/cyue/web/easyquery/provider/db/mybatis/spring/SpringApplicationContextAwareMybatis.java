package net.cyue.web.easyquery.provider.db.mybatis.spring;

import net.cyue.web.easyquery.core.util.TaskUtil;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationContextAwareMybatis implements ApplicationContextAware {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.logger.info("设置 WebApplicationContext...");
        TaskUtil.runTask(MyBatisSpringProviderTaskType.SET_CONTEXT, applicationContext);
    }
}
