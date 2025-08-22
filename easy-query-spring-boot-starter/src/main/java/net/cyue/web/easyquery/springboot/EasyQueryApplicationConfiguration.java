package net.cyue.web.easyquery.springboot;

import jakarta.servlet.ServletContext;
import net.cyue.util.ResourceUtil;
import net.cyue.web.easyquery.core.EasyQueryApplication;
import net.cyue.web.easyquery.core.EasyQueryApplicationFactory;
import net.cyue.web.easyquery.core.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Configuration
@ComponentScan(basePackages = "net.cyue.web.easyquery")
public class EasyQueryApplicationConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private EasyQueryApplication<ServletContext> app;
    private final String[] propertiesPathArray;

    @Autowired
    public EasyQueryApplicationConfiguration(EasyQueryApplicationPropertiesContainer container) {
        this.logger.info("Configuration init...");
        this.propertiesPathArray = container.getPropertiesPathArray();
        this.logger.info("properties: {}", Arrays.toString(this.propertiesPathArray));
    }

    @Bean({"EasyQueryApplication", "easyQueryApplication"})
    public EasyQueryApplication<ServletContext> easyQueryApplication(ServletContext context)
        throws ConfigException, IOException
    {
        this.logger.info("EasyQueryApplication init...");
        this.app = EasyQueryApplicationFactory.create(context);
        for (String path : this.propertiesPathArray) {
            InputStream configInputStream = ResourceUtil.getResourceAsStream(path);
            if (configInputStream == null) {
                this.logger.warn("{} not found", path);
                continue;
            }
            this.logger.info("{} run properties: {}", this.app, path);
            this.app.runByProperties(configInputStream);
            configInputStream.close();
        }
        return this.app;
    }

}
