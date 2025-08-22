package net.cyue.web.easyquery.provider.http;

import jakarta.servlet.ServletContext;
import net.cyue.web.easyquery.core.util.TaskUtil;

public class HTTPProviderTaskType {
    public static final TaskUtil.TaskType<ServletContext> INIT =
        new TaskUtil.TaskType<>("task-provider-http-init");
    public static final TaskUtil.TaskType<ServletContext> ADD_ROUTE =
        new TaskUtil.TaskType<>("task-provider-http-add-route");
}
