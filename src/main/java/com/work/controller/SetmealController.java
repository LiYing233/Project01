package com.work.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.work.common.CustomException;
import com.work.common.R;
import com.work.dto.DishDto;
import com.work.dto.SetmealDto;
import com.work.entity.Setmeal;
import com.work.entity.SetmealDish;
import com.work.service.CategoryService;
import com.work.service.SetmealDishService;
import com.work.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    //新增套餐
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.addSetmeal(setmealDto);

        return R.success("新增套餐成功");
    }

    //获取到套餐信息，但是Setmeal中的是套餐Id，不是套餐名，需要重新获取信息更换为套餐名
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage=new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //pageInfo.getRecords()获取的是Setmeal对象列表，后面复制修改返回的是SetmealDto对象，也就变成了SetmealDto对象列表
        List<SetmealDto> records = pageInfo.getRecords().stream().map((item)->{
            SetmealDto setmealDto =new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            //赋值套餐名
            String name1 = categoryService.getById(categoryId).getName();
            setmealDto.setCategoryName(name1);
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(records);
        return R.success(dtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.deleteWithId(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable Integer status, @RequestParam List<Long> ids){

        setmealService.changeStatusWithIds(ids,status);
        if(status==1)
        return R.success("成功启售");
        else return R.success("成功停售");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }

}
