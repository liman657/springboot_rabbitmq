package com.learn.springbootrabbitmqlearn.mapper;

import com.learn.springbootrabbitmqlearn.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUserNamePassword(@Param("userName") String userName, @Param("password") String password);
}