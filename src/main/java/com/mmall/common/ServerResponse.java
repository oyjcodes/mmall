package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * @ClassName:ServerResponse
 * @Description 状态记录类
 * @Author oyj
 * @Date 2018/11/23 16:48
 * @Version 1.0
 **/
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
//保证json序列化的时候,如果此时有null的对象，key也会消失
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;//消息
    private T data;//返回的数据
    private ServerResponse(int status){
        this.status = status;
    }
    public ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }
    public ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    //使之不在json序列化中
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }
    public int getStatus(){
        return status;
    }
    public String getMsg(){
        return msg;
    }
    public T getData(){
        return data;
    }
    //创建一个成功的服务器响应
    public static<T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    //创建一个成功的服务器响应，并且把msg填充进去
    public static<T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    //创建一个成功的服务器响应，并且把data填充进去
    public static<T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    //创建一个成功的服务器响应，并且把msg和data填充进去
    public static<T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    //error
    public static<T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public static<T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    public static<T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }
}
