package com.work.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.work.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理器，基于AOP代理，（）里表明拦截哪些Controller
@ControllerAdvice(annotations ={RestController.class, Controller.class})
//返回的是Json格式
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    //处理哪些异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split=ex.getMessage().split(" ");
            String s = split[2]+"已存在";
            return R.error(s);
        }
        return R.error("未知错误");
    }

    //处理自定义的异常类
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());
        //获取异常信息
        return R.error(ex.getMessage());
    }


}
