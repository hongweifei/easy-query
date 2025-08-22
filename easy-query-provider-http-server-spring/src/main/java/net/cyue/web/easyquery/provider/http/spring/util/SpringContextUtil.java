package net.cyue.web.easyquery.provider.http.spring.util;

import jakarta.servlet.ServletContext;
import net.cyue.web.easyquery.provider.http.spring.DefaultSpringConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 判断Spring环境
     */
    public static boolean hasApplicationContext() {
        return applicationContext != null;
    }

    /**
     * 安全获取Bean
     */
    public static <T> T getBeanSafely(Class<T> clazz) {
        if (hasApplicationContext()) {
            try {
                return applicationContext.getBean(clazz);
            } catch (Exception e) {
                // Spring容器中不存在该Bean
                return null;
            }
        }
        return null;
    }

    // 从ServletContext获取WebApplicationContext
    public static ConfigurableWebApplicationContext getWebApplicationContext(ServletContext servletContext) {
        return (ConfigurableWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }

    public static ApplicationContext createXMLApplicationContext(String classpath) {
        return new ClassPathXmlApplicationContext("spring.xml");
    }

    public static ApplicationContext createConfigApplicationContext() {
        return createConfigApplicationContext(DefaultSpringConfig.class);
    }
    public static ApplicationContext createConfigApplicationContext(Class<?>... componentClasses) {
        return new AnnotationConfigApplicationContext(componentClasses);
    }

    public static AnnotationConfigWebApplicationContext createConfigWebApplicationContext() {
        return createConfigWebApplicationContext(DefaultSpringConfig.class);
    }
    public static AnnotationConfigWebApplicationContext createConfigWebApplicationContext(Class<?>... componentClasses) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(componentClasses);
        return context;
    }

}
