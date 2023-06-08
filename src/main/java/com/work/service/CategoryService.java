package com.work.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.work.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
