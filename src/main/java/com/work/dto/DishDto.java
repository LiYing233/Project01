package com.work.dto;


import com.work.entity.Dish;
import com.work.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    //除了有Dish类的变量外，还有Json的数组flavors
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
