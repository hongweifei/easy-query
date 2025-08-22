package net.cyue.web.easyquery.provider.http.servlet;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import net.cyue.util.StreamUtil;
import net.cyue.web.easyquery.core.http.HTTPRequestBody;
import net.cyue.web.easyquery.core.http.HTTPRequestMethod;
import net.cyue.web.easyquery.core.http.api.IHTTPRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ServletHTTPRequest implements IHTTPRequest {

    private final HttpServletRequest request;

    public ServletHTTPRequest(ServletRequest request) {
        this.request = (HttpServletRequest) request;
        try {
            this.request.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String url() {
        return this.request.getRequestURL().toString();
    }

    @Override
    public String scheme() {
        return this.request.getScheme();
    }

    @Override
    public HTTPRequestMethod method() {
        return HTTPRequestMethod.valueOf(this.request.getMethod().toUpperCase());
    }

    @Override
    public String uri() {
        return this.request.getRequestURI();
    }

    @Override
    public HTTPRequestBody body() {
        try {
            final InputStream input = this.request.getInputStream();
            String bodyString;
            String contentType = this.getHeader("Content-Type");
            if (contentType == null) {
                contentType = "";
            }

            if (contentType.contains("form-data")) {
                String boundary = contentType.split(";")[1].split("=")[1];
                return HTTPRequestBody.fromFormData(boundary, input);
            } else {
                bodyString = StreamUtil.readAsString(input);
            }

            if (contentType.contains("application/json")) {
                return HTTPRequestBody.fromJSONString(bodyString);
            } else {
                return HTTPRequestBody.fromQueryString(bodyString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getHeader(String name) {
        return this.request.getHeader(name);
    }

    @Override
    public String getParameter(String name) {
        return this.request.getParameter(name);
    }
    @Override
    public String[] getParameterValues(String name) {
        return this.request.getParameterValues(name);
    }
    @Override
    public Map<String, String[]> getParameterMap() {
        return this.request.getParameterMap();
    }
}

