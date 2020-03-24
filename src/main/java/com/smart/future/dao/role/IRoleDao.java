package com.smart.future.dao.role;

import com.smart.future.user.vo.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IRoleDao {

    RoleVO findRoleByCode(@Param("code")String code);
}
