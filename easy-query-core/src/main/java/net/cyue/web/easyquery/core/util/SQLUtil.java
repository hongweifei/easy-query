package net.cyue.web.easyquery.core.util;

import net.cyue.util.ReflectUtil;
import org.apache.commons.text.StringEscapeUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL 工具类
 * <p>
 * 提供SQL处理相关功能，包括：
 * - SQL预编译参数处理
 * - 参数名提取和值映射
 * - XSS攻击防护
 * - SQL字符串拼接和转义
 * - 数组和集合类型的特殊处理
 * </p>
 */
public class SQLUtil {

    // 匹配 #{parameter} 格式的参数
    private static final Pattern PARAM_PATTERN = Pattern.compile("#\\{([^}]+)}");

    /**
     * 用于存储预编译SQL和参数名的数据结构
     */
    public static class SqlParams {
        private final String sql;
        private final List<String> paramNames;

        /**
         * 构造函数
         *
         * @param sql 预编译SQL
         * @param paramNames 参数名列表
         */
        public SqlParams(String sql, List<String> paramNames) {
            this.sql = sql;
            this.paramNames = paramNames;
        }

        /**
         * 获取预编译SQL
         *
         * @return 预编译SQL
         */
        public String getSql() {
            return sql;
        }

        /**
         * 获取参数名列表
         *
         * @return 参数名列表
         */
        public List<String> getParamNames() {
            return paramNames;
        }

        /**
         * 根据参数名映射获取参数值数组
         *
         * @param paramMap 参数名到参数值的映射
         * @return 参数值数组，按照SQL中出现的顺序排列
         */
        public Object[] getParamValues(Map<String, Object> paramMap) {
            List<Object> values = new ArrayList<>();
            for (String paramName : paramNames) {
                Object value = resolveValue(paramMap, paramName);
                if (isArrayOrCollection(value)) {
                    // 对于数组或集合，展开为多个参数
                    values.addAll(flattenArrayOrCollection(value));
                } else {
                    values.add(value);
                }
            }
            return values.toArray();
        }

        /**
         * 解析参数值，支持嵌套属性访问（如 user.name）
         *
         * @param paramMap 参数映射
         * @param key 参数键名，支持点号分隔的嵌套属性
         * @return 解析后的参数值
         */
        private Object resolveValue(Map<String, Object> paramMap, String key) {
            if (key.contains(".")) {
                String[] parts = key.split("\\.", 2);
                Object obj = paramMap.get(parts[0]);
                if (obj instanceof Map) {
                    return ((Map<?, ?>) obj).get(parts[1]);
                } else if (obj != null) {
                    try {
                        return ReflectUtil.getFieldValue(obj, parts[1]);
                    } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException("无法获取属性: " + key, e);
                    }
                }
            } else {
                return paramMap.get(key);
            }
            return null;
        }
    }

    /**
     * 将包含#{paramName}的SQL转换为?占位符的JDBC预编译SQL
     * 特别处理数组和集合类型的参数，生成多个?占位符
     *
     * @param sql 原始SQL，包含#{paramName}格式的参数
     * @param paramMap 参数映射表
     * @return 转换后的预编译SQL，参数替换为?占位符
     */
    public static String convertToPreparedStatement(String sql, Map<String, Object> paramMap) {
        if (sql == null || sql.isEmpty()) {
            return sql;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = PARAM_PATTERN.matcher(sql);

        while (matcher.find()) {
            String paramName = matcher.group(1);
            Object value = resolveSimpleValue(paramMap, paramName);

            if (isArrayOrCollection(value)) {
                int size = getCollectionSize(value);
                if (size > 0) {
                    StringBuilder placeholders = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        if (i > 0) placeholders.append(",");
                        placeholders.append("?");
                    }
                    matcher.appendReplacement(result, placeholders.toString());
                } else {
                    matcher.appendReplacement(result, "NULL");
                }
            } else {
                matcher.appendReplacement(result, "?");
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 简化版本的参数解析，不处理嵌套
     *
     * @param paramMap 参数映射表
     * @param key 参数键名
     * @return 参数值
     */
    private static Object resolveSimpleValue(Map<String, Object> paramMap, String key) {
        if (key.contains(".")) {
            String[] parts = key.split("\\.", 2);
            Object obj = paramMap.get(parts[0]);
            if (obj instanceof Map) {
                return ((Map<?, ?>) obj).get(parts[1]);
            }
        }
        return paramMap.get(key);
    }

    /**
     * 提取SQL中的参数名
     *
     * @param sql 原始SQL
     * @return 参数名列表
     */
    private static List<String> extractParamNames(String sql) {
        List<String> paramNames = new ArrayList<>();
        Matcher matcher = PARAM_PATTERN.matcher(sql);
        while (matcher.find()) {
            paramNames.add(matcher.group(1));
        }
        return paramNames;
    }

    /**
     * 将SQL转换为预编译语句并提取参数
     *
     * @param sql 原始SQL
     * @param paramMap 参数映射表
     * @return SqlParams对象，包含预编译SQL和参数名列表
     */
    public static SqlParams parseSql(String sql, Map<String, Object> paramMap) {
        String preparedSql = convertToPreparedStatement(sql, paramMap);
        List<String> paramNames = extractParamNames(sql);
        return new SqlParams(preparedSql, paramNames);
    }

    /**
     * 保持向后兼容的版本
     *
     * @param sql 原始SQL
     * @return SqlParams对象，包含预编译SQL和参数名列表
     */
    public static SqlParams parseSql(String sql) {
        return parseSql(sql, new HashMap<>());
    }

    // === XSS 过滤相关方法 ===

    /**
     * 过滤 XSS
     *
     * @param input 原始输入字符串
     * @return 过滤后的安全字符串
     */
    public static String sanitizeForXSS(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String escaped = input.replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");
        escaped = StringEscapeUtils.escapeHtml4(escaped);
        escaped = escaped
                .replaceAll("(?i)javascript:", "javascript&#58;")
                .replaceAll("(?i)data:", "data&#58;")
                .replaceAll("(?i)vbscript:", "vbscript&#58;");
        escaped = escaped.replace("\"", "&#34;");
        return escaped;
    }

    /**
     * 消除XSS漏洞
     *
     * @param paramMap 参数
     * @return 参数
     */
    public static Map<String, Object> sanitizeParamMap(Map<String, Object> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return paramMap;
        }

        Map<String, Object> sanitizedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            Object value = entry.getValue();
            sanitizedMap.put(entry.getKey(), sanitizeValue(value));
        }
        return sanitizedMap;
    }

    /**
     * 对值进行XSS过滤处理
     *
     * @param value 原始值
     * @return 过滤后的值
     */
    private static Object sanitizeValue(Object value) {
        if (value instanceof String) {
            return sanitizeForXSS((String) value);
        } else if (value instanceof Map) {
            return sanitizeParamMap((Map<String, Object>) value);
        } else if (value instanceof List) {
            List<Object> sanitizedList = new ArrayList<>();
            for (Object item : (List<?>) value) {
                sanitizedList.add(sanitizeValue(item));
            }
            return sanitizedList;
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            List<Object> sanitizedList = new ArrayList<>();
            for (Object item : array) {
                sanitizedList.add(sanitizeValue(item));
            }
            return sanitizedList.toArray();
        } else {
            return value;
        }
    }

    // === SQL 拼接相关方法 ===

    /**
     * 转义SQL字符串中的单引号
     *
     * @param input 原始字符串
     * @return 转义后的字符串
     */
    private static String escapeSqlString(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("'", "''");
    }

    /**
     * 填充参数
     *
     * @param sql 预编译SQL
     * @param paramMap 参数映射表
     * @return 填充后的SQL
     */
    public static String fillParameters(String sql, Map<String, Object> paramMap) {
        if (sql == null || sql.isEmpty() || paramMap == null || paramMap.isEmpty()) {
            return sql;
        }

        paramMap = sanitizeParamMap(paramMap);

        StringBuffer result = new StringBuffer();
        Matcher matcher = PARAM_PATTERN.matcher(sql);

        while (matcher.find()) {
            String paramName = matcher.group(1);
            Object value = resolveValue(paramMap, paramName);

            String replacement;
            if (value == null) {
                replacement = "NULL";
            } else if (isArrayOrCollection(value)) {
                replacement = formatArrayOrCollection(value);
            } else if (value instanceof String) {
                replacement = "'" + escapeSqlString((String) value) + "'";
            } else if (value instanceof Number || value instanceof Boolean) {
                replacement = value.toString();
            } else if (value instanceof Date) {
                Timestamp timestamp = new Timestamp(((Date) value).getTime());
                replacement = "'" + timestamp.toString() + "'";
            } else if (value instanceof Enum) {
                replacement = "'" + escapeSqlString(((Enum<?>) value).name()) + "'";
            } else if (value instanceof BigDecimal) {
                replacement = value.toString();
            } else {
                replacement = "'" + escapeSqlString(value.toString()) + "'";
            }

            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 解析参数值，支持嵌套属性访问（如 user.name）
     *
     * @param paramMap 参数映射
     * @param key 参数键名，支持点号分隔的嵌套属性
     * @return 解析后的参数值
     */
    private static Object resolveValue(Map<String, Object> paramMap, String key) {
        if (key.contains(".")) {
            String[] parts = key.split("\\.", 2);
            Object obj = paramMap.get(parts[0]);
            if (obj instanceof Map) {
                return ((Map<?, ?>) obj).get(parts[1]);
            } else if (obj != null) {
                try {
                    return ReflectUtil.getFieldValue(obj, parts[1]);
                } catch (Exception e) {
                    throw new RuntimeException("无法获取属性: " + key, e);
                }
            }
        } else {
            return paramMap.get(key);
        }
        return null;
    }

    // === 数组和集合处理方法 ===

    /**
     * 判断对象是否为数组或集合类型
     *
     * @param obj 待判断的对象
     * @return 如果是数组或集合类型返回true，否则返回false
     */
    private static boolean isArrayOrCollection(Object obj) {
        return obj != null && (obj.getClass().isArray() || obj instanceof Collection);
    }

    /**
     * 获取数组或集合的大小
     *
     * @param obj 数组或集合对象
     * @return 对象包含的元素数量
     */
    private static int getCollectionSize(Object obj) {
        if (obj == null) return 0;
        if (obj.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(obj);
        } else if (obj instanceof Collection) {
            return ((Collection<?>) obj).size();
        }
        return 0;
    }

    /**
     * 将数组或集合展开为列表
     *
     * @param obj 数组或集合对象
     * @return 包含所有元素的列表
     */
    private static List<Object> flattenArrayOrCollection(Object obj) {
        List<Object> result = new ArrayList<>();
        if (obj == null) return result;

        if (obj.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                result.add(java.lang.reflect.Array.get(obj, i));
            }
        } else if (obj instanceof Collection) {
            result.addAll((Collection<?>) obj);
        }
        return result;
    }

    /**
     * 格式化数组或集合作为SQL中的IN子句参数
     *
     * @param obj 数组或集合对象
     * @return 格式化后的字符串，如 ('a','b','c')
     */
    private static String formatArrayOrCollection(Object obj) {
        if (obj == null) return "NULL";

        List<Object> items = flattenArrayOrCollection(obj);
        if (items.isEmpty()) return "NULL";

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");

            Object item = items.get(i);
            if (item instanceof String) {
                sb.append("'").append(escapeSqlString((String) item)).append("'");
            } else if (item instanceof Number || item instanceof Boolean) {
                sb.append(item.toString());
            } else if (item instanceof Date) {
                Timestamp timestamp = new Timestamp(((Date) item).getTime());
                sb.append("'").append(timestamp.toString()).append("'");
            } else if (item instanceof Enum) {
                sb.append("'").append(escapeSqlString(((Enum<?>) item).name())).append("'");
            } else if (item instanceof BigDecimal) {
                sb.append(item.toString());
            } else {
                sb.append("'").append(escapeSqlString(item.toString())).append("'");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
