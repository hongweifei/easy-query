package net.cyue.web.easyquery.core;

import net.cyue.web.easyquery.core.util.TaskUtil;

public class EasyQueryApplicationTaskType {
    public static final TaskUtil.TaskType<EasyQueryApplication<?>> INIT =
        new TaskUtil.TaskType<>("task-application-init");
}
