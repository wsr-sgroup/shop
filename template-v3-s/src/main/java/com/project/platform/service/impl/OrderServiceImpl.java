package com.project.platform.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.project.platform.entity.Order;
import com.project.platform.entity.OrderItem;
import com.project.platform.dto.CreateOrderDTO;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.OrderItemDTO;
import com.project.platform.dto.UpdatePasswordDTO;
import com.project.platform.dto.RetrievePasswordDTO;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.OrderMapper;
// import com.project.platform.mapper.ProductMapper; // 临时注释，等商品功能实现后取消注释
import com.project.platform.service.OrderItemService;
import com.project.platform.service.OrderService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单业务服务实现类
 * 实现订单的 CRUD 操作和业务逻辑
 * 包括：创建、查询、更新、删除、状态转换、支付等功能
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;
    
    @Resource
    private OrderItemService orderItemService;
    
    // @Resource
    // private ProductMapper productMapper; // 临时注释，等商品功能实现后取消注释

    /**
     * 分页查询订单列表
     */
    @Override
    public PageVO<Order> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Order> page = new PageVO<>();
        // 计算数据库查询的偏移量
        List<Order> list = orderMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(orderMapper.queryCount(query));
        return page;
    }

    /**
     * 根据ID查询单个订单
     */
    @Override
    public Order selectById(Integer id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        return order;
    }

    /**
     * 根据订单编号查询订单
     * 如果订单不存在，抛出异常
     */
    @Override
    public Order selectByOrderNo(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        return order;
    }

    /**
     * 查询用户的所有订单
     */
    @Override
    public List<Order> selectByUserId(Integer userId) {
        return orderMapper.selectByUserId(userId);
    }

    /**
     * 创建订单
     * 业务逻辑：
     * 1. 验证用户是否已登录
     * 2. 验证必填字段（收货地址、支付方式、订单项）
     * 3. 计算订单总金额
     * 4. 保存订单到数据库
     * 5. 创建订单项
     */
    @Override
    @Transactional
    public Order createOrder(Integer userId, CreateOrderDTO dto) {
        // 验证用户是否已登录
        if (userId == null) {
            throw new CustomException("用户未登录");
        }
        // 验证收货地址必填
        if (dto.getAddressId() == null) {
            throw new CustomException("收货地址不能为空");
        }
        // 验证支付方式必填并限制长度/合法性
        String paymentMethod = dto.getPaymentMethod();
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new CustomException("支付方式不能为空");
        }
        paymentMethod = paymentMethod.trim();
        if (paymentMethod.length() > 20) {
            throw new CustomException("支付方式长度不能超过20个字符");
        }
        // 建议校验支付方式枚举（开发中可逐步完善）
        
        // 验证订单项列表必填
        if (dto.getOrderItems() == null || dto.getOrderItems().isEmpty()) {
            throw new CustomException("订单项不能为空");
        }
        
        // 验证 buyerNote 和 invoiceTitle 长度，防止数据库截断异常
        if (dto.getBuyerNote() != null && dto.getBuyerNote().length() > 500) {
            throw new CustomException("买家留言不能超过500个字符");
        }
        if (dto.getInvoiceTitle() != null && dto.getInvoiceTitle().length() > 200) {
            throw new CustomException("发票抬头不能超过200个字符");
        }

        Order order = new Order();
        // 生成唯一的订单编号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddressId(dto.getAddressId());
        order.setPaymentMethod(paymentMethod);
        order.setBuyerNote(dto.getBuyerNote());
        order.setInvoiceNeeded(dto.getInvoiceNeeded() != null ? dto.getInvoiceNeeded() : 0);
        order.setInvoiceTitle(dto.getInvoiceTitle());
        
        // 计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDTO itemDTO : dto.getOrderItems()) {
            // 验证订单项必填字段
            if (itemDTO.getProductId() == null) {
                throw new CustomException("商品ID不能为空");
            }
            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                throw new CustomException("购买数量必须大于0");
            }
            
            // 计算订单项金额
            BigDecimal unitPrice = itemDTO.getUnitPrice() != null ? itemDTO.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal itemTotal = unitPrice.multiply(new BigDecimal(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        
        // 设置订单金额信息
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO); // 优惠金额可以根据业务规则计算
        order.setShippingFee(BigDecimal.ZERO); // 运费可以根据业务规则计算
        order.setFinalAmount(totalAmount); // 实付金额 = 商品总金额 - 优惠金额 + 运费
        
        // 初始化订单状态
        order.setPaymentStatus(0); // 0 = 待支付
        order.setOrderStatus("pending"); // pending = 待发货
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        // 保存到数据库，捕获常见的数据完整性异常并包装为友好提示
        try {
            orderMapper.insert(order);
        } catch (DataIntegrityViolationException e) {
            String msg = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            log.error("插入订单时出现数据完整性错误：{}", msg);
            if (msg != null && msg.contains("fk_orders_user")) {
                throw new CustomException("关联的用户不存在或已被删除");
            } else if (msg != null && msg.contains("fk_orders_address")) {
                throw new CustomException("收货地址不存在或不可用");
            } else if (msg != null && msg.contains("Data too long for column 'payment_method'")) {
                throw new CustomException("支付方式字段太长，请使用短标识（如 alipay/wechat）");
            } else {
                throw new CustomException("创建订单失败：" + (msg != null ? msg : e.getMessage()));
            }
        } catch (Exception e) {
            log.error("插入订单时发生未知异常", e);
            throw new CustomException("创建订单失败，请稍后重试");
        }
        
        // 创建订单项
        try {
            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderItemDTO itemDTO : dto.getOrderItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setOrderNo(orderNo);
                orderItem.setProductId(itemDTO.getProductId());
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setUnitPrice(itemDTO.getUnitPrice() != null ? itemDTO.getUnitPrice() : BigDecimal.ZERO);
                orderItem.setProductSpecifications(itemDTO.getProductSpec());
                orderItem.setTotalPrice(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQuantity())));
                
                // 临时使用固定值代替商品信息，等商品功能实现后再从商品表获取
                // 注意：实际生产环境中需要从商品服务获取真实数据
                orderItem.setProductName("临时商品名称"); // 临时字段，商品功能实现后替换
                orderItem.setProductImage("/images/temp-product.jpg"); // 临时字段，商品功能实现后替换
                
                // 设置时间信息
                orderItem.setCreatedAt(order.getCreatedAt());
                //orderItem.setUpdatedAt(order.getUpdatedAt());
                
                orderItems.add(orderItem);
            }
            
            // 批量创建订单项
            orderItemService.batchCreateOrderItems(orderItems);
        } catch (Exception e) {
            log.error("创建订单项失败", e);
            throw new CustomException("创建订单成功，但创建订单项失败，请联系管理员");
        }
        
        return order;
    }

    /**
     * 更新订单信息
     * 先检查订单是否存在，再更新
     */
    @Override
    public void updateById(Order entity) {
        Order existingOrder = orderMapper.selectById(entity.getId());
        if (existingOrder == null) {
            throw new CustomException("订单不存在");
        }
        // 更新时间戳
        entity.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(entity);
    }

    /**
     * 删除订单
     * 业务规则：仅允许删除已取消或已关闭的订单
     * 这是为了防止意外删除重要的订单数据
     */
    @Override
    @Transactional
    public void deleteById(Integer id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        // 检查订单状态，仅允许删除已取消或已关闭的订单
        if (!order.getOrderStatus().equals("cancelled") && !order.getOrderStatus().equals("closed")) {
            throw new CustomException("只能删除已取消或已关闭的订单");
        }
        
        // 删除关联的订单项
        orderItemService.deleteByOrderNo(order.getOrderNo());
        
        // 删除订单
        orderMapper.deleteById(id);
    }

    /**
     * 取消订单
     * 业务规则：仅允许取消待发货的订单（pending 状态）
     */
    @Override
    public void cancelOrder(String orderNo, String cancelReason) {
        Order order = selectByOrderNo(orderNo);
        
        // 验证订单状态是否允许取消
        if (!order.getOrderStatus().equals("pending")) {
            throw new CustomException("订单不能取消");
        }
        
        // 构建更新对象
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setOrderStatus("cancelled"); // 更新为已取消
        updateOrder.setCancelReason(cancelReason); // 记录取消原因
        updateOrder.setClosedTime(LocalDateTime.now()); // 设置关闭时间
        updateOrder.setUpdatedAt(LocalDateTime.now());
        
        orderMapper.updateById(updateOrder);
    }

    /**
     * 更新订单状态
     * 业务规则：严格按照状态转换规则更新
     * 状态流程：pending → shipped → received → closed 或 → cancelled
     */
    @Override
    public void updateOrderStatus(String orderNo, String orderStatus) {
        Order order = selectByOrderNo(orderNo);
        
        // 验证订单状态转换是否合法
        String currentStatus = order.getOrderStatus();
        if (!isValidStatusTransition(currentStatus, orderStatus)) {
            throw new CustomException("订单状态转换不合法");
        }
        
        // 构建更新对象
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setOrderStatus(orderStatus);
        updateOrder.setUpdatedAt(LocalDateTime.now());
        
        // 根据新状态更新相关时间戳
        if (orderStatus.equals("shipped")) {
            updateOrder.setShippedTime(LocalDateTime.now());
        } else if (orderStatus.equals("received")) {
            updateOrder.setReceivedTime(LocalDateTime.now());
        } else if (orderStatus.equals("closed")) {
            updateOrder.setClosedTime(LocalDateTime.now());
        }
        
        orderMapper.updateById(updateOrder);
    }

    /**
     * 确认收货
     * 用户收到商品后调用此方法
     * 仅允许对已发货的订单进行此操作
     */
    @Override
    public void confirmReceipt(String orderNo) {
        Order order = selectByOrderNo(orderNo);
        
        // 检查订单是否已发货
        if (!order.getOrderStatus().equals("shipped")) {
            throw new CustomException("只有已发货的订单才能确认收货");
        }
        
        // 更新订单状态为已收货
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setOrderStatus("received");
        updateOrder.setReceivedTime(LocalDateTime.now());
        updateOrder.setUpdatedAt(LocalDateTime.now());
        
        orderMapper.updateById(updateOrder);
    }

    /**
     * 更新支付信息
     * 支付完成后由支付网关回调此接口
     * 更新支付状态为已支付，保存交易ID
     */
    @Override
    public void updatePaymentInfo(String orderNo, String paymentMethod, String transactionId) {
        Order order = selectByOrderNo(orderNo);
        
        // 检查订单是否处于待支付状态
        if (!order.getOrderStatus().equals("pending")) {
            throw new CustomException("只有待支付的订单才能更新支付信息");
        }
        
        // 构建更新对象
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setPaymentMethod(paymentMethod);
        updateOrder.setPaymentTransactionId(transactionId); // 保存第三方支付交易ID
        updateOrder.setPaymentStatus(1); // 1 = 已支付
        updateOrder.setPaymentTime(LocalDateTime.now());
        // 支付成功后保持 pending 状态，等待商家发货
        updateOrder.setUpdatedAt(LocalDateTime.now());
        
        orderMapper.updateById(updateOrder);
    }

    /**
     * 生成唯一的订单编号
     * 格式：ORD + 时间戳 + UUID（截取前 8 位）
     * 确保全局唯一且易于识别
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 验证订单状态转换的合法性
     * 定义了严格的状态转换规则
     * @param currentStatus 当前订单状态
     * @param newStatus 目标订单状态
     * @return 是否为合法的状态转换
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // 取消订单：待发货时可以取消
        if (newStatus.equals("cancelled")) {
            // cancelled 不能从已发货、已收货、已关闭的订单转换而来
            return !currentStatus.equals("cancelled") && !currentStatus.equals("shipped") 
                   && !currentStatus.equals("received") && !currentStatus.equals("closed");
        }
        
        // 定义合法的状态转换路径
        // pending(待发货) → shipped(已发货) → received(已收货) → closed(已关闭)
        return (currentStatus.equals("pending") && newStatus.equals("shipped")) ||
               (currentStatus.equals("shipped") && newStatus.equals("received")) ||
               (currentStatus.equals("received") && newStatus.equals("closed"));
    }

    // ==================== 以下方法来自 CommonService 接口，但订单服务不使用，仅做异常抛出 ====================

    @Override
    public CurrentUserDTO login(String username, String password) {
        throw new CustomException("订单服务不支持登录操作");
    }

    @Override
    public void register(JSONObject data) {
        throw new CustomException("订单服务不支持注册操作");
    }

    @Override
    public void updateCurrentUserInfo(CurrentUserDTO currentUserDTO) {
        throw new CustomException("订单服务不支持更新用户信息操作");
    }

    @Override
    public void updateCurrentUserPassword(UpdatePasswordDTO updatePassword) {
        throw new CustomException("订单服务不支持更新密码操作");
    }

    @Override
    public void resetPassword(Integer id) {
        throw new CustomException("订单服务不支持重置密码操作");
    }

    @Override
    public void retrievePassword(RetrievePasswordDTO retrievePasswordDTO) {
        throw new CustomException("订单服务不支持找回密码操作");
    }
}
