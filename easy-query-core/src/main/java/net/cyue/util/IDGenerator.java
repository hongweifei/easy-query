package net.cyue.util;

/**
 * ID生成器
 */
public class IDGenerator {
    private static long lastGettingTime = System.currentTimeMillis();
    private static int lastGettingCount = 0;

    /**
     * 获取ID
     * @return ID
     */
    public static long getID() {
        long nowTime = System.currentTimeMillis();
        int number = 1;

        if (nowTime == lastGettingTime) {
            number += ++lastGettingCount;
        } else if (nowTime < lastGettingTime) {
            nowTime = lastGettingTime;
            number += ++lastGettingCount;
        } else {
            lastGettingTime = nowTime;
            lastGettingCount = 0;
        }
        String numberString = String.valueOf(number);
        numberString = StringUtil.repeatString("0", 5 - numberString.length()) + numberString;

        String idString = nowTime + numberString;
        return Long.parseLong(idString);
    }

    /**
     * 获取ID字符串
     * @return ID字符串
     */
    public static String getIDString() {
        return String.valueOf(IDGenerator.getID());
    }
}

