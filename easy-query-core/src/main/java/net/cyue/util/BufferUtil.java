package net.cyue.util;

import java.lang.reflect.Array;
import java.nio.*;

/**
 * 缓冲区工具类
 * <p>
 * 提供了ByteBuffer与其他类型缓冲区之间的转换、数组与缓冲区之间的转换等功能。
 * 支持多种基本数据类型（byte、short、int、long、float、double、char、boolean）的处理。
 * </p>
 */
public class BufferUtil {

    /**
     * 私有构造函数，防止实例化
     */
    private BufferUtil() {
        // Prevent instantiation
    }

    /**
     * 将ByteBuffer转换为特定类型的缓冲区
     * <p>
     * 根据指定的数据类型将ByteBuffer转换为对应的类型特定缓冲区，
     * 如FloatBuffer、IntBuffer等。转换后缓冲区的位置会被重置为0。
     * </p>
     *
     * @param byteBuffer 输入的ByteBuffer
     * @param type       表示数据类型的Class对象（如float.class、int.class）
     * @return 类型特定的缓冲区（如FloatBuffer、IntBuffer等）
     * @throws IllegalArgumentException 当不支持指定的数据类型时抛出
     */
    public static Buffer toTypedBuffer(ByteBuffer byteBuffer, Class<?> type) {
        byteBuffer.rewind(); // Ensure the buffer is ready for reading

        if (type == float.class) {
            return byteBuffer.asFloatBuffer();
        } else if (type == int.class) {
            return byteBuffer.asIntBuffer();
        } else if (type == double.class) {
            return byteBuffer.asDoubleBuffer();
        } else if (type == long.class) {
            return byteBuffer.asLongBuffer();
        } else if (type == short.class) {
            return byteBuffer.asShortBuffer();
        } else if (type == byte.class) {
            return byteBuffer;
        } else if (type == char.class) {
            return byteBuffer.asCharBuffer();
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + type);
        }
    }

    /**
     * 将数组转换为ByteBuffer
     * <p>
     * 将基本数据类型的数组转换为直接ByteBuffer，使用本地字节序。
     * 支持多维数组。
     * </p>
     *
     * @param array 输入数组
     * @return 包含数组数据的ByteBuffer
     * @throws IllegalArgumentException 当不支持数组元素类型时抛出
     */
    public static ByteBuffer arrayToByteBuffer(Object array) {
        Class<?> componentType = array.getClass().getComponentType();
        int elementTypeSize = getTypeSize(componentType);
        int arrayLength = getArrayLength(array);
        ByteBuffer buffer = ByteBuffer.allocateDirect(arrayLength * elementTypeSize)
                .order(ByteOrder.nativeOrder());

        writeToBuffer(buffer, array, componentType);

        buffer.position(0);
        return buffer;
    }

    /**
     * 将ByteBuffer转换为多维数组
     * <p>
     * 将ByteBuffer中的数据转换为指定维度和类型的多维数组。
     * </p>
     *
     * @param buffer      输入的ByteBuffer
     * @param dimensions  数组各维度的大小
     * @param elementType 数组元素类型
     * @return 指定维度和类型的多维数组
     */
    public static Object byteBufferToArray(ByteBuffer buffer, int[] dimensions, Class<?> elementType) {
        int[] shape = dimensions.clone();
        int length = calculateTotalElements(shape);

        Object array = createMultiDimensionalArray(elementType, shape);
        buffer.position(0);

        Object flatArray = readFromBuffer(buffer, length, elementType);

        fillMultiArray(array, shape, 0, flatArray, 0);

        return array;
    }

    /**
     * 扩展ByteBuffer
     * <p>
     * 根据新的形状扩展ByteBuffer的容量。
     * </p>
     *
     * @param original       原始ByteBuffer
     * @param originalShape  原始形状
     * @param newShape       新形状
     * @param elementType    元素类型
     * @return 扩展后的ByteBuffer
     * @throws IllegalArgumentException 当原始缓冲区剩余字节数与预期大小不匹配时抛出
     */
    public static ByteBuffer expandByteBuffer(
        ByteBuffer original,
        int[] originalShape,
        int[] newShape,
        Class<?> elementType
    ) {
        int originalSize = calculateTotalElements(originalShape);
        int newSize = calculateTotalElements(newShape);
        int elementTypeSize = getTypeSize(elementType);

        if (original.remaining() != originalSize * elementTypeSize) {
            throw new IllegalArgumentException("Original buffer remaining bytes doesn't match expected size.");
        }

        ByteBuffer expandedBuffer = ByteBuffer.allocateDirect(newSize * elementTypeSize).order(original.order());
        expandedBuffer.put(original);
        expandedBuffer.flip();

        return expandedBuffer;
    }

    /**
     * 将数组数据写入缓冲区
     * <p>
     * 根据数组元素类型将数据写入ByteBuffer。
     * </p>
     *
     * @param buffer       目标ByteBuffer
     * @param array        源数组
     * @param elementType  数组元素类型
     * @throws IllegalArgumentException 当不支持数组元素类型时抛出
     */
    private static void writeToBuffer(ByteBuffer buffer, Object array, Class<?> elementType) {
        switch (elementType.getName()) {
            case "byte":
                buffer.put((byte[]) array);
                break;
            case "short":
                ShortBuffer sb = buffer.asShortBuffer();
                sb.put((short[]) array);
                break;
            case "int":
                IntBuffer ib = buffer.asIntBuffer();
                ib.put((int[]) array);
                break;
            case "long":
                LongBuffer lb = buffer.asLongBuffer();
                lb.put((long[]) array);
                break;
            case "float":
                FloatBuffer fb = buffer.asFloatBuffer();
                fb.put((float[]) array);
                break;
            case "double":
                DoubleBuffer db = buffer.asDoubleBuffer();
                db.put((double[]) array);
                break;
            case "char":
                CharBuffer cb = buffer.asCharBuffer();
                cb.put((char[]) array);
                break;
            case "boolean":
                byte[] boolArray = booleanArrayToByteArray((boolean[]) array);
                buffer.put(boolArray);
                break;
            default:
                throw new IllegalArgumentException("Unsupported element type: " + elementType);
        }
    }

    /**
     * 从缓冲区读取数据到数组
     * <p>
     * 根据元素类型从ByteBuffer中读取指定长度的数据到数组。
     * </p>
     *
     * @param buffer      源ByteBuffer
     * @param length      要读取的元素数量
     * @param elementType 元素类型
     * @return 包含读取数据的数组
     * @throws IllegalArgumentException 当不支持元素类型时抛出
     */
    private static Object readFromBuffer(ByteBuffer buffer, int length, Class<?> elementType) {
        switch (elementType.getName()) {
            case "byte":
                byte[] byteArray = new byte[length];
                buffer.get(byteArray);
                return byteArray;
            case "short":
                ShortBuffer sb = buffer.asShortBuffer();
                short[] shortArray = new short[length];
                sb.get(shortArray);
                return shortArray;
            case "int":
                IntBuffer ib = buffer.asIntBuffer();
                int[] intArray = new int[length];
                ib.get(intArray);
                return intArray;
            case "long":
                LongBuffer lb = buffer.asLongBuffer();
                long[] longArray = new long[length];
                lb.get(longArray);
                return longArray;
            case "float":
                FloatBuffer fb = buffer.asFloatBuffer();
                float[] floatArray = new float[length];
                fb.get(floatArray);
                return floatArray;
            case "double":
                DoubleBuffer db = buffer.asDoubleBuffer();
                double[] doubleArray = new double[length];
                db.get(doubleArray);
                return doubleArray;
            case "char":
                CharBuffer cb = buffer.asCharBuffer();
                char[] charArray = new char[length];
                cb.get(charArray);
                return charArray;
            case "boolean":
                byte[] boolBytes = new byte[length];
                buffer.get(boolBytes);
                boolean[] boolArray = byteArrayToBooleanArray(boolBytes);
                return boolArray;
            default:
                throw new IllegalArgumentException("Unsupported element type: " + elementType);
        }
    }

    /**
     * 计算多维数组的总元素数量
     * <p>
     * 根据各维度大小计算多维数组的总元素数量。
     * </p>
     *
     * @param shape 各维度大小数组
     * @return 总元素数量
     */
    private static int calculateTotalElements(int[] shape) {
        int total = 1;
        for (int dim : shape) {
            total *= dim;
        }
        return total;
    }

    /**
     * 获取数组长度
     * <p>
     * 获取数组的总长度，对于多维数组会递归计算所有维度的乘积。
     * </p>
     *
     * @param array 数组对象
     * @return 数组总长度
     */
    private static int getArrayLength(Object array) {
        int length = Array.getLength(array);
        Class<?> componentType = array.getClass().getComponentType();
        if (componentType != null && componentType.isArray()) {
            return length * getArrayLength(Array.get(array, 0));
        }
        return length;
    }

    /**
     * 获取类型的大小（以字节为单位）
     * <p>
     * 获取指定基本数据类型的大小（以字节为单位）。
     * </p>
     *
     * @param type 数据类型
     * @return 类型大小（以字节为单位）
     * @throws IllegalArgumentException 当不支持指定类型时抛出
     */
    private static int getTypeSize(Class<?> type) {
        if (type == byte.class || type == Byte.class) {
            return 1;
        } else if (type == short.class || type == Short.class) {
            return 2;
        } else if (type == int.class || type == Integer.class) {
            return 4;
        } else if (type == long.class || type == Long.class) {
            return 8;
        } else if (type == float.class || type == Float.class) {
            return 4;
        } else if (type == double.class || type == Double.class) {
            return 8;
        } else if (type == char.class || type == Character.class) {
            return 2;
        } else if (type == boolean.class || type == Boolean.class) {
            return 1;
        } else {
            throw new IllegalArgumentException("Unsupported element type: " + type);
        }
    }

    /**
     * 创建多维数组
     * <p>
     * 根据元素类型和各维度大小创建多维数组。
     * </p>
     *
     * @param elementType 元素类型
     * @param dimensions  各维度大小
     * @return 创建的多维数组
     * @throws IllegalArgumentException 当维度数组为空时抛出
     */
    private static Object createMultiDimensionalArray(Class<?> elementType, int[] dimensions) {
        int dimensionsLength = dimensions.length;
        if (dimensionsLength == 0) {
            throw new IllegalArgumentException("Dimensions array must have at least one dimension.");
        }
        Object array = Array.newInstance(elementType, dimensions[0]);
        if (dimensionsLength > 1) {
            for (int i = 0; i < dimensions[0]; i++) {
                Array.set(array, i, createMultiDimensionalArray(elementType, copyOfRange(dimensions, 1, dimensionsLength)));
            }
        }
        return array;
    }

    /**
     * 填充多维数组
     * <p>
     * 将一维数组中的数据填充到多维数组中。
     * </p>
     *
     * @param array      目标多维数组
     * @param shape      各维度大小
     * @param currentDim 当前处理的维度
     * @param flatArray  源一维数组
     * @param index      当前处理的索引
     */
    private static void fillMultiArray(Object array, int[] shape, int currentDim, Object flatArray, int index) {
        if (currentDim == shape.length - 1) {
            System.arraycopy(flatArray, index, array, 0, shape[currentDim]);
        } else {
            for (int i = 0; i < shape[currentDim]; i++) {
                Object subArray = Array.get(array, i);
                fillMultiArray(subArray, shape, currentDim + 1, flatArray, index + i * getStride(shape, currentDim + 1));
            }
        }
    }

    /**
     * 计算步长
     * <p>
     * 计算从指定维度开始的步长（即后续所有维度大小的乘积）。
     * </p>
     *
     * @param shape     各维度大小
     * @param startDim  起始维度
     * @return 步长
     */
    private static int getStride(int[] shape, int startDim) {
        int stride = 1;
        for (int i = startDim; i < shape.length; i++) {
            stride *= shape[i];
        }
        return stride;
    }

    /**
     * 将布尔数组转换为字节数组
     * <p>
     * 将布尔数组转换为字节数组，true转换为1，false转换为0。
     * </p>
     *
     * @param boolArray 布尔数组
     * @return 字节数组
     */
    private static byte[] booleanArrayToByteArray(boolean[] boolArray) {
        byte[] byteArray = new byte[boolArray.length];
        for (int i = 0; i < boolArray.length; i++) {
            byteArray[i] = (byte) (boolArray[i] ? 1 : 0);
        }
        return byteArray;
    }

    /**
     * 将字节数组转换为布尔数组
     * <p>
     * 将字节数组转换为布尔数组，非0字节转换为true，0字节转换为false。
     * </p>
     *
     * @param byteArray 字节数组
     * @return 布尔数组
     */
    private static boolean[] byteArrayToBooleanArray(byte[] byteArray) {
        boolean[] boolArray = new boolean[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            boolArray[i] = byteArray[i] != 0;
        }
        return boolArray;
    }

    /**
     * 复制数组范围
     * <p>
     * 复制数组中指定范围的元素到新数组。
     * </p>
     *
     * @param original 原始数组
     * @param from     起始索引（包含）
     * @param to       结束索引（不包含）
     * @return 新数组
     */
    private static int[] copyOfRange(int[] original, int from, int to) {
        int[] result = new int[to - from];
        System.arraycopy(original, from, result, 0, result.length);
        return result;
    }
}