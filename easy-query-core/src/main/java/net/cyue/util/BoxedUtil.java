package net.cyue.util;

import java.util.Arrays;

/**
 * 工具类，提供基本数据类型与包装类型的转换功能
 */
public class BoxedUtil {

    private BoxedUtil() {
        // Prevent instantiation
    }

    /**
     * 将int基本类型转换为Integer包装类型
     * 
     * @param value int值
     * @return Integer包装类型值
     */
    public static Integer boxed(int value) {
        return value;
    }
    
    /**
     * 将long基本类型转换为Long包装类型
     * 
     * @param value long值
     * @return Long包装类型值
     */
    public static Long boxed(long value) {
        return value;
    }
    
    /**
     * 将int数组转换为Integer数组
     * 
     * @param array int数组
     * @return Integer数组
     */
    public static Integer[] boxed(int[] array) {
        return Arrays.stream(array).boxed().toArray(Integer[]::new);
    }
    
    /**
     * 将long数组转换为Long数组
     * 
     * @param array long数组
     * @return Long数组
     */
    public static Long[] boxed(long[] array)
    {
        return Arrays.stream(array).boxed().toArray(Long[]::new);
    }

    /**
     * 将Integer数组转换为int数组
     * 
     * @param array Integer数组
     * @return int数组
     */
    public static int[] unboxed(Integer[] array) {
        return Arrays.stream(array).mapToInt(Integer::intValue).toArray();
    }
    
    /**
     * 将Long数组转换为long数组
     * 
     * @param array Long数组
     * @return long数组
     */
    public static long[] unboxed(Long[] array) {
        return Arrays.stream(array).mapToLong(Long::longValue).toArray();
    }
}