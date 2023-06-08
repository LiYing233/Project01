package com.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.work.common.BaseContext;
import com.work.common.CustomException;
import com.work.entity.AddressBook;
import com.work.entity.OrderDetail;
import com.work.entity.Orders;
import com.work.entity.ShoppingCart;
import com.work.mapper.OrderMapper;
import com.work.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private UserService userService;

    //事务，出错回滚
    @Transactional
    public void submitOrder(Orders orders) {
        //获取用户信息
        Long userId = BaseContext.getCurrentId();
        String userName = userService.getById(userId).getName();

        //获取地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        String consignee = addressBook.getConsignee();
        String phone = addressBook.getPhone();
        String address = (addressBook.getCityName()==null?"":addressBook.getCityName())+
                (addressBook.getProvinceName()==null?"":addressBook.getProvinceName())+
                (addressBook.getDistrictName()==null?"":addressBook.getDistrictName())+
                addressBook.getDetail();

        if (address==null){
            throw new CustomException("地址有误，不能下单");
        }

        //生成订单Id
        long orderId = IdWorker.getId();
        //原子操作的Integer类型，线程安全
        AtomicInteger amount=new AtomicInteger();

        //获取购物车信息，并计算总金额
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        if (list.size()==0||list==null){
            throw new CustomException("购物车为空，不能下单");
        }
        List<OrderDetail> orderDetailList=list.stream().map((item)->{
            OrderDetail orderDetail=new OrderDetail();
            String image = item.getImage();
            orderDetail.setImage(image);
            Integer number = item.getNumber();
            orderDetail.setNumber(number);
            BigDecimal amount1 = item.getAmount();
            //计算金额*数量加入总金额
            amount.addAndGet((amount1.multiply(new BigDecimal(number))).intValue());
            orderDetail.setAmount(amount1);
            orderDetail.setDishId(item.getDishId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setOrderId(orderId);

            return orderDetail;
        }).collect(Collectors.toList());


        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setPhone(phone);
        orders.setAddress(address);
        orders.setConsignee(consignee);
        orders.setUserName(userName);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setNumber(String.valueOf(orderId));
        this.save(orders);

        orderDetailService.saveBatch(orderDetailList);

        //清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}