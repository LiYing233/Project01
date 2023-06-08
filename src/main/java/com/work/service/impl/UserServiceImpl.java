package com.work.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.entity.User;
import com.work.mapper.UserMapper;
import com.work.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
