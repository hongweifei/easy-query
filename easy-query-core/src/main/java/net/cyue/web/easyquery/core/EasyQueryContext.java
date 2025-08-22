package net.cyue.web.easyquery.core;

import net.cyue.util.ReflectUtil;
import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.db.api.ISQLExecutor;
import net.cyue.web.easyquery.core.http.api.IHTTPRouter;
import net.cyue.web.easyquery.core.http.api.IHTTPServer;
import net.cyue.web.easyquery.core.http.data.PathInfo;
import net.cyue.web.easyquery.core.http.handler.DefaultWebExceptionHandler;
import net.cyue.web.easyquery.core.http.handler.DefaultWebResponseHandler;
import net.cyue.web.easyquery.core.http.handler.DefaultWebResultHandler;
import net.cyue.web.easyquery.core.http.handler.api.IWebExceptionHandler;
import net.cyue.web.easyquery.core.http.handler.api.IWebResponseHandler;
import net.cyue.web.easyquery.core.http.handler.api.IWebResultHandler;
import net.cyue.web.easyquery.core.provider.InstanceProviderLoader;
import net.cyue.web.easyquery.core.provider.ServiceProviderLoader;
import net.cyue.web.easyquery.core.service.api.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * EasyQueryContext
 * @param <TContext> 原始服务上下文
 */
public class EasyQueryContext<TContext> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ISQLExecutor sqlExecutor;
    private IHTTPServer httpServer;

    private String contextPath;
    private IWebResultHandler resultHandler = new DefaultWebResultHandler();;
    private IWebResponseHandler responseHandler = new DefaultWebResponseHandler();
    private IWebExceptionHandler exceptionHandler = new DefaultWebExceptionHandler();

    /**
     * 构造函数
     * @throws IOException IO异常
     * @throws ConfigException 配置异常
     */
    public EasyQueryContext()
        throws IOException, ConfigException
    {
        this.sqlExecutor = InstanceProviderLoader.load(ISQLExecutor.class).getInstance();
    }

    /**
     * 构造函数
     * @param serverContext 原始服务上下文
     * @throws IOException IO异常
     * @throws ConfigException 配置异常
     */
    public EasyQueryContext(TContext serverContext)
        throws IOException, ConfigException
    {
        this(serverContext, "");
    }

    /**
     * 构造函数
     * @param serverContext 原始服务上下文
     * @param contextPath 上下文路径
     * @throws IOException IO异常
     * @throws ConfigException 配置异常
     */
    public EasyQueryContext(TContext serverContext, String contextPath)
        throws IOException, ConfigException
    {
        this.contextPath = contextPath;
        this.sqlExecutor = InstanceProviderLoader.load(ISQLExecutor.class).getInstance();
        this.initHTTPComponents(serverContext);
    }

    /**
     * 设置 原始服务上下文
     * @param serverContext 原始服务上下文
     * @throws IOException IO异常
     * @throws ConfigException 配置异常
     */
    public void setServerContext(TContext serverContext)
        throws IOException, ConfigException
    {
        this.initHTTPComponents(serverContext);
    }

    /**
     * 设置服务上下文路径
     * @param contextPath 服务上下文路径
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * 设置 SQL执行器
     * @param sqlExecutor SQL执行器
     */
    public void setSQLExecutor(ISQLExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    /**
     * 设置 HTTP服务
     * @param httpServer HTTP服务
     */
    public void setHTTPServer(IHTTPServer httpServer) {
        this.httpServer = httpServer;
    }

    /**
     * 设置 HTTP路由器
     * @param httpRouter HTTP路由器
     */
    public void setHTTPRouter(IHTTPRouter httpRouter) {
        Class<?> clz = this.httpServer.getClass();
        while (true) {
            try {
                Field field = ReflectUtil.getField(clz, IHTTPRouter.class);
                ReflectUtil.setFieldValue(this.httpServer, field, httpRouter);
                break;
            } catch (NoSuchFieldException | InvocationTargetException e) {
                this.logger.warn(e.getMessage());
                clz = clz.getSuperclass();
            }
        }
    }

    /**
     * 设置 Web结果处理器
     * @param resultHandler Web结果处理器
     */
    public void setResultHandler(IWebResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    /**
     * 设置 Web响应处理器
     * @param responseHandler Web响应处理器
     */
    public void setResponseHandler(IWebResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    /**
     * 获取 Web异常处理器
     * @param exceptionHandler Web异常处理器
     */
    public void setExceptionHandler(IWebExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }


    private void initHTTPComponents(TContext serverContext)
        throws IOException, ConfigException
    {
        IHTTPServer tempServer;
        IHTTPRouter tempRouter = null;
        // this.httpServer = (IHTTPServer<TContext>) ServiceProviderLoader.load(serverContext).getService();
        try {
            tempServer = InstanceProviderLoader.load(IHTTPServer.class, serverContext).getInstance();
        } catch (ConfigException e) {
            this.logger.warn(e.getMessage());
            IService<?> service;
            service = ServiceProviderLoader.load(serverContext).getService();
            if (service instanceof IHTTPServer) {
                tempServer = (IHTTPServer) service;
            } else {
                throw new ConfigException("Service 未实现 IHTTPServer 接口");
            }
        }
        try {
            tempRouter = InstanceProviderLoader.load(IHTTPRouter.class, serverContext).getInstance();
        } catch (ConfigException ce1) {
            this.logger.warn(ce1.getMessage());
            try {
                IService<?> service = ServiceProviderLoader.load(serverContext).getService();
                if (service instanceof IHTTPRouter) {
                    tempRouter = (IHTTPRouter) service;
                }
            } catch (ConfigException ce2) {
                this.logger.warn(ce2.getMessage());
            }
        }

        this.httpServer = tempServer;
        if (tempRouter != null) {
            this.setHTTPRouter(tempRouter);
        }
    }


    /**
     * 注册查询接口
     * @param pathInfo 接口信息
     * @param sql 用于查询的预编译SQL
     * @throws ConfigException 配置异常
     */
    public void registerQuery(
        PathInfo pathInfo,
        String sql
    ) throws ConfigException {
        if (this.sqlExecutor == null) {
            this.logger.warn("SQLExecutor 为空");
            throw new ConfigException("SQLExecutor 未初始化，请先注册服务");
        }
        if (this.httpServer == null) {
            this.logger.warn("HTTPServer 为空");
            throw new ConfigException("HTTPServer 未初始化，请先注册服务");
        }

        this.logger.info("注册查询：" + pathInfo.getAPIPath());
        pathInfo.setContextPath(this.contextPath);
        this.httpServer.get(pathInfo.getAPIPath(), (request, response) -> {
            Map<String, Object> parameterMap = pathInfo.parsePathParameterMap(request.uri());
            for (String parameterName : pathInfo.getQueryParameterList()) {
                String[] parameters = request.getParameterValues(parameterName);
                if (parameters == null || parameters.length == 0) {
                    continue;
                }
                if (parameters.length == 1) {
                    parameterMap.put(parameterName, parameters[0]);
                    continue;
                }
                parameterMap.put(parameterName, parameters);
            }
            try {
                List<Map<String, Object>> list = this.sqlExecutor.query(sql, parameterMap);
                Object result = this.resultHandler.handle(list);
                this.responseHandler.handle(response, result);
            } catch (Exception e) {
                if (!this.exceptionHandler.handle(e, response)) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
