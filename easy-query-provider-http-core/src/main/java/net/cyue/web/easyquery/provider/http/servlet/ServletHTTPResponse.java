package net.cyue.web.easyquery.provider.http.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import net.cyue.web.easyquery.core.http.HTTPRequestBody;
import net.cyue.web.easyquery.core.http.HTTPStatus;
import net.cyue.web.easyquery.core.http.api.IHTTPResponse;
import net.cyue.web.easyquery.core.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

public class ServletHTTPResponse implements IHTTPResponse {

    private static final String MESSAGE_OUT_NULL = "writer 为空";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final HttpServletResponse response;
    private final PrintWriter out;

    public ServletHTTPResponse(ServletResponse response) {
        this.response = (HttpServletResponse) response;
        this.response.setCharacterEncoding("utf-8");
        try {
            this.out = this.response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void status(HTTPStatus status) {
        this.response.setStatus(status.code());
    }

    @Override
    public void putHeader(String name, String value) {
        if (this.response.getHeader(name) == null) {
            this.response.addHeader(name, value);
        } else {
            this.response.setHeader(name, value);
        }
    }

    @Override
    public void removeHeader(String name) {
        if (this.response.containsHeader(name)) {
            this.response.setHeader(name, "");
        }
    }

    @Override
    public void redirect(HTTPStatus status, String url) {
        this.status(status);
        try {
            this.response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void redirect(String url) {
        try {
            this.response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void end(String s) {
        if (this.out == null) {
            this.logger.warn(MESSAGE_OUT_NULL);
            return;
        }

        this.out.print(s);
        this.out.flush();
        this.out.close();
    }

    @Override
    public void send(String body) {
        if (this.out == null) {
            this.logger.warn(MESSAGE_OUT_NULL);
            return;
        }

        try {
            final HTTPRequestBody b = HTTPRequestBody.fromJSONString(body);
            this.out.print(b);
        } catch (JsonProcessingException e) {
            this.out.print(body);
        }
    }
    @Override
    public void send(Object body) {
        if (this.out == null) {
            this.logger.warn(MESSAGE_OUT_NULL);
            return;
        }

        String jsonString = JSONUtil.toJSONString(body);
        this.response.setContentType("application/json;charset=utf-8");
        this.out.print(jsonString);
    }

    @Override
    public void write(String s) {
        if (this.out == null) {
            this.logger.warn(MESSAGE_OUT_NULL);
            return;
        }
        this.out.print(s);
    }
}
