package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //检查用户名是否存在
    int checkUsername(String username);
    //进行登陆
    User selectLogin(@Param("username") String username, @Param("password") String password);
    //检查邮箱是否存在
    int checkEmail(String email);
    //返回用户的问题
    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username,@Param("question")String question,@Param("answer")String answer);

    int updatePasswordByUsername(@Param("username") String username,@Param("passwordNew")String passwordNew);

    int checkPassword(@Param("password")String password,@Param("userId")Integer userId);

    //检查邮箱是否被使用
    int checkEmailByUserId(@Param("email") String email,@Param("userId") int userId);
}