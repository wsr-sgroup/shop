package com.project.platform.service;

import com.project.platform.entity.OrderItem;
import java.util.List;

/**
 * 订单项业务服务接口
 * 定义订单项管理的业务逻辑方法
 * 实现类：OrderItemServiceImpl
 */
public interface OrderItemService {
    /**
     * 根据订单项ID查询订单项
     * @param id 订单项ID
     * @return 订单项对象，若不存在则抛出异常
     */
    OrderItem selectById(Integer id);

    /**
     * 根据订单ID查询所有订单项
     * @param orderId 订单ID
     * @return 订单项列表
     */
    List<OrderItem> selectByOrderId(Integer orderId);

    /**
     * 根据订单编号查询所有订单项
     * @param orderNo 订单编号
     * @return 订单项列表
     */
    List<OrderItem> selectByOrderNo(String orderNo);

    /**
     * 创建单个订单项
     * @param orderItem 订单项对象
     * @return 创建后的订单项对象
     */
    OrderItem createOrderItem(OrderItem orderItem);

    /**
     * 批量创建订单项
     * @param orderItems 订单项列表
     * @return 影响行数
     */
    int batchCreateOrderItems(List<OrderItem> orderItems);

    /**
     * 更新订单项信息
     * @param orderItem 订单项对象
     */
    void updateOrderItem(OrderItem orderItem);

    /**
     * 根据订单ID删除所有订单项
     * @param orderId 订单ID
     */
    void deleteByOrderId(Integer orderId);

    /**
     * 根据订单编号删除所有订单项
     * @param orderNo 订单编号
     */
    void deleteByOrderNo(String orderNo);
}