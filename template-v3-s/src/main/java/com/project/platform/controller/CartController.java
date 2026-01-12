package com.project.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // 导入Spring的@Autowired
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.platform.dto.CartItemAddDTO;
import com.project.platform.dto.CartItemUpdateDTO;
import com.project.platform.service.CartItemService;
import com.project.platform.vo.CartItemVO;
import com.project.platform.vo.ResponseVO;

/**
 * 购物车接口控制器（对接前端购物车页面）
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin // 解决跨域问题（生产环境配置全局CORS）
public class CartController {

    // 替换 @Resource 为 @Autowired（推荐，无需额外依赖）
    @Autowired
    private CartItemService cartItemService;

    /**
     * 1. 查询用户购物车列表（前端页面初始化调用）
     * 前端调用：GET /api/cart/list?userId=xxx
     */
    @GetMapping("/list")
    public ResponseVO<List<CartItemVO>> getCartList(@RequestParam Integer userId) {
        try {
            List<CartItemVO> cartItemVOList = cartItemService.getCartItemVOListByUserId(userId);
            return ResponseVO.ok(cartItemVOList);
        } catch (Exception e) {
            return ResponseVO.fail(500, "获取购物车列表失败：" + e.getMessage(), null);
        }
    }

    /**
     * 2. 更新购物车商品数量（前端数量修改时调用）
     * 前端调用：PUT /api/cart/updateQuantity
     */
    @PutMapping("/updateQuantity")
    public ResponseVO<Boolean> updateQuantity(@RequestBody CartItemUpdateDTO updateDTO) {
        boolean success = cartItemService.updateCartItemQuantity(updateDTO);
        if (success) {
            return ResponseVO.ok(success);
        }
        return ResponseVO.fail(500, "更新失败，库存不足或购物车项不存在", null);
    }

    /**
     * 3. 删除购物车商品（前端点击删除时调用）
     * 前端调用：DELETE /api/cart/remove?cartItemId=xxx&userId=xxx
     */
    @DeleteMapping("/remove")
    public ResponseVO<Boolean> removeCartItem(
            @RequestParam Integer cartItemId,
            @RequestParam Integer userId
    ) {
        boolean success = cartItemService.removeCartItem(cartItemId, userId);
        if (success) {
            return ResponseVO.ok(success);
        }
        return ResponseVO.fail(500, "删除失败", null);
    }

    /**
     * 4. 更新购物车商品选中状态（前端勾选/取消勾选时调用）
     * 前端调用：PUT /api/cart/updateSelected
     */
    @PutMapping("/updateSelected")
    public ResponseVO<Boolean> updateSelected(
            @RequestParam Integer cartItemId,
            @RequestParam boolean selected
    ) {
        boolean success = cartItemService.updateCartItemSelected(cartItemId, selected);
        if (success) {
            return ResponseVO.ok(success);
        }
        return ResponseVO.fail(500, "更新选中状态失败", null);
    }

    /**
     * 5. 添加商品到购物车（前端点击添加购物车时调用）
     * 前端调用：POST /api/cart/add
     */
    @PostMapping("/add")
    public ResponseVO<Boolean> addCartItem(@RequestBody CartItemAddDTO addDTO) {
        try {
            boolean success = cartItemService.addCartItem(addDTO.getUserId(), addDTO.getProductId(), addDTO.getQuantity());
            if (success) {
                return ResponseVO.ok(success);
            }
            return ResponseVO.fail(500, "添加失败", null);
        } catch (Exception e) {
            return ResponseVO.fail(500, e.getMessage(), null);
        }
    }
}