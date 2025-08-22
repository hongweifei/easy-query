package net.cyue.web.easyquery.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;


public class ConfigurationImportSelector implements ImportSelector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        this.logger.info("EasyQueryApplicationConfiguration");
        return new String[]{EasyQueryApplicationConfiguration.class.getName()};
    }

}
