package com.project.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 购物车主表
 */
@Data
@TableName("carts")
public class Cart {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;//主键
    private Integer userId;//用户id
    private String sessionId;//会话id(未登录用户)
    private Integer itemCount;//商品条目数量
    private Integer totalQuantity;//商品总数量
    private BigDecimal totalAmount;//商品总金额（替换double，避免精度丢失）
    @TableField("created_at")
    private LocalDateTime createTime;//创建时间
    @TableField("updated_at")
    private LocalDateTime updateTime;//更新时间
}