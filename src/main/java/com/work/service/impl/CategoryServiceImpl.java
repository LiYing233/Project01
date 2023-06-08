package com.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.common.CustomException;
import com.work.entity.Category;
import com.work.entity.Dish;
import com.work.entity.Setmeal;
import com.work.mapper.CategoryMapper;
import com.work.service.CategoryService;
import com.work.service.DishService;
import com.work.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    //根据ID删除分类，删除之前要看是否关联菜品或套餐，关联就抛出业务异常
   @Autowired
    private DishService dishService;
    @Autowired
   private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //查询菜品的分类
        //查询Dish实体类对应的表
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据id查
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        //获取查询的id有多少菜品
        int count = dishService.count(dishLambdaQueryWrapper);
        log.info("数量为：{}",count);
        //是否关联菜品
        if (count > 0) {

            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询套餐的分类
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        //是否关联套餐
        if(count1>0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

    //        正常删除分类,super是父类，即使用的是IService实现的removeById
        super.removeById(id);
    }
}
