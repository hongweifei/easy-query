package net.cyue.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 流处理工具类
 * <p>
 * 提供了多种处理InputStream和ByteBuffer的方法，包括：
 * - 大文件分块读取
 * - 流数据分块处理
 * - InputStream转ByteBuffer
 * - InputStream转字符串等
 * </p>
 */
public class StreamUtil {
    private static final int READ_BLOCK_LENGTH = 1024; // 1024 Byte
    private static final int BUFFER_SIZE = 8192; // 8KB
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private static final int CHUNK_SIZE = 64 * 1024 * 1024; // 64MB chunks
    private static final long MAX_FILE_SIZE = 2L * 1024 * 1024 * 1024; // 2GB limit

    /**
     * 私有构造函数，防止实例化
     */
    private StreamUtil() {
        // Prevent instantiation
    }

    /**
     * 对大文件进行分块映射
     * <p>
     * 将大文件分割成多个块并映射到内存中，每块大小由CHUNK_SIZE常量定义（默认64MB）。
     * 此方法适用于处理大文件，避免一次性加载整个文件到内存导致的内存溢出问题。
     * </p>
     *
     * @param fis 文件输入流
     * @return 包含映射字节缓冲区的列表
     * @throws IOException 当文件过大（超过2GB）或读取过程中发生I/O错误时抛出
     */
    public static List<ByteBuffer> readLargeFile(FileInputStream fis)
        throws IOException
    {
        List<ByteBuffer> buffers = new ArrayList<>();

        FileChannel channel = fis.getChannel();
        long size = channel.size();
        if (size > MAX_FILE_SIZE) {
            throw new IOException("文件过大: " + size + " bytes");
        }

        long position = 0;
        while (position < size) {
            long remaining = size - position;
            long chunkSize = Math.min(remaining, CHUNK_SIZE);

            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_ONLY,
                position,
                chunkSize
            );
            buffer.order(ByteOrder.nativeOrder());
            buffers.add(buffer);

            position += chunkSize;
        }

        fis.close();
        return buffers;
    }

    /**
     * 从输入流分块读取数据
     * <p>
     * 将输入流数据写入临时文件，然后通过内存映射方式读取。
     * 适用于处理大小未知的数据流，最大支持2GB数据。
     * </p>
     *
     * @param stream 输入流
     * @return 映射的字节缓冲区
     * @throws IOException 当输入流为null、数据过大或发生I/O错误时抛出
     */
    public static ByteBuffer readInChunks(InputStream stream)
        throws IOException
    {
        if (stream == null) {
            throw new NullPointerException("输入流不能为null");
        }

        // 使用临时文件存储大数据
        File tempFile = File.createTempFile("stream", ".tmp");
        tempFile.deleteOnExit();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalSize = 0;

            // 将输入流写入临时文件
            while ((bytesRead = stream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalSize += bytesRead;
                if (totalSize > MAX_FILE_SIZE) {
                    throw new IOException("数据过大: " + totalSize + " bytes");
                }
            }
        }

        // 使用内存映射读取临时文件
        try (FileChannel channel = FileChannel.open(tempFile.toPath(), StandardOpenOption.READ)) {
            long size = channel.size();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
            buffer.order(ByteOrder.nativeOrder());
            return buffer;
        } finally {
            tempFile.delete();
        }
    }

    /**
     * 读取大小未知的输入流
     * <p>
     * 将输入流数据读取到字节数组中，然后创建直接缓冲区并复制数据。
     * 适用于处理大小未知的数据流。
     * </p>
     *
     * @param stream 输入流
     * @return 字节缓冲区
     * @throws IOException 当流数据过大或内存不足时抛出
     */
    private static ByteBuffer readUnknownSizeStream(InputStream stream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] chunk = new byte[BUFFER_SIZE];
        int bytesRead;
        long totalSize = 0;

        // 分块读取数据
        while ((bytesRead = stream.read(chunk)) != -1) {
            totalSize += bytesRead;
            if (totalSize > MAX_ARRAY_SIZE) {
                throw new IOException("流数据过大");
            }
            byteStream.write(chunk, 0, bytesRead);
        }

        // 创建直接缓冲区并复制数据
        try {
            ByteBuffer buffer = ByteBuffer.allocateDirect(byteStream.size())
                    .order(ByteOrder.nativeOrder());
            buffer.put(byteStream.toByteArray());
            buffer.flip();
            return buffer;

        } catch (OutOfMemoryError e) {
            throw new IOException("内存不足，无法分配缓冲区", e);
        }
    }

    /**
     * 将输入流读取为字节缓冲区
     * <p>
     * 根据输入流类型选择合适的读取方式：
     * - 如果是FileInputStream，则直接映射为ByteBuffer
     * - 否则使用分块读取方式处理
     * </p>
     *
     * @param stream 输入流
     * @return 字节缓冲区
     * @throws IOException 当输入流为null、流大小无效或发生I/O错误时抛出
     */
    public static ByteBuffer readAsByteBuffer(InputStream stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("输入流不能为 null");
        }

        // 首先获取流的大小（如果可能）
        int size;
        if (stream instanceof FileInputStream) {
            size = (int) ((FileInputStream) stream).getChannel().size();
        } else {
            // 对于未知大小的流，先读取到字节数组
            return readUnknownSizeStream(stream);
        }

        // 检查大小是否合理
        if (size <= 0 || size > MAX_ARRAY_SIZE) {
            throw new IOException("无效的流大小: " + size);
        }

        try {
            // 创建直接缓冲区，确保8字节对齐
            ByteBuffer buffer = ByteBuffer.allocateDirect(size)
                    .order(ByteOrder.nativeOrder());

            // 使用较小的缓冲区分块读取
            byte[] chunk = new byte[BUFFER_SIZE];
            int bytesRead;
            int totalBytesRead = 0;

            while ((bytesRead = stream.read(chunk)) != -1) {
                buffer.put(chunk, 0, bytesRead);
                totalBytesRead += bytesRead;

                if (totalBytesRead > size) {
                    throw new IOException("流大小超出预期");
                }
            }

            // 确保读取了完整的数据
            if (totalBytesRead != size) {
                throw new IOException("未能读取完整数据");
            }

            buffer.flip();
            return buffer;

        } catch (OutOfMemoryError e) {
            throw new IOException("内存不足，无法分配缓冲区", e);
        }
    }


    /**
     * 将文件输入流读取为映射字节缓冲区
     * <p>
     * 使用文件通道将整个文件映射到内存中，适用于处理已知大小的文件。
     * </p>
     *
     * @param stream 文件输入流
     * @return 映射字节缓冲区
     * @throws IOException 当输入流为null或发生I/O错误时抛出
     */
    public static ByteBuffer readAsMappedByteBuffer(FileInputStream stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("输入流不能为 null");
        }
        FileChannel channel = stream.getChannel();
        long size = channel.size();
        return channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
    }

    /**
     * 将输入流读取为字符串
     * <p>
     * 使用BufferedReader逐行读取输入流内容并构建字符串。
     * </p>
     *
     * @param inputStream 输入流
     * @return 读取的字符串内容
     * @throws IOException 当输入流为null或发生I/O错误时抛出
     */
    public static String readAsString(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException("输入流不能为 null");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine())!= null) {
                stringBuilder.append(line).append('\n');
            }
            // 移除最后一个多余的换行符（如果有）
            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == '\n') {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }
            return stringBuilder.toString();
        }
    }

    /**
     * 将输入流按照指定字符集读取为字符串
     * <p>
     * 使用指定字符集分块读取输入流内容并构建字符串。
     * </p>
     *
     * @param inputStream 输入流
     * @param charset 字符集
     * @return 读取的字符串内容
     * @throws IOException 当发生I/O错误时抛出
     */
    public static String readAsString(InputStream inputStream, Charset charset)
        throws IOException
    {
        StringBuilder builder = new StringBuilder();
        int br = 0;
        byte[] bs = new byte[StreamUtil.READ_BLOCK_LENGTH];
        while ((br = inputStream.read(bs)) != -1) {
            String str;
            if (br != bs.length) {
                str = new String(bs, charset);
            } else {
                byte[] newBs = Arrays.copyOfRange(bs, 0, br);
                str = new String(newBs, charset);
            }
            builder.append(str);
        }
        return builder.toString();
    }
}
