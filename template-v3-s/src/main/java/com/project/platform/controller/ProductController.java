package com.project.platform.controller;

import com.project.platform.entity.Product;
import com.project.platform.service.ProductService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    public ResponseVO<PageVO<Product>> page(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String series,
            @RequestParam(required = false) Integer isOnSale,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageVO<Product> page = productService.page(name, sku, series, isOnSale, pageNum, pageSize);
        return ResponseVO.success(page);
    }

    @GetMapping("/{id}")
    public ResponseVO<Product> getById(@PathVariable Integer id) {
        return ResponseVO.success(productService.getById(id));
    }

    @PostMapping("/add")
    public ResponseVO<Integer> add(@RequestBody Product product) {
        int r = productService.create(product);
        return ResponseVO.success(r);
    }

    @PutMapping("/update")
    public ResponseVO<Integer> update(@RequestBody Product product) {
        int r = productService.update(product);
        return ResponseVO.success(r);
    }

    @DeleteMapping("/delBatch")
    public ResponseVO<Integer> deleteBatch(@RequestBody List<Integer> ids) {
        int r = productService.deleteByIds(ids);
        return ResponseVO.success(r);
    }
}