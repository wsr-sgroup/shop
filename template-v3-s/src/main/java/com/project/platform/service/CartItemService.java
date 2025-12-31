package com.project.platform.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.platform.dto.CartItemUpdateDTO;
import com.project.platform.entity.Cart;
import com.project.platform.entity.CartItem;
import com.project.platform.mapper.CartItemMapper;
import com.project.platform.mapper.CartMapper;
import com.project.platform.vo.CartItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车项业务层（移除商品库存校验逻辑）
 */
@Service
public class CartItemService extends ServiceImpl<CartItemMapper, CartItem> {

    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private CartMapper cartMapper;
    // 移除 ProductMapper 依赖（无需查询商品库存，可删除该注入）
//    @Autowired
//    private ProductMapper productMapper;

    /**
     * 根据用户ID查询购物车列表（含商品信息，给前端展示）
     */
    public List<CartItemVO> getCartItemVOListByUserId(Integer userId) {
        // 1. 查询用户购物车
        Cart cart = cartMapper.selectByUserId(userId);
        if (cart == null) {
            return null; // 无购物车返回空
        }
        // 2. 查询购物车项（关联商品信息）
        return cartItemMapper.selectCartItemVOByCartId(cart.getId());
    }

    /**
     * 更新购物车商品数量（移除库存校验逻辑）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCartItemQuantity(CartItemUpdateDTO updateDTO) {
        // 1. 校验购物车项是否存在且归属当前用户
        CartItem cartItem = cartItemMapper.selectByIdAndUserId(
                updateDTO.getCartItemId(),
                updateDTO.getUserId()
        );
        if (cartItem == null) {
            return false;
        }

        // 移除：商品库存校验相关逻辑（无需查询商品、无需对比库存）
        // ===== 原库存校验代码已删除 =====
        // Product product = productMapper.selectById(cartItem.getProductId());
        // if (product == null || updateDTO.getQuantity() > product.getProductStock()) {
        //     return false; // 库存不足更新失败
        // }

        // 3. 更新购物车项数量和总价
        cartItem.setQuantity(updateDTO.getQuantity());
        cartItem.setTotalPrice(cartItem.getUnitPrice().multiply(new BigDecimal(updateDTO.getQuantity())));
        boolean itemUpdateSuccess = this.updateById(cartItem);

        // 4. 更新购物车主表的总数量和总金额
        if (itemUpdateSuccess) {
            updateCartTotalInfo(cartItem.getCartId());
        }

        return itemUpdateSuccess;
    }

    /**
     * 删除购物车商品
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeCartItem(Integer cartItemId, Integer userId) {
        // 1. 校验购物车项归属
        CartItem cartItem = cartItemMapper.selectByIdAndUserId(cartItemId, userId);
        if (cartItem == null) {
            return false;
        }
        Integer cartId = cartItem.getCartId();

        // 2. 删除购物车项
        boolean removeSuccess = this.removeById(cartItemId);

        // 3. 更新购物车主表信息
        if (removeSuccess) {
            updateCartTotalInfo(cartId);
        }

        return removeSuccess;
    }

    /**
     * 更新购物车项选中状态
     */
    public boolean updateCartItemSelected(Integer cartItemId, boolean selected) {
        return cartItemMapper.updateSelectedStatus(cartItemId, selected) > 0;
    }

    /**
     * 私有方法：更新购物车主表的总数量、总金额、商品条目数
     */
    private void updateCartTotalInfo(Integer cartId) {
        // 1. 查询该购物车下所有有效项
        List<CartItem> cartItemList = this.lambdaQuery()
                .eq(CartItem::getCartId, cartId)
                .list();

        // 2. 计算总数量、总金额
        int totalQuantity = 0;
        BigDecimal totalAmount = new BigDecimal(0);
        for (CartItem item : cartItemList) {
            totalQuantity += item.getQuantity();
            totalAmount = totalAmount.add(item.getTotalPrice());
        }

        // 3. 更新购物车主表
        Cart cart = cartMapper.selectById(cartId);
        if (cart != null) {
            cart.setItemCount(cartItemList.size());
            cart.setTotalQuantity(totalQuantity);
            cart.setTotalAmount(totalAmount);
            cart.setUpdateTime(LocalDateTime.now());
            cartMapper.updateById(cart);
        }
    }
}