package com.smart.future.dao.user;

import com.smart.future.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IUserDao {

    void insertUser(@Param("userVO") UserVO userVO);

    List<UserVO> findAll();

    void updateUser(@Param("userVO") UserVO userVO);

    UserVO findById(@Param("id") Long id);

    Long getMaxId();

    UserVO findByPhoneId(@Param("phoneId")Long phoneId);

    void insertUserRoleRelation(@Param("userId") Long userId,@Param("ruleCodes") List<String> ruleCodes);
}
