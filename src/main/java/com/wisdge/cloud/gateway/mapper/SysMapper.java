package com.wisdge.cloud.gateway.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface SysMapper {

    @Select("select A.RES_NAME, A.RES_CONTENT, group_concat(B.ROLE_ID) as ROLE_IDS from SYS_RES A left join SYS_RES_ROLE B on B.RES_ID=A.ID where RES_TYPE='SERVICE' group by A.RES_CONTENT")
    List<Map<String, String>> resRoles();
}
