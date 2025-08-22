package net.cyue.web.easyquery.test.spring;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Tomcat TOMCAT = new Tomcat();

    public static void main(String[] args)
        throws ClassNotFoundException
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        TOMCAT.enableNaming();

        String webappPath = new File("test-spring/src/main/webapp").getAbsolutePath();
        TOMCAT.addWebapp("", webappPath);
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

