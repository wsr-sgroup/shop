package com.project.platform.mapper;

import com.project.platform.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 订单数据访问接口（Mapper）
 * 定义订单表的数据库操作方法
 * 使用 MyBatis 框架进行 ORM 映射
 */
public interface OrderMapper {
    /**
     * 分页查询订单列表
     * @param offset 分页偏移量 = (pageNum - 1) * pageSize
     * @param pageSize 每页记录数
     * @param query 查询条件 Map（支持 orderNo、userId、orderStatus、paymentStatus 等）
     * @return 订单列表
     */
    List<Order> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    /**
     * 查询符合条件的订单总数
     * @param query 查询条件 Map
     * @return 订单总数
     */
    int queryCount(@Param("query") Map<String, Object> query);

    /**
     * 根据订单ID查询单个订单
     * @param id 订单ID
     * @return 订单对象，不存在返回 null
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order selectById(Integer id);

    /**
     * 根据订单编号查询单个订单
     * @param orderNo 订单编号（唯一）
     * @return 订单对象，不存在返回 null
     */
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Order selectByOrderNo(String orderNo);

    /**
     * 查询指定用户的所有订单
     * @param userId 用户ID
     * @return 订单列表，按创建时间倒序
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> selectByUserId(Integer userId);

    /**
     * 插入新订单
     * @param entity 订单对象（会自动生成 id）
     * @return 影响行数
     */
    int insert(Order entity);

    /**
     * 根据ID更新订单信息
     * @param entity 订单对象（包含要更新的字段）
     * @return 影响行数
     */
    int updateById(Order entity);

    /**
     * 根据ID删除订单
     * @param id 订单ID
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 根据订单编号更新订单状态
     * @param orderNo 订单编号
     * @param orderStatus 新的订单状态
     * @return 影响行数
     */
    int updateOrderStatusByOrderNo(@Param("orderNo") String orderNo, @Param("orderStatus") String orderStatus);
}
