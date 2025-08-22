package net.cyue.web.easyquery.core.http.data;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP API 路径信息
 * <p>
 * 用于解析和匹配HTTP请求路径，支持路径参数和查询参数的提取。
 * 支持必需参数（如 {id}）和可选参数（如 {optional?}）。
 * </p>
 */
public class PathInfo {

    // 用于匹配花括号参数的正则表达式
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{([^}]+)}");

    // 用于匹配可选参数的正则表达式 {param?}
    private static final Pattern OPTIONAL_PARAM_PATTERN = Pattern.compile("\\{([^}]+)\\?}");

    private String contextPath;
    private final String apiPath;
    private final List<String> pathParameterList;
    private final List<String> optionalPathParameterList; // 可选路径参数
    private final List<String> queryParameterList;
    private final Pattern pathPattern;
    private final boolean hasOptionalParams; // 是否包含可选参数

    /**
     * 创建一个 PathInfo 对象
     * @param contextPath 上下文路径
     * @param apiPath API 路径
     * @param pathParameterList 路径参数列表
     * @param optionalPathParameterList 可选路径参数列表
     * @param queryParameterList 查询参数列表
     * @param pathPattern 路径模式
     * @param hasOptionalParams 是否包含可选参数
     */
    private PathInfo(
        String contextPath,
        String apiPath,
        List<String> pathParameterList,
        List<String> optionalPathParameterList,
        List<String> queryParameterList,
        Pattern pathPattern,
        boolean hasOptionalParams
    ) {
        this.contextPath = contextPath == null ? "" : contextPath;
        this.apiPath = apiPath;
        this.pathParameterList = pathParameterList == null ? new ArrayList<>() : pathParameterList;
        this.optionalPathParameterList = optionalPathParameterList == null ? new ArrayList<>() : optionalPathParameterList;
        this.queryParameterList = queryParameterList == null ? new ArrayList<>() : queryParameterList;
        this.pathPattern = pathPattern;
        this.hasOptionalParams = hasOptionalParams;
    }

    /**
     * 从API路径创建一个 PathInfo 对象
     *
     * @param apiPath API 路径
     * @return PathInfo 实例
     */
    public static PathInfo fromAPIPath(String apiPath) {
        return fromAPIPath(apiPath, ""); // 默认上下文路径为空
    }

    /**
     * 从API路径和上下文路径创建一个 PathInfo 对象
     *
     * @param apiPath API 路径
     * @param contextPath 上下文路径
     * @return PathInfo 实例
     */
    public static PathInfo fromAPIPath(
        String apiPath,
        String contextPath
    ) {
        return fromAPIPath(apiPath, contextPath, new ArrayList<>());
    }

    /**
     * 从API路径、上下文路径和查询参数列表创建一个 PathInfo 对象
     *
     * @param apiPath API 路径
     * @param contextPath 上下文路径
     * @param queryParameterList 查询参数列表
     * @return PathInfo 实例
     */
    public static PathInfo fromAPIPath(
        String apiPath,
        String contextPath,
        List<String> queryParameterList
    ) {
        if (apiPath == null) {
            throw new IllegalArgumentException("API path cannot be null");
        }

        String[] parts = apiPath.split("/");
        List<String> pathParameterList = new ArrayList<>();
        List<String> optionalPathParameterList = new ArrayList<>();
        StringBuilder regexBuilder = new StringBuilder("^");

        boolean hasOptional = false;

        for (String part : parts) {
            if (part.isEmpty()) continue;

            // 检查是否为可选参数 {param?}
            Matcher optionalMatcher = OPTIONAL_PARAM_PATTERN.matcher(part);
            if (optionalMatcher.find()) {
                String paramName = optionalMatcher.group(1);
                optionalPathParameterList.add(paramName);
                regexBuilder.append("(/([^/]+))?");
                hasOptional = true;
                continue;
            }

            // 检查当前部分是否包含必需参数
            Matcher matcher = PARAM_PATTERN.matcher(part);
            if (matcher.find()) {
                String paramName = matcher.group(1);
                pathParameterList.add(paramName);
                regexBuilder.append("/([^/]+)");
            } else {
                regexBuilder.append("/").append(Pattern.quote(part));
            }
        }

        // 允许路径以斜杠结尾或不结尾
        if (hasOptional) {
            regexBuilder.append("/?");
        } else {
            regexBuilder.append("/?$");
        }

        Pattern pathPattern = Pattern.compile(regexBuilder.toString());

        if (!apiPath.startsWith("/")) {
            apiPath = "/" + apiPath;
        }

        return new PathInfo(
            contextPath,
            apiPath,
            pathParameterList,
            optionalPathParameterList,
            queryParameterList,
            pathPattern,
            hasOptional
        );
    }

    /**
     * 设置上下文路径
     *
     * @param contextPath 上下文路径
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * 获取API路径
     *
     * @return API路径
     */
    public String getAPIPath() {
        return apiPath;
    }

    /**
     * 获取路径参数列表
     *
     * @return 路径参数列表
     */
    public List<String> getPathParameterList() {
        return new ArrayList<>(pathParameterList);
    }

    /**
     * 获取可选路径参数列表
     *
     * @return 可选路径参数列表
     */
    public List<String> getOptionalPathParameterList() {
        return new ArrayList<>(optionalPathParameterList);
    }

    /**
     * 获取查询参数列表
     *
     * @return 查询参数列表
     */
    public List<String> getQueryParameterList() {
        return new ArrayList<>(queryParameterList);
    }

    /**
     * 判断路径是否包含可选参数
     *
     * @return 是否包含可选参数
     */
    public boolean hasOptionalParameters() {
        return hasOptionalParams;
    }

    /**
     * 检查给定路径是否匹配当前API路径模式
     *
     * @param fullPath 完整路径
     * @return 是否匹配
     */
    public boolean matches(String fullPath) {
        if (fullPath == null) return false;

        String path = normalizePath(fullPath);
        return pathPattern.matcher(path).matches();
    }

    /**
     * 从路径中提取路径参数（包括可选参数）
     *
     * @param fullPath 完整资源路径，如 /api/users/abc
     * @return 路径参数映射
     */
    public Map<String, Object> parsePathParameterMap(String fullPath) {
        String path = normalizePath(fullPath);
        Map<String, Object> pathParameterMap = new HashMap<>();
        Matcher matcher = pathPattern.matcher(path);

        if (matcher.matches()) {
            int groupIndex = 1;

            // 处理必需参数
            for (String parameterName : pathParameterList) {
                if (groupIndex <= matcher.groupCount()) {
                    String parameterValue = matcher.group(groupIndex++);
                    pathParameterMap.put(parameterName, parameterValue);
                }
            }

            // 处理可选参数
            for (String parameterName : optionalPathParameterList) {
                if (groupIndex <= matcher.groupCount()) {
                    String parameterValue = matcher.group(groupIndex++);
                    if (parameterValue != null) {
                        pathParameterMap.put(parameterName, parameterValue);
                    }
                }
            }
        }

        return pathParameterMap;
    }

    /**
     * 从路径中提取查询参数
     *
     * @param fullPath 完整资源路径，如：<code>/api/users?page=1&amp;size=10</code>
     * @return 查询参数映射
     */
    public Map<String, Object> parseQueryParameterMap(String fullPath) {
        Map<String, Object> queryParamMap = new HashMap<>();

        if (fullPath == null) {
            return queryParamMap;
        }

        // 查找 '?' 的位置
        int queryIndex = fullPath.indexOf('?');
        if (queryIndex == -1) {
            return queryParamMap; // 没有查询参数
        }

        String queryString = fullPath.substring(queryIndex + 1);

        // 拆分键值对
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;

            int eqIndex = pair.indexOf('=');
            String key;
            String value;

            if (eqIndex != -1) {
                key = decodeURIComponent(pair.substring(0, eqIndex));
                value = decodeURIComponent(pair.substring(eqIndex + 1));
            } else {
                key = decodeURIComponent(pair);
                value = "";
            }

            // 如果 key 已存在，则转为 List 处理多值情况
            if (queryParamMap.containsKey(key)) {
                Object existing = queryParamMap.get(key);
                if (existing instanceof List) {
                    ((List<String>) existing).add(value);
                } else {
                    List<String> list = new ArrayList<>();
                    list.add((String) existing);
                    list.add(value);
                    queryParamMap.put(key, list);
                }
            } else {
                queryParamMap.put(key, value);
            }
        }

        return queryParamMap;
    }

    /**
     * 同时提取路径参数和查询参数
     *
     * @param fullPath 完整资源路径，如：<code>/api/users/status/online?page=1&amp;size=10</code>
     * @return 包含所有参数的映射
     */
    public Map<String, Object> parseAllParameters(String fullPath) {
        Map<String, Object> allParams = new HashMap<>();

        // 合并路径参数
        allParams.putAll(this.parsePathParameterMap(fullPath));

        // 合并查询参数
        allParams.putAll(this.parseQueryParameterMap(fullPath));

        return allParams;
    }

    /**
     * 标准化路径，移除上下文路径前缀并确保以斜杠开头
     *
     * @param fullPath 完整路径
     * @return 标准化后的路径
     */
    private String normalizePath(String fullPath) {
        if (fullPath == null) return "";

        // 移除上下文路径前缀
        String path = fullPath.startsWith(contextPath)
                ? fullPath.substring(contextPath.length())
                : fullPath;

        // 确保路径以斜杠开头
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        // 移除查询字符串部分（如果有的话）
        int queryIndex = path.indexOf('?');
        if (queryIndex != -1) {
            path = path.substring(0, queryIndex);
        }

        return path;
    }

    /**
     * URL 解码
     *
     * @param encoded 编码字符串
     * @return 解码后的字符串
     */
    private String decodeURIComponent(String encoded) {
        try {
            return URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return encoded; // fallback
        }
    }

    /**
     * 获取上下文路径
     *
     * @return 上下文路径
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * 获取完整的正则表达式模式
     *
     * @return 正则表达式模式
     */
    public Pattern getPathPattern() {
        return pathPattern;
    }

    @Override
    public String toString() {
        return "PathInfo{" +
            "contextPath='" + contextPath + '\'' +
            ", apiPath='" + apiPath + '\'' +
            ", pathParameterList=" + pathParameterList +
            ", optionalPathParameterList=" + optionalPathParameterList +
            ", hasOptionalParams=" + hasOptionalParams +
            '}';
    }

    /**
     * 构建器模式支持
     */
    public static class Builder {
        private String contextPath = "";
        private String apiPath;
        private List<String> queryParameterList = new ArrayList<>();

        /**
         * 设置apiPath
         *
         * @param apiPath apiPath
         * @return Builder
         */
        public Builder apiPath(String apiPath) {
            this.apiPath = apiPath;
            return this;
        }

        /**
         * 设置contextPath
         *
         * @param contextPath contextPath
         * @return Builder
         */
        public Builder contextPath(String contextPath) {
            this.contextPath = contextPath;
            return this;
        }

        /**
         * 设置queryParameters
         *
         * @param queryParameters queryParameters
         * @return Builder
         */
        public Builder queryParameters(List<String> queryParameters) {
            this.queryParameterList = queryParameters;
            return this;
        }

        /**
         * 添加查询参数
         *
         * @param param 参数名
         * @return Builder
         */
        public Builder addQueryParameter(String param) {
            if (this.queryParameterList == null) {
                this.queryParameterList = new ArrayList<>();
            }
            this.queryParameterList.add(param);
            return this;
        }

        /**
         * 构建PathInfo实例
         *
         * @return PathInfo
         */
        public PathInfo build() {
            if (apiPath == null) {
                throw new IllegalStateException("API path must be set");
            }
            return PathInfo.fromAPIPath(apiPath, contextPath, queryParameterList);
        }
    }

    /**
     * 创建PathInfo构建器
     *
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }
}
