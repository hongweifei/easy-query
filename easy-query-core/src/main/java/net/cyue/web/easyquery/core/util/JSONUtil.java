package net.cyue.web.easyquery.core.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON工具类
 */
public final class JSONUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.setVisibility(
            PropertyAccessor.ALL,
            JsonAutoDetect.Visibility.ANY
        );
    }

    /**
     * 将对象转为JSON字符串
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJSONString(Object obj) {
        try {
            return JSONUtil.OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JSON字符串转为对象
     * @param <T> 目标类型
     * @param text JSON字符串
     * @param clz 目标类
     * @return 目标对象
     */
    public static <T> T parseObject(String text, Class<T> clz) {
        try {
            return JSONUtil.OBJECT_MAPPER.readValue(text, clz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

