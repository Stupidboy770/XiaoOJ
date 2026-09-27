package org.example.demooj.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.demooj.entity.User;

import java.util.List;

public interface UserMapper {

    Integer addUser(User user);

    //根据userId查询信息
    User getUserById(String userId);

    //查询全部非管理用户
    List<User> getAllUsers();

    //查询全部非管理用户人数
    Integer getInteger();

}
