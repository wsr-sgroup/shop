package com.project.platform.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.platform.entity.Cart;
import com.project.platform.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired; // 替换为Spring原生注解
import org.springframework.stereotype.Service;

import java.math.BigDecimal; // 补充缺失的BigDecimal导入
import java.time.LocalDateTime; // 补充缺失的LocalDateTime导入

/**
 * 购物车主表业务层
 */
@Service
public class CartService extends ServiceImpl<CartMapper, Cart> {

    // 替换 @Resource 为 @Autowired，无需额外依赖
    @Autowired
    private CartMapper cartMapper;

    /**
     * 获取用户购物车（无则创建，简化版）
     */
    public Cart getOrCreateCart(Integer userId, String sessionId) {
        Cart cart = cartMapper.selectByUserId(userId);
        if (cart == null) {
            // 未登录用户按sessionId查询
            if (userId == null && sessionId != null) {
                cart = cartMapper.selectBySessionId(sessionId);
            }
            // 仍无则创建新购物车
            if (cart == null) {
                cart = new Cart();
                cart.setUserId(userId);
                cart.setSessionId(sessionId);
                cart.setItemCount(0);
                cart.setTotalQuantity(0);
                cart.setTotalAmount(new BigDecimal(0));
                cart.setCreateTime(LocalDateTime.now());
                cart.setUpdateTime(LocalDateTime.now());
                cartMapper.insert(cart);
            }
        }
        return cart;
    }
}