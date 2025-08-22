package net.cyue.web.easyquery.provider.http.router.spring;

import net.cyue.web.easyquery.core.util.TaskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanFactoryAwareSpringRouter implements BeanFactoryAware {

    private final Logger logger = LoggerFactory.getLogger(this.toString());

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.logger.info("设置 BeanFactory...");
        TaskUtil.runTask(SpringProviderTaskType.SET_BEAN_FACTORY, beanFactory);
    }
}
