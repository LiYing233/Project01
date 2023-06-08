package com.work.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.work.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
