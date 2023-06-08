package com.work.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.work.entity.Orders;

public interface OrderService extends IService<Orders> {

public void submitOrder(Orders orders);
}
