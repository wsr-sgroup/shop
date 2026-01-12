package com.project.platform.service;

import com.project.platform.entity.Category;
import com.project.platform.vo.PageVO;

import java.util.List;

public interface CategoryService {
    PageVO<Category> page(String name, String slug, Integer parentId, int pageNum, int pageSize);

    Category getById(Integer id);

    int create(Category category);

    int update(Category category);

    int deleteByIds(List<Integer> ids);
}