package com.project.platform.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 购物车主表
 */
@Data
public class Cart {
    private Integer id;//主键
    private Integer userId;//用户id
    private String sessionId;//会话id(未登录用户)
    private Integer itemCount;//商品条目数量
    private Integer totalQuantity;//商品总数量
    private BigDecimal totalAmount;//商品总金额（替换double，避免精度丢失）
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
}