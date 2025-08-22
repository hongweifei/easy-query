package net.cyue.util;

import java.nio.charset.StandardCharsets;


/**
 * 字符串处理工具类
 * <p>
 * 提供了常用的字符串处理方法，包括判断字符串是否为空、转换为UTF-8编码等操作。
 * </p>
 */
public class StringUtil {

    /**
     * 私有构造函数，防止实例化
     */
    private StringUtil() {
        // Prevent instantiation
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 待检查的字符串
     * @return 如果字符串为null、空字符串或只包含空白字符则返回true，否则返回false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty() || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否为空白
     *
     * @param str 待检查的字符串
     * @return 如果字符串为null、空字符串或只包含空白字符则返回true，否则返回false
     */
    public static boolean isBlank(String str) {
        return str == null || str.isEmpty() || str.trim().isEmpty();
    }

    /**
     * 将给定的字符串重复 n 次并返回结果。
     *
     * @param str 字符串，要重复的字符串。
     * @param n   整数，重复次数。
     * @return 返回重复 n 次后的字符串。
     */
    public static String repeatString(String str, int n) {
        if (str == null || str.isEmpty() || n <= 0) {
            return "";
        }

        // 预先计算总长度
        int len = str.length();
        StringBuilder result = new StringBuilder(len * n);

        // 使用 while 循环代替 for 循环，减少变量声明
        int count = n;
        while (count-- > 0) {
            result.append(str);
        }

        return result.toString();
    }

    /**
     * 将字符串转换为UTF-8编码格式
     * <p>
     * 使用UTF-8字符集重新编码给定的字符串。
     * </p>
     *
     * @param str 原始字符串
     * @return UTF-8编码的字符串
     */
    public static String toUTF8String(String str) {
        return new String(str.getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 将字符串数组中的每个元素转换为UTF-8编码格式
     * <p>
     * 遍历字符串数组，使用UTF-8字符集重新编码每个元素。
     * </p>
     *
     * @param strArray 字符串数组
     * @return UTF-8编码的字符串数组
     */
    public static String[] toUTF8StringArray(String[] strArray) {
        String[] utf8StrArray = new String[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            utf8StrArray[i] = new String(strArray[i].getBytes(), StandardCharsets.UTF_8);
        }
        return utf8StrArray;
    }
}

