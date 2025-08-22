package net.cyue.web.easyquery.core.http.api;

import net.cyue.web.easyquery.core.http.HTTPStatus;

/**
 * HTTP响应接口
 */
public interface IHTTPResponse {
    /**
     * 设置状态码
     * @param status 状态码
     */
    default void code(HTTPStatus status) {
        this.status(status);
    }

    /**
     * 设置状态码
     * @param status 状态码
     */
    void status(HTTPStatus status);

    /**
     * 添加响应头
     * @param name 响应头名称
     * @param value 响应头值
     */
    void putHeader(String name, String value);

    /**
     * 移除响应头
     * @param name 响应头名称
     */
    void removeHeader(String name);

    /**
     * 设置重定向
     * @param status 状态码
     * @param url 重定向的URL
     */
    void redirect(HTTPStatus status, String url);

    /**
     * 设置重定向
     * @param url 重定向的URL
     */
    void redirect(String url);

    /**
     * 结束响应
     * @param s 响应值
     */
    void end(String s);

    /**
     * 发送响应
     * @param body 响应体
     */
    void send(String body);

    /**
     * 发送响应
     * @param body 响应体
     */
    void send(Object body);

    /**
     * 输出响应
     * @param s 响应值
     */
    void write(String s);
}
