package net.cyue.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 对象转换工具类，提供各种类型之间的安全转换功能
 * 支持基本类型、包装类、字符串、日期等常见类型的相互转换
 */
public final class ObjectConverter {

    // 存储所有注册的转换器 [源类型 -> [目标类型 -> 转换函数]]
    private static final Map<String, Function<Object, ?>> CONVERTERS = new HashMap<>();

    // 静态块初始化默认转换器
    static {
        // 基本类型 <-> 包装类转换
        registerConverter(short.class, Short.class, s -> s);
        registerConverter(int.class, Integer.class, i -> i);
        registerConverter(long.class, Long.class, l -> l);
        registerConverter(float.class, Float.class, f -> f);
        registerConverter(double.class, Double.class, d -> d);
        registerConverter(char.class, Character.class, c -> c);
        registerConverter(boolean.class, Boolean.class, b -> b);

        registerConverter(Short.class, short.class, Short::shortValue);
        registerConverter(Integer.class, int.class, Integer::intValue);
        registerConverter(Long.class, long.class, Long::longValue);
        registerConverter(Float.class, float.class, Float::floatValue);
        registerConverter(Double.class, double.class, Double::doubleValue);
        registerConverter(Character.class, char.class, Character::charValue);
        registerConverter(Boolean.class, boolean.class, Boolean::booleanValue);

        // 字符串转数字
        registerConverter(String.class, BigDecimal.class, BigDecimal::new);
        registerConverter(String.class, BigInteger.class, BigInteger::new);
        registerConverter(String.class, Short.class, Short::parseShort);
        registerConverter(String.class, Integer.class, Integer::parseInt);
        registerConverter(String.class, Long.class, Long::parseLong);
        registerConverter(String.class, Float.class, Float::parseFloat);
        registerConverter(String.class, Double.class, Double::parseDouble);
        registerConverter(String.class, Boolean.class, Boolean::new);
        registerConverter(String.class, short.class, Short::parseShort);
        registerConverter(String.class, int.class, Integer::parseInt);
        registerConverter(String.class, long.class, Long::parseLong);
        registerConverter(String.class, float.class, Float::parseFloat);
        registerConverter(String.class, double.class, Double::parseDouble);
        registerConverter(String.class, boolean.class, Boolean::new);

        // 数字转字符串
        registerConverter(BigDecimal.class, String.class, BigDecimal::toString);
        registerConverter(BigInteger.class, String.class, BigInteger::toString);
        registerConverter(Short.class, String.class, Object::toString);
        registerConverter(Integer.class, String.class, Object::toString);
        registerConverter(Long.class, String.class, Object::toString);
        registerConverter(Float.class, String.class, Object::toString);
        registerConverter(Double.class, String.class, Object::toString);
        registerConverter(Boolean.class, String.class, Object::toString);
        registerConverter(short.class, String.class, Object::toString);
        registerConverter(int.class, String.class, Object::toString);
        registerConverter(long.class, String.class, Object::toString);
        registerConverter(float.class, String.class, Object::toString);
        registerConverter(double.class, String.class, Object::toString);
        registerConverter(boolean.class, String.class, Object::toString);

        // 字符串转日期
        registerConverter(String.class, Date.class, DateUtil::parseDate);
        // 日期转字符串
        registerConverter(Date.class, String.class, date -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));

        // 数字类型互转
        // 浮点到整型
        registerConverter(Float.class, int.class, Float::intValue);
        registerConverter(Double.class, int.class, Double::intValue);
        registerConverter(Float.class, long.class, Float::longValue);
        registerConverter(Double.class, long.class, Double::longValue);

        // 整型到浮点
        registerConverter(Integer.class, float.class, Integer::floatValue);
        registerConverter(Long.class, float.class, Long::floatValue);
        registerConverter(Integer.class, double.class, Integer::doubleValue);
        registerConverter(Long.class, double.class, Long::doubleValue);

        // 同类型转换（避免自动装箱问题）
        registerConverter(Byte.class, byte.class, b -> b);
        registerConverter(Short.class, short.class, s -> s);
        registerConverter(Integer.class, int.class, i -> i);
        registerConverter(Long.class, long.class, l -> l);
        registerConverter(Float.class, float.class, f -> f);
        registerConverter(Double.class, double.class, d -> d);
        registerConverter(Character.class, char.class, c -> c);
        registerConverter(Boolean.class, boolean.class, b -> b);
    }

    /**
     * 自定义转换异常类，当类型转换失败时抛出
     */
    public static class ConversionException extends RuntimeException {

        /**
         * 构造一个新的转换异常
         * @param message 异常信息
         */
        public ConversionException(String message) {
            super(message);
        }

        /**
         * 构造一个新的转换异常
         * @param message 异常信息
         * @param cause 异常原因
         */
        public ConversionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // 私有构造防止实例化
    private ObjectConverter() {}

    /**
     * 生成类型转换键，用于在转换器映射中查找对应的转换函数
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 类型转换键字符串，格式为"源类型名->目标类型名"
     */
    private static String generateKey(Class<?> sourceType, Class<?> targetType) {
        return sourceType.getName() + "->" + targetType.getName();
    }

    /**
     * 注册类型转换器，用于添加自定义的类型转换逻辑
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @param converter 转换函数
     * @param <S> 源类型泛型
     * @param <T> 目标类型泛型
     */
    public static <S, T> void registerConverter(Class<S> sourceType, Class<T> targetType, Function<S, T> converter) {
        String key = generateKey(sourceType, targetType);
        CONVERTERS.put(key, obj -> converter.apply(sourceType.cast(obj)));
    }

    /**
     * 核心转换方法，将源对象转换为目标类型
     * @param source 源对象
     * @param targetType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     * @throws ConversionException 如果无法转换
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object source, Class<T> targetType) {
        if (source == null) {
            return handleNull(targetType);
        }

        // 如果已经是目标类型
        if (targetType.isInstance(source)) {
            return (T) source;
        }

        // 获取转换器
        Class<?> sourceType = source.getClass();
        Function<Object, ?> converter = CONVERTERS.get(generateKey(sourceType, targetType));

        if (converter != null) {
            try {
                return (T) converter.apply(source);
            } catch (ClassCastException e) {
                throw new ConversionException("Type conversion failed", e);
            }
        }

        // 特殊处理：对象转字符串
        if (targetType == String.class) {
            return (T) source.toString();
        }

        // 处理大数字类型
        if (source instanceof BigDecimal) {
            return convertBigDecimal((BigDecimal) source, targetType);
        }

        if (source instanceof BigInteger) {
            return convertBigInteger((BigInteger) source, targetType);
        }

        throw new ConversionException("No converter found from " + sourceType + " to " + targetType);
    }

    /**
     * 将数字类型转换为目标类型
     * @param number 源数字
     * @param targetType 目标类型
     * @return 转换后的对象
     */
    private static Object convertNumber(Number number, Class<?> targetType) {
        String typeName = targetType.getSimpleName().toLowerCase();
        double value = number.doubleValue();
        switch (typeName) {
            case "byte": case "java.lang.byte": return (byte) value;
            case "short": case "java.lang.short": return (short) value;
            case "int": case "integer": return (int) value;
            case "long": case "java.lang.long": return (long) value;
            case "float": case "java.lang.float": return (float) value;
            case "double": case "java.lang.double": return value;
            default: throw new UnsupportedOperationException("Unsupported number type: " + targetType);
        }
    }

    /**
     * 处理null值的转换
     * @param targetType 目标类型
     * @param <T> 目标类型泛型
     * @return null或抛出异常
     * @throws ConversionException 如果目标类型是基本类型
     */
    private static <T> T handleNull(Class<T> targetType) {
        if (targetType.isPrimitive()) {
            throw new ConversionException("Cannot convert null to primitive type: " + targetType);
        }
        return null;
    }

    /**
     * 从字符串转换为目标类型
     * @param source 源字符串
     * @param targetType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     * @throws ConversionException 如果转换失败
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertFromString(String source, Class<T> targetType) {
        if (targetType == String.class) {
            return (T) source;
        }

        String typeName = targetType.getSimpleName().toLowerCase();

        try {
            switch (typeName) {
                case "byte": case "java.lang.byte":
                    return (T) Byte.valueOf(source);
                case "short": case "java.lang.short":
                    return (T) Short.valueOf(source);
                case "int": case "integer":
                    return (T) Integer.valueOf(source);
                case "long": case "java.lang.long":
                    return (T) Long.valueOf(source);
                case "float": case "java.lang.float":
                    return (T) Float.valueOf(source);
                case "double": case "java.lang.double":
                    return (T) Double.valueOf(source);
                case "boolean": case "java.lang.boolean":
                    return (T) Boolean.valueOf(source);
                case "char": case "character":
                    return source.length() > 0 ? (T) Character.valueOf(source.charAt(0)) : null;
                default:
                    throw new ConversionException("Unsupported string conversion to: " + targetType);
            }
        } catch (NumberFormatException e) {
            throw new ConversionException("Conversion error for '" + source + "' to " + targetType, e);
        }
    }

    /**
     * 将BigDecimal转换为目标类型
     * @param source 源BigDecimal
     * @param targetType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertBigDecimal(BigDecimal source, Class<T> targetType) {
        if (targetType == BigInteger.class) {
            return (T) source.toBigInteger();
        }
        return (T) convertNumber(source, targetType);
    }

    /**
     * 将BigInteger转换为目标类型
     * @param source 源BigInteger
     * @param targetType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertBigInteger(BigInteger source, Class<T> targetType) {
        if (targetType == BigDecimal.class) {
            return (T) new BigDecimal(source);
        }
        return (T) convertNumber(source.doubleValue(), targetType);
    }


    // ========== 使用示例 ==========
    /**
     * 示例
     * @param args args
     */
    public static void main(String[] args) {
        // 基本数据类型转换
        int intValue = convert(123.45f, int.class); // 123
        double doubleValue = convert(100, double.class); // 100.0

        // 包装类转换
        Long longObj = convert("12345", Long.class); // 12345L

        // 字符串转换
        String strValue = convert(true, String.class); // "true"

        // 数字类型互转
        float floatValue = convert(9876543210L, float.class);

        // 注册自定义转换器
        registerConverter(String.class, char[].class, String::toCharArray);
        char[] charArray = convert("hello", char[].class);

        System.out.println("Conversion examples:");
        System.out.println("Float to int: " + intValue);
        System.out.println("Int to double: " + doubleValue);
        System.out.println("String to Long: " + longObj);
        System.out.println("Boolean to String: " + strValue);
        System.out.println("Long to float: " + floatValue);
        System.out.println("String to char[]: " + new String(charArray));

        // 基本类型转换
        intValue = ObjectConverter.convert("123", int.class);
        System.out.println("String to int: " + intValue);

        // 数字类型转换
        doubleValue = ObjectConverter.convert(100L, double.class);
        System.out.println("Long to double: " + doubleValue);

        // 大数字处理
        BigDecimal decimal = new BigDecimal("123.456");
        String decimalStr = ObjectConverter.convert(decimal, String.class);
        System.out.println("BigDecimal to String: " + decimalStr);

        // 自定义转换器
        ObjectConverter.registerConverter(String.class, int[].class, s -> {
            String[] parts = s.split(",");
            int[] result = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Integer.parseInt(parts[i].trim());
            }
            return result;
        });

        int[] array = ObjectConverter.convert("1,2,3,4,5", int[].class);
        System.out.println("String to int array: " + java.util.Arrays.toString(array));
    }
}

