package com.project.platform.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单查询数据传输对象（DTO）
 * 用于前端向后端发送订单查询/筛选条件
 */
@Data
public class OrderQueryDTO {
    /** 订单编号（模糊查询） */
    private String orderNo;
    
    /** 订单状态：pending、shipped、received、closed、cancelled */
    private String orderStatus;
    
    /** 支付状态：0=待支付，1=已支付，2=已退款 */
    private String paymentStatus;
    
    /** 查询开始日期 */
    private LocalDateTime startDate;
    
    /** 查询结束日期 */
    private LocalDateTime endDate;
}
