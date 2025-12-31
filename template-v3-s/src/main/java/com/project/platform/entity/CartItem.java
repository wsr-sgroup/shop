package com.project.platform.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 购物车项明细表
 */
@Data
public class CartItem {
    private Integer id;//主键
    private Integer cartId;//购物车id
    private Integer productId;//商品id
    private Integer quantity;//商品数量
    private BigDecimal unitPrice;//商品单价
    private BigDecimal totalPrice;//商品总价
    private boolean selected;//是否选中
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
}