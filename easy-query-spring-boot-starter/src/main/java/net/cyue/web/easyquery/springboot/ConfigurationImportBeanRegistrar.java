package net.cyue.web.easyquery.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;

public class ConfigurationImportBeanRegistrar implements ImportBeanDefinitionRegistrar {

    private final Logger logger = LoggerFactory.getLogger(this.toString());

    public void registerBeanDefinitions(
        AnnotationMetadata importingClassMetadata,
        BeanDefinitionRegistry registry
    ) {
        // 获取@EnableEasyQueryApplication注解的属性
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(EnableEasyQueryApplication.class.getName())
        );

        if (attributes == null) {
            return;
        }
        // 解析配置路径
        String[] propertiesPathArray = attributes.getStringArray("properties");
        this.logger.info("解析到 EasyQueryApplication 配置路径: {}", Arrays.toString(propertiesPathArray));

        // 注册EasyQueryProperties Bean
        BeanDefinition definition = BeanDefinitionBuilder
            .genericBeanDefinition(EasyQueryApplicationPropertiesContainer.class)
            .addConstructorArgValue(propertiesPathArray)
            .getBeanDefinition();

        registry.registerBeanDefinition("easyQueryApplicationPropertiesContainer", definition);
    }
}
