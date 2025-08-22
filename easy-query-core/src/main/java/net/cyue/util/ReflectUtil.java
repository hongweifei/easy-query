package net.cyue.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * 反射工具类，提供对类、字段、方法等反射操作的便捷方法
 * <p>
 * 该工具类封装了Java反射API的常用操作，包括：
 * - 获取构造函数并创建实例
 * - 调用方法
 * - 访问字段值
 * - 类型匹配检查等
 * </p>
 */
public final class ReflectUtil {
    private static final Logger LOGGER = Logger.getLogger(ReflectUtil.class.getName());

    /**
     * 获取参数类型数组
     * <p>
     * 根据传入的参数对象数组，获取对应的Class类型数组。
     * </p>
     *
     * @param args 参数对象数组
     * @return 参数类型数组，如果参数为null则返回空数组
     */
    public static Class<?>[] getArgTypes(Object... args) {
        if (args == null) {
            return new Class<?>[0];
        }
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        return types;
    }

    /**
     * 获取与给定参数匹配的构造函数
     * <p>
     * 根据类和参数对象获取对应的构造函数，首先尝试精确匹配，
     * 如果失败则查找兼容的构造函数。
     * </p>
     *
     * @param clz  类对象
     * @param args 参数对象数组
     * @return 匹配的构造函数，如果找不到则返回null
     */
    public static Constructor<?> getConstructor(
        Class<?> clz,
        Object... args
    ) {
        return ReflectUtil.getConstructor(clz, ReflectUtil.getArgTypes(args));
    }

    /**
     * 获取与给定参数类型匹配的构造函数
     * <p>
     * 根据类和参数类型获取对应的构造函数，首先尝试精确匹配，
     * 如果失败则查找兼容的构造函数（支持接口实现匹配）。
     * </p>
     *
     * @param <T>      目标类型
     * @param clz      类对象
     * @param argTypes 参数类型数组
     * @return 匹配的构造函数，如果找不到则返回null
     */
    public static <T> Constructor<T> getConstructor(
        Class<T> clz,
        Class<?>... argTypes
    ) {
        try {
            // 首先尝试精确匹配
            Constructor<T> exactConstructor = clz.getDeclaredConstructor(argTypes);
            exactConstructor.setAccessible(true);
            return exactConstructor;
        } catch (NoSuchMethodException e) {
            // 如果精确匹配失败，尝试查找兼容的构造函数
            return findCompatibleConstructor(clz, argTypes);
        }
    }

    /**
     * 查找兼容的构造函数（每个参数都支持接口实现匹配）
     * <p>
     * 在给定类的所有构造函数中查找与指定参数类型兼容的构造函数。
     * </p>
     *
     * @param clz 类对象
     * @param argTypes    参数类型数组
     * @return 兼容的构造函数，如果找不到则返回null
     */
    private static <T> Constructor<T> findCompatibleConstructor(Class<T> clz, Class<?>... argTypes) {
        Constructor<T>[] constructors = (Constructor<T>[]) clz.getDeclaredConstructors();

        // 按参数数量筛选
        List<Constructor<T>> candidateConstructors = new ArrayList<>();
        for (Constructor<T> constructor : constructors) {
            if (constructor.getParameterCount() == argTypes.length) {
                candidateConstructors.add(constructor);
            }
        }

        if (candidateConstructors.isEmpty()) {
            return null;
        }

        // 查找第一个兼容的构造函数
        for (Constructor<T> constructor : candidateConstructors) {
            if (isConstructorCompatible(constructor, argTypes)) {
                constructor.setAccessible(true);
                return constructor;
            }
        }

        return null;
    }

    /**
     * 检查构造函数是否兼容（每个参数都支持接口实现匹配）
     * <p>
     * 检查构造函数的参数类型是否与给定的参数类型兼容，
     * 支持继承关系、接口实现和基本类型与包装类型的匹配。
     * </p>
     *
     * @param constructor 构造函数
     * @param argTypes    参数类型数组
     * @return 如果构造函数兼容则返回true，否则返回false
     */
    private static boolean isConstructorCompatible(Constructor<?> constructor, Class<?>... argTypes) {
        Class<?>[] paramTypes = constructor.getParameterTypes();

        if (paramTypes.length != argTypes.length) {
            return false;
        }

        // 每个参数都必须兼容
        for (int i = 0; i < paramTypes.length; i++) {
            if (!isAssignable(paramTypes[i], argTypes[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查目标类型是否可以接受源类型（支持接口实现匹配）
     * <p>
     * 检查源类型是否可以赋值给目标类型，支持以下几种匹配方式：
     * 1. 精确匹配
     * 2. null值处理
     * 3. 基本类型和包装类型匹配
     * 4. 继承关系和接口实现匹配
     * 5. 数组类型匹配
     * </p>
     *
     * @param targetType  目标类型
     * @param sourceType  源类型
     * @return 如果目标类型可以接受源类型则返回true，否则返回false
     */
    private static boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
        if (targetType == null || sourceType == null) {
            return false;
        }

        // 1. 精确匹配
        if (targetType.equals(sourceType)) {
            return true;
        }

        // 2. null值处理
        if (sourceType == null) {
            return !targetType.isPrimitive();
        }

        // 3. 基本类型和包装类型匹配
        if (isPrimitiveAndWrapper(targetType, sourceType)) {
            return true;
        }

        // 4. 继承关系和接口实现匹配
        // 这是最关键的部分：targetType.isAssignableFrom(sourceType)
        // 如果targetType是接口，sourceType实现了该接口，则返回true
        // 如果targetType是父类，sourceType是子类，则返回true
        if (targetType.isAssignableFrom(sourceType)) {
            return true;
        }

        // 5. 数组类型匹配
        if (targetType.isArray() && sourceType.isArray()) {
            return isAssignable(targetType.getComponentType(), sourceType.getComponentType());
        }

        return false;
    }

    /**
     * 检查基本类型和包装类型是否匹配
     * <p>
     * 检查两个类型是否为基本类型和对应的包装类型关系，
     * 支持双向匹配（基本类型到包装类型，包装类型到基本类型）。
     * </p>
     *
     * @param targetType  目标类型
     * @param sourceType  源类型
     * @return 如果是基本类型和包装类型匹配关系则返回true，否则返回false
     */
    private static boolean isPrimitiveAndWrapper(Class<?> targetType, Class<?> sourceType) {
        // 基本类型到包装类型
        if (targetType.isPrimitive()) {
            return (targetType == int.class && sourceType == Integer.class) ||
                (targetType == long.class && sourceType == Long.class) ||
                (targetType == double.class && sourceType == Double.class) ||
                (targetType == float.class && sourceType == Float.class) ||
                (targetType == boolean.class && sourceType == Boolean.class) ||
                (targetType == byte.class && sourceType == Byte.class) ||
                (targetType == char.class && sourceType == Character.class) ||
                (targetType == short.class && sourceType == Short.class);
        }
        // 包装类型到基本类型
        else if (sourceType.isPrimitive()) {
            return (sourceType == int.class && targetType == Integer.class) ||
                (sourceType == long.class && targetType == Long.class) ||
                (sourceType == double.class && targetType == Double.class) ||
                (sourceType == float.class && targetType == Float.class) ||
                (sourceType == boolean.class && targetType == Boolean.class) ||
                (sourceType == byte.class && targetType == Byte.class) ||
                (sourceType == char.class && targetType == Character.class) ||
                (sourceType == short.class && targetType == Short.class);
        }
        return false;
    }

    /**
     * 获取所有兼容的构造函数
     * <p>
     * 获取给定类中所有与指定参数类型兼容的构造函数。
     * </p>
     *
     * @param providerClz 类对象
     * @param argTypes    参数类型数组
     * @return 兼容的构造函数列表
     */
    public static List<Constructor<?>> getAllCompatibleConstructors(Class<?> providerClz, Class<?>... argTypes) {
        List<Constructor<?>> result = new ArrayList<>();
        Constructor<?>[] constructors = providerClz.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == argTypes.length) {
                if (isConstructorCompatible(constructor, argTypes)) {
                    constructor.setAccessible(true);
                    result.add(constructor);
                }
            }
        }

        return result;
    }

    /**
     * 按匹配质量排序获取构造函数
     * <p>
     * 获取给定类中所有与指定参数类型兼容的构造函数，并按匹配质量排序（预留扩展功能）。
     * </p>
     *
     * @param providerClz 类对象
     * @param argTypes    参数类型数组
     * @return 兼容的构造函数列表
     */
    public static List<Constructor<?>> getCompatibleConstructorsSorted(Class<?> providerClz, Class<?>... argTypes) {
        List<Constructor<?>> compatibleConstructors = getAllCompatibleConstructors(providerClz, argTypes);

        // 可以根据需要添加排序逻辑
        // 比如按精确匹配数量排序

        return compatibleConstructors;
    }

    /**
     * 调用对象的方法
     * <p>
     * 根据方法名和参数调用对象的方法，自动处理访问权限。
     * </p>
     *
     * @param obj        对象实例
     * @param methodName 方法名
     * @param args       方法参数
     * @return 方法调用结果
     * @throws NoSuchMethodException     当找不到指定方法时抛出
     * @throws InvocationTargetException 当方法调用过程中发生异常时抛出
     * @throws IllegalAccessException    当无法访问方法时抛出
     */
    public static Object invokeMethod(Object obj, String methodName, Object... args)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        final Class<?> clz = obj.getClass();
        final Class<?>[] argTypes = ReflectUtil.getArgTypes(args);
        final Method method = clz.getDeclaredMethod(methodName, argTypes);
        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    /**
     * 获取方法的返回类型
     * <p>
     * 根据类、方法名和参数类型获取方法的返回类型，支持递归查找父类中的方法。
     * </p>
     *
     * @param recursive  是否递归查找父类
     * @param clz        类对象
     * @param methodName 方法名
     * @param argTypes   参数类型数组
     * @return 方法的返回类型
     * @throws NoSuchMethodException 当找不到指定方法时抛出
     */
    public static Class<?> getMethodReturnType(
        boolean recursive,
        Class<?> clz,
        String methodName,
        Class<?>... argTypes
    ) throws NoSuchMethodException
    {
        if (argTypes == null) {
            argTypes = new Class<?>[0];
        }

        Class<?>[] finalArgTypes = argTypes;
        Consumer<Void> logConsumer = (v) -> {
            // 输出详细的错误信息
            LOGGER.warning("方法 " + methodName + " 未找到!");
            LOGGER.warning("搜索参数类型: " + Arrays.toString(finalArgTypes));

            // 尝试查找名称匹配的方法，无论参数如何
            LOGGER.info("类中名称为 " + methodName + " 的所有方法:");
            boolean foundSimilarMethod = false;
            for (Method method : clz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    LOGGER.info("  找到类似方法: " + method);
                    foundSimilarMethod = true;
                }
            }

            if (!foundSimilarMethod) {
                LOGGER.warning("类中没有任何名为 " + methodName + " 的方法!");
            }
        };

        if (recursive) {
            Class<?> superClz = clz;
            Method method = null;
            while (!superClz.equals(Object.class)) {
                try {
                    method = superClz.getDeclaredMethod(methodName, argTypes);
                    LOGGER.info("找到方法: " + method);
                    method.setAccessible(true);
                    return method.getReturnType();
                } catch (NoSuchMethodException e) {
                    superClz = superClz.getSuperclass();
                }
            }
            if (method == null) {
                logConsumer.accept(null);
                throw new NoSuchMethodException("No such method: " + methodName);
            }
        }
        // no recursive
        try {
            // 尝试获取指定方法
            Method m = clz.getDeclaredMethod(methodName, argTypes);
            LOGGER.info("找到方法: " + m);
            m.setAccessible(true);
            return m.getReturnType();
        } catch (NoSuchMethodException e) {
            logConsumer.accept(null);
            throw e; // 重新抛出异常，保持原有行为
        }
    }

    /**
     * 加载类
     * <p>
     * 使用当前线程的上下文类加载器加载指定名称的类。
     * </p>
     *
     * @param className 类名
     * @return 加载的类对象
     * @throws ClassNotFoundException 当找不到指定类时抛出
     */
    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            String message = "Class " + className + " not found";
            LOGGER.warning(message);
            throw new ClassNotFoundException(message);
        }
    }

    /**
     * 创建类实例
     * <p>
     * 根据类对象和构造参数创建类的新实例，自动处理访问权限和异常转换。
     * </p>
     *
     * @param clz  类对象
     * @param args 构造函数参数
     * @param <T>  实例类型
     * @return 创建的实例
     * @throws NoSuchMethodException 当找不到匹配的构造函数时抛出
     */
    public static <T> T createInstance(Class<T> clz, Object... args)
        throws NoSuchMethodException
    {
        try {
            final Class<?>[] argTypes = ReflectUtil.getArgTypes(args);
            Constructor<T> constructor = ReflectUtil.getConstructor(clz, argTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (
            InvocationTargetException |
            InstantiationException |
            IllegalAccessException e
        ) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置字段值（通过setter方法或直接设置）
     * <p>
     * 尝试通过setter方法设置字段值，如果失败则直接设置字段值，自动处理访问权限。
     * </p>
     *
     * @param obj   对象实例
     * @param field 字段对象
     * @param value 要设置的值
     * @throws InvocationTargetException 当方法调用过程中发生异常时抛出
     */
    public static void setFieldValue(Object obj, Field field, Object value) throws InvocationTargetException {
        if (obj == null) {
            throw new NullPointerException("obj is null");
        }
        if (field == null) {
            throw new NullPointerException("field is null");
        }
        field.setAccessible(true);
        String fieldName = field.getName();
        String setMethodName =
            "set" +
            fieldName.substring(0, 1).toUpperCase() +
            fieldName.substring(1);
        try {
            ReflectUtil.invokeMethod(obj, setMethodName, value);
        } catch (IllegalAccessException e) {
            LOGGER.warning("无法访问方法: " + setMethodName);
        } catch (NoSuchMethodException e) {
            try {
                field.set(obj, value);
            } catch (IllegalAccessException ex) {
                LOGGER.warning("无法设置字段: " + field);
            }
        }
    }

    /**
     * 设置字段值
     * <p>
     * 根据字段名设置对象的字段值，自动处理访问权限和异常转换。
     * </p>
     *
     * @param obj       对象实例
     * @param fieldName 字段名
     * @param value     要设置的值
     * @throws NoSuchFieldException      当找不到指定字段时抛出
     * @throws InvocationTargetException 当方法调用过程中发生异常时抛出
     */
    public static void setFieldValue(Object obj, String fieldName, Object value)
        throws NoSuchFieldException, InvocationTargetException
    {
        Field field = obj.getClass().getField(fieldName);
        ReflectUtil.setFieldValue(obj, field, value);
    }


    /**
     * 根据字段类型获取字段
     * <p>
     * 在给定类中查找指定类型的字段，如果找不到则抛出异常。
     * </p>
     *
     * @param clz       类对象
     * @param fieldType 字段类型
     * @return 匹配的字段对象
     * @throws NoSuchFieldException 当找不到指定类型的字段时抛出
     */
    public static Field getField(Class<?> clz, Class<?> fieldType) throws NoSuchFieldException {
        Field field = Arrays.stream(clz.getDeclaredFields())
                .filter(f -> f.getType().equals(fieldType))
                .findFirst()
                .orElse(null);
        if (field == null) {
            throw new NoSuchFieldException("No field of type " + fieldType + " found in class " + clz);
        }
        field.setAccessible(true);
        return field;
    }

    /**
     * 根据字段名获取字段
     * <p>
     * 在给定类中查找指定名称的字段，自动处理访问权限。
     * </p>
     *
     * @param clz       类对象
     * @param fieldName 字段名
     * @return 匹配的字段对象
     * @throws NoSuchFieldException 当找不到指定名称的字段时抛出
     */
    public static Field getField(Class<?> clz, String fieldName) throws NoSuchFieldException {
        Field field = clz.getField(fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * 通过getter方法获取字段值
     * <p>
     * 根据字段名构造getter方法名并调用该方法获取字段值。
     * </p>
     *
     * @param obj       对象实例
     * @param fieldName 字段名
     * @return 字段值
     * @throws InvocationTargetException 当方法调用过程中发生异常时抛出
     * @throws NoSuchMethodException     当找不到指定方法时抛出
     * @throws IllegalAccessException    当无法访问方法时抛出
     */
    public static Object getFieldValueByMethod(Object obj, String fieldName)
        throws InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
        if (obj == null) {
            throw new NullPointerException("obj is null");
        }
        String getMethodName =
                "get" +
                fieldName.substring(0, 1).toUpperCase() +
                fieldName.substring(1);
        return ReflectUtil.invokeMethod(obj, getMethodName);
    }

    /**
     * 通过字段直接获取字段值
     * <p>
     * 直接访问字段获取字段值，自动处理访问权限。
     * </p>
     *
     * @param obj   对象实例
     * @param field 字段对象
     * @return 字段值
     * @throws IllegalAccessException 当无法访问字段时抛出
     */
    public static Object getFieldValueByField(Object obj, Field field)
        throws IllegalAccessException
    {
        if (obj == null) {
            throw new NullPointerException("obj is null");
        }
        if (field == null) {
            throw new NullPointerException("field is null");
        }
        field.setAccessible(true);
        return field.get(obj);
    }

    /**
     * 获取字段值
     * <p>
     * 首先尝试通过getter方法获取字段值，如果失败则直接获取字段值，自动处理访问权限和异常转换。
     * </p>
     *
     * @param obj       对象实例
     * @param fieldName 字段名
     * @return 字段值
     * @throws NoSuchFieldException      当找不到指定字段时抛出
     * @throws InvocationTargetException 当方法调用过程中发生异常时抛出
     * @throws NoSuchMethodException     当找不到指定方法时抛出
     * @throws IllegalAccessException    当无法访问字段或方法时抛出
     */
    public static Object getFieldValue(Object obj, String fieldName)
        throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
        Field field = obj.getClass().getField(fieldName);
        Object value = ReflectUtil.getFieldValueByMethod(obj, fieldName);
        if (value == null) {
            value = ReflectUtil.getFieldValueByField(obj, field);
        }
        return value;
    }
}

