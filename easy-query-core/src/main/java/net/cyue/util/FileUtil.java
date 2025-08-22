package net.cyue.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文件工具类，提供文件读写操作的便捷方法
 */
public class FileUtil {

    private FileUtil() {
        // Prevent instantiation
    }

    /**
     * 读取大文件并将其内容分割为ByteBuffer列表
     *
     * @param filePath 文件路径
     * @return 包含文件内容的ByteBuffer列表
     * @throws IOException 当文件读取失败时抛出
     */
    public static List<ByteBuffer> readLargeFile(String filePath) throws IOException {
        return StreamUtil.readLargeFile(new FileInputStream(filePath));
    }

    /**
     * 将文件内容读取为单个ByteBuffer
     *
     * @param filePath 文件路径
     * @return 包含文件内容的ByteBuffer
     * @throws IOException 当文件读取失败时抛出
     */
    public static ByteBuffer readAsByteBuffer(String filePath) throws IOException {
        return StreamUtil.readAsByteBuffer(Files.newInputStream(Paths.get(filePath)));
    }

    /**
     * 将文件内容读取为MappedByteBuffer（内存映射文件）
     *
     * @param filePath 文件路径
     * @return 包含文件内容的MappedByteBuffer
     * @throws IOException 当文件读取失败时抛出
     */
    public static ByteBuffer readAsMappedByteBuffer(String filePath) throws IOException {
        return StreamUtil.readAsMappedByteBuffer(new FileInputStream(filePath));
    }

    /**
     * 将文件内容读取为字符串
     *
     * @param file 文件对象
     * @return 文件内容字符串
     * @throws IOException 当文件读取失败时抛出
     */
    public static String readAsString(File file) throws IOException {
        InputStream inputStream = Files.newInputStream(file.toPath());
        return StreamUtil.readAsString(inputStream);
    }

    /**
     * 将文件内容读取为字符串
     *
     * @param filePath 文件路径
     * @return 文件内容字符串
     * @throws IOException 当文件读取失败时抛出
     */
    public static String readAsString(String filePath) throws IOException {
        return FileUtil.readAsString(new File(filePath));
    }

    /**
     * 将字符串写入文件
     *
     * @param file 文件对象
     * @param content 要写入的字符串内容
     * @throws IOException 当文件写入失败时抛出
     */
    public static void writeStringToFile(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content 要写入的字符串内容
     * @throws IOException 当文件写入失败时抛出
     */
    public static void writeStringToFile(String filePath, String content) throws IOException {
        FileUtil.writeStringToFile(new File(filePath), content);
    }
}


