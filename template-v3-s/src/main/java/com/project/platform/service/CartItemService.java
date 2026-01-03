package com.project.platform.service;

import com.project.platform.dto.CartItemUpdateDTO;
import com.project.platform.vo.CartItemVO;

import java.util.List;

/**
 * 购物车项服务接口
 */
public interface CartItemService {
    /**
     * 根据用户ID查询购物车列表（含商品信息，给前端展示）
     */
    List<CartItemVO> getCartItemVOListByUserId(Integer userId);

    /**
     * 更新购物车商品数量（移除库存校验逻辑）
     */
    boolean updateCartItemQuantity(CartItemUpdateDTO updateDTO);

    /**
     * 删除购物车商品
     */
    boolean removeCartItem(Integer cartItemId, Integer userId);

    /**
     * 更新购物车项选中状态
     */
    boolean updateCartItemSelected(Integer cartItemId, boolean selected);

    /**
     * 添加商品到购物车
     */
    boolean addCartItem(Integer userId, Integer productId, Integer quantity);
}