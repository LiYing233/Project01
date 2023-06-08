package com.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.dto.DishDto;
import com.work.entity.Dish;
import com.work.entity.DishFlavor;
import com.work.mapper.DishMapper;
import com.work.service.DishFlavorService;
import com.work.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
//操作多张表，需要加入事务控制的注解
@Transactional
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{
    @Autowired
    public DishFlavorService dishFlavorService;
    @Autowired
    public DishService dishService;



    public void saveWithDishFlavor(DishDto dishDto) {
        //dishDto继承于Dish，可以直接传入dishDto，会对应存入
        this.save(dishDto);
        //DishFlavor表中有传入的Json格式flavor没有的字段菜品id，需要另外添加才可存入
        Long id = dishDto.getId();
        //获取口味列表
        List<DishFlavor> flavorList = dishDto.getFlavors();
        //使用流设置每个flavor的id并变回list
        flavorList=flavorList.stream().map((item)->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        //批量存储
        dishFlavorService.saveBatch(flavorList);
//      方法二
//        for(DishFlavor dishFlavor:flavorList) {
//            dishFlavor.setDishId(id);
//            dishFlavorService.save(dishFlavor);
//        }
    }


    public DishDto getDishAndFlavorById(Long id) {
        DishDto dishDto=new DishDto();
        Dish dish = dishService.getById(id);
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavorList);
        return dishDto;
    }


    public void updateDishAndFlavorsById(DishDto dishDto) {
        this.updateById(dishDto);
        Long id = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
