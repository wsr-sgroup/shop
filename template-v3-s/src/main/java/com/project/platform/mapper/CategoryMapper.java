package com.project.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.platform.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {
    List<Category> selectPage(@Param("name") String name, @Param("slug") String slug, @Param("parentId") Integer parentId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    int count(@Param("name") String name, @Param("slug") String slug, @Param("parentId") Integer parentId);

    Category selectBySlug(@Param("slug") String slug);
}