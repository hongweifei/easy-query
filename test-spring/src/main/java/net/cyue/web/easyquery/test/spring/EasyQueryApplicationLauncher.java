package net.cyue.web.easyquery.test.spring;

import jakarta.servlet.ServletContext;
import net.cyue.util.ResourceUtil;
import net.cyue.web.easyquery.core.EasyQueryApplication;
import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.http.data.PathInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EasyQueryApplicationLauncher {

    private final Logger logger = LoggerFactory.getLogger(this.toString());

    @Autowired
    private EasyQueryApplication<ServletContext> app;

    @EventListener(ContextRefreshedEvent.class)
    public void start() throws ConfigException, IOException {
        this.logger.info("运行EasyQueryApplication");
        if (this.app == null) {
            this.logger.warn("未找到EasyQueryApplication实例");
            return;
        }
        this.app.getContext().registerQuery(
            PathInfo
                .builder()
                .apiPath("/api/v1/userinfo")
                .addQueryParameter("id")
                .addQueryParameter("username")
                .build(),
            "select * from user where id = #{id} or username = #{username}"
        );
        this.app.runByProperties(ResourceUtil.getResourceAsStream("example.properties"));
    }
}
