package net.cyue.web.easyquery.core.provider;

import net.cyue.util.FileUtil;
import net.cyue.util.ReflectUtil;
import net.cyue.util.ResourceUtil;
import net.cyue.web.easyquery.core.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 抽象的提供者加载器
 */
public abstract class ProviderLoader {

    private static final String PREFIX = "META-INF/provider";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderLoader.class);

    /**
     * 原型（实例）类名与提供者类名的映射表
     */
    private static final Map<String, String> PROTOTYPE_PROVIDER_MAP = new HashMap<>();
    static {
        LOGGER.info("初始化 ProviderLoader");
        LOGGER.info("Loading provider definitions...");
        ClassLoader classLoader =  ProviderLoader.class.getClassLoader();
        URL folderURL = classLoader.getResource(PREFIX);
        URI folderURI;
        String folderProtocol;
        if (folderURL == null) {
            throw new RuntimeException("Provider folder not found");
        }

        try {
            folderURI = folderURL.toURI();
            folderProtocol = folderURL.getProtocol();
            LOGGER.info("Provider folder URL: " + folderURL);
            LOGGER.info("Provider folder URI: " + folderURI);
            LOGGER.info("Provider folder protocol: " + folderProtocol);
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }

        if ("file".equals(folderProtocol)) {
            Path folderPath = Paths.get(folderURI);
            File folder = folderPath.toFile();
            LOGGER.info("Provider folder path: " + folderPath);
            File[] files = folder.listFiles();
            if (files == null) {
                files = new File[0];
            }
            for (File f : files) {
                if (!f.isFile()) {
                    continue;
                }
                try {
                    String providedClassName = f.getName();
                    String providerClassName = FileUtil.readAsString(f);
                    LOGGER.info("Found provider {} for {}", providerClassName, providedClassName);
                    PROTOTYPE_PROVIDER_MAP.put(providedClassName, providerClassName);
                } catch (IOException e) {
                    LOGGER.warn(e.getMessage());
                }
            }
        } else if ("jar".equals(folderProtocol)) {
            // JAR 包环境：从 JAR 包中读取
            // throw new UnsupportedOperationException("不支持的资源协议：" + folderProtocol);
            LOGGER.warn("不支持的资源协议：{}", folderProtocol);
        } else {
            throw new UnsupportedOperationException("不支持的资源协议：" + folderProtocol);
        }
    }

    /**
     * 构造函数，防止实例化
     */
    protected ProviderLoader() {}

    /**
     * 获取原型（实例）类名与提供者类名的映射表
     * @return 原型（实例）类名与提供者类名的映射表
     */
    protected static Map<String, String> getPrototypeProviderMap() {
        return PROTOTYPE_PROVIDER_MAP;
    }

    /**
     * 通过原型（实例）类型获取提供者类名
     * @param prototypeClz 原型（实例）类型
     * @return 提供者类名
     * @throws IOException IO异常
     * @throws ConfigException 配置异常
     */
    protected static String getProviderClassName(Class<?> prototypeClz)
        throws IOException, ConfigException
    {
        if (PROTOTYPE_PROVIDER_MAP.containsKey(prototypeClz.getName())) {
            return PROTOTYPE_PROVIDER_MAP.get(prototypeClz.getName());
        }

        String fileName = prototypeClz.getName();
        InputStream is = ResourceUtil.getResourceAsStream(PREFIX + "/" + fileName);
        StringBuilder errorMessage = new StringBuilder("No provider found for " + prototypeClz.getName());
        if (is == null) {
            if (prototypeClz.getInterfaces().length == 0) {
                LOGGER.warn(errorMessage.toString());
                throw new ConfigException(errorMessage.toString());
            }
            for (Class<?> interfaceClz : prototypeClz.getInterfaces()) {
                fileName = interfaceClz.getName();
                is = ResourceUtil.getResourceAsStream(PREFIX + "/" + fileName);
                if (is != null) {
                    break;
                }
                errorMessage.append(" or ").append(interfaceClz.getName());
            }
        }
        if (is == null) {
            LOGGER.warn(errorMessage.toString());
            throw new ConfigException(errorMessage.toString());
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String className = bufferedReader.readLine();
        is.close();
        if (className == null) {
            LOGGER.warn(errorMessage.toString());
            throw new ConfigException(errorMessage.toString());
        }
        LOGGER.info("Found provider {} for {}", className, prototypeClz.getName());
        return className;
    }


    /**
     * 获取提供者构造器
     * @param providerClz 提供者类
     * @param args 构造参数
     * @return 提供者构造器
     */
    protected static Constructor<?> getProviderConstructor(
        Class<?> providerClz,
        Object... args
    ) {
        return ProviderLoader.getProviderConstructor(providerClz, ReflectUtil.getArgTypes(args));
    }

    /**
     * 获取提供者构造器
     * @param providerClz 提供者类
     * @param argTypes 构造参数类型
     * @return 提供者构造器
     */
    protected static Constructor<?> getProviderConstructor(
        Class<?> providerClz,
        Class<?>... argTypes
    ) {
        return ReflectUtil.getConstructor(providerClz, argTypes);
    }
}
