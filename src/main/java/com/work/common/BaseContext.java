package com.work.common;

//基于Thread Local封装工具类，用户保存和获取当前登录用户id,每个线程是单独的副本
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
