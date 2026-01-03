package com.project.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 购物车项明细表
 */
@Data
@TableName("cart_items")
public class CartItem {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;//主键
    private Integer cartId;//购物车id
    private Integer productId;//商品id
    private Integer quantity;//商品数量
    private BigDecimal unitPrice;//商品单价
    private boolean selected;//是否选中
    @TableField("created_at")
    private LocalDateTime createTime;//创建时间
    @TableField("updated_at")
    private LocalDateTime updateTime;//更新时间
}