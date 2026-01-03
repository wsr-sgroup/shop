package com.project.platform.service;

import com.project.platform.entity.Order;
import com.project.platform.dto.CreateOrderDTO;
import com.project.platform.vo.OrderVO;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 订单业务服务接口
 * 定义订单管理的业务逻辑方法（CRUD 和业务操作）
 * 实现类：OrderServiceImpl
 */
public interface OrderService extends CommonService {

    /**
     * 分页查询订单
     * @param query 查询条件（支持 orderNo、userId、orderStatus、paymentStatus 等）
     * @param pageNum 页码（从 1 开始）
     * @param pageSize 每页记录数
     * @return 分页结果，包含订单列表和总数
     */
    PageVO<Order> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    /**
     * 根据订单ID查询订单
     * @param id 订单ID
     * @return 订单对象，若不存在则抛出异常
     */
    Order selectById(Integer id);

    /**
     * 根据订单编号查询订单
     * @param orderNo 订单编号（唯一）
     * @return 订单对象，若不存在则抛出异常
     */
    Order selectByOrderNo(String orderNo);

    /**
     * 查询用户的订单列表
     * @param userId 用户ID
     * @return 订单列表，按创建时间倒序
     */
    List<Order> selectByUserId(Integer userId);

    /**
     * 创建订单
     * 初始化订单的所有基本信息，订单状态为 pending（待发货）
     * @param userId 用户ID（通过登录获取）
     * @param dto 订单创建数据（包含收货地址、支付方式等）
     * @return 新创建的订单对象
     * @throws CustomException 若用户未登录或必填字段缺失
     */
    Order createOrder(Integer userId, CreateOrderDTO dto);

    /**
     * 更新订单信息
     * 用于更新订单的各字段信息（如收货地址、备注等）
     * @param entity 订单对象（仅更新非空字段）
     * @throws CustomException 若订单不存在
     */
    void updateById(Order entity);

    /**
     * 删除订单
     * 仅允许删除已取消或已关闭的订单
     * @param id 订单ID
     * @throws CustomException 若订单不存在或状态不允许删除
     */
    void deleteById(Integer id);

    /**
     * 取消订单
     * 仅允许取消未发货的订单（pending 状态）
     * @param orderNo 订单编号
     * @param cancelReason 取消原因
     * @throws CustomException 若订单不存在或不能取消
     */
    void cancelOrder(String orderNo, String cancelReason);

    /**
     * 更新订单状态
     * 按照严格的状态转换规则更新订单状态
     * 状态流程：pending → shipped → received → closed 或 → cancelled
     * @param orderNo 订单编号
     * @param orderStatus 新的订单状态
     * @throws CustomException 若订单不存在或状态转换不合法
     */
    void updateOrderStatus(String orderNo, String orderStatus);

    /**
     * 确认收货
     * 将订单状态从 shipped（已发货）更新为 received（已收货）
     * @param orderNo 订单编号
     * @throws CustomException 若订单不处于 shipped 状态
     */
    void confirmReceipt(String orderNo);

    /**
     * 更新支付信息
     * 支付完成后调用此方法，更新支付状态和交易ID
     * @param orderNo 订单编号
     * @param paymentMethod 支付方式
     * @param transactionId 支付平台返回的交易ID
     * @throws CustomException 若订单不存在或状态不允许支付
     */
    void updatePaymentInfo(String orderNo, String paymentMethod, String transactionId);
}
