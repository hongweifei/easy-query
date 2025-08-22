package net.cyue.web.easyquery.core.http.handler.api;

import java.util.List;
import java.util.Map;

/**
 * Web返回结果处理器接口
 */
public interface IWebResultHandler {
    /**
     * 处理查询结果
     * @param queryResult 查询结果
     * @return 处理后的结果
     */
    Object handle(List<Map<String, Object>> queryResult);
}
