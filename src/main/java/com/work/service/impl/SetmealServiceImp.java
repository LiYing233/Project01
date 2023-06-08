package com.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.common.CustomException;
import com.work.dto.SetmealDto;
import com.work.entity.Setmeal;
import com.work.entity.SetmealDish;
import com.work.mapper.SetmealMapper;
import com.work.service.SetmealDishService;
import com.work.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImp extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    //处理事务，防止有问题回滚
    @Transactional
    public void addSetmeal(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> dishList = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();
        dishList=dishList.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(dishList);

    }

    @Transactional
    public void deleteWithId(List<Long> ids) {
        //查询要删除套餐中的售卖情况
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        //ids是列表有多个值，因此要用in来查询
        queryWrapper.in(Setmeal::getId,ids);

        //如果状态为1说明在售卖不能删除，抛出异常
        queryWrapper.eq(Setmeal::getStatus,1);
        if (this.count(queryWrapper)>0){
            throw new CustomException("有套餐在售卖中，不能删除");
        }
        //删除套餐表，removeByIds是可以根据id列表删除多个记录
        this.removeByIds(ids);

        //删除套餐和菜品的关联表记录
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }

    //status=0停用，1启用
    @Transactional
    public void changeStatusWithIds(List<Long> ids,Integer status) {
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,status==1?0:1);
        List<Setmeal> list = this.list(queryWrapper);
        list.stream().map((item)->{
            Integer status1 = item.getStatus();
            item.setStatus(status1==0?1:0);
            return item;
        }).collect(Collectors.toList());
        this.updateBatchById(list);

    }
}
