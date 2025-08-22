package net.cyue.util;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 数组工具类
 */
public class ArrayUtil {
    
    private ArrayUtil() {
        // Prevent instantiation
    }

    /**
     * 创建指定类型和维度的空数组
     *
     * @param elementType 数组元素的类型
     * @param dimensions 数组各维度的大小
     * @return 创建的数组实例
     * @throws IllegalArgumentException 当未提供维度或维度为负数时抛出异常
     */
    public static Object createEmptyArray(Class<?> elementType, int... dimensions) {
        if (dimensions == null || dimensions.length == 0) {
            throw new IllegalArgumentException("Dimensions must be provided.");
        }

        for (int dimension : dimensions) {
            if (dimension < 0) {
                throw new IllegalArgumentException("Dimensions must be non-negative.");
            }
        }

        return Array.newInstance(elementType, dimensions);
    }

    /**
     * 扩展数组到新的维度大小
     *
     * @param array 原始数组
     * @param newDimensions 新的维度大小
     * @return 扩展后的新数组，包含原始数组的数据
     * @throws IllegalArgumentException 当新旧数组维度数量不一致时抛出异常
     */
    public static Object expandArray(Object array, int[] newDimensions) {
        int[] oldDimensions = getArrayDimensions(array);
        if (oldDimensions.length != newDimensions.length) {
            throw new IllegalArgumentException("The number of dimensions in the old and new arrays must be the same.");
        }
        if (Arrays.equals(oldDimensions, newDimensions)) {
            return array;
        }
    
        Class<?> elementType = getArrayElementType(array);
        Object newArray = Array.newInstance(elementType, newDimensions);
        copyArrayElements(array, oldDimensions, newArray, newDimensions, new int[oldDimensions.length]);
    
        return newArray;
    }

    /**
     * 复制数组元素到新数组
     *
     * @param oldArray 原始数组
     * @param oldDimensions 原始数组维度
     * @param newArray 新数组
     * @param newDimensions 新数组维度
     * @param indices 当前处理的索引位置
     */
    private static void copyArrayElements(Object oldArray, int[] oldDimensions, Object newArray, int[] newDimensions, int[] indices) {
        if (indices.length == oldDimensions.length) {
            boolean inBounds = true;
            for (int i = 0; i < indices.length; i++) {
                if (indices[i] >= oldDimensions[i]) {
                    inBounds = false;
                    break;
                }
            }
    
            if (inBounds) {
                Object element = getArrayElement(oldArray, indices);
                setArrayElement(newArray, indices, element);
            }
        } else {
            for (int i = 0; i < newDimensions[indices.length]; i++) {
                indices[indices.length] = i;
                copyArrayElements(oldArray, oldDimensions, newArray, newDimensions, indices);
            }
        }
    }

    /**
     * 获取数组指定位置的元素
     *
     * @param array 目标数组
     * @param indices 元素索引位置
     * @return 指定位置的元素
     */
    private static Object getArrayElement(Object array, int[] indices) {
        Object current = array;
        for (int index : indices) {
            current = Array.get(current, index);
        }
        return current;
    }

    /**
     * 设置数组指定位置的元素
     *
     * @param array 目标数组
     * @param indices 元素索引位置
     * @param element 要设置的元素
     */
    private static void setArrayElement(Object array, int[] indices, Object element) {
        Object current = array;
        for (int i = 0; i < indices.length - 1; i++) {
            current = Array.get(current, indices[i]);
        }
        Array.set(current, indices[indices.length - 1], element);
    }

    /**
     * 获取数组各维度的大小
     *
     * @param array 目标数组
     * @return 各维度大小的数组
     */
    private static int[] getArrayDimensions(Object array) {
        int[] dimensions = new int[0];
        Object current = array;
        while (current.getClass().isArray()) {
            int length = Array.getLength(current);
            dimensions = Arrays.copyOf(dimensions, dimensions.length + 1);
            dimensions[dimensions.length - 1] = length;
            current = Array.get(current, 0);
        }
        return dimensions;
    }

    /**
     * 获取数组元素的类型
     *
     * @param array 目标数组
     * @return 数组元素的类型
     */
    private static Class<?> getArrayElementType(Object array) {
        Class<?> componentType = array.getClass().getComponentType();
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
        }
        return componentType;
    }
}
