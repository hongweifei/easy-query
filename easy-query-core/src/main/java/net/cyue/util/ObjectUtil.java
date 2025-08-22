package net.cyue.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 对象工具类，提供对象类型判断和对象内容打印功能
 */
public class ObjectUtil {

    private ObjectUtil() {
        // Prevent instantiation
    }

    /**
     * 判断对象是否为基本数据类型或其包装类
     *
     * @param obj 待判断的对象
     * @return 如果是基本数据类型或其包装类返回true，否则返回false
     */
    public static boolean isPrimitive(Object obj) {
        if (obj == null) return false;
        if (
                obj instanceof Integer ||
                        obj instanceof Boolean ||
                        obj instanceof Character ||
                        obj instanceof Byte ||
                        obj instanceof Short ||
                        obj instanceof Long ||
                        obj instanceof Float ||
                        obj instanceof Double
        ) {
            return true;
        }
        return obj.getClass().isPrimitive();
    }

    /**
     * 判断对象是否为字符串类型
     *
     * @param obj 待判断的对象
     * @return 如果是字符串类型返回true，否则返回false
     */
    public static boolean isString(Object obj) {
        return obj instanceof String;
    }

    /**
     * 判断对象是否为数组类型
     *
     * @param obj 待判断的对象
     * @return 如果是数组类型返回true，否则返回false
     */
    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    /**
     * 判断对象是否为Set类型
     *
     * @param obj 待判断的对象
     * @return 如果是Set类型返回true，否则返回false
     */
    public static boolean isSet(Object obj) {
        return obj instanceof Set;
    }

    /**
     * 判断对象是否为Map类型
     *
     * @param obj 待判断的对象
     * @return 如果是Map类型返回true，否则返回false
     */
    public static boolean isMap(Object obj) {
        return obj instanceof Map;
    }

    /**
     * 打印带制表符缩进的文本
     *
     * @param text 要打印的文本
     * @param tN 制表符数量
     */
    private static void printTextWithTab(String text, int tN) {
        if (tN <= 0) {
            tN = 1;
        }
        if (tN == 1) {
            System.out.print(text);
        } else {
            System.out.print(StringUtil.repeatString("\t", tN) + text);
        }
    }

    /**
     * 打印带制表符缩进的文本并换行
     *
     * @param text 要打印的文本
     * @param tN 制表符数量
     */
    private static void printlnTextWithTab(String text, int tN) {
        ObjectUtil.printTextWithTab(text, tN - 1);
        System.out.println();
    }


    /**
     * 打印字符串，用双引号包围
     *
     * @param str 要打印的字符串
     * @param tN 制表符数量
     */
    public static void printString(String str, int tN) {
        if (tN <= 0) {
            tN = 1;
        }
        ObjectUtil.printTextWithTab("\"" + str + "\"", tN - 1);
    }

    /**
     * 打印对象数组内容
     *
     * @param array 要打印的对象数组
     * @param tN 制表符数量
     */
    public static void printArray(Object[] array, int tN) {
        if (tN <= 0) {
            tN = 1;
        }
        if (array == null) {
            System.out.print("null");
            return;
        }
        if (array.length == 0) {
            System.out.print("[]");
            return;
        }

        String tString = StringUtil.repeatString("\t", tN);
        System.out.println("[");
        for (Object v : array) {
            System.out.print(tString);
            ObjectUtil.printObject(v, false, tN + 1);
            System.out.println(",");
        }
        ObjectUtil.printTextWithTab("]", tN - 1);
    }

    /**
     * 打印整型数组内容
     *
     * @param array 要打印的整型数组
     * @param tN 制表符数量
     */
    public static void printArray(int[] array, int tN) {
        ObjectUtil.printArray(
            Arrays
            .stream(array)
            .boxed()
            .toArray(),
            tN
        );
    }

    /**
     * 打印长整型数组内容
     *
     * @param array 要打印的长整型数组
     * @param tN 制表符数量
     */
    public static void printArray(long[] array, int tN) {
        ObjectUtil.printArray(
            Arrays
            .stream(array)
            .boxed()
            .toArray(),
            tN
        );
    }

    /**
     * 打印Set集合内容
     *
     * @param set 要打印的Set集合
     * @param tN 制表符数量
     */
    public static void printSet(Set<?> set, int tN) {
        if (tN <= 0) {
            tN = 1;
        }
        if (set == null) {
            System.out.print("null");
            return;
        }

        String tString = StringUtil.repeatString("\t", tN);
        System.out.println("{");
        for (Object v : set) {
            System.out.print(tString);
            ObjectUtil.printObject(v, false, tN + 1);
            System.out.println(",");
        }

        ObjectUtil.printTextWithTab("}", tN - 1);
    }

    /**
     * 打印Map映射内容
     *
     * @param map 要打印的Map映射
     * @param tN 制表符数量
     */
    public static void printMap(Map<?, ?> map, int tN) {
        if (tN <= 0) {
            tN = 1;
        }
        if (map == null) {
            System.out.print("null");
            return;
        }

        String tString = StringUtil.repeatString("\t", tN);
        // Field[] fields = map.getClass().getDeclaredFields();

        System.out.println("{");
//        for (Field f : fields) {
//            f.setAccessible(true);
//            try {
//                System.out.println(
//                    tString +
//                    f.getName() +
//                    ": " +
//                    f.get(map) +
//                    ","
//                );
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String keyString = entry.getKey() != null ? entry.getKey().toString() : "null";
            Object value = entry.getValue();
            System.out.print(tString + keyString + ": ");
            ObjectUtil.printObject(value, false, tN + 1);
            System.out.println(",");
        }

        ObjectUtil.printTextWithTab("}", tN - 1);
    }

    /**
     * 打印对象内容的核心方法
     * 根据对象类型选择相应的打印方式
     *
     * @param object 要打印的对象
     * @param showMethod 是否显示方法
     * @param tN 制表符数量
     */
    private static void printObject(Object object, boolean showMethod, int tN) {
        if (tN <= 0) {
            tN = 1;
        }
        if (object == null) {
            System.out.print("null");
            return;
        }
        if (ObjectUtil.isPrimitive(object)) {
            System.out.print(object);
            return;
        }
        else if (ObjectUtil.isString(object)) {
            ObjectUtil.printString(object.toString(), tN);
            return;
        }
        else if (ObjectUtil.isArray(object)) {
            ObjectUtil.printArray((Object[]) object, tN);
            return;
        }
        else if (ObjectUtil.isSet(object)) {
            ObjectUtil.printSet((Set<?>) object, tN);
            return;
        }
        else if (ObjectUtil.isMap(object)) {
            ObjectUtil.printMap((Map<?, ?>) object, tN);
            return;
        }

        String tString = StringUtil.repeatString("\t", tN);
        Class<?> clz = object.getClass();
        Field[] fields = clz.getDeclaredFields();
        Method[] methods = clz.getDeclaredMethods();
        System.out.print(clz.getName());
        System.out.println("{");
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Object value = f.get(object);
                System.out.print(
                    tString +
                    f.getName() +
                    "："
                );
                ObjectUtil.printObject(value, showMethod, tN + 1);
                System.out.println(",");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (showMethod) {
            for (Method m : methods) {
                System.out.println(tString + m.getName() + ",");
            }
        }

        ObjectUtil.printTextWithTab("}", tN - 1);
    }

    /**
     * 打印对象内容，可选择是否显示方法
     *
     * @param object 要打印的对象
     * @param showMethod 是否显示方法
     */
    public static void printObject(Object object, boolean showMethod) {
        ObjectUtil.printObject(object, showMethod, 1);
    }

    /**
     * 打印对象内容
     *
     * @param object 要打印的对象
     */
    public static void printObject(Object object) {
        ObjectUtil.printObject(object, false);
    }
}
