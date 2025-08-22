package net.cyue.web.easyquery.core.http.handler;

import net.cyue.web.easyquery.core.http.data.DefaultWebResult;
import net.cyue.web.easyquery.core.http.handler.api.IWebResultHandler;

import java.util.List;
import java.util.Map;

/**
 * 默认结果处理器
 */
public class DefaultWebResultHandler implements IWebResultHandler {
    @Override
    public Object handle(List<Map<String, Object>> queryResult) {
        return DefaultWebResult.success("success", queryResult);
    }
}
