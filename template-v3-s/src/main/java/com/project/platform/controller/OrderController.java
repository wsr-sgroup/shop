package com.project.platform.controller;

import com.project.platform.entity.Order;
import com.project.platform.entity.OrderItem;
import com.project.platform.dto.CreateOrderDTO;
import com.project.platform.service.OrderItemService;
import com.project.platform.service.OrderService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理控制器
 * 处理所有订单相关的 HTTP 请求
 * 路由前缀：/order
 * 
 * API 接口列表：
 * 1. GET  /order/page                     - 分页查询订单（管理员，公开）
 * 2. GET  /order/selectById/{id}          - 根据ID查询订单详情（公开）
 * 3. GET  /order/selectByOrderNo/{orderNo} - 根据订单编号查询订单（公开）
 * 4. GET  /order/myOrders                 - 查询当前用户订单（需登录）
 * 5. POST /order/create                   - 创建订单（需登录）
 * 6. PUT  /order/update                   - 更新订单信息（需登录）
 * 7. POST /order/cancel/{orderNo}         - 取消订单（需登录）
 * 8. POST /order/updateStatus/{orderNo}/{status} - 更新订单状态（需登录）
 * 9. POST /order/confirmReceipt/{orderNo} - 确认收货（需登录）
 * 10. POST /order/updatePayment           - 更新支付信息（支付回调，无需登录）
 * 11. DELETE /order/delete/{id}           - 删除订单（需登录）
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    
    @Resource
    private OrderItemService orderItemService;

    /**
     * 分页查询订单（管理员）
     * 
     * HTTP 方法：GET
     * 路由：/order/page
     * 认证：否（公开接口）
     * 
     * 查询参数示例：
     * - pageNum: 1 (页码，默认为1)
     * - pageSize: 10 (每页记录数，默认为10)
     * - orderNo: ORD... (可选，订单编号)
     * - userId: 1 (可选，用户ID)
     * - orderStatus: pending (可选，订单状态)
     * - paymentStatus: 0 (可选，支付状态)
     * 
     * 示例请求：
     * GET /order/page?pageNum=1&pageSize=10&orderStatus=pending
     * 
     * 返回值：分页订单列表，包含订单的所有信息
     * 
     * @param query 查询条件 Map，支持多种过滤条件
     * @param pageNum 页码，默认为 1，需 >= 1
     * @param pageSize 每页记录数，默认为 10，通常建议 10-100 之间
     * @return ResponseVO 响应对象，包含 PageVO<Order> 分页数据
     */
    @GetMapping("page")
    public ResponseVO<PageVO<Object>> page(
            @RequestParam Map<String, Object> query,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 调用 Service 层进行分页查询并映射结果为稳定的响应结构
        PageVO<Order> pageObj = orderService.page(query, pageNum, pageSize);
        java.util.List<Object> mappedList = new java.util.ArrayList<>();
        if (pageObj.getList() != null) {
            for (Order o : pageObj.getList()) {
                mappedList.add(orderToSafeMap(o));
            }
        }
        PageVO<Object> resp = new PageVO<>();
        resp.setList(mappedList);
        resp.setTotal(pageObj.getTotal());
        return ResponseVO.ok(resp);
    }

    /**
     * 根据ID查询订单详情
     * 
     * HTTP 方法：GET
     * 路由：/order/selectById/{id}
     * 认证：否（公开接口）
     * 
     * 示例请求：
     * GET /order/selectById/1
     * 
     * 返回值：订单详情，包含所有字段信息
     * 
     * @param id 订单的主键ID，必须为正整数
     * @return ResponseVO 响应对象，包含 Order 对象
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<Object> selectById(@PathVariable("id") Integer id) {
        if (id == null || id <= 0) {
            return ResponseVO.fail(400, "id 必须为正整数");
        }
        // 根据主键ID查询订单，Service 层会在不存在时抛出异常
        Order order = orderService.selectById(id);
        return ResponseVO.ok(orderToSafeMap(order));
    }

    /**
     * 根据订单编号查询订单
     * 
     * HTTP 方法：GET
     * 路由：/order/selectByOrderNo/{orderNo}
     * 认证：否（公开接口）
     * 
     * 订单编号格式：ORD+时间戳+UUID 的简写
     * 示例订单编号：ORD1735017600000ABC1234567
     * 
     * 示例请求：
     * GET /order/selectByOrderNo/ORD1735017600000ABC1234567
     * 
     * 返回值：订单详情，如果订单不存在则返回 null
     * 
     * @param orderNo 订单编号，唯一标识一个订单，通常由系统生成
     * @return ResponseVO 响应对象，包含 Order 对象
     */
    @GetMapping("selectByOrderNo/{orderNo}")
    public ResponseVO<Object> selectByOrderNo(@PathVariable("orderNo") String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            return ResponseVO.fail(400, "orderNo 不能为空");
        }
        // 根据订单编号查询订单信息
        Order order = orderService.selectByOrderNo(orderNo);
        return ResponseVO.ok(orderToSafeMap(order));
    }

    /**
     * 查询当前用户的订单列表
     * 
     * HTTP 方法：GET
     * 路由：/order/myOrders
     * 认证：是（需要登录，LoginInterceptor 会从 Token 中提取用户ID）
     * 
     * 查询参数：
     * - pageNum: 1 (页码，默认为1)
     * - pageSize: 10 (每页记录数，默认为10)
     * 
     * 示例请求（需要在 Header 中提供 Token）：
     * GET /order/myOrders?pageNum=1&pageSize=10
     * Header: Authorization: Bearer <token>
     * 
     * 返回值：当前登录用户的订单分页列表
     * 
     * 业务说明：
     * - 自动过滤为当前登录用户的订单
     * - 无需在参数中传递 userId，系统自动获取
     * 
     * @param pageNum 页码，默认为 1
     * @param pageSize 每页记录数，默认为 10
     * @return ResponseVO 响应对象，包含 PageVO<Order> 当前用户的订单分页数据
     */
    @GetMapping("myOrders")
    public ResponseVO<PageVO<Object>> myOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 从线程本地变量获取当前登录用户的ID
        Integer userId = CurrentUserThreadLocal.getCurrentUserId();
        if (userId == null) {
            return ResponseVO.<PageVO<Object>>fail(401, "请先登录");
        }
        
        // 构建查询条件，仅查询当前用户的订单
        Map<String, Object> query = java.util.Collections.singletonMap("userId", userId);
        PageVO<Order> pageObj = orderService.page(query, pageNum, pageSize);
        java.util.List<Object> mappedList = new java.util.ArrayList<>();
        if (pageObj.getList() != null) {
            for (Order o : pageObj.getList()) {
                mappedList.add(orderToSafeMap(o));
            }
        }
        PageVO<Object> resp = new PageVO<>();
        resp.setList(mappedList);
        resp.setTotal(pageObj.getTotal());
        return ResponseVO.ok(resp);
    }

    /**
     * 创建订单
     * 
     * HTTP 方法：POST
     * 路由：/order/create
     * 认证：是（需要登录）
     * Content-Type: application/json
     * 
     * 请求体示例：
     * {
     *   "addressId": 1,           // 收货地址ID，必填，须为有效的地址ID
     *   "paymentMethod": "alipay", // 支付方式，可选值：alipay、wechat、credit_card
     *   "buyerNote": "请尽快发货", // 买家备注，可选
     *   "invoiceNeeded": 0         // 是否需要发票，0=否，1=是
     * }
     * 
     * 示例请求（使用 curl）：
     * curl -X POST http://localhost:1000/order/create \
     *   -H "Authorization: Bearer <token>" \
     *   -H "Content-Type: application/json" \
     *   -d '{"addressId":1,"paymentMethod":"alipay","buyerNote":"请尽快发货","invoiceNeeded":0}'
     * 
     * 返回值：新创建的订单对象，包含生成的订单编号和初始状态
     * 
     * 业务逻辑：
     * 1. 获取当前登录用户ID（从 Token 中提取）
     * 2. 验证用户是否已登录（若未登录返回 401 错误）
     * 3. 校验请求参数有效性
     * 4. 生成唯一的订单编号（ORD+时间戳+UUID）
     * 5. 初始化订单状态为 pending，金额初始为0
     * 6. 保存订单到数据库
     * 
     * 可能的异常：
     * - 未登录：返回 401 错误 "请先登录"
     * - 地址不存在：CustomException "地址ID不存在"
     * - 数据库异常：CustomException "创建订单失败"
     * 
     * @param dto 订单创建数据传输对象，包含收货地址、支付方式等信息
     * @return ResponseVO 响应对象，包含新创建的 Order 对象
     */
    @PostMapping("create")
    public ResponseVO<Object> createOrder(@RequestBody CreateOrderDTO dto) {
        // 从线程本地获取当前登录用户的ID
        Integer userId = CurrentUserThreadLocal.getCurrentUserId();
        
        // 如果用户未登录，userId 为 null
        if (userId == null) {
            // 返回 401 Unauthorized 错误
            return ResponseVO.fail(401, "请先登录");
        }
        
        // 调用 Service 创建订单
        // Service 层会进行进一步的业务验证和数据库操作
        Order order = orderService.createOrder(userId, dto);
        
        // 将 Order 转换为对外稳定的响应结构：
        // - 将可选字符串字段的 null 转为空字符串，避免 API 验证失败
        // - 将 LocalDateTime 字段格式化为统一的字符串（yyyy-MM-dd HH:mm:ss），若为 null 则返回空字符串
        java.util.Map<String, Object> resp = orderToSafeMap(order);
        
        return ResponseVO.ok(resp);
    }

    // 将 Order 转换为对外响应的 Map（确保字段类型稳定，兼容 API 测试工具）
    private java.util.Map<String, Object> orderToSafeMap(Order order) {
        if (order == null) {
            return null;
        }
        
        java.util.Map<String, Object> m = new java.util.HashMap<>();
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        m.put("id", order.getId() == null ? 0 : order.getId());
        m.put("orderNo", order.getOrderNo() == null ? "" : order.getOrderNo());
        m.put("userId", order.getUserId() == null ? 0 : order.getUserId());
        m.put("addressId", order.getAddressId() == null ? 0 : order.getAddressId());

        // 金额信息（保证数值字段不为 null）
        m.put("totalAmount", order.getTotalAmount() == null ? java.math.BigDecimal.ZERO : order.getTotalAmount());
        m.put("discountAmount", order.getDiscountAmount() == null ? java.math.BigDecimal.ZERO : order.getDiscountAmount());
        m.put("shippingFee", order.getShippingFee() == null ? java.math.BigDecimal.ZERO : order.getShippingFee());
        m.put("finalAmount", order.getFinalAmount() == null ? java.math.BigDecimal.ZERO : order.getFinalAmount());

        // 支付信息
        m.put("paymentMethod", order.getPaymentMethod() == null ? "" : order.getPaymentMethod());
        m.put("paymentStatus", order.getPaymentStatus() == null ? 0 : order.getPaymentStatus());
        m.put("paymentTime", order.getPaymentTime() == null ? "" : order.getPaymentTime() != null ? order.getPaymentTime().format(dtf) : "");
        m.put("paymentTransactionId", order.getPaymentTransactionId() == null ? "" : order.getPaymentTransactionId());

        // 订单状态
        m.put("orderStatus", order.getOrderStatus() == null ? "" : order.getOrderStatus());

        // 物流信息
        m.put("shippingMethod", order.getShippingMethod() == null ? "" : order.getShippingMethod());
        m.put("shippingTrackingNo", order.getShippingTrackingNo() == null ? "" : order.getShippingTrackingNo());
        m.put("shippedTime", order.getShippedTime() == null ? "" : order.getShippedTime() != null ? order.getShippedTime().format(dtf) : "");
        m.put("receivedTime", order.getReceivedTime() == null ? "" : order.getReceivedTime() != null ? order.getReceivedTime().format(dtf) : "");

        // 备注信息
        m.put("closedTime", order.getClosedTime() == null ? "" : order.getClosedTime() != null ? order.getClosedTime().format(dtf) : "");
        m.put("cancelReason", order.getCancelReason() == null ? "" : order.getCancelReason());
        m.put("buyerNote", order.getBuyerNote() == null ? "" : order.getBuyerNote());
        m.put("adminNote", order.getAdminNote() == null ? "" : order.getAdminNote());

        // 发票信息（确保不为 null）
        m.put("invoiceNeeded", order.getInvoiceNeeded() == null ? 0 : order.getInvoiceNeeded());
        m.put("invoiceTitle", order.getInvoiceTitle() == null ? "" : order.getInvoiceTitle());

        // 时间戳
        m.put("createdAt", order.getCreatedAt() == null ? "" : order.getCreatedAt() != null ? order.getCreatedAt().format(dtf) : "");
        m.put("updatedAt", order.getUpdatedAt() == null ? "" : order.getUpdatedAt() != null ? order.getUpdatedAt().format(dtf) : "");
        
        // 订单项信息
        try {
            List<OrderItem> orderItems = orderItemService.selectByOrderNo(order.getOrderNo());
            m.put("orderItems", orderItems);
        } catch (Exception e) {
            // 如果查询订单项失败，返回空列表而不是null
            m.put("orderItems", java.util.Collections.emptyList());
        }

        return m;
    }

    /**
     * 更新订单信息（管理员操作）
     * 
     * HTTP 方法：PUT
     * 路由：/order/update
     * 认证：是（需要登录）
     * Content-Type: application/json
     * 
     * 请求体示例（任意可选字段的 Order 对象）：
     * {
     *   "id": 1,                 // 订单ID，必填，用来定位要更新的订单
     *   "receiverAddress": "...", // 可选，修改收货地址
     *   "buyerNote": "...",      // 可选，修改备注
     *   "paymentMethod": "...",  // 可选，修改支付方式
     *   "invoiceNeeded": 1       // 可选，修改是否需要发票
     * }
     * 
     * 返回值：无具体数据，仅表示操作成功或失败
     * 
     * 业务说明：
     * - 仅更新请求体中非 null 的字段
     * - id 字段用于定位要更新的订单
     * - 某些字段的修改可能受限于订单状态（例如已发货的订单可能不能修改地址）
     * 
     * 可能的异常：
     * - 未登录：返回 401 错误
     * - 订单不存在：CustomException "订单不存在"
     * - 数据库异常：CustomException "更新订单失败"
     * 
     * @param order 订单对象，必须包含 id 字段，其他字段可选
     * @return ResponseVO 响应对象，成功时无额外数据
     */
    @PutMapping("update")
    public ResponseVO<Void> updateOrder(@RequestBody Order order) {
        // 权限校验：检查用户是否已登录
        Integer userId = CurrentUserThreadLocal.getCurrentUserId();
        if (userId == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        
        if (order == null || order.getId() == null || order.getId() <= 0) {
            return ResponseVO.fail(400, "id 必须提供且为正整数");
        }
        // 防止将非空字段显式置空（示例性校验）
        if (order.getOrderNo() != null && order.getOrderNo().trim().isEmpty()) {
            return ResponseVO.fail(400, "orderNo 不能为空字符串");
        }
        
        // 调用 Service 层更新订单信息
        orderService.updateById(order);
        
        return ResponseVO.ok();
    }

    /**
     * 取消订单
     * 
     * HTTP 方法：POST
     * 路由：/order/cancel/{orderNo}
     * 认证：是（需要登录）
     * 
     * 路径参数：
     * - orderNo: 订单编号，唯一标识一个订单
     * 
     * 查询参数：
     * - cancelReason: 取消原因，可选参数
     * 
     * 示例请求：
     * POST /order/cancel/ORD1735017600000ABC1234567?cancelReason=不需要了
     * Header: Authorization: Bearer <token>
     * 
     * 返回值：无具体数据，仅表示操作成功或失败
     * 
     * 业务规则：
     * - 仅允许取消状态为 pending（待发货）的订单
     * - 已发货、已收货或已关闭的订单不能取消
     * - 已取消的订单无法重复取消
     * - 取消订单后，订单状态变为 cancelled
     * - 如果订单已支付，应该触发退款流程
     * 
     * 可能的异常：
     * - 未登录：返回 401 错误
     * - 订单不存在：CustomException "订单不存在"
     * - 无法取消（状态不对）：CustomException "只有待发货的订单才能取消"
     * - 数据库异常：CustomException "取消订单失败"
     * 
     * @param orderNo 订单编号，格式：ORD+时间戳+UUID
     * @param cancelReason 取消原因，可选，用于记录用户为什么取消
     * @return ResponseVO 响应对象，成功时无额外数据
     */
    @PostMapping("cancel/{orderNo}")
    public ResponseVO<Void> cancelOrder(
            @PathVariable("orderNo") String orderNo,
            @RequestParam(required = false) String cancelReason) {
        // 权限校验：检查用户是否已登录
        Integer userId = CurrentUserThreadLocal.getCurrentUserId();
        if (userId == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        if (orderNo == null || orderNo.trim().isEmpty()) {
            return ResponseVO.fail(400, "orderNo 不能为空");
        }
        
        // 调用 Service 层执行取消操作
        // Service 会验证订单状态是否允许取消
        orderService.cancelOrder(orderNo, cancelReason);
        
        return ResponseVO.ok();
    }

    /**
     * 更新订单状态（管理员操作）
     * 
     * HTTP 方法：POST
     * 路由：/order/updateStatus/{orderNo}/{status}
     * 认证：是（需要登录）
     * 
     * 路径参数：
     * - orderNo: 订单编号
     * - status: 新的订单状态，允许值：pending、shipped、received、closed、cancelled
     * 
     * 示例请求：
     * POST /order/updateStatus/ORD1735017600000ABC1234567/shipped
     * Header: Authorization: Bearer <token>
     * 
     * 返回值：无具体数据，仅表示操作成功或失败
     * 
     * 订单状态转换规则（状态机）：
     * pending (待发货)
     *   ↓
     * shipped (已发货)
     *   ↓
     * received (已收货)
     *   ↓
     * closed (已关闭)
     * 
     * 任何状态都可以转换到 cancelled (已取消)，但 cancelled 不能转换到其他状态
     * 
     * 一般流程：
     * 1. 用户创建订单：pending
     * 2. 商家确认并发货：shipped
     * 3. 用户确认收货：received
     * 4. 订单自动或手动关闭：closed
     * 或者任何时刻用户取消：cancelled
     * 
     * 可能的异常：
     * - 未登录：返回 401 错误
     * - 订单不存在：CustomException "订单不存在"
     * - 无效的状态转换：CustomException "无法从 xxx 转换到 yyy"
     * - 数据库异常：CustomException "更新订单状态失败"
     * 
     * @param orderNo 订单编号
     * @param status 新的订单状态
     * @return ResponseVO 响应对象，成功时无额外数据
     */
    @PostMapping("updateStatus/{orderNo}/{status}")
    public ResponseVO<Void> updateOrderStatus(
            @PathVariable("orderNo") String orderNo,
            @PathVariable("status") String status) {
        // 权限校验：检查用户是否已登录
        Integer userId = CurrentUserThreadLocal.getCurrentUserId();
        if (userId == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        if (orderNo == null || orderNo.trim().isEmpty() || status == null || status.trim().isEmpty()) {
            return ResponseVO.fail(400, "orderNo 和 status 均不能为空");
        }
        java.util.Set<String> allowed = java.util.Set.of("pending","shipped","received","closed","cancelled");
        if (!allowed.contains(status)) {
            return ResponseVO.fail(400, "status 非法，允许值：pending, shipped, received, closed, cancelled");
        }
        
        // 调用 Service 层更新状态
        // Service 会验证状态转换是否合法
        orderService.updateOrderStatus(orderNo, status);
        
        return ResponseVO.ok();
    }

    /**
     * 确认收货
     * 
     * HTTP 方法：POST
     * 路由：/order/confirmReceipt/{orderNo}
     * 认证：是（需要登录）
     * 
     * 路径参数：
     * - orderNo: 订单编号
     * 
     * 示例请求：
     * POST /order/confirmReceipt/ORD1735017600000ABC1234567
     * Header: Authorization: Bearer <token>
     * 
     * 返回值：无具体数据，仅表示操作成功或失败
     * 
     * 业务说明：
     * - 用户收到商品后调用此接口确认收货
     * - 订单状态从 shipped (已发货) 转换为 received (已收货)
     * - 确认收货后，订单进入售后期，用户可以申请退货等
     * - 一般来说，用户有 7 天或 15 天的收货确认期（取决于商家规则）
     * 
     * 可能的异常：
     * - 未登录：返回 401 错误
     * - 订单不存在：CustomException "订单不存在"
     * - 订单未发货：CustomException "订单未发货，无法确认收货"
     * - 数据库异常：CustomException "确认收货失败"
     * 
     * @param orderNo 订单编号
     * @return ResponseVO 响应对象，成功时无额外数据
     */
    @PostMapping("confirmReceipt/{orderNo}")
    public ResponseVO<Void> confirmReceipt(@PathVariable("orderNo") String orderNo) {
        // 权限校验：检查用户是否已登录
        Integer userId = CurrentUserThreadLocal.getCurrentUserId();
        if (userId == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        if (orderNo == null || orderNo.trim().isEmpty()) {
            return ResponseVO.fail(400, "orderNo 不能为空");
        }
        // 调用 Service 层执行确认收货操作
        orderService.confirmReceipt(orderNo);
        
        return ResponseVO.ok();
    }

    /**
     * 更新支付信息（支付回调接口）
     * 
     * HTTP 方法：POST
     * 路由：/order/updatePayment
     * 认证：否（由支付网关内部调用，不需要用户 Token）
     * 
     * 查询参数：
     * - orderNo: 订单编号（必填）
     * - paymentMethod: 支付方式，可选值：alipay、wechat、credit_card（必填）
     * - transactionId: 支付平台的交易号，用于对账（必填）
     * 
     * 示例请求（由支付网关发送）：
     * POST /order/updatePayment?orderNo=ORD1735017600000ABC1234567&paymentMethod=alipay&transactionId=2025010100001234567890
     * 
     * 返回值：无具体数据，仅表示操作成功或失败
     * 
     * 业务说明：
     * - 这是一个回调接口，由支付网关（如支付宝、微信支付）在用户成功支付后调用
     * - 此接口更新订单的支付状态和支付信息
     * - 调用此接口后，订单的 paymentStatus 字段变为 1（已支付）
     * - transactionId 用于与支付平台对账，防止重复支付
     * 
     * 典型的支付流程：
     * 1. 用户创建订单，状态为 pending，paymentStatus 为 0（待支付）
     * 2. 用户选择支付方式，前端跳转到支付网关
     * 3. 用户在支付网关完成支付
     * 4. 支付网关成功后，调用此接口通知商城后端
     * 5. 此接口更新订单为已支付状态
     * 
     * 安全考虑：
     * - 实际生产环境中，应该验证支付网关的回调签名
     * - 防止伪造的支付回调
     * - 处理重复回调的情况
     * 
     * 可能的异常：
     * - 订单不存在：CustomException "订单不存在"
     * - transactionId 重复：CustomException "此交易号已处理，防止重复支付"
     * - 数据库异常：CustomException "更新支付信息失败"
     * 
     * @param orderNo 订单编号，用于定位要更新的订单
     * @param paymentMethod 支付方式，如 alipay、wechat 等
     * @param transactionId 支付平台的唯一交易号，用于对账和防重复
     * @return ResponseVO 响应对象，成功时无额外数据
     */
    @PostMapping("updatePayment")
    public ResponseVO<Void> updatePaymentInfo(
            @RequestParam String orderNo,
            @RequestParam String paymentMethod,
            @RequestParam String transactionId) {
        // 无需登录校验，因为这是支付网关的回调接口
        if (orderNo == null || orderNo.trim().isEmpty() || paymentMethod == null || paymentMethod.trim().isEmpty() || transactionId == null || transactionId.trim().isEmpty()) {
            return ResponseVO.fail(400, "orderNo、paymentMethod、transactionId 均不能为空");
        }
        if (paymentMethod.length() > 20) {
            return ResponseVO.fail(400, "paymentMethod 长度不能超过 20");
        }
        // 调用 Service 层更新支付信息
        // Service 会验证 transactionId 是否已存在（防止重复支付）
        orderService.updatePaymentInfo(orderNo, paymentMethod, transactionId);
        
        return ResponseVO.ok();
    }

    /**
     * 删除订单（管理员操作）
     * 
     * HTTP 方法：DELETE
     * 路由：/order/delete/{id}
     * 认证：是（需要登录）
     * 
     * 路径参数：
     * - id: 订单的主键 ID
     * 
     * 示例请求：
     * DELETE /order/delete/1
     * Header: Authorization: Bearer <token>
     * 
     * 返回值：无具体数据，仅表示操作成功或失败
     * 
     * 业务规则：
     * - 仅允许删除状态为 cancelled（已取消）或 closed（已关闭）的订单
     * - 处于进行中的订单（pending、shipped、received）不能直接删除
     * - 删除后的订单数据无法恢复，谨慎操作
     * - 实际生产中，通常不真正删除，而是标记为逻辑删除
     * 
     * 可能的异常：
     * - 未登录：返回 401 错误
     * - 订单不存在：CustomException "订单不存在"
     * - 无法删除（状态不对）：CustomException "只有已取消或已关闭的订单才能删除"
     * - 数据库异常：CustomException "删除订单失败"
     * 
     * @param id 订单的主键 ID，必须为有效的正整数
     * @return ResponseVO 响应对象，成功时无额外数据
     */
    @DeleteMapping("delete/{id}")
    public ResponseVO<Void> deleteOrder(@PathVariable("id") Integer id) {
        // 权限校验：检查用户是否已登录
        Integer userId = CurrentUserThreadLocal.getCurrentUserId();
        if (userId == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        if (id == null || id <= 0) {
            return ResponseVO.fail(400, "id 必须为正整数");
        }
        
        // 调用 Service 层删除订单
        // Service 会验证订单状态是否允许删除
        orderService.deleteById(id);
        
        return ResponseVO.ok();
    }
}
