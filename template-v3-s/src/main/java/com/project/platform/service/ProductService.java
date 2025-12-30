package com.project.platform.service;

import com.project.platform.entity.Product;
import com.project.platform.vo.PageVO;

import java.util.List;

public interface ProductService {
    PageVO<Product> page(String name, String sku, String series, Integer isOnSale, int pageNum, int pageSize);

    Product getById(Integer id);

    int create(Product product);

    int update(Product product);

    int deleteByIds(List<Integer> ids);
}