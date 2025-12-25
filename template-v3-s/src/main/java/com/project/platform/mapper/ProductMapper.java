package com.project.platform.mapper;

import com.project.platform.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
        int insert(Product product);

        int update(Product product);

        int deleteById(@Param("id") Integer id);

        int deleteByIds(@Param("ids") List<Integer> ids);

        Product selectById(@Param("id") Integer id);

        Product selectBySku(@Param("sku") String sku);

        List<Product> selectPage(@Param("name") String name,
                        @Param("sku") String sku,
                        @Param("series") String series,
                        @Param("isOnSale") Integer isOnSale,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

        int count(@Param("name") String name,
                        @Param("sku") String sku,
                        @Param("series") String series,
                        @Param("isOnSale") Integer isOnSale);
}