package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 订单实体类
 * 对应数据库 orders 表，记录订单的完整信息
 */
@Data
public class Order {
    // ==================== 基本信息 ====================
    /** 订单ID，主键 */
    private Integer id;
    
    /** 订单编号，面向用户的唯一编号（格式：ORD+时间戳+UUID） */
    private String orderNo;
    
    /** 用户ID，外键关联 users 表 */
    private Integer userId;
    
    /** 收货地址ID，外键关联 user_addresses 表 */
    private Integer addressId;

    // ==================== 金额信息 ====================
    /** 订单商品总金额 */
    private BigDecimal totalAmount;
    
    /** 优惠金额 */
    private BigDecimal discountAmount;
    
    /** 运费 */
    private BigDecimal shippingFee;
    
    /** 实付金额 = totalAmount - discountAmount + shippingFee */
    private BigDecimal finalAmount;

    // ==================== 支付信息 ====================
    /** 支付方式（alipay、wechat、bank 等） */
    private String paymentMethod;
    
    /** 支付状态：0=待支付，1=已支付，2=已退款 */
    private Integer paymentStatus;
    
    /** 支付完成时间 */
    private LocalDateTime paymentTime;
    
    /** 支付平台交易号（第三方支付返回的交易ID） */
    private String paymentTransactionId;

    // ==================== 订单状态 ====================
    /** 订单状态：pending(待发货) → shipped(已发货) → received(已收货) → closed(已关闭)
     *  或任何阶段可转为 cancelled(已取消) */
    private String orderStatus;

    // ==================== 物流信息 ====================
    /** 配送方式（顺丰、申通、圆通 等） */
    private String shippingMethod;
    
    /** 物流单号 */
    private String shippingTrackingNo;
    
    /** 发货时间 */
    private LocalDateTime shippedTime;
    
    /** 收货时间 */
    private LocalDateTime receivedTime;

    // ==================== 订单备注 ====================
    /** 订单关闭时间 */
    private LocalDateTime closedTime;
    
    /** 取消原因 */
    private String cancelReason;
    
    /** 买家留言 */
    private String buyerNote;
    
    /** 管理员备注 */
    private String adminNote;

    // ==================== 发票信息 ====================
    /** 是否需要发票：0=不需要，1=需要 */
    private Integer invoiceNeeded;
    
    /** 发票抬头 */
    private String invoiceTitle;

    // ==================== 时间戳 ====================
    /** 订单创建时间 */
    private LocalDateTime createdAt;
    
    /** 订单最后更新时间 */
    private LocalDateTime updatedAt;
}
