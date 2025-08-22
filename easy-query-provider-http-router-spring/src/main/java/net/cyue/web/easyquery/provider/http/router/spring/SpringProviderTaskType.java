package net.cyue.web.easyquery.provider.http.router.spring;

import net.cyue.web.easyquery.core.util.TaskUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;

public class SpringProviderTaskType {
    public static final TaskUtil.TaskType<WebApplicationContext> SET_CONTEXT =
        new TaskUtil.TaskType<>("task-provider-http-spring-set-context");
    public static final TaskUtil.TaskType<BeanFactory> SET_BEAN_FACTORY =
            new TaskUtil.TaskType<>("task-provider-http-spring-set-bean-factory");
}
