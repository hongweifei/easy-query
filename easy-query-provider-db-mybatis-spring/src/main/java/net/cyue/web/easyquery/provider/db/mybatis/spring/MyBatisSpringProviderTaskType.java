package net.cyue.web.easyquery.provider.db.mybatis.spring;

import net.cyue.web.easyquery.core.util.TaskUtil;
import org.springframework.context.ApplicationContext;

public class MyBatisSpringProviderTaskType {
    public static final TaskUtil.TaskType<ApplicationContext> SET_CONTEXT =
        new TaskUtil.TaskType<>("task-provider-db-mybatis-set-context");
}
