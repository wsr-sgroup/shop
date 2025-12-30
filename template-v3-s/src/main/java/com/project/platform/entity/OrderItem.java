package com.project.platform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体类
 * 对应数据库 order_items 表，记录订单中的商品明细
 */
@Data
public class OrderItem {
    // ==================== 基本信息 ====================
    /** 订单项ID，主键 */
    private Integer id;
    
    /** 订单ID，外键关联 orders 表 */
    private Integer orderId;
    
    /** 订单编号，冗余字段，方便查询 */
    private String orderNo;
    
    /** 商品ID，外键关联 products 表 */
    private Integer productId;
    
    /** 商品名称 */
    private String productName;
    
    /** 商品图片 */
    private String productImage;
    
    /** 商品规格（JSON格式） */
    private String productSpecifications;
    
    /** 购买数量 */
    private Integer quantity;
    
    /** 商品单价 */
    private BigDecimal unitPrice;
    
    /** 商品总价 = unitPrice * quantity */
    private BigDecimal totalPrice;
    
    // ==================== 时间信息 ====================
    /** 创建时间 */
    private LocalDateTime createdAt;
}