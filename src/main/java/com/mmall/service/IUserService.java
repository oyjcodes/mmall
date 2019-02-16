package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * @InterfaceName:IUserService
 * @Description 前台用户service接口
 * @Author oyj
 * @Date 2018/11/23 16:21
 * @Version 1.0
 **/
public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse selectQuestion(String username);
    ServerResponse<String> checkAnswer (String username,String question,String answer);
    ServerResponse<String> forgetRestPassword(String username,String passwoedNew,String forgetToken);
    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);
    ServerResponse<User> updateInformation(User user);
    ServerResponse<User> getInformation(int userId);
}
