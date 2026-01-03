package com.project.platform.dto;

import lombok.Data;

/**
 * 购物车商品添加DTO
 */
@Data
public class CartItemAddDTO {
    private Integer userId;//用户ID
    private Integer productId;//商品ID
    private Integer quantity;//商品数量
}