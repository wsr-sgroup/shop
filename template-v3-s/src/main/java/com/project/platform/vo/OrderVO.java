package com.project.platform.vo;

import com.project.platform.entity.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单响应值对象（VO）
 * 用于后端向前端返回订单数据，与 Order 实体类结构相同
 * 封装所有与订单相关的响应信息
 */
@Data
public class OrderVO {
    // ==================== 基本信息 ====================
    /** 订单ID */
    private Integer id;
    /** 订单编号 */
    private String orderNo;
    /** 用户ID */
    private Integer userId;
    /** 收货地址ID */
    private Integer addressId;

    // ==================== 金额信息 ====================
    /** 订单商品总金额 */
    private BigDecimal totalAmount;
    /** 优惠金额 */
    private BigDecimal discountAmount;
    /** 运费 */
    private BigDecimal shippingFee;
    /** 实付金额 */
    private BigDecimal finalAmount;

    // ==================== 支付信息 ====================
    /** 支付方式 */
    private String paymentMethod;
    /** 支付状态 */
    private Integer paymentStatus;
    /** 支付完成时间 */
    private LocalDateTime paymentTime;
    /** 支付交易ID */
    private String paymentTransactionId;

    // ==================== 订单状态 ====================
    /** 订单状态 */
    private String orderStatus;

    // ==================== 物流信息 ====================
    /** 配送方式 */
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
    /** 是否需要发票 */
    private Integer invoiceNeeded;
    /** 发票抬头 */
    private String invoiceTitle;

    // ==================== 时间戳 ====================
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 最后更新时间 */
    private LocalDateTime updatedAt;
    
    // ==================== 订单项列表 ====================
    /** 订单项列表 */
    private List<OrderItem> orderItems;
}
