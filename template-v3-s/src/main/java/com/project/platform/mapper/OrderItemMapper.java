package com.project.platform.mapper;

import com.project.platform.entity.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单项数据访问接口（Mapper）
 * 定义订单项表的数据库操作方法
 * 使用 MyBatis 框架进行 ORM 映射
 */
public interface OrderItemMapper {
    /**
     * 根据订单项ID查询单个订单项
     * @param id 订单项ID
     * @return 订单项对象，不存在返回 null
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
     * 插入新订单项
     * @param entity 订单项对象（会自动生成 id）
     * @return 影响行数
     */
    int insert(OrderItem entity);

    /**
     * 批量插入订单项
     * @param orderItems 订单项列表
     * @return 影响行数
     */
    int batchInsert(@Param("orderItems") List<OrderItem> orderItems);

    /**
     * 根据ID更新订单项信息
     * @param entity 订单项对象（包含要更新的字段）
     * @return 影响行数
     */
    int updateById(OrderItem entity);

    /**
     * 根据订单ID删除所有订单项
     * @param orderId 订单ID
     * @return 影响行数
     */
    int deleteByOrderId(Integer orderId);

    /**
     * 根据订单编号删除所有订单项
     * @param orderNo 订单编号
     * @return 影响行数
     */
    int deleteByOrderNo(String orderNo);
}