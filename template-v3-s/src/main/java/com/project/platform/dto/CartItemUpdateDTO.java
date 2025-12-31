package com.project.platform.dto;

import lombok.Data;

/**
 * 购物车商品数量更新DTO
 */
@Data
public class CartItemUpdateDTO {
    private Integer cartItemId;//购物车项ID
    private Integer quantity;//新数量
    private Integer userId;//用户ID（用于权限校验）
}