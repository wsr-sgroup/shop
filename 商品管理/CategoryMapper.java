package com.project.platform.mapper;

import com.project.platform.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {
        int insert(Category category);

        int update(Category category);

        int deleteById(@Param("id") Integer id);

        int deleteByIds(@Param("ids") List<Integer> ids);

        Category selectById(@Param("id") Integer id);

        Category selectBySlug(@Param("slug") String slug);

        List<Category> selectPage(@Param("name") String name,
                        @Param("slug") String slug,
                        @Param("parentId") Integer parentId,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

        int count(@Param("name") String name,
                        @Param("slug") String slug,
                        @Param("parentId") Integer parentId);
}
