package net.cyue.web.easyquery.test.springboot;

import jakarta.servlet.ServletContext;
import net.cyue.util.ResourceUtil;
import net.cyue.web.easyquery.core.EasyQueryApplication;
import net.cyue.web.easyquery.core.EasyQueryApplicationFactory;
import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.http.data.PathInfo;
import net.cyue.web.easyquery.springboot.EnableEasyQueryApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@SpringBootApplication
// @ComponentScan(basePackages = "net.cyue.web.easyquery")
@EnableEasyQueryApplication("example.properties")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // @Bean
    public static EasyQueryApplication<ServletContext> easyQueryApplication(ServletContext context)
        throws ConfigException, IOException
    {
        EasyQueryApplication<ServletContext> app = EasyQueryApplicationFactory.create(context);
        app.runByProperties(ResourceUtil.getResourceAsStream("example.properties"));
        app.getContext().registerQuery(
            PathInfo
                .builder()
                .apiPath("/api/v1/userinfo")
                .addQueryParameter("id")
                .addQueryParameter("username")
                .build(),
        "select * from user where id = #{id} or username = #{username}"
        );
        return app;
    }
}
