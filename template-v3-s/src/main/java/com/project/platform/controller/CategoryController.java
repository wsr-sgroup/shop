package com.project.platform.controller;

import com.project.platform.entity.Category;
import com.project.platform.service.CategoryService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public ResponseVO<PageVO<Category>> page(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String slug,
            @RequestParam(required = false) Integer parentId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageVO<Category> page = categoryService.page(name, slug, parentId, pageNum, pageSize);
        return ResponseVO.success(page);
    }

    @GetMapping("/{id}")
    public ResponseVO<Category> getById(@PathVariable Integer id) {
        return ResponseVO.success(categoryService.getById(id));
    }

    @PostMapping("/add")
    public ResponseVO<Integer> add(@RequestBody Category category) {
        int r = categoryService.create(category);
        return ResponseVO.success(r);
    }

    @PutMapping("/update")
    public ResponseVO<Integer> update(@RequestBody Category category) {
        int r = categoryService.update(category);
        return ResponseVO.success(r);
    }

    @DeleteMapping("/delBatch")
    public ResponseVO<Integer> deleteBatch(@RequestBody List<Integer> ids) {
        int r = categoryService.deleteByIds(ids);
        return ResponseVO.success(r);
    }
}