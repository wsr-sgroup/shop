package com.project.platform.service.impl;

import com.project.platform.entity.Category;
import com.project.platform.mapper.CategoryMapper;
import com.project.platform.service.CategoryService;
import com.project.platform.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageVO<Category> page(String name, String slug, Integer parentId, int pageNum, int pageSize) {
        int offset = (Math.max(pageNum, 1) - 1) * pageSize;
        List<Category> list = categoryMapper.selectPage(name, slug, parentId, offset, pageSize);
        int total = categoryMapper.count(name, slug, parentId);
        PageVO<Category> page = new PageVO<>();
        page.setList(list);
        page.setTotal(total);
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return page;
    }

    @Override
    public Category getById(Integer id) {
        return categoryMapper.selectById(id);
    }

    @Override
    @Transactional
    public int create(Category category) {
        if (category.getSlug() != null && categoryMapper.selectBySlug(category.getSlug()) != null) {
            throw new IllegalArgumentException("Slug 已存在");
        }
        return categoryMapper.insert(category);
    }

    @Override
    @Transactional
    public int update(Category category) {
        if (category.getSlug() != null) {
            Category exist = categoryMapper.selectBySlug(category.getSlug());
            if (exist != null && !exist.getId().equals(category.getId())) {
                throw new IllegalArgumentException("Slug 已存在");
            }
        }
        return categoryMapper.update(category);
    }

    @Override
    @Transactional
    public int deleteByIds(List<Integer> ids) {
        return categoryMapper.deleteByIds(ids);
    }
}
