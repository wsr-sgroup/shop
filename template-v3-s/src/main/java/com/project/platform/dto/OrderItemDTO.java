package com.project.platform.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单项数据传输对象（DTO）
 * 用于前端向后端发送创建订单项的请求数据
 */
@Data
public class OrderItemDTO {
    /** 商品ID（必填） */
    private Integer productId;
    
    /** 购买数量（必填） */
    private Integer quantity;
    
    /** 商品单价（可选，后端可以根据商品ID查询） */
    private BigDecimal unitPrice;
    
    /** 商品规格（可选） */
    private String productSpec;
}