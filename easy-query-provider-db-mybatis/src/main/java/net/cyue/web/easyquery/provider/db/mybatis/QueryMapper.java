package net.cyue.web.easyquery.provider.db.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface QueryMapper {
    @Select("${dynamic_sql_for_query}")
    List<Map<String, Object>> dynamicQuery(Map<String, Object> params);
}
