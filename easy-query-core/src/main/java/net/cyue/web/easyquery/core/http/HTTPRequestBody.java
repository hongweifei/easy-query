package net.cyue.web.easyquery.core.http;


import com.fasterxml.jackson.core.JsonProcessingException;
import net.cyue.util.ReflectUtil;
import net.cyue.util.StreamUtil;
import net.cyue.util.StringUtil;
import net.cyue.web.easyquery.core.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FormDataItem {
    private String contentDisposition = "form-data";
    private String contentType = "text/plain";
    private String name;
    private String filename;
    private byte[] data;

    @Override
    public String toString() {
        return "FormDataItem {\n" +
            "\tcontentDisposition='" + contentDisposition + "',\n" +
            "\tcontentType='" + contentType + "',\n" +
            "\tname='" + name + "',\n" +
            "\tfilename='" + filename + "',\n" +
            "\tdata='" + new String(data, StandardCharsets.ISO_8859_1) + "',\n" +
            '}';
    }

    public void setContentDispositionLine(String contentDispositionLine) {
        this.setContentDisposition(contentDispositionLine);
        String[] kv = contentDispositionLine.split(";");

        // 获取 name
        {
            String name = kv[1].trim().split("=")[1];
            // 去掉引号
            this.name = name.substring(1, name.length() - 1);
        }

        // 获取 filename
        if (kv.length == 3) {
            String filename = kv[2].trim().split("=")[1];
            // 去掉引号
            this.filename = filename.substring(1, filename.length() - 1);
        }
    }
    public void setContentDisposition(String contentDisposition) {
        if (contentDisposition.startsWith("Content-Disposition")) {
            contentDisposition = contentDisposition.substring(21).split(";")[0];
        }
        this.contentDisposition = contentDisposition;
    }
    public void setContentType(String contentType) {
        if (contentType.startsWith("Content-Type")) {
            contentType = contentType.substring(14);
        }
        this.contentType = contentType;
    }
    public void setData(byte[] bytes) {
        this.data = bytes;
    }

    public String getContentDisposition() {
        return this.contentDisposition;
    }
    public String getContentType() {
        return this.contentType;
    }
    public String getName() {
        return this.name;
    }
    public String getFilename() {
        return this.filename;
    }
    public byte[] getData() {
        return this.data;
    }
}

/**
 * HTTP请求体
 */
public class HTTPRequestBody extends HashMap<String, Object> {

    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 构造函数
     */
    public HTTPRequestBody() {
        super();
    }

    /**
     * 构造函数
     * @param map 源数据
     */
    public HTTPRequestBody(Map map) {
        super(map);
    }

    /**
     * 将 HTTPRequestBody 转换成对象
     * @param <T> 目标对象类型
     * @param clz 目标对象类
     * @return 目标对象
     */
    public <T> T toObject(Class<T> clz) {
        try {
            T instance = ReflectUtil.createInstance(clz);
            Field[] fields = clz.getDeclaredFields();
            for (final Field f : fields) {
                final Object value = this.get(f.getName());
                if (value == null) {
                    continue;
                }
                ReflectUtil.setFieldValue(instance, f, value);
            }
            return instance;
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
            return null;
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 byte[] 类型的值
     * @param name 键
     * @return byte[]
     */
    public byte[] getBytes(String name) {
        return (byte[]) this.get(name);
    }

    /**
     * 获取 String 类型的值
     * @param name 键
     * @return String
     */
    public String getString(String name) {
        return (String) this.get(name);
    }

    /**
     * 获取 int 类型的值
     * @param name 键
     * @return int
     */
    public int getInt(String name) {
        return (int) this.get(name);
    }

    /**
     * 获取 float 类型的值
     * @param name 键
     * @return float
     */
    public float getFloat(String name) {
        return (float) this.get(name);
    }

    /**
     * 获取 double 类型的值
     * @param name 键
     * @return double
     */
    public double getDouble(String name) {
        return (double) this.get(name);
    }

    /**
     * 从 JSON 字符串创建 HTTPRequestBody
     * @param json JSON 字符串
     * @return HTTPRequestBody
     * @throws JsonProcessingException 抛出 JsonProcessingException
     */
    public static HTTPRequestBody fromJSONString(String json)
        throws JsonProcessingException
    {
        if (StringUtil.isBlank(json)) {
            return new HTTPRequestBody();
        }
        final Map map = JSONUtil.parseObject(json, Map.class);
        return new HTTPRequestBody(map);
    }

    /**
     * 从 query string 创建 HTTPRequestBody
     * @param queryString query string
     * @return HTTPRequestBody
     */
    public static HTTPRequestBody fromQueryString(String queryString) {
        if (StringUtil.isBlank(queryString)) {
            return new HTTPRequestBody();
        }
        final HTTPRequestBody body = new HTTPRequestBody();
        final String[] params = queryString.split("&");
        for (final String p : params) {
            final String[] kv = p.split("=");
            if (kv.length != 2) {
                continue;
            }
            final String k = kv[0];
            final String v = kv[1];
            body.put(k, v);
        }
        return body;
    }

    /**
     * 从 FormData 创建 HTTPRequestBody
     * @param boundary boundary
     * @param input 输入流
     * @return HTTPRequestBody
     * @throws IOException 抛出 IOException
     */
    public static HTTPRequestBody fromFormData(
        String boundary,
        InputStream input
    ) throws IOException {
        String formData = StreamUtil.readAsString(input, StandardCharsets.ISO_8859_1);
        final String startString = "--" + boundary;
        final String endString = startString + "--";
        final String lf = "\r\n";
        final List<FormDataItem> dataItemList = new ArrayList<>();

        // System.out.println(formData);
        while (!formData.startsWith(endString)) {
            // ------WebKitFormBoundary6zSUvTdS6WkabSBL
            // Content-Disposition: form-data; name="data"
            //
            // data
            // ------WebKitFormBoundary6zSUvTdS6WkabSBL--

            FormDataItem item = new FormDataItem();

            // 去掉 ------WebKitFormBoundary6zSUvTdS6WkabSBL
            int contentDispositionStartIndex = formData.indexOf(lf) + lf.length();
            formData = formData.substring(contentDispositionStartIndex);
            // 获取 Content-Disposition
            int contentDispositionLineEndIndex = formData.indexOf(lf);
            String contentDispositionLine = formData.substring(0, contentDispositionLineEndIndex);
            formData = formData.substring(contentDispositionLineEndIndex + lf.length());
            byte[] contentDispositionBytes = contentDispositionLine.getBytes(StandardCharsets.ISO_8859_1);
            String contentDispositionLineUTF8 = new String(contentDispositionBytes, StandardCharsets.UTF_8);
            item.setContentDispositionLine(contentDispositionLineUTF8);

            // 获取Content-Type
            if (formData.startsWith("Content-Type:")) {
                int contentTypeEndIndex = formData.indexOf(lf);
                String contentTypeLine = formData.substring(0, contentTypeEndIndex);
                formData = formData.substring(contentTypeEndIndex + lf.length() * 2);
                item.setContentType(contentTypeLine);
            } else {
                formData = formData.substring(lf.length());
            }

            // 获取 data
            int dataEndIndex = formData.indexOf(lf + startString);
            String data = formData.substring(0, dataEndIndex);
            formData = formData.substring(dataEndIndex + lf.length());
            item.setData(data.getBytes(StandardCharsets.ISO_8859_1));

            dataItemList.add(item);
        }

        final HTTPRequestBody body = new HTTPRequestBody();
        for (final FormDataItem item : dataItemList) {
            // System.out.println(item);
            body.put(item.getName(), item.getData());
        }
        return body;
    }
}
