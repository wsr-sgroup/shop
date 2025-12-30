package com.project.platform.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单数据传输对象（DTO）
 * 用于前端向后端发送创建订单的请求数据
 */
@Data
public class CreateOrderDTO {
    /** 收货地址ID（必填） */
    private Integer addressId;
    
    /** 支付方式：alipay(支付宝)、wechat(微信)、bank(银行卡) 等（必填） */
    private String paymentMethod;
    
    /** 买家留言（可选） */
    private String buyerNote;
    
    /** 是否需要发票：0=不需要，1=需要（可选） */
    private Integer invoiceNeeded;
    
    /** 发票抬头（可选，需要发票时填写） */
    private String invoiceTitle;
    
    /** 订单项列表（必填） */
    private List<OrderItemDTO> orderItems;
}
