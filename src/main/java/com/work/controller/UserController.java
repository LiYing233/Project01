package com.work.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.work.common.R;
import com.work.entity.User;
import com.work.service.UserService;
import com.work.utils.SMSUtils;
import com.work.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession session){
        String phone = user.getPhone();
        if(!StringUtils.isEmpty(phone)){
            //获取4位随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //发送验证码
//            SMSUtils.sendMessage("英雄",,phone,code);
            log.info("code是"+code);
            //存储手机验证码到session中
            session.setAttribute(phone,code);
            return R.success("发送验证码成功");
        }
        else return R.error("发送验证码失败");
    }

    //传来的Json是多个内容，可用Map传
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        //获取传来的手机号与验证码
        String phone = map.get("phone").toString();
        Object code = map.get("code");
        //与生成的验证码比对
        String password = session.getAttribute(phone).toString();
        if(!StringUtils.isEmpty(code)&code.equals(password)){
            //是否为新用户，新用户就注册存入数据库
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user=userService.getOne(queryWrapper);
            if(user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            };
            //登录成功需要将用户ID存入session，否则会被拦截
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
