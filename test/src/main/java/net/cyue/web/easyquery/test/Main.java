package net.cyue.web.easyquery.test;

import jakarta.servlet.ServletContext;
import net.cyue.util.ResourceUtil;
import net.cyue.web.easyquery.core.EasyQueryApplication;
import net.cyue.web.easyquery.core.EasyQueryApplicationFactory;
import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.http.data.PathInfo;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Tomcat TOMCAT = new Tomcat();

    public static void main(String[] args)
        throws ClassNotFoundException, ConfigException, IOException
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        TOMCAT.enableNaming();
        String webappPath = new File(".").getAbsolutePath();
        Context context = TOMCAT.addWebapp("", webappPath);
        ServletContext servletContext = context.getServletContext();
        context.setRequestCharacterEncoding("UTF-8");
        context.setResponseCharacterEncoding("UTF-8");
        EasyQueryApplication<ServletContext> app = EasyQueryApplicationFactory.create(servletContext);
        app.getContext().registerQuery(
            PathInfo
                .builder()
                .apiPath("/api/v1/userinfo")
                .addQueryParameter("id")
                .addQueryParameter("username")
                .build(),
            "select * from user where id = #{id} or username = #{username}"
        );
        InputStream is = ResourceUtil.getResourceAsStream("example.properties");
        app.runByProperties(is);
        Main.listen(8080);
    }

    public static void close() {
        try {
            TOMCAT.stop();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }


    public static void listen(int port) {
        LOGGER.info("listen {}", port);
        LOGGER.debug("listen {}", port);
        try {
            Connector connector = new Connector();
            connector.setPort(port);
            connector.setMaxPostSize(0);
            TOMCAT.setConnector(connector);

            TOMCAT.start();
            TOMCAT.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    public static void listen(String port) {
        Main.listen(Integer.parseInt(port));
    }
}


