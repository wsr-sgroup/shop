package com.project.platform.service.impl;

import com.project.platform.entity.Product;
import com.project.platform.mapper.ProductMapper;
import com.project.platform.service.ProductService;
import com.project.platform.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageVO<Product> page(String name, String sku, String series, Integer isOnSale, int pageNum, int pageSize) {
        int offset = (Math.max(pageNum, 1) - 1) * pageSize;
        List<Product> list = productMapper.selectPage(name, sku, series, isOnSale, offset, pageSize);
        int total = productMapper.count(name, sku, series, isOnSale);
        PageVO<Product> page = new PageVO<>();
        page.setList(list);
        page.setTotal(total);
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return page;
    }

    @Override
    public Product getById(Integer id) {
        return productMapper.selectById(id);
    }

    @Override
    @Transactional
    public int create(Product product) {
        if (product.getSku() != null && productMapper.selectBySku(product.getSku()) != null) {
            throw new IllegalArgumentException("SKU 已存在");
        }
        return productMapper.insert(product);
    }

    @Override
    @Transactional
    public int update(Product product) {
        if (product.getSku() != null) {
            Product exist = productMapper.selectBySku(product.getSku());
            if (exist != null && !exist.getId().equals(product.getId())) {
                throw new IllegalArgumentException("SKU 已存在");
            }
        }
        return productMapper.update(product);
    }

    @Override
    @Transactional
    public int deleteByIds(List<Integer> ids) {
        return productMapper.deleteByIds(ids);
    }
}