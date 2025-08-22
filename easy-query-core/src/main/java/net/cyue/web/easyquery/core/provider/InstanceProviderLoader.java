package net.cyue.web.easyquery.core.provider;

import net.cyue.util.ReflectUtil;
import net.cyue.web.easyquery.core.config.ConfigException;
import net.cyue.web.easyquery.core.provider.api.IInstanceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * 实例提供者加载器
 */
public class InstanceProviderLoader extends ProviderLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceProviderLoader.class);
    private static final String PROVIDER_GET_INSTANCE_METHOD_NAME = "getInstance";

    private InstanceProviderLoader() {}


    /**
     * 注册提供者
     * @param prototypeClz 原型（实例）类
     * @param providerClz 提供者类
     * @param <T> 原型（实例）类型
     * @throws IllegalArgumentException 当原型（类型）不为提供者类型父类型时抛出
     */
    public static <T> void registerProvider(
        Class<T> prototypeClz,
        Class<? extends T> providerClz
    ) throws IllegalArgumentException {
        if (!prototypeClz.isAssignableFrom(providerClz)) {
            String notProviderMessage = "Class " + providerClz.getName() + " is not a provider for " + prototypeClz.getName();
            LOGGER.warn(notProviderMessage);
            throw new IllegalArgumentException(notProviderMessage);
        }
        getPrototypeProviderMap().put(prototypeClz.getName(), providerClz.getName());
    }

    /**
     * Load provider for provided class
     * @param <T> 实例类型
     * @param prototypeClz the class to be provided
     * @return 返回实现类
     * @throws IOException IO异常
     * @throws ConfigException 配置异常
     */
    public static <T> IInstanceProvider<T> load(
        Class<T> prototypeClz
    ) throws ConfigException, IOException {
        return load(prototypeClz, (Object[]) null);
    }

    /**
     * Load provider for provided class
     * @param <T> 实例类型
     * @param prototypeClz the class to be provided
     * @param args 构造参数
     * @return 返回实现类
     * @throws IOException IO异常
     * @throws ConfigException 配置异常
     */
    public static <T> IInstanceProvider<T> load(
        Class<T> prototypeClz,
        Object... args
    ) throws IOException, ConfigException
    {
        String instanceProviderClassName = InstanceProviderLoader.getProviderClassName(prototypeClz);
        // IInstanceProvider 的实现类 或 providedClass 的子类
        Class<?> providerClz;
        try {
            providerClz = ReflectUtil.loadClass(instanceProviderClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.warn(e.getMessage());
            throw new ConfigException(e.getMessage());
        }

        // InstanceProvider
        String notProviderMessage = "Class " + instanceProviderClassName + " is not a provider for " + prototypeClz.getName();
        String noConstructorMessage =  "Provider " + providerClz.getName() + " has no constructor";
        if (IInstanceProvider.class.isAssignableFrom(providerClz)) {
            IInstanceProvider<?> provider;
            try {
                Constructor<?> constructor = InstanceProviderLoader.getProviderConstructor(providerClz, args);
                if (constructor == null) {
                    LOGGER.warn(noConstructorMessage);
                    throw new ConfigException(noConstructorMessage);
                }
                provider = (IInstanceProvider<?>) constructor.newInstance(args);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                LOGGER.error(e.getMessage());
                throw new ConfigException(e.getMessage());
            }

            // 判断 提供的实例类型
            // 其实泛型擦除后都是 Object
            // Class<?> returnPrototypeClz = ReflectUtil.getMethodReturnType(true, providerClz, PROVIDER_GET_INSTANCE_METHOD_NAME);
            Class<?> returnPrototypeClz = provider.getInstance().getClass();
            LOGGER.info(
            "\nprototype：" +
                prototypeClz.getName() +
                providerClz.getName() +
                " provide " +
                returnPrototypeClz.getName()
            );
            // 与 prototype 不同类
            if (!prototypeClz.isAssignableFrom(returnPrototypeClz)) {
                LOGGER.warn(notProviderMessage);
                throw new ConfigException(notProviderMessage);
            }
            return (IInstanceProvider<T>) provider;
        }

        // 非 InstanceProvider
        // provider，providedClass（需要的被提供的类） 需为 providerClz 的 父类或同类，providerClz 的实例即为 provider
        Class<?>[] implClzArray = providerClz.getInterfaces();
        LOGGER.info(
            "\nprototype: {}\nprovide: {} impl {}",
            prototypeClz.getName(),
            providerClz.getName(),
            Arrays.toString(implClzArray)
        );
        if (!prototypeClz.isAssignableFrom(providerClz)) {
            LOGGER.warn(notProviderMessage);
            throw new ConfigException(notProviderMessage);
        }
        try {
            if (args == null || args.length == 0) {
                T loadedInstance = (T) ReflectUtil.createInstance(providerClz);
                return SimpleInstanceProviderFactory.getInstanceProvider(loadedInstance);
            }

            Constructor<?> constructor = InstanceProviderLoader.getProviderConstructor(providerClz, args);
            if (constructor == null) {
                LOGGER.warn(noConstructorMessage);
                throw new ConfigException(noConstructorMessage);
            }
            T loadedInstance = (T) constructor.newInstance(args);
            return SimpleInstanceProviderFactory.getInstanceProvider(loadedInstance);
        } catch (
            NoSuchMethodException |
            InvocationTargetException |
            InstantiationException |
            IllegalAccessException e
        ) {
            LOGGER.error(noConstructorMessage);
            throw new RuntimeException(e);
        }

    }

}
