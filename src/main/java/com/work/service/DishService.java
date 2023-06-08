package com.work.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.work.dto.DishDto;
import com.work.entity.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithDishFlavor(DishDto dishDto);

    public DishDto getDishAndFlavorById(Long id);

    public void updateDishAndFlavorsById(DishDto dishDto);
}
