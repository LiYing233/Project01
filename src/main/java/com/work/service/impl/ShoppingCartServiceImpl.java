package com.work.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.entity.ShoppingCart;
import com.work.mapper.ShoppingCartMapper;
import com.work.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
