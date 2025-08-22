package net.cyue.web.easyquery.core.db.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * SQL执行器接口
 */
public interface ISQLExecutor {
    /**
     * 执行查询
     * @param sql 预编译SQL
     * @param params 参数
     * @return 结果
     * @throws SQLException SQL异常
     */
    List<Map<String, Object>> query(String sql, Map<String, Object> params) throws SQLException;
}
