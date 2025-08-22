package net.cyue.web.easyquery.core.http.handler;

import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.http.HTTPStatus;
import net.cyue.web.easyquery.core.http.api.IHTTPResponse;
import net.cyue.web.easyquery.core.http.data.DefaultWebResult;
import net.cyue.web.easyquery.core.http.handler.api.IWebExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * 默认的Web异常处理器
 */
public class DefaultWebExceptionHandler implements IWebExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean handle(Exception e, IHTTPResponse response) {
        this.logger.error(e.getMessage());
        response.code(HTTPStatus.INTERNAL_SERVER_ERROR);
        if (e instanceof SQLException) {
            // 这里可以发送一个特定的错误响应，而不是仅仅记录日志
            // 例如：response.sendError(500, "Database query failed");
            response.send(DefaultWebResult.error("Database query failed"));
            return true; // 异常已处理
        } else if (e instanceof ConfigException) {
            response.send(DefaultWebResult.error("Configuration error"));
            return true;
        }
        // 对于其他未处理的异常，返回 false，让外层代码抛出 RuntimeException
        return false;
    }
}

