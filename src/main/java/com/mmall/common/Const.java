package com.mmall.common;

/**
 * @ClassName:Const
 * @Description 常量类
 * @Author oyj
 * @Date 2018/11/24 16:10
 * @Version 1.0
 **/
public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1; //管理员
    }
}
