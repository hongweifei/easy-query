package net.cyue.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 */
public class MD5Util {
    private static final MessageDigest MD5;
    static  {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * MD5加密
     * @param str 待加密的字符串
     * @return 加密后的字符串
     */
    public static String encode(String str) {
        byte[] hashBytes = MD5.digest(str.getBytes());
        return new BigInteger(1, hashBytes).toString(16);
    }
}

