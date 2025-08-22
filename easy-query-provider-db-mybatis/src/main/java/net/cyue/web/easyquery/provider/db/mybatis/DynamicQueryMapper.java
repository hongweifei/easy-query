package net.cyue.web.easyquery.provider.db.mybatis;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DynamicQueryMapper {
    List<Map<String, Object>> dynamicQuery(Map<String, Object> params);
}
