package com.work.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.entity.OrderDetail;
import com.work.mapper.OrderDetailMapper;
import com.work.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}