package com.work.controller;

import com.work.common.R;
import com.work.entity.Orders;
import com.work.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders order){
        orderService.submitOrder(order);
        //用户以及登录，因此不需要传入购物车内容，直接可以通过用户id查
        return R.success("支付成功");
    }
}
