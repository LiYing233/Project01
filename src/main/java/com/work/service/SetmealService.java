package com.work.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.work.dto.SetmealDto;
import com.work.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void addSetmeal(SetmealDto setmealDto);

    public void deleteWithId(List<Long> ids);
    public void changeStatusWithIds(List<Long> ids,Integer status);
}
