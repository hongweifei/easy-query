package net.cyue.web.easyquery.springboot;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
    ConfigurationImportBeanRegistrar.class,
    ConfigurationImportSelector.class
})  // 导入自动配置类
public @interface EnableEasyQueryApplication {
    @AliasFor("properties")
    String[] value() default {};
    @AliasFor("value")
    String[] properties() default {};
}
