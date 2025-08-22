package net.cyue.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * 图片工具类，提供图片与Base64编码之间的相互转换功能
 */
public class ImageUtil {

    /**
     * 图片转换异常类，当图片转换过程中发生错误时抛出
     */
    public static class ImageConversionException extends Exception {

        /**
         * 构造一个图片转换异常
         *
         * @param message 异常信息
         */
        public ImageConversionException(String message) {
            super(message);
        }
    }

    private ImageUtil() {
        // Prevent instantiation
    }

    /**
     * 将图片转换为Base64编码字符串
     *
     * @param img 图片对象，类型为RenderedImage
     * @param formatName 图片格式名称，如"png"、"jpg"等
     * @return 图片的Base64编码字符串
     * @throws ImageConversionException 当图片转换失败时抛出
     */
    public static String imageToBase64(RenderedImage img, String formatName) throws ImageConversionException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, formatName, os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ImageConversionException("转换图片至Base64编码失败");
        }
    }

    /**
     * 将Base64编码字符串转换为图片
     *
     * @param base64 Base64编码的图片字符串
     * @return BufferedImage对象
     * @throws ImageConversionException 当Base64转换为图片失败时抛出
     */
    public static BufferedImage base64ToImage(String base64) throws ImageConversionException {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ImageConversionException("转换Base64编码至图片失败");
        }
    }
}
