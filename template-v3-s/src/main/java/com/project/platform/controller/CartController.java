package com.project.platform.controller;

import com.project.platform.dto.CartItemUpdateDTO;
import com.project.platform.vo.CartItemVO;
import com.project.platform.vo.ResultVO;
import com.project.platform.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired; // 导入Spring的@Autowired
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResultVO<List<CartItemVO>> getCartList(@RequestParam Integer userId) {
        List<CartItemVO> cartItemVOList = cartItemService.getCartItemVOListByUserId(userId);
        return ResultVO.ok(cartItemVOList);
    }

    /**
     * 2. 更新购物车商品数量（前端数量修改时调用）
     * 前端调用：PUT /api/cart/updateQuantity
     */
    @PutMapping("/updateQuantity")
    public ResultVO<Boolean> updateQuantity(@RequestBody CartItemUpdateDTO updateDTO) {
        boolean success = cartItemService.updateCartItemQuantity(updateDTO);
        if (success) {
            return ResultVO.ok(true);
        }
        return ResultVO.error("更新失败，库存不足或购物车项不存在");
    }

    /**
     * 3. 删除购物车商品（前端点击删除时调用）
     * 前端调用：DELETE /api/cart/remove?cartItemId=xxx&userId=xxx
     */
    @DeleteMapping("/remove")
    public ResultVO<Boolean> removeCartItem(
            @RequestParam Integer cartItemId,
            @RequestParam Integer userId
    ) {
        boolean success = cartItemService.removeCartItem(cartItemId, userId);
        if (success) {
            return ResultVO.ok(true);
        }
        return ResultVO.error("删除失败");
    }

    /**
     * 4. 更新购物车商品选中状态（前端勾选/取消勾选时调用）
     * 前端调用：PUT /api/cart/updateSelected
     */
    @PutMapping("/updateSelected")
    public ResultVO<Boolean> updateSelected(
            @RequestParam Integer cartItemId,
            @RequestParam boolean selected
    ) {
        boolean success = cartItemService.updateCartItemSelected(cartItemId, selected);
        if (success) {
            return ResultVO.ok(true);
        }
        return ResultVO.error("更新选中状态失败");
    }
}