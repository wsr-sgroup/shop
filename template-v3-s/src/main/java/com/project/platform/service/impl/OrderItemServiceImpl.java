package com.project.platform.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.platform.entity.OrderItem;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.OrderItemMapper;
import com.project.platform.service.OrderItemService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单项业务服务实现类
 * 实现订单项的 CRUD 操作和业务逻辑
 */
@Service
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {
    @Resource
    private OrderItemMapper orderItemMapper;

    /**
     * 根据ID查询单个订单项
     */
    @Override
    public OrderItem selectById(Integer id) {
        OrderItem orderItem = orderItemMapper.selectById(id);
        if (orderItem == null) {
            throw new CustomException("订单项不存在");
        }
        return orderItem;
    }

    /**
     * 根据订单ID查询所有订单项
     */
    @Override
    public List<OrderItem> selectByOrderId(Integer orderId) {
        if (orderId == null) {
            throw new CustomException("订单ID不能为空");
        }
        return orderItemMapper.selectByOrderId(orderId);
    }

    /**
     * 根据订单编号查询所有订单项
     */
    @Override
    public List<OrderItem> selectByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new CustomException("订单编号不能为空");
        }
        return orderItemMapper.selectByOrderNo(orderNo);
    }

    /**
     * 创建单个订单项
     */
    @Override
    @Transactional
    public OrderItem createOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            throw new CustomException("订单项信息不能为空");
        }

        // 自动计算商品总价
        if (orderItem.getUnitPrice() != null && orderItem.getQuantity() != null) {
            orderItem.setTotalPrice(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQuantity())));
        }

        // 设置时间信息
        LocalDateTime now = LocalDateTime.now();
        orderItem.setCreatedAt(now);

        // 插入订单项
        int rows = orderItemMapper.insert(orderItem);
        if (rows != 1) {
            throw new CustomException("创建订单项失败");
        }

        return orderItem;
    }

    /**
     * 批量创建订单项
     */
    @Override
    @Transactional
    public int batchCreateOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new CustomException("订单项列表不能为空");
        }

        // 设置时间信息并计算商品总价
        LocalDateTime now = LocalDateTime.now();
        for (OrderItem orderItem : orderItems) {
            orderItem.setCreatedAt(now);
            
            // 确保商品规格是有效的JSON字符串
            String productSpec = orderItem.getProductSpecifications();
            // 强制使用空JSON对象，避免任何可能的无效JSON
            productSpec = "{}";
            orderItem.setProductSpecifications(productSpec);
            
            // 自动计算商品总价
            if (orderItem.getUnitPrice() != null && orderItem.getQuantity() != null) {
                orderItem.setTotalPrice(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQuantity())));
            }
        }

        // 批量插入订单项
        int rows = orderItemMapper.batchInsert(orderItems);
        if (rows != orderItems.size()) {
            throw new CustomException("批量创建订单项失败");
        }

        return rows;
    }

    /**
     * 更新订单项信息
     */
    @Override
    @Transactional
    public void updateOrderItem(OrderItem orderItem) {
        if (orderItem == null || orderItem.getId() == null) {
            throw new CustomException("订单项ID不能为空");
        }

        // 检查订单项是否存在
        OrderItem existingItem = orderItemMapper.selectById(orderItem.getId());
        if (existingItem == null) {
            throw new CustomException("订单项不存在");
        }

        // 更新方法不再设置更新时间，因为数据库中没有对应的字段

        // 更新订单项
        int rows = orderItemMapper.updateById(orderItem);
        if (rows != 1) {
            throw new CustomException("更新订单项失败");
        }
    }

    /**
     * 根据订单ID删除所有订单项
     */
    @Override
    @Transactional
    public void deleteByOrderId(Integer orderId) {
        if (orderId == null) {
            throw new CustomException("订单ID不能为空");
        }

        orderItemMapper.deleteByOrderId(orderId);
    }

    /**
     * 根据订单编号删除所有订单项
     */
    @Override
    @Transactional
    public void deleteByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new CustomException("订单编号不能为空");
        }

        orderItemMapper.deleteByOrderNo(orderNo);
    }
}