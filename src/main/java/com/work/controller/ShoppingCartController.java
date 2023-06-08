package com.work.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.work.common.BaseContext;
import com.work.common.R;
import com.work.entity.ShoppingCart;
import com.work.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    //添加购物车
    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart){
        //设置用户Id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询用户是否添加过该购物车的食品
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //看添加的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        if (dishId!=null){
            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else {
            //添加的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
            ShoppingCart current = shoppingCartService.getOne(queryWrapper);
            //有该菜品或套餐
            if (current!=null){
                Integer number = current.getNumber();
                current.setNumber(number+1);
                shoppingCartService.updateById(current);
                shoppingCart=current;
            }else{
               //没有该菜品或套餐，添加记录
               shoppingCart.setNumber(1);
               shoppingCart.setCreateTime(LocalDateTime.now());
               shoppingCartService.save(shoppingCart);
            }
        return R.success(shoppingCart);
    }

    //删减购物车
    @PostMapping("/sub")
    public R<ShoppingCart> subShoppingCart(@RequestBody ShoppingCart shoppingCart){
        //设置用户Id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询用户是否添加过该购物车的食品
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //看删除的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        if (dishId!=null){
            //删除的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else {
            //删除的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart current = shoppingCartService.getOne(queryWrapper);
        //有该菜品或套餐,且数量大于1
        if (current.getNumber()>1){
            Integer number = current.getNumber();
            current.setNumber(number-1);
            shoppingCartService.updateById(current);
            shoppingCart=current;
        }else{
            //有该菜品或套餐,且数量=1，删除记录
            shoppingCartService.remove(queryWrapper);
            shoppingCart.setNumber(0);
        }
        return R.success(shoppingCart);
    }

    //查看购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> showShoppingCart(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCartList);
    }

    //清空购物车
    @DeleteMapping("/clean")
    public R<String> deleteShoppingCart(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
