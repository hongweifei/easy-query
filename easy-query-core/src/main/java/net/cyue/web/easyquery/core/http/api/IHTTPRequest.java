package net.cyue.web.easyquery.core.http.api;

import net.cyue.util.ObjectConverter;
import net.cyue.web.easyquery.core.http.HTTPRequestBody;
import net.cyue.web.easyquery.core.http.HTTPRequestMethod;

import java.lang.reflect.Array;
import java.util.Map;

/**
 * HTTP请求接口
 */
public interface IHTTPRequest {

    /**
     * @return 访问路径（也许不包含query），如 <a href="http://localhost:8080/api/v1/user/list">list</a>
     */
    String url();
    /**
     * @return 资源路径（也许不包含query）
     */
    String uri();

    /**
     * @return 请求协议
     */
    String scheme();

    /**
     * @return 请求方法
     */
    HTTPRequestMethod method();

    /**
     * @return 请求 body
     */
    HTTPRequestBody body();

    /**
     * @param name header 名称
     * @return 请求 header
     */
    String getHeader(String name);

    /**
     * @param name query 参数名称
     * @return query 参数值
     */
    String getParameter(String name);

    /**
     * @param <T> 目标类型
     * @param name query 参数名称
     * @param clazz 目标类
     * @return query 参数值
     */
    default <T> T getParameter(String name, Class<T> clazz) {
        String value = this.getParameter(name);
        return ObjectConverter.convert(value, clazz);
    }

    /**
     * @param name query 参数名称
     * @return query 参数值列表
     */
    String[] getParameterValues(String name);

    /**
     * @param <T> 目标类型
     * @param name query 参数名称
     * @param clazz 目标类
     * @return query 参数值列表
     */
    default <T> T[] getParameterValues(String name, Class<T> clazz)
    {
        Object values = this.getParameterValues(name);
        T[] arrayInstance = (T[]) Array.newInstance(clazz, 0);
        return (T[]) ObjectConverter.convert(values, arrayInstance.getClass());
    }

    /**
     * 获取 query 参数表
     * @return query 参数表
     */
    Map<String, String[]> getParameterMap();
}
