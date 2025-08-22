package net.cyue.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 资源工具类，提供获取类路径下资源文件的便捷方法
 * 支持从类加载器或指定类路径获取资源文件流、URL和文件路径
 */
public class ResourceUtil {

    private ResourceUtil() {}

    /**
     * 获取资源文件流，从类路径中获取
     * @param resourceName 资源文件名
     * @return 输入流
     */
    public static InputStream getResourceAsStream(String resourceName) {
        return ResourceUtil.class.getClassLoader().getResourceAsStream(resourceName);
    }

    /**
     * 获取资源文件流，从提供的类的路径中获取
     * @param clazz  类
     * @param resourceName 资源文件名
     * @return 输入流
     */
    public static InputStream getResourceAsStream(Class<?> clazz, String resourceName) {
        return clazz.getResourceAsStream(resourceName);
    }

    /**
     * 获取资源文件的URL
     * @param name 资源文件名
     * @return 资源文件的URL，如果未找到返回null
     */
    public static URL getResourceURL(String name) {
        return ResourceUtil.class.getClassLoader().getResource(name);
    }

    /**
     * 获取资源文件的物理路径
     * @param name 资源文件名
     * @return 资源文件的物理路径
     * @throws FileNotFoundException 当资源文件未找到时抛出
     */
    public static String getResourcePath(String name)
        throws FileNotFoundException
    {
        URL url = ResourceUtil.getResourceURL(name);
        if (url == null) {
            throw new FileNotFoundException("Resource not found");
        }
        String protocol = url.getProtocol();
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if ("file".equals(protocol)) {
            return uri.getPath();
        } else if ("jar".equals(protocol)) {
            String path = uri.getSchemeSpecificPart();
            int separator = path.indexOf("!");
            return path.substring(0, separator);
        } else {
            throw new UnsupportedOperationException("Unsupported protocol: " + protocol);
        }
    }
}
