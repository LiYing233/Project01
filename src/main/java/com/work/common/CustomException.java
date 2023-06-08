package com.work.common;
//自定义业务异常
public class CustomException extends RuntimeException{
    //传入要提示的异常信息
    public CustomException(String message){
        super(message);
    }
}
