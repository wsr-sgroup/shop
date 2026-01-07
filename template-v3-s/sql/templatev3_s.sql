/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : localhost:3306
 Source Schema         : templatev3_s

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 26/12/2024 00:17:51
*/

SET NAMES utf8mb4;  -- 字符集
USE templatev3_s;
SET FOREIGN_KEY_CHECKS = 0; -- 关闭外键约束检查

-- ----------------------------
-- 管理员表
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名称',
  `password` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- 用户表
CREATE TABLE `users` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名，用于登录和显示',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱地址，用于登录和找回密码',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像图片URL',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账户是否激活（0=禁用，1=正常）',
  `is_admin` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否是管理员（0=普通用户，1=管理员）',
  `last_login` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  INDEX `idx_phone` (`phone`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='用户信息表';

-- 商品分类表
CREATE TABLE `categories` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID，主键',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `slug` VARCHAR(50) NOT NULL COMMENT '分类URL标识（英文）',
  `parent_id` INT UNSIGNED DEFAULT NULL COMMENT '父分类ID（用于多级分类）',
  `description` TEXT DEFAULT NULL COMMENT '分类描述',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重（越大越靠前）',
  `is_visible` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否显示（0=隐藏，1=显示）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slug` (`slug`),
  INDEX `idx_parent_id` (`parent_id`),
  INDEX `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_categories_parent` 
    FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`) 
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='商品分类表';

-- 商品表
CREATE TABLE `products` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品ID，主键',
  `sku` VARCHAR(50) NOT NULL COMMENT '商品SKU（库存单位），唯一标识',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `subtitle` VARCHAR(500) DEFAULT NULL COMMENT '商品副标题/简短描述',
  `description` TEXT NOT NULL COMMENT '商品详细描述',
  `price` DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
  `original_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '商品原价（用于显示折扣）',
  `cost_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '成本价',
  `stock` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '库存数量',
  `stock_low_threshold` INT UNSIGNED DEFAULT 10 COMMENT '库存预警阈值',
  `main_image` VARCHAR(255) NOT NULL COMMENT '主图URL',
  `image_gallery` JSON DEFAULT NULL COMMENT '商品图库（JSON数组格式）',
  `specifications` JSON NOT NULL COMMENT '商品规格参数（JSON格式）',
  `series` VARCHAR(50) DEFAULT NULL COMMENT '产品系列（如：ThinkPad, Yoga, Legion等）',
  `model` VARCHAR(100) DEFAULT NULL COMMENT '具体型号',
  `cpu` VARCHAR(100) DEFAULT NULL COMMENT 'CPU型号',
  `gpu` VARCHAR(100) DEFAULT NULL COMMENT '显卡型号',
  `ram` VARCHAR(50) DEFAULT NULL COMMENT '内存规格',
  `storage` VARCHAR(100) DEFAULT NULL COMMENT '存储规格',
  `screen_size` DECIMAL(4,1) DEFAULT NULL COMMENT '屏幕尺寸（英寸）',
  `weight` DECIMAL(5,2) DEFAULT NULL COMMENT '重量（kg）',
  `is_on_sale` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否上架销售（0=下架，1=上架）',
  `is_featured` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否推荐商品',
  `is_hot` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否热销商品',
  `sales_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '销售数量',
  `view_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览数量',
  `rating_avg` DECIMAL(3,2) DEFAULT 0.00 COMMENT '平均评分',
  `review_count` INT UNSIGNED DEFAULT 0 COMMENT '评价数量',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku` (`sku`),
  INDEX `idx_name` (`name`),
  INDEX `idx_price` (`price`),
  INDEX `idx_stock` (`stock`),
  INDEX `idx_series` (`series`),
  INDEX `idx_is_on_sale` (`is_on_sale`),
  INDEX `idx_sales_count` (`sales_count`),
  INDEX `idx_created_at` (`created_at`),
  FULLTEXT KEY `ft_search` (`name`, `description`, `subtitle`, `model`, `series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='商品信息表';

-- 商品分类关联表
CREATE TABLE `product_category` (
  `product_id` INT UNSIGNED NOT NULL COMMENT '商品ID',
  `category_id` INT UNSIGNED NOT NULL COMMENT '分类ID',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`product_id`, `category_id`),
  INDEX `idx_category_id` (`category_id`),
  CONSTRAINT `fk_pc_product` 
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_pc_category` 
    FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='商品与分类关联表';

-- 购物车表
CREATE TABLE `carts` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车ID，主键',
  `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
  `session_id` VARCHAR(100) DEFAULT NULL COMMENT '会话ID（用于未登录用户）',
  `item_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '商品种类数量',
  `total_quantity` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '商品总数量',
  `total_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品总金额',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  INDEX `idx_session_id` (`session_id`),
  INDEX `idx_updated_at` (`updated_at`),
  CONSTRAINT `fk_carts_user` 
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='购物车表';

-- 购物车项表
CREATE TABLE `cart_items` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `cart_id` INT UNSIGNED NOT NULL COMMENT '购物车ID',
  `product_id` INT UNSIGNED NOT NULL COMMENT '商品ID',
  `quantity` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '商品数量',
  `unit_price` DECIMAL(10, 2) NOT NULL COMMENT '加入购物车时的单价',
  `selected` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否选中（用于结算）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_product` (`cart_id`, `product_id`),
  INDEX `idx_cart_id` (`cart_id`),
  INDEX `idx_product_id` (`product_id`),
  CONSTRAINT `fk_cartitems_cart` 
    FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cartitems_product` 
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='购物车项表';

-- 用户地址表
CREATE TABLE `user_addresses` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
  `recipient_name` VARCHAR(50) NOT NULL COMMENT '收件人姓名',
  `recipient_phone` VARCHAR(20) NOT NULL COMMENT '收件人电话',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `city` VARCHAR(50) NOT NULL COMMENT '城市',
  `district` VARCHAR(50) NOT NULL COMMENT '区县',
  `street_address` VARCHAR(255) NOT NULL COMMENT '详细街道地址',
  `postal_code` VARCHAR(10) DEFAULT NULL COMMENT '邮政编码',
  `is_default` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址（0=否，1=是）',
  `tag` VARCHAR(20) DEFAULT NULL COMMENT '地址标签（如：家、公司、学校）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_is_default` (`is_default`),
  CONSTRAINT `fk_addresses_user` 
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='用户收货地址表';

-- 订单表
CREATE TABLE `orders` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID（内部使用）',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号（面向用户）',
  `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
  `address_id` INT UNSIGNED NOT NULL COMMENT '收货地址ID',
  `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
  `discount_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
  `shipping_fee` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '运费',
  `final_amount` DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
  `payment_method` VARCHAR(20) DEFAULT NULL COMMENT '支付方式（alipay, wechat, bank）',
  `payment_status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '支付状态（0=待支付，1=已支付，2=已退款）',
  `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `payment_transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '支付平台交易号',
  `order_status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '订单状态',
  `shipping_method` VARCHAR(50) DEFAULT NULL COMMENT '配送方式',
  `shipping_tracking_no` VARCHAR(100) DEFAULT NULL COMMENT '物流单号',
  `shipped_time` DATETIME DEFAULT NULL COMMENT '发货时间',
  `received_time` DATETIME DEFAULT NULL COMMENT '收货时间',
  `closed_time` DATETIME DEFAULT NULL COMMENT '订单关闭时间',
  `cancel_reason` VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
  `buyer_note` VARCHAR(500) DEFAULT NULL COMMENT '买家留言',
  `admin_note` TEXT DEFAULT NULL COMMENT '管理员备注',
  `invoice_needed` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否需要发票',
  `invoice_title` VARCHAR(200) DEFAULT NULL COMMENT '发票抬头',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_order_no` (`order_no`),
  INDEX `idx_order_status` (`order_status`),
  INDEX `idx_payment_status` (`payment_status`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_orders_user` 
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_orders_address` 
    FOREIGN KEY (`address_id`) REFERENCES `user_addresses` (`id`) 
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='订单主表';

-- 订单项表
CREATE TABLE `order_items` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` INT UNSIGNED NOT NULL COMMENT '订单ID（内部）',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号（冗余，方便查询）',
  `product_id` INT UNSIGNED NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称（下单时的快照）',
  `product_image` VARCHAR(255) NOT NULL COMMENT '商品主图（下单时的快照）',
  `product_specifications` JSON DEFAULT NULL COMMENT '商品规格（下单时的快照）',
  `unit_price` DECIMAL(10, 2) NOT NULL COMMENT '下单时的单价',
  `quantity` INT UNSIGNED NOT NULL COMMENT '购买数量',
  `item_amount` DECIMAL(10, 2) NOT NULL COMMENT '商品项总金额',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_order_no` (`order_no`),
  INDEX `idx_product_id` (`product_id`),
  CONSTRAINT `fk_orderitems_order` 
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_orderitems_product` 
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) 
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='订单项表（记录订单中的商品快照）';

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '123456', '管理员', 'http://localhost:1000/file/8826e8c280cb3bec6a4fbeb61514ee74.png', '123456', '123456@javadh.com', '启用', '2024-12-07 17:29:35');




INSERT INTO `admin` VALUES (2, 'admin2', '123456', '管理员2', 'http://localhost:1000/file/8826e8c280cb3bec6a4fbeb61514ee75.png', '123457', '123457@javadh.com', '启用', '2024-12-08 17:29:35');
INSERT INTO `admin` VALUES (3, 'admin3', '123456', '管理员3', 'http://localhost:1000/file/8826e8c280cb3bec6a4fbeb61514ee76.png', '123458', '123458@javadh.com', '启用', '2024-12-09 17:29:35');
INSERT INTO `admin` VALUES (4, 'admin4', '123456', '管理员4', 'http://localhost:1000/file/8826e8c280cb3bec6a4fbeb61514ee77.png', '123459', '123459@javadh.com', '启用', '2024-12-10 17:29:35');
INSERT INTO `admin` VALUES (5, 'admin5', '123456', '管理员5', 'http://localhost:1000/file/8826e8c280cb3bec6a4fbeb61514ee78.png', '123460', '123460@javadh.com', '禁用', '2024-12-11 17:29:35');
INSERT INTO `admin` VALUES (6, 'admin6', '123456', '管理员6', 'http://localhost:1000/file/8826e8c280cb3bec6a4fbeb61514ee79.png', '123461', '123461@javadh.com', '启用', '2024-12-12 17:29:35');

-- 用户表测试数据
INSERT INTO `users` (username, email, password_hash, phone, avatar_url, is_active, is_admin, last_login, created_at, updated_at) VALUES 
('user1', 'user1@example.com', '$2a$10$NPEzT6B2yIyU1hN3y8YzXu8l7p5v3s9w4r6t9y2u1i5o8p7a3s2d1', '13800138001', 'http://localhost:1000/avatar/user1.png', 1, 0, '2024-12-20 10:00:00', '2024-12-01 10:00:00', '2024-12-20 10:00:00'),
('user2', 'user2@example.com', '$2a$10$NPEzT6B2yIyU1hN3y8YzXu8l7p5v3s9w4r6t9y2u1i5o8p7a3s2d1', '13800138002', 'http://localhost:1000/avatar/user2.png', 1, 0, '2024-12-21 11:00:00', '2024-12-02 11:00:00', '2024-12-21 11:00:00'),
('user3', 'user3@example.com', '$2a$10$NPEzT6B2yIyU1hN3y8YzXu8l7p5v3s9w4r6t9y2u1i5o8p7a3s2d1', '13800138003', 'http://localhost:1000/avatar/user3.png', 1, 0, '2024-12-22 12:00:00', '2024-12-03 12:00:00', '2024-12-22 12:00:00'),
('user4', 'user4@example.com', '$2a$10$NPEzT6B2yIyU1hN3y8YzXu8l7p5v3s9w4r6t9y2u1i5o8p7a3s2d1', '13800138004', 'http://localhost:1000/avatar/user4.png', 0, 0, '2024-12-23 13:00:00', '2024-12-04 13:00:00', '2024-12-23 13:00:00'),
('user5', 'user5@example.com', '$2a$10$NPEzT6B2yIyU1hN3y8YzXu8l7p5v3s9w4r6t9y2u1i5o8p7a3s2d1', '13800138005', 'http://localhost:1000/avatar/user5.png', 1, 1, '2024-12-24 14:00:00', '2024-12-05 14:00:00', '2024-12-24 14:00:00');

-- 商品分类表测试数据
INSERT INTO `categories` (name, slug, parent_id, description, sort_order, is_visible, created_at, updated_at) VALUES 
('电子产品', 'electronics', NULL, '各类电子产品', 10, 1, '2024-12-01 10:00:00', '2024-12-01 10:00:00'),
('手机', 'phones', 1, '各类手机产品', 20, 1, '2024-12-01 10:00:00', '2024-12-01 10:00:00'),
('电脑', 'computers', 1, '各类电脑产品', 15, 1, '2024-12-01 10:00:00', '2024-12-01 10:00:00'),
('服装', 'clothing', NULL, '各类服装产品', 5, 1, '2024-12-01 10:00:00', '2024-12-01 10:00:00'),
('男装', 'menswear', 4, '男性服装', 8, 1, '2024-12-01 10:00:00', '2024-12-01 10:00:00');

-- 商品表测试数据
INSERT INTO `products` (sku, name, subtitle, description, price, original_price, cost_price, stock, stock_low_threshold, main_image, image_gallery, specifications, series, model, cpu, gpu, ram, storage, screen_size, weight, is_on_sale, is_featured, is_hot, sales_count, view_count, rating_avg, review_count, created_at, updated_at) VALUES 
('PROD001', 'iPhone 15 Pro', '苹果最新款手机', 'iPhone 15 Pro，配备A17 Pro芯片，6.1英寸超视网膜XDR显示屏', 7999.00, 8999.00, 6500.00, 50, 10, 'http://localhost:1000/uploads/product/主机.jpg', '["http://localhost:1000/uploads/product/主机.jpg", "http://localhost:1000/uploads/product/r9000p.jpg"]', '{"color": "深空灰", "storage": "128GB"}', 'iPhone', 'iPhone 15 Pro', 'A17 Pro', '集成GPU', '6GB', '128GB', 6.1, 0.20, 1, 1, 1, 150, 1200, 4.8, 85, '2024-12-01 10:00:00', '2024-12-20 10:00:00'),
('PROD002', 'MacBook Pro 14', '苹果专业笔记本电脑', 'MacBook Pro 14英寸，M3芯片，18GB内存，512GB SSD', 18999.00, 19999.00, 16000.00, 20, 5, 'http://localhost:1000/product/macbookpro14.jpg', '["http://localhost:1000/product/macbookpro141.jpg", "http://localhost:1000/product/macbookpro142.jpg"]', '{"color": "银色", "processor": "M3"}', 'MacBook Pro', 'MacBook Pro 14', 'M3', 'M3集成图形处理器', '18GB', '512GB', 14.2, 1.51, 1, 1, 0, 45, 800, 4.9, 32, '2024-12-02 10:00:00', '2024-12-21 10:00:00'),
('PROD003', 'Nike Air Max', '男士运动鞋', 'Nike Air Max 270，舒适缓震，适合日常穿着和运动', 899.00, 1099.00, 400.00, 100, 20, 'http://localhost:1000/product/nikeairmax.jpg', '["http://localhost:1000/product/nikeairmax1.jpg", "http://localhost:1000/product/nikeairmax2.jpg"]', '{"size": "42", "color": "黑色"}', 'Air Max', 'Air Max 270', NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 1, 200, 1500, 4.7, 120, '2024-12-03 10:00:00', '2024-12-22 10:00:00'),
('PROD004', '华为MateBook X Pro', '华为旗舰笔记本电脑', '华为MateBook X Pro，2024款，i7处理器，16GB内存，1TB SSD', 12999.00, 13999.00, 10000.00, 15, 5, 'http://localhost:1000/product/matebookxpro.jpg', '["http://localhost:1000/product/matebookxpro1.jpg", "http://localhost:1000/product/matebookxpro2.jpg"]', '{"color": "深空灰", "processor": "i7-1260P"}', 'MateBook', 'MateBook X Pro', 'i7-1260P', 'Intel Iris Xe', '16GB', '1TB', 14.0, 1.25, 1, 0, 1, 80, 600, 4.6, 45, '2024-12-04 10:00:00', '2024-12-23 10:00:00'),
('PROD005', '小米13', '小米旗舰手机', '小米13，第二代骁龙8，徕卡光学镜头，4500mAh电池', 3999.00, 4499.00, 3000.00, 80, 15, 'http://localhost:1000/product/mi13.jpg', '["http://localhost:1000/product/mi131.jpg", "http://localhost:1000/product/mi132.jpg"]', '{"color": "黑色", "storage": "128GB"}', '小米', '小米13', 'Snapdragon 8 Gen 2', 'Adreno 740', '8GB', '128GB', 6.36, 0.185, 1, 1, 1, 300, 2000, 4.5, 200, '2024-12-05 10:00:00', '2024-12-24 10:00:00');

-- 商品分类关联表测试数据
INSERT INTO `product_category` (product_id, category_id, created_at) VALUES 
(1, 2, '2024-12-01 10:00:00'), -- iPhone 15 Pro 属于手机分类
(2, 3, '2024-12-01 10:00:00'), -- MacBook Pro 14 属于电脑分类
(3, 4, '2024-12-01 10:00:00'), -- Nike Air Max 属于服装分类
(3, 5, '2024-12-01 10:00:00'), -- Nike Air Max 也属于男装分类
(4, 3, '2024-12-01 10:00:00'); -- 华为MateBook X Pro 属于电脑分类

-- 用户地址表测试数据
INSERT INTO `user_addresses` (user_id, recipient_name, recipient_phone, province, city, district, street_address, postal_code, is_default, tag, created_at, updated_at) VALUES 
(1, '张三', '13800138001', '广东省', '深圳市', '南山区', '科技园南区高新南一道9号', '518000', 1, '公司', '2024-12-01 10:00:00', '2024-12-01 10:00:00'),
(1, '张三', '13800138001', '广东省', '深圳市', '福田区', '福华路3001号会展中心', '518000', 0, '家', '2024-12-02 10:00:00', '2024-12-02 10:00:00'),
(2, '李四', '13800138002', '北京市', '北京市', '朝阳区', '建国门外大街1号国贸大厦', '100000', 1, '公司', '2024-12-03 10:00:00', '2024-12-03 10:00:00'),
(3, '王五', '13800138003', '上海市', '上海市', '浦东新区', '世纪大道1001号', '200000', 1, '家', '2024-12-04 10:00:00', '2024-12-04 10:00:00'),
(4, '赵六', '13800138004', '浙江省', '杭州市', '西湖区', '文三路90号', '310000', 1, '学校', '2024-12-05 10:00:00', '2024-12-05 10:00:00');

-- 购物车表测试数据
INSERT INTO `carts` (user_id, session_id, item_count, total_quantity, total_amount, created_at, updated_at) VALUES 
(1, NULL, 2, 3, 8898.00, '2024-12-20 10:00:00', '2024-12-20 10:00:00'),
(2, NULL, 1, 1, 899.00, '2024-12-21 11:00:00', '2024-12-21 11:00:00'),
(3, NULL, 3, 5, 14397.00, '2024-12-22 12:00:00', '2024-12-22 12:00:00'),
(4, NULL, 1, 2, 1798.00, '2024-12-23 13:00:00', '2024-12-23 13:00:00'),
(5, NULL, 0, 0, 0.00, '2024-12-24 14:00:00', '2024-12-24 14:00:00');

-- 购物车项表测试数据
INSERT INTO `cart_items` (cart_id, product_id, quantity, unit_price, selected, created_at, updated_at) VALUES 
(1, 1, 1, 7999.00, 1, '2024-12-20 10:00:00', '2024-12-20 10:00:00'),
(1, 3, 2, 899.00, 1, '2024-12-20 10:00:00', '2024-12-20 10:00:00'),
(2, 3, 1, 899.00, 1, '2024-12-21 11:00:00', '2024-12-21 11:00:00'),
(3, 2, 1, 18999.00, 1, '2024-12-22 12:00:00', '2024-12-22 12:00:00'),
(3, 4, 1, 12999.00, 1, '2024-12-22 12:00:00', '2024-12-22 12:00:00'),
(3, 5, 3, 3999.00, 1, '2024-12-22 12:00:00', '2024-12-22 12:00:00'),
(4, 3, 2, 899.00, 1, '2024-12-23 13:00:00', '2024-12-23 13:00:00');

-- 订单表测试数据
INSERT INTO `orders` (order_no, user_id, address_id, total_amount, discount_amount, shipping_fee, final_amount, payment_method, payment_status, payment_time, payment_transaction_id, order_status, shipping_method, shipping_tracking_no, shipped_time, received_time, closed_time, cancel_reason, buyer_note, admin_note, invoice_needed, invoice_title, created_at, updated_at) VALUES 
('ORD202412200001', 1, 1, 8898.00, 0.00, 0.00, 8898.00, 'alipay', 1, '2024-12-20 11:00:00', '202412200000001', 'completed', '顺丰快递', 'SF20241220000001', '2024-12-20 14:00:00', '2024-12-22 10:00:00', NULL, NULL, '请尽快发货', '订单正常', 1, '个人', '2024-12-20 10:00:00', '2024-12-22 10:00:00'),
('ORD202412210002', 2, 3, 899.00, 50.00, 10.00, 859.00, 'wechat', 1, '2024-12-21 12:00:00', '202412210000002', 'completed', '圆通快递', 'YT20241221000002', '2024-12-21 15:00:00', '2024-12-23 10:00:00', NULL, NULL, '包装仔细', '订单正常', 0, NULL, '2024-12-21 11:00:00', '2024-12-23 10:00:00'),
('ORD202412220003', 3, 4, 14397.00, 0.00, 15.00, 14412.00, 'alipay', 1, '2024-12-22 13:00:00', '202412220000003', 'shipped', '中通快递', 'ZTO20241222000003', '2024-12-22 16:00:00', NULL, NULL, NULL, '尽快发货', '订单正常', 1, '企业', '2024-12-22 12:00:00', '2024-12-22 16:00:00'),
('ORD202412230004', 4, 5, 1798.00, 0.00, 8.00, 1806.00, 'bank', 0, NULL, NULL, 'pending', NULL, NULL, NULL, NULL, NULL, NULL, '需要发票', '待支付', 1, '个人', '2024-12-23 13:00:00', '2024-12-23 13:00:00'),
('ORD202412240005', 1, 2, 3999.00, 100.00, 0.00, 3899.00, 'alipay', 1, '2024-12-24 14:00:00', '202412240000005', 'completed', '京东快递', 'JD20241224000005', '2024-12-24 17:00:00', '2024-12-25 09:00:00', NULL, NULL, '注意包装', '订单正常', 0, NULL, '2024-12-24 14:00:00', '2024-12-25 09:00:00');

-- 订单项表测试数据
INSERT INTO `order_items` (order_id, order_no, product_id, product_name, product_image, product_specifications, unit_price, quantity, item_amount, created_at) VALUES 
(1, 'ORD202412200001', 1, 'iPhone 15 Pro', 'http://localhost:1000/product/iphone15pro.jpg', '{"color": "深空灰", "storage": "128GB"}', 7999.00, 1, 7999.00, '2024-12-20 10:00:00'),
(1, 'ORD202412200001', 3, 'Nike Air Max', 'http://localhost:1000/product/nikeairmax.jpg', '{"size": "42", "color": "黑色"}', 899.00, 1, 899.00, '2024-12-20 10:00:00'),
(2, 'ORD202412210002', 3, 'Nike Air Max', 'http://localhost:1000/product/nikeairmax.jpg', '{"size": "42", "color": "黑色"}', 899.00, 1, 899.00, '2024-12-21 11:00:00'),
(3, 'ORD202412220003', 2, 'MacBook Pro 14', 'http://localhost:1000/product/macbookpro14.jpg', '{"color": "银色", "processor": "M3"}', 18999.00, 1, 18999.00, '2024-12-22 12:00:00'),
(3, 'ORD202412220003', 4, '华为MateBook X Pro', 'http://localhost:1000/product/matebookxpro.jpg', '{"color": "深空灰", "processor": "i7-1260P"}', 12999.00, 1, 12999.00, '2024-12-22 12:00:00'),
(3, 'ORD202412220003', 5, '小米13', 'http://localhost:1000/product/mi13.jpg', '{"color": "黑色", "storage": "128GB"}', 3999.00, 3, 11997.00, '2024-12-22 12:00:00'),
(4, 'ORD202412230004', 3, 'Nike Air Max', 'http://localhost:1000/product/nikeairmax.jpg', '{"size": "42", "color": "黑色"}', 899.00, 2, 1798.00, '2024-12-23 13:00:00'),
(5, 'ORD202412240005', 5, '小米13', 'http://localhost:1000/product/mi13.jpg', '{"color": "黑色", "storage": "128GB"}', 3999.00, 1, 3999.00, '2024-12-24 14:00:00');

SET FOREIGN_KEY_CHECKS = 1;
