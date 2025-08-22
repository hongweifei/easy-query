# EasyQuery

for easier http query

用于快速创建查询接口，只需编写API接口路径与对应查询SQL。



使用`Provider`来支持多样的框架，现在已经有：

- `provider-db-jdbc`
- `provider-db-mybatis`
- `provider-db-mybatis-spring`
- `provider-http-router-servlet`
- `provider-http-router-spring`
- `provider-http-server-servlet`
- `provider-http-server-spring`

在依赖中更换不同的`Provider`，程序将自动加载。



并且现在已经开发了`easy-query-spring-boot-starter`，只需在`SprintBoot`项目中引入依赖：

```xml
<dependency>
    <groupId>net.cyue.web.easyquery</groupId>
    <artifactId>easy-query-spring-boot-starter</artifactId>
    <version>0.1.3</version>
</dependency>
```

并引入需要使用的`provider-db`，启用`EasyQueryApplication`即可使用。示例代码：

```java
@SpringBootApplication
@EnableEasyQueryApplication("example.properties")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

或不使用注解：

```java
@Bean
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
```





`EasyQueryApplication`配置文件实例：

```properties
application.context.path=/
application.context.handler.exception=自定义的异常处理器（IWebExceptionHandler）实现类类名
application.context.handler.result=自定义的查询结果处理器（IWebResultHandler）实现类类名
application.context.handler.response=自定义的响应处理器（IWebResponseHandler）实现类类名
api.v1.user.list=select * from user
api.v1.user.{username}=select * from user where username = #{username}
```

配置文件使用`getResourceAsStream`进行加载，请将配置文件放在resources文件夹。



`provider-db-jdbc`与`provider-db-mybatis`配置文件实例：

```properties
url=jdbc:mysql://localhost:3306/jdbc_test?useUnicode=true&characterEncoding=utf8
user=root
password=123456
```



在`Spring`项目中使用`provider-db-mybatis-spring`：

```xml
<bean
    id="dataSource"
    class="org.springframework.jdbc.datasource.DriverManagerDataSource"
>
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/jdbc_test?useUnicode=true&amp;characterEncoding=utf8"/>
    <property name="username" value="root"/>
    <property name="password" value="123456"/>
</bean>

<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource" />
    <property name="mapperLocations" value="classpath:mapper/**/*.xml"/>
</bean>
```

或使用`mybatis.properties`。



在`Spring-Boot`项目中使用`provider-db-mybatis-spring`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jdbc_test?useUnicode=true&characterEncoding=utf8
    username: root
    password: 123456

mybatis:
  mapper-locations: classpath:mapper/**/*.xml
```

或使用`mybatis.properties`。
