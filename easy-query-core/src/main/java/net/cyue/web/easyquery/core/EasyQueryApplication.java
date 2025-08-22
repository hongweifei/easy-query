package net.cyue.web.easyquery.core;

import net.cyue.util.ReflectUtil;
import net.cyue.util.StringUtil;
import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.config.ConfigGroup;
import net.cyue.web.easyquery.core.config.ConfigItem;
import net.cyue.web.easyquery.core.config.IConfigItemHandler;
import net.cyue.web.easyquery.core.db.api.ISQLExecutor;
import net.cyue.web.easyquery.core.http.api.IHTTPServer;
import net.cyue.web.easyquery.core.http.data.PathInfo;
import net.cyue.web.easyquery.core.http.handler.api.IWebExceptionHandler;
import net.cyue.web.easyquery.core.http.handler.api.IWebResponseHandler;
import net.cyue.web.easyquery.core.http.handler.api.IWebResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * EasyQueryApplication
 * @param <TContext> 原始服务上下文
 */
public class EasyQueryApplication<TContext> {

    /// 处理器配置组
    private static final ConfigGroup CONFIG_GROUP_HANDLER = ConfigGroup.create(ConfigGroup.APPLICATION_CONTEXT, "handler");

    /// 日志设置配置组
    private static final ConfigGroup CONFIG_GROUP_LOG = ConfigGroup.create(ConfigGroup.APPLICATION, "log");

    /// 主包名
    private static final String MAIN_PACKAGE_NAME = "net.cyue.web.easyquery";

    private boolean handlerInitialized = false;
    private final Map<ConfigItem, IConfigItemHandler> additionalConfigItemHandlerMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private EasyQueryContext<TContext> context;

    /**
     * 构造函数
     */
    public EasyQueryApplication()
    {
        this.registerDefaultConfigItemHandlers();
        this.logConfigItemInfo();
    }

    /**
     * 构造函数
     * @param context EasyQueryContext
     */
    public EasyQueryApplication(EasyQueryContext<TContext> context)
    {
        this.context = context;
        this.registerDefaultConfigItemHandlers();
        this.logConfigItemInfo();
    }

    private void logConfigItemInfo() {
        // 打印信息
        StringBuilder sb = new StringBuilder("\n附加配置项：");
        for (ConfigItem item : this.additionalConfigItemHandlerMap.keySet()) {
            sb.append("\n\t").append(item.getFullName());
        }
        logger.info(sb.toString());
    }

    private void registerDefaultConfigItemHandlers() {
        if (handlerInitialized) {
            return;
        }
        handlerInitialized = true;

        // 上下文路径 配置
        this.registerConfigItem(
            ConfigItem.create(
                ConfigGroup.APPLICATION_CONTEXT,
                "path",
                "context path"
            ),
            (ctx, _configItem, contextPath) -> {
                ctx.setContextPath(contextPath);
            }
        );

        // 自定义 WebResult 处理器
        this.registerConfigItem(
            ConfigItem.create(
                CONFIG_GROUP_HANDLER,
                "result",
                "WebResult 处理器"
            ),
            (ctx, _configItem, className) -> {
                ConfigException configException = new ConfigException("Invalid result handler class: " + className);
                if (StringUtil.isBlank(className) || !className.contains(".")) {
                    throw configException;
                }
                try {
                    Class<?> clz = Class.forName(className);
                    if (!IWebResultHandler.class.isAssignableFrom(clz)) {
                        throw configException;
                    }
                    IWebResultHandler handler = ReflectUtil.createInstance((Class<IWebResultHandler>) clz);
                    ctx.setResultHandler(handler);
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    throw configException;
                }
            }
        );

        // 自定义 Response 处理器
        this.registerConfigItem(
            ConfigItem.create(
                CONFIG_GROUP_HANDLER,
                "response",
                "WebResponse 处理器"
            ),
            (ctx, _configItem, className) -> {
                ConfigException configException = new ConfigException("Invalid response handler class: " + className);
                if (StringUtil.isBlank(className) || !className.contains(".")) {
                    throw configException;
                }
                try {
                    Class<?> clz = Class.forName(className);
                    if (!IWebResponseHandler.class.isAssignableFrom(clz)) {
                        throw configException;
                    }
                    IWebResponseHandler handler = ReflectUtil.createInstance((Class<IWebResponseHandler>) clz);
                    ctx.setResponseHandler(handler);
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    throw configException;
                }
            }
        );

        // 自定义 WebException 处理器
        this.registerConfigItem(
            ConfigItem.create(
                CONFIG_GROUP_HANDLER,
                "exception",
                "WebException 处理器"
            ),
            (ctx, _configItem, className) -> {
                if (StringUtil.isBlank(className) || !className.contains(".")) {
                    className = "net.cyue.web.easyquery.core.http.DefaultExceptionHandler";
                }
                ConfigException configException = new ConfigException("Invalid exception handler class: " + className);
                try {
                    Class<?> clz = Class.forName(className);
                    if (!IWebExceptionHandler.class.isAssignableFrom(clz)) {
                        throw configException;
                    }
                    IWebExceptionHandler handler = ReflectUtil.createInstance((Class<IWebExceptionHandler>) clz);
                    ctx.setExceptionHandler(handler);
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    throw configException;
                }
            }
        );
    }


    /**
     * 注册自定义配置项
     * @param configItem 配置项
     * @param handler 配置项处理器
     */
    public void registerConfigItem(ConfigItem configItem, IConfigItemHandler handler) {
        this.additionalConfigItemHandlerMap.put(configItem, handler);
    }

    /**
     * 设置原始服务上下文
     * @param serverContext 原始服务上下文
     * @throws ConfigException 配置异常
     * @throws IOException IO异常
     */
    public void setServerContext(TContext serverContext)
        throws ConfigException, IOException
    {
        this.context = new EasyQueryContext<>(serverContext);
    }

    /**
     * 设置 EasyQueryApplication 上下文
     * @param context EasyQueryContext
     */
    public void setContext(EasyQueryContext<TContext> context) {
        this.context = context;
    }

    /**
     * 设置上下文路径
     * @param path 上下文路径
     */
    public void setContextPath(String path) {
        this.context.setContextPath(path);
    }

    /**
     * 设置 SQL 执行器
     * @param sqlExecutor SQL 执行器
     */
    public void setSQLExecutor(ISQLExecutor sqlExecutor) {
        this.context.setSQLExecutor(sqlExecutor);
    }

    /**
     * 设置 HTTP 服务器
     * @param httpServer HTTP 服务器
     */
    public void setHTTPServer(IHTTPServer httpServer) {
        this.context.setHTTPServer(httpServer);
    }

    /**
     * 设置 WebResult 处理器
     * @param resultHandler 结果处理器
     */
    public void setResultHandler(IWebResultHandler resultHandler) {
        this.context.setResultHandler(resultHandler);
    }

    /**
     * 设置 Web 响应处理器
     * @param responseHandler 设置 HTTP 响应处理器
     */
    public void setResponseHandler(IWebResponseHandler responseHandler) {
        this.context.setResponseHandler(responseHandler);
    }

    /**
     * 设置 Web 异常处理器
     * @param exceptionHandler Web 异常处理器
     */
    public void setExceptionHandler(IWebExceptionHandler exceptionHandler) {
        this.context.setExceptionHandler(exceptionHandler);
    }

    /**
     * 获取 EasyQuery 上下文
     * @return EasyQueryContext
     */
    public EasyQueryContext<TContext> getContext() {
        return this.context;
    }


    /**
     * 运行 EasyQueryApplication By Properties
     * @param configFilePath 配置文件路径
     * @throws IOException IO 异常
     * @throws ConfigException 配置异常
     */
    public void runByProperties(String configFilePath)
        throws IOException, ConfigException
    {
        this.runByProperties(new File(configFilePath));
    }

    /**
     * 运行 EasyQueryApplication By Properties
     * @param configFile 配置文件
     * @throws IOException IO 异常
     * @throws ConfigException 配置异常
     */
    public void runByProperties(File configFile)
        throws IOException, ConfigException
    {
        try (InputStream inputStream = Files.newInputStream(configFile.toPath())) {
            this.runByProperties(inputStream);
        }
    }

    /**
     * 通过配置文件运行
     * @param is 配置文件输入流
     * @throws IOException IO 异常
     * @throws ConfigException 配置异常
     */
    public void runByProperties(InputStream is)
        throws IOException, ConfigException
    {
        Properties properties = new Properties();
        properties.load(is);
        this.runByProperties(properties);
    }

    /**
     * 通过配置文件运行
     * @param properties 配置文件实例对象
     * @throws ConfigException 配置异常
     */
    public void runByProperties(Properties properties) throws ConfigException {
        Enumeration<?> keyEnumeration = properties.propertyNames();
        Map<PathInfo, String> pathQueryMap = new HashMap<>();
        Map<ConfigItem, Boolean> configItemHandledMap = new HashMap<>(); // 处理过的配置项
        while (keyEnumeration.hasMoreElements()) {
            String key = (String) keyEnumeration.nextElement();
            // 附加配置项
            boolean isConfigItem = false;
            for (Map.Entry<ConfigItem, IConfigItemHandler> entry : this.additionalConfigItemHandlerMap.entrySet()) {
                ConfigItem configItem = entry.getKey();
                IConfigItemHandler handler = entry.getValue();

                // 跳过处理过的
                if (configItemHandledMap.getOrDefault(configItem, false)) {
                    continue;
                }

                if (configItem.equals(key)) {
                    if (handler == null) {
                        throw new ConfigException("Invalid config item: " + configItem.getFullName());
                    }
                    this.logger.info("处理配置项：" + configItem.getFullName());
                    isConfigItem = true;
                    configItemHandledMap.put(configItem, true);
                    handler.handle(this.context, configItem, properties.getProperty(key));
                    break;
                }
            }
            if (isConfigItem) {
                continue;
            }

            // 默认查询配置项
            String sql = properties.getProperty(key);
            if (StringUtil.isBlank(key) || StringUtil.isBlank(sql)) {
                continue;
            }
            if (sql.startsWith("\"")) {
                sql = sql.substring(1, sql.length() - 1);
            }
            if (sql.endsWith("\"")) {
                sql = sql.substring(0, sql.length() - 1);
            }
            if (sql.startsWith("'")) {
                sql = sql.substring(1, sql.length() - 1);
            }
            if (sql.endsWith("'")) {
                sql = sql.substring(0, sql.length() - 1);
            }

            PathInfo pathInfo = PathInfo.fromAPIPath(key.replace('.', '/'));
            pathQueryMap.put(pathInfo, sql);
        }

        for (Map.Entry<PathInfo, String> entry : pathQueryMap.entrySet()) {
            PathInfo pathInfo = entry.getKey();
            String sql = entry.getValue();
            this.context.registerQuery(pathInfo, sql);
        }
    }
}
