package com.project.platform.vo;

import com.project.platform.entity.CartItem;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 购物车项展示VO（前端购物车页面专用）
 */
@Data
public class CartItemVO extends CartItem {
    // 商品名称（关联商品表）
    private String productName;
    // 商品图片（关联商品表）
    private String productImage;
    // 商品规格（关联商品表）
    private String productSpecs;
    // 商品库存（关联商品表，用于前端数量限制）
    private Integer productStock;
}