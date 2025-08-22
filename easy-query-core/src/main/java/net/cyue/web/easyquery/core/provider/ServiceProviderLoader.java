package net.cyue.web.easyquery.core.provider;

import net.cyue.util.ReflectUtil;
import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.provider.api.IServiceProvider;
import net.cyue.web.easyquery.core.service.api.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 服务提供者加载器
 */
public class ServiceProviderLoader extends ProviderLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProviderLoader.class);
    private static final String PROVIDER_GET_PROTOTYPE_METHOD_NAME = "getPrototype";

    private ServiceProviderLoader() {
        super();
    }

    /**
     * 判断 provider 是否为 ServiceProvider
     * @param clz provider 的 Class
     * @return true if clz is a ServiceProvider, false otherwise
     */
    private static boolean isServiceProvider(Class<?> clz) {
        return IServiceProvider.class.isAssignableFrom(clz);
    }

    /**
     * 判断 provider 是否提供 prototype，即判断 provider 是否为 prototype 的 provider
     * @param prototypeClz prototype 的 Class
     * @param providerClz provider 的 Class
     * @return true if provider provide prototype, false otherwise
     * @param <TPrototype> prototype 的类型
     */
    private static <TPrototype> boolean ifProvidePrototype(
        Class<TPrototype> prototypeClz,
        Class<?> providerClz
    ) {
        if (!isServiceProvider(providerClz)) {
            return false;
        }
        Constructor<?> constructor = ServiceProviderLoader.getProviderConstructor(providerClz, prototypeClz);
        return constructor != null;
    }

    /**
     * 注册服务提供者
     * @param <TPrototype> 原型类型
     * @param prototypeClz 原型类
     * @param providerClz 提供者类
     * @throws IllegalArgumentException 当提供者不是服务提供者或不提供原型类时抛出
     */
    public static <TPrototype> void registerProvider(
        Class<TPrototype> prototypeClz,
        Class<? extends IServiceProvider<TPrototype, ? extends IService<TPrototype>>> providerClz
    ) throws IllegalArgumentException {
        if (!isServiceProvider(providerClz)) {
            String notProviderMessage = "Class " + providerClz.getName() + " is not a service provider";
            LOGGER.warn(notProviderMessage);
            throw new IllegalArgumentException(notProviderMessage);
        }
        if (!ifProvidePrototype(prototypeClz, providerClz)) {
            String notProviderMessage = "Class " + providerClz.getName() + " is not a service provider for " + prototypeClz.getName();
            LOGGER.warn(notProviderMessage);
            throw new IllegalArgumentException(notProviderMessage);
        }
        getPrototypeProviderMap().put(prototypeClz.getName(), providerClz.getName());
    }



    /**
     * 通过 Instance 加载其对应的 ServiceProvider
     * @param <TPrototype> the type of the instance
     * @param instance the instance for service
     * @param args 构造参数实例对象
     * @return 返回服务实现类
     * @throws IOException 当无法加载配置文件时抛出
     * @throws ConfigException 当无法加载配置文件时抛出
     */
    public static
    <TPrototype> IServiceProvider<TPrototype, ? extends IService<TPrototype>>
    load(
        TPrototype instance,
        Object... args
    ) throws IOException, ConfigException
    {
        Class<TPrototype> prototypeClz = (Class<TPrototype>) instance.getClass();
        // IServiceProvider<TPrototype, TService extends IService>
        String serviceProviderClassName = ServiceProviderLoader.getProviderClassName(prototypeClz);
        // 服务提供者的Class
        Class<?> serviceProviderClz;
        try {
            serviceProviderClz = ReflectUtil.loadClass(serviceProviderClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.warn(e.getMessage());
            throw new ConfigException(e.getMessage());
        }

        // 判断 是否为 ServiceProvider
        if (!isServiceProvider(serviceProviderClz)) {
            String message = "Class " + serviceProviderClassName + " is not a service provider";
            LOGGER.warn(message);
            throw new ConfigException(message);
        }
        // prototype 需为 providedClass（需要的被提供的类） 的 父类或同类，providedClass 的实例将会用于 ServiceProvider 的实例化
        String notProviderMessage = "Class " + serviceProviderClassName + " is not a service provider for " + prototypeClz.getName();
//        try {
//            // 其实泛型擦除后都是 Object
//            Class<?> returnPrototypeClz = ReflectUtil.getMethodReturnType(true, serviceProviderClz, PROVIDER_GET_PROTOTYPE_METHOD_NAME);
//            if (!prototypeClz.isAssignableFrom(returnPrototypeClz)) {
//                LOGGER.warning(notProviderMessage);
//                LOGGER.info("\nprototype：" + prototypeClz.getName() + "\nprovide：" + returnPrototypeClz.getName());
//                throw new ConfigException(notProviderMessage);
//            }
//        } catch (NoSuchMethodException e) {
//            LOGGER.warning(notProviderMessage);
//            throw new ConfigException(notProviderMessage);
//        }

        // 判断 provider 是否提供 prototype，即判断 provider 是否为 prototype 的 provider
        if (!ifProvidePrototype(prototypeClz, serviceProviderClz)) {
            LOGGER.warn(notProviderMessage);
            throw new ConfigException(notProviderMessage);
        }
        try {
            List<Object> constructorArgList = new ArrayList<>();
            constructorArgList.add(instance);
            constructorArgList.addAll(List.of(args));
            Object[] constructorArgs = constructorArgList.toArray(new Object[0]);
            Class<?>[] argTypes = ReflectUtil.getArgTypes(constructorArgs);
            Constructor<?> constructor = ServiceProviderLoader.getProviderConstructor(serviceProviderClz, argTypes);
            if (constructor == null) {
                String errorMessage = "Cannot find constructor for " + serviceProviderClassName + " with args " + Arrays.toString(argTypes);
                LOGGER.warn(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            return (IServiceProvider<TPrototype, ? extends IService<TPrototype>>) constructor.newInstance(instance);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
            throw new ConfigException(e.getMessage());
        }
    }
}
