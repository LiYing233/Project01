package com.work.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.work.common.R;
import com.work.dto.DishDto;
import com.work.entity.Category;
import com.work.entity.Dish;
import com.work.entity.DishFlavor;
import com.work.service.CategoryService;
import com.work.service.DishFlavorService;
import com.work.service.DishService;
import com.work.service.impl.DishFlavorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
//    @Autowired
//    private DishFlavorServiceImpl dishFlavorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;

    //新增菜品
@PostMapping
//传入的除了Dish还有Flavor,因此新建一个数据传输对象DishDto
    private R<String> save(@RequestBody DishDto dishDto){
    dishService.saveWithDishFlavor(dishDto);
    return R.success("新增菜品成功");
}

@GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
    //查询到的categoryId不是菜品分类，需要查询展示
    Page<Dish> pageInfo=new Page(page,pageSize);
    Page<DishDto> dtoPage=new Page<>();
    LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.like(!StringUtils.isEmpty(name),Dish::getName,name);
    queryWrapper.orderByDesc(Dish::getUpdateTime);
    dishService.page(pageInfo,queryWrapper);
//   除了records属性，将第一个对象拷贝到第二个对象，records用来存放查询出来的数据，菜品分类名称不包含在查出来的数据中，因此需要重新添加
    BeanUtils.copyProperties(pageInfo,dtoPage,"records");
    List<Dish> pageInfoRecords = pageInfo.getRecords();
    List<DishDto> dtoRecords=pageInfoRecords.stream().map((item)->{
        Long categoryId = item.getCategoryId();
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(item,dishDto);
        //查询菜品分类名称
        String categoryName = categoryService.getById(categoryId).getName();
        dishDto.setCategoryName(categoryName);
        return dishDto;
    }).collect(Collectors.toList());
    //添加records
    dtoPage.setRecords(dtoRecords);
    return R.success(dtoPage);
}

//根据菜品id查询菜品信息及口味信息
@GetMapping("/{id}")
    public R<DishDto> change(@PathVariable Long id){
    DishDto dishDto=dishService.getDishAndFlavorById(id);
    return R.success(dishDto);
}

@PutMapping
    public R<String> change_save(@RequestBody DishDto dishDto){

    dishService.updateDishAndFlavorsById(dishDto);
    return R.success("修改成功");
}

@GetMapping("/list")
    public R<List<DishDto>> getDish(Dish dish){
    LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
    //添加状态为1的条件
    queryWrapper.eq(Dish::getStatus,1);
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    List<Dish> dishList = dishService.list(queryWrapper);

    List<DishDto> dishDtoList=dishList.stream().map((item)->{
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(item,dishDto);
        Long id = item.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }).collect(Collectors.toList());
    return R.success(dishDtoList);
}

}
