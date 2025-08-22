package net.cyue.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类
 */
public class DateUtil {
    /**
     * 常见的日期格式模板（可根据实际需求扩展）
     * 注意：格式的顺序会影响检测优先级，建议将更可能出现的格式放在前面
     */
    private static final List<String> DATE_FORMAT_PATTERNS = new ArrayList<String>() {{
        // 带时间的格式
        add("yyyy-MM-dd HH:mm:ss");
        add("yyyy/MM/dd HH:mm:ss");
        add("yyyy.MM.dd HH:mm:ss");
        add("yyyy-MM-dd HH:mm");
        add("yyyy/MM/dd HH:mm");
        add("yyyy.MM.dd HH:mm");
        add("yyyy-MM HH:mm:ss");
        add("yyyy/MM HH:mm:ss");
        add("yyyy.MM HH:mm:ss");
        add("yyyy-MM HH:mm");
        add("yyyy/MM HH:mm");
        add("yyyy.MM HH:mm");

        // 仅日期的格式
        add("yyyy-MM");
        add("yyyy/MM");
        add("yyyy.MM");
        add("yyyy-MM-dd");
        add("yyyy/MM/dd");
        add("yyyy.MM.dd");
        add("MM-dd-yyyy");
        add("MM/dd/yyyy");
        add("MM.dd.yyyy");
        add("dd-MM-yyyy");
        add("dd/MM/yyyy");
        add("dd.MM.yyyy");

        // 带星期和时区的格式（如 "Thu Feb 01 00:00:00 CST 2024"）
        add("EEE MMM dd HH:mm:ss zzz yyyy");
    }};

    /**
     * 添加日期格式
     * @param pattern 日期格式
     */
    public static void addDateFormatPattern(String pattern) {
        DATE_FORMAT_PATTERNS.add(pattern);
    }

    /**
     * 获取日期字符串对应的格式
     *
     * @param dateStr 待检测的日期字符串
     * @return 匹配的日期格式字符串；若未匹配到，返回null
     */
    public static String getDateFormat(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        // 尝试每种格式模板进行解析
        for (String pattern : DATE_FORMAT_PATTERNS) {
            SimpleDateFormat sdf;
            // 针对带星期和时区的格式，需要指定Locale为US（避免中文环境解析英文星期/月份失败）
            if (pattern.contains("EEE") || pattern.contains("MMM")) {
                sdf = new SimpleDateFormat(pattern, Locale.US);
            } else {
                sdf = new SimpleDateFormat(pattern);
            }

            // 设置严格模式，避免宽松解析导致的误判（如"2024-13-01"不会被解析为有效日期）
            sdf.setLenient(false);

            try {
                // 尝试解析字符串
                sdf.parse(dateStr.trim());
                // 解析成功，返回当前格式模板
                return pattern;
            } catch (ParseException e) {
                // 解析失败，尝试下一个格式模板
            }
        }

        // 所有格式均不匹配
        return null;
    }

    /**
     * 解析日期
     * @param dateStr 日期字符串
     * @return 日期对象
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        // 尝试每种格式模板进行解析
        for (String pattern : DATE_FORMAT_PATTERNS) {
            SimpleDateFormat sdf;
            // 针对带星期和时区的格式，需要指定Locale为US（避免中文环境解析英文星期/月份失败）
            if (pattern.contains("EEE") || pattern.contains("MMM")) {
                sdf = new SimpleDateFormat(pattern, Locale.US);
            } else {
                sdf = new SimpleDateFormat(pattern);
            }

            // 设置严格模式，避免宽松解析导致的误判（如"2024-13-01"不会被解析为有效日期）
            sdf.setLenient(false);

            try {
                // 尝试解析字符串
                return sdf.parse(dateStr.trim());
            } catch (ParseException e) {
                // 解析失败，尝试下一个格式模板
            }
        }
        // 所有格式均不匹配
        return null;
    }

    /**
     * 按照指定格式格式化日期对象
     * @param date 日期对象
     * @param format 日期格式
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 将日期对象格式化为数据库日期格式，格式为 yyyy-MM-dd
     * @param date 日期对象
     * @return 数据库日期字符串
     */
    public static String getDBDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    /**
     * 获取当前数据库日期，格式为 yyyy-MM-dd
     * @return 当前数据库日期字符串
     */
    public static String getDBDateNow() {
        return getDBDate(new Date());
    }

    /**
     * 将日期对象格式化为数据库日期时间格式，格式为 yyyy-MM-dd HH:mm:ss
     * @param date 日期对象
     * @return 数据库日期时间字符串
     */
    public static String getDBDateTime(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    /**
     * 获取当前数据库日期时间，格式为 yyyy-MM-dd HH:mm:ss
     * @return 当前数据库日期时间字符串
     */
    public static String getDBDateTimeNow() {
        return getDBDateTime(new Date());
    }


    /**
     * 获取指定日期若干天后的日期
     * @param dateStr 日期字符串
     * @param days 天数
     * @return 若干天后的日期字符串，格式为 yyyy-MM-dd
     */
    public static String getAfterDate(String dateStr, int days) {
        Calendar cal = Calendar.getInstance();
        Date o = parseDate(dateStr);
        cal.setTime(o);
        cal.add(Calendar.DATE, days);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    /**
     * 计算两个日期之间的时间差
     * @param d1 第一个日期字符串
     * @param d2 第二个日期字符串
     * @param unit 时间单位，如 ONE_MINUTE、ONE_HOUR、ONE_DAY
     * @return 时间差，如果解析失败返回 -1
     */
    public static int getTimeDiff(String d1, String d2, int unit) {
        int diff = -1;
        if (d1 != null && d2 != null) {
            Date date1 = parseDate(d1);
            Date date2 = parseDate(d2);
            return getTimeDiff(date1, date2, unit);
        }
        return diff;
    }

    /**
     * 计算两个日期对象之间的时间差
     * @param date1 第一个日期对象
     * @param date2 第二个日期对象
     * @param unit 时间单位，如 ONE_MINUTE、ONE_HOUR、ONE_DAY
     * @return 时间差，如果日期对象为 null 返回 -1
     */
    public static int getTimeDiff(Date date1, Date date2, int unit) {
        int diff = -1;
        if (date1 != null && date2 != null) {
            long millisecond = date1.getTime() - date2.getTime();
            switch (unit) {
                case 60000:
                    diff = (int)(millisecond / 60000L);
                    break;
                case 3600000:
                    diff = (int)(millisecond / 3600000L);
                    break;
                case 86400000:
                    diff = (int)(millisecond / 86400000L);
            }
            return Math.abs(diff);
        } else {
            return diff;
        }
    }

    /**
     * 将毫秒数转换为易读的时间间隔字符串
     * @param time 毫秒数
     * @return 易读的时间间隔字符串
     */
    public static String getElapsed(long time) {
        if (time > 31104000000L) {
            int year = (int)Math.floor((double)(time / 31104000000L));
            long m_diff = time % 31104000000L;
            if (m_diff > 2592000000L) {
                int month = (int)Math.floor((double)(m_diff / 2592000000L));
                return String.format("%d年 %d个月", year, month);
            } else {
                return String.format("%d年", year);
            }
        } else if (time > 2592000000L) {
            int month = (int)Math.floor((double)(time / 2592000000L));
            long d_diff = time % 2592000000L;
            if (d_diff > 86400000L) {
                int day = (int)Math.floor((double)(d_diff / 86400000L));
                return String.format("%d月 %d天", month, day);
            } else {
                return String.format("%d月", month);
            }
        } else if (time > 86400000L) {
            int day = (int)Math.floor((double)(time / 86400000L));
            long h_diff = time % 86400000L;
            if (h_diff > 3600000L) {
                int hour = (int)Math.floor((double)(h_diff / 3600000L));
                return String.format("%d天 %d小时", day, hour);
            } else {
                return String.format("%d天", day);
            }
        } else if (time > 3600000L) {
            int hour = (int)Math.floor((double)(time / 3600000L));
            long m_diff = time % 3600000L;
            if (m_diff > 60000L) {
                int minute = (int)Math.floor((double)(m_diff / 60000L));
                return String.format("%d 小时 %d 分钟", hour, minute);
            } else {
                return String.format("%d 小时", hour);
            }
        } else if (time > 60000L) {
            int minute = (int)Math.floor((double)(time / 60000L));
            return String.format("%d 分钟", minute);
        } else {
            return String.format("%d 秒", (int)(time / 1000L));
        }
    }

    /**
     * 计算指定时间距离当前时间的时间间隔，并返回易读的时间间隔字符串
     * @param time 指定时间的毫秒数
     * @return 易读的时间间隔字符串
     */
    public static String getTimeAgo(long time) {
        return getTimeAgo(time, (new Date()).getTime());
    }

    /**
     * 计算指定时间距离给定时间的时间间隔，并返回易读的时间间隔字符串
     * @param time 指定时间的毫秒数
     * @param now 给定时间的毫秒数
     * @return 易读的时间间隔字符串
     */
    public static String getTimeAgo(long time, long now) {
        long diff = Math.abs(now - time);
        if (diff > 31104000000L) {
            int year = (int)Math.floor((double)(diff / 31104000000L));
            long m_diff = diff % 31104000000L;
            if (m_diff > 2592000000L) {
                int month = (int)Math.floor((double)(m_diff / 2592000000L));
                return String.format("%d 年 %d 个月前", year, month);
            } else {
                return String.format("%d 年前", year);
            }
        } else if (diff > 2592000000L) {
            int month = (int)Math.floor((double)(diff / 2592000000L));
            long d_diff = diff % 2592000000L;
            if (d_diff > 86400000L) {
                int day = (int)Math.floor((double)(d_diff / 86400000L));
                return String.format("%d 月 %d 天前", month, day);
            } else {
                return String.format("%d 月前", month);
            }
        } else if (diff > 86400000L) {
            int day = (int)Math.floor((double)(diff / 86400000L));
            long h_diff = diff % 86400000L;
            if (h_diff > 3600000L) {
                int hour = (int)Math.floor((double)(h_diff / 3600000L));
                return String.format("%d 天 %d 小时前", day, hour);
            } else {
                return String.format("%d 天前", day);
            }
        } else if (diff > 3600000L) {
            int hour = (int)Math.floor((double)(diff / 3600000L));
            long m_diff = diff % 3600000L;
            if (m_diff > 60000L) {
                int minute = (int)Math.floor((double)(m_diff / 60000L));
                return String.format("%d 小时 %d 分钟前", hour, minute);
            } else {
                return String.format("%d 小时前", hour);
            }
        } else if (diff > 60000L) {
            int minute = (int)Math.floor((double)(diff / 60000L));
            return String.format("%d 分钟前", minute);
        } else {
            return String.format("%d 秒前", (int)(diff / 1000L));
        }
    }
}

