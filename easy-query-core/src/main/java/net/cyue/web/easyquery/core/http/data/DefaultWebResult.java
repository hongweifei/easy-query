package net.cyue.web.easyquery.core.http.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.cyue.web.easyquery.core.http.HTTPStatus;

import java.util.Date;

/**
 * 默认的 WebResult
 */
public class DefaultWebResult {

    /**
     * 响应结果状态枚举
     */
    public enum Status {
        /**
         * 成功
         */
        SUCCESS,
        /**
         * 失败
         */
        FAILURE,
        /**
         * 错误（when exception）
         */
        ERROR
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    private HTTPStatus code;
    private Status status;
    private String message;
    private Object data;

    private DefaultWebResult() {}

    /**
     * 创建一个成功结果
     * @param message 提示信息
     * @return DefaultWebResult
     */
    public static DefaultWebResult success(String message) {
        DefaultWebResult result = new DefaultWebResult();
        result.time = new Date();
        result.code = HTTPStatus.OK;
        result.status = Status.SUCCESS;
        result.message = message;
        return result;
    }

    /**
     * 创建一个成功结果
     * @param message 提示信息
     * @param data 数据
     * @return DefaultWebResult
     */
    public static DefaultWebResult success(String message, Object data) {
        DefaultWebResult result = DefaultWebResult.success(message);
        result.data = data;
        return result;
    }

    /**
     * 创建一个失败结果
     * @param message 提示信息
     * @return DefaultWebResult
     */
    public static DefaultWebResult failure(String message) {
        DefaultWebResult result = new DefaultWebResult();
        result.time = new Date();
        result.code = HTTPStatus.BAD_REQUEST;
        result.status = Status.FAILURE;
        result.message = message;
        return result;
    }

    /**
     * 创建一个错误结果
     * @param message 提示信息
     * @return DefaultWebResult
     */
    public static DefaultWebResult error(String message) {
        DefaultWebResult result = new DefaultWebResult();
        result.time = new Date();
        result.code = HTTPStatus.INTERNAL_SERVER_ERROR;
        result.status = Status.ERROR;
        result.message = message;
        return result;
    }
}
