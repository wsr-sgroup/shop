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
  `image` VARCHAR(255) DEFAULT NULL COMMENT '分类图片URL',
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
INSERT INTO `categories` (name, slug, parent_id, description, sort_order, is_visible, image, created_at, updated_at) VALUES 
('手机', 'mobile-phones', NULL, '各类手机产品', 40, 1, 'http://localhost:1000/file/手机.jpg', '2024-12-01 10:00:00', '2024-12-01 10:00:00'),
('平板', 'tablets', NULL, '各类平板电脑产品', 30, 1, 'http://localhost:1000/file/小新平板.jpg', '2024-12-01 10:00:00', '2024-12-01 10:00:00'),
('笔记本电脑', 'laptops', NULL, '各类笔记本电脑产品', 20, 1, 'http://localhost:1000/file/y9000p.jpg', '2024-12-01 10:00:00', '2024-12-01 10:00:00'),
('其他', 'other', NULL, '其他电子产品及配件', 10, 1, 'http://localhost:1000/file/显示屏.jpg', '2024-12-01 10:00:00', '2024-12-01 10:00:00');

-- 商品表测试数据
INSERT INTO `products` (sku, name, subtitle, description, price, original_price, cost_price, stock, stock_low_threshold, main_image, image_gallery, specifications, series, model, cpu, gpu, ram, storage, screen_size, weight, is_on_sale, is_featured, is_hot, sales_count, view_count, rating_avg, review_count, created_at, updated_at) VALUES 
('PROD001', '联想拯救者R9000P', '2024款高性能游戏本', '联想拯救者R9000P 2024款，搭载AMD R7处理器和RTX4060独显，适合游戏和创作', 8999.00, 9999.00, 7500.00, 30, 5, 'http://localhost:1000/file/r9000p.jpg', '["http://localhost:1000/file/r9000p.jpg"]', '{"颜色": "灰色", "屏幕尺寸": "16英寸", "刷新率": "240Hz"}', '拯救者', 'R9000P', 'AMD R7 7745HX', 'NVIDIA GeForce RTX 4060', '16GB DDR5', '1TB SSD', 16.0, 2.54, 1, 1, 1, 120, 1800, 4.7, 65, '2024-12-01 10:00:00', '2024-12-20 10:00:00'),
('PROD002', '联想Y7000P', '2024款高性能笔记本', '联想Y7000P 2024款，搭载i7处理器和RTX4060独显，轻薄便携性能强', 7499.00, 8499.00, 6200.00, 25, 5, 'http://localhost:1000/file/y7000p.jpg', '["http://localhost:1000/file/y7000p.jpg"]', '{"颜色": "黑色", "屏幕尺寸": "15.6英寸", "刷新率": "120Hz"}', 'Y7000P', 'Y7000P-2024', 'Intel i7-13700H', 'NVIDIA GeForce RTX 4060', '16GB DDR5', '512GB SSD', 15.6, 2.21, 1, 1, 0, 85, 1200, 4.6, 45, '2024-12-02 10:00:00', '2024-12-21 10:00:00'),
('PROD003', '联想Y9000P', '2024款旗舰游戏本', '联想Y9000P 2024款，搭载i9处理器和RTX4070独显，旗舰级游戏体验', 12999.00, 13999.00, 11000.00, 15, 5, 'http://localhost:1000/file/y9000p.jpg', '["http://localhost:1000/file/y9000p.jpg"]', '{"颜色": "灰色", "屏幕尺寸": "16英寸", "刷新率": "240Hz"}', 'Y9000P', 'Y9000P-2024', 'Intel i9-13900HX', 'NVIDIA GeForce RTX 4070', '32GB DDR5', '1TB SSD', 16.0, 2.56, 1, 1, 1, 65, 950, 4.8, 38, '2024-12-03 10:00:00', '2024-12-22 10:00:00'),
('PROD004', '联想小新Pro14', '2024款轻薄本', '联想小新Pro14 2024款，搭载R5处理器，轻薄便携，适合办公学习', 4599.00, 5299.00, 3600.00, 40, 10, 'http://localhost:1000/file/小新pro14.jpg', '["http://localhost:1000/file/小新pro14.jpg"]', '{"颜色": "银色", "屏幕尺寸": "14英寸", "刷新率": "120Hz"}', '小新Pro', '小新Pro14', 'AMD R5 7640HS', 'AMD Radeon 680M', '16GB DDR5', '512GB SSD', 14.0, 1.32, 1, 0, 1, 200, 2500, 4.5, 120, '2024-12-04 10:00:00', '2024-12-23 10:00:00'),
('PROD005', '联想小新Pro16', '2024款创作本', '联想小新Pro16 GT版，搭载R7处理器和RTX4050独显，适合内容创作', 5999.00, 6699.00, 4800.00, 35, 8, 'http://localhost:1000/file/小新pro16gt.jpg', '["http://localhost:1000/file/小新pro16gt.jpg"]', '{"颜色": "灰色", "屏幕尺寸": "16英寸", "刷新率": "120Hz"}', '小新Pro', '小新Pro16 GT', 'AMD R7 7840HS', 'NVIDIA GeForce RTX 4050', '16GB DDR5', '1TB SSD', 16.0, 1.92, 1, 1, 1, 150, 1800, 4.6, 85, '2024-12-05 10:00:00', '2024-12-24 10:00:00'),
('PROD006', '联想小新16', '2024款大屏轻薄本', '联想小新16 2024款，16英寸大屏，性价比之选，适合日常办公娱乐', 3999.00, 4499.00, 3200.00, 50, 10, 'http://localhost:1000/file/小新16.jpg', '["http://localhost:1000/file/小新16.jpg"]', '{"颜色": "银色", "屏幕尺寸": "16英寸", "刷新率": "60Hz"}', '小新', '小新16', 'AMD R5 7530U', 'AMD Radeon 610M', '8GB DDR4', '512GB SSD', 16.0, 1.75, 1, 0, 0, 180, 1600, 4.4, 95, '2024-12-06 10:00:00', '2024-12-25 10:00:00'),
('PROD007', '联想小新平板', '2024款智能平板', '联想小新平板，11英寸高清屏，支持手写笔，适合阅读和轻度办公', 2299.00, 2599.00, 1800.00, 60, 10, 'http://localhost:1000/file/小新平板.jpg', '["http://localhost:1000/file/小新平板.jpg", "http://localhost:1000/file/小新平板2.jpg"]', '{"颜色": "灰色", "屏幕尺寸": "11英寸", "存储": "128GB"}', '小新平板', '小新平板2024', '联发科 Kompanio 1300T', 'ARM Mali-G77 MC9', '6GB', '128GB', 11.0, 0.45, 1, 0, 1, 95, 1100, 4.3, 75, '2024-12-07 10:00:00', '2024-12-26 10:00:00'),
('PROD008', '华为Pura70', '2024款旗舰手机', '华为Pura70 Pro，麒麟9010处理器，XMAGE影像系统，卫星通信', 6999.00, 7999.00, 5600.00, 45, 5, 'http://localhost:1000/file/手机.jpg', '["http://localhost:1000/file/手机.jpg"]', '{"颜色": "白色", "存储": "256GB", "屏幕尺寸": "6.8英寸"}', 'Pura', 'Pura70 Pro', '麒麟9010', 'Maleoon 910', '12GB', '256GB', 6.8, 0.22, 1, 1, 1, 280, 3200, 4.8, 180, '2024-12-08 10:00:00', '2024-12-27 10:00:00'),
('PROD009', '联想斗战者', 'G5000台式主机', '联想斗战者G5000，i5处理器搭配RTX4060独显，游戏台式机', 5299.00, 5999.00, 4200.00, 20, 5, 'http://localhost:1000/file/斗战者.jpg', '["http://localhost:1000/file/斗战者.jpg"]', '{"颜色": "白色", "机箱": "中塔式", "散热": "风冷"}', '斗战者', 'G5000', 'Intel i5-13400F', 'NVIDIA GeForce RTX 4060', '16GB DDR4', '1TB SSD', NULL, 7.50, 1, 0, 1, 110, 1300, 4.6, 55, '2024-12-09 10:00:00', '2024-12-28 10:00:00'),
('PROD010', '联想显示屏', '27英寸4K显示器', '联想27英寸4K超清显示器，IPS面板，99% sRGB色域，护眼设计', 1899.00, 2199.00, 1500.00, 30, 5, 'http://localhost:1000/file/显示屏.jpg', '["http://localhost:1000/file/显示屏.jpg"]', '{"颜色": "黑色", "屏幕尺寸": "27英寸", "分辨率": "3840x2160"}', '显示器', 'Q27h', '无', '无', '无', '无', 27.0, 4.20, 1, 0, 0, 150, 900, 4.5, 40, '2024-12-10 10:00:00', '2024-12-29 10:00:00'),
('PROD011', '联想主机', 'M900t台式机', '联想M900t商用台式机，i7处理器，8GB内存，256GB SSD，稳定可靠', 4299.00, 4799.00, 3400.00, 25, 5, 'http://localhost:1000/file/主机.jpg', '["http://localhost:1000/file/主机.jpg"]', '{"颜色": "黑色", "机箱": "小型立式", "用途": "商务办公"}', 'M900t', 'M900t', 'Intel i7-10700', 'Intel UHD Graphics 630', '8GB DDR4', '256GB SSD', NULL, 6.80, 1, 0, 0, 75, 700, 4.4, 30, '2024-12-11 10:00:00', '2024-12-30 10:00:00'),
('PROD012', '监控摄像头', '智能网络摄像头', '1080P高清智能网络摄像头，夜视功能，远程监控，家用安防', 299.00, 399.00, 180.00, 100, 20, 'http://localhost:1000/file/摄像头.jpg', '["http://localhost:1000/file/摄像头.jpg"]', '{"颜色": "白色", "像素": "200万", "功能": "夜视,双向语音"}', '摄像头', 'C200', '无', '无', '无', '无', NULL, 0.30, 1, 0, 1, 320, 2100, 4.2, 180, '2024-12-12 10:00:00', '2024-12-31 10:00:00');

-- 商品分类关联表测试数据
INSERT INTO `product_category` (product_id, category_id, created_at) VALUES 
(1, 3, '2024-12-01 10:00:00'), -- 联想拯救者R9000P 属于笔记本电脑分类
(2, 3, '2024-12-01 10:00:00'), -- 联想Y7000P 属于笔记本电脑分类
(3, 3, '2024-12-01 10:00:00'), -- 联想Y9000P 属于笔记本电脑分类
(4, 3, '2024-12-01 10:00:00'), -- 联想小新Pro14 属于笔记本电脑分类
(5, 3, '2024-12-01 10:00:00'), -- 联想小新Pro16 属于笔记本电脑分类
(6, 3, '2024-12-01 10:00:00'), -- 联想小新16 属于笔记本电脑分类
(7, 2, '2024-12-01 10:00:00'), -- 联想小新平板 属于平板分类
(8, 1, '2024-12-01 10:00:00'), -- 华为Pura70 属于手机分类
(9, 4, '2024-12-01 10:00:00'), -- 联想斗战者 属于其他分类
(10, 4, '2024-12-01 10:00:00'), -- 联想显示屏 属于其他分类
(11, 4, '2024-12-01 10:00:00'), -- 联想主机 属于其他分类
(12, 4, '2024-12-01 10:00:00'); -- 监控摄像头 属于其他分类

-- 用户地址表测试数据
INSERT INTO `user_addresses` (user_id, recipient_name, recipient_phone, province, city, district, street_address, postal_code, is_default, tag, created_at, updated_at) VALUES 
(1, '张三', '13800138001', '广东省', '深圳市', '南山区', '科技园南区高新南一道9号', '518000', 1, '公司', '2024-12-01 10:00:00', '2024-12-01 10:00:00'),
(1, '张三', '13800138001', '广东省', '深圳市', '福田区', '福华路3001号会展中心', '518000', 0, '家', '2024-12-02 10:00:00', '2024-12-02 10:00:00'),
(2, '李四', '13800138002', '北京市', '北京市', '朝阳区', '建国门外大街1号国贸大厦', '100000', 1, '公司', '2024-12-03 10:00:00', '2024-12-03 10:00:00'),
(3, '王五', '13800138003', '上海市', '上海市', '浦东新区', '世纪大道1001号', '200000', 1, '家', '2024-12-04 10:00:00', '2024-12-04 10:00:00'),
(4, '赵六', '13800138004', '浙江省', '杭州市', '西湖区', '文三路90号', '310000', 1, '学校', '2024-12-05 10:00:00', '2024-12-05 10:00:00');

-- 购物车表测试数据
INSERT INTO `carts` (user_id, session_id, item_count, total_quantity, total_amount, created_at, updated_at) VALUES 
(1, NULL, 2, 3, 13597.00, '2024-12-20 10:00:00', '2024-12-20 10:00:00'),
(2, NULL, 1, 1, 2299.00, '2024-12-21 11:00:00', '2024-12-21 11:00:00'),
(3, NULL, 3, 5, 32896.00, '2024-12-22 12:00:00', '2024-12-22 12:00:00'),
(4, NULL, 1, 2, 4598.00, '2024-12-23 13:00:00', '2024-12-23 13:00:00'),
(5, NULL, 0, 0, 0.00, '2024-12-24 14:00:00', '2024-12-24 14:00:00');

-- 购物车项表测试数据
INSERT INTO `cart_items` (cart_id, product_id, quantity, unit_price, selected, created_at, updated_at) VALUES 
(1, 1, 1, 8999.00, 1, '2024-12-20 10:00:00', '2024-12-20 10:00:00'),
(1, 7, 2, 2299.00, 1, '2024-12-20 10:00:00', '2024-12-20 10:00:00'),
(2, 7, 1, 2299.00, 1, '2024-12-21 11:00:00', '2024-12-21 11:00:00'),
(3, 2, 1, 7499.00, 1, '2024-12-22 12:00:00', '2024-12-22 12:00:00'),
(3, 4, 1, 4599.00, 1, '2024-12-22 12:00:00', '2024-12-22 12:00:00'),
(3, 8, 3, 6999.00, 1, '2024-12-22 12:00:00', '2024-12-22 12:00:00'),
(4, 7, 2, 2299.00, 1, '2024-12-23 13:00:00', '2024-12-23 13:00:00');

-- 订单表测试数据
INSERT INTO `orders` (order_no, user_id, address_id, total_amount, discount_amount, shipping_fee, final_amount, payment_method, payment_status, payment_time, payment_transaction_id, order_status, shipping_method, shipping_tracking_no, shipped_time, received_time, closed_time, cancel_reason, buyer_note, admin_note, invoice_needed, invoice_title, created_at, updated_at) VALUES 
('ORD202412200001', 1, 1, 11298.00, 0.00, 0.00, 11298.00, 'alipay', 1, '2024-12-20 11:00:00', '202412200000001', 'completed', '顺丰快递', 'SF20241220000001', '2024-12-20 14:00:00', '2024-12-22 10:00:00', NULL, NULL, '请尽快发货', '订单正常', 1, '个人', '2024-12-20 10:00:00', '2024-12-22 10:00:00'),
('ORD202412210002', 2, 3, 2299.00, 50.00, 10.00, 2259.00, 'wechat', 1, '2024-12-21 12:00:00', '202412210000002', 'completed', '圆通快递', 'YT20241221000002', '2024-12-21 15:00:00', '2024-12-23 10:00:00', NULL, NULL, '包装仔细', '订单正常', 0, NULL, '2024-12-21 11:00:00', '2024-12-23 10:00:00'),
('ORD202412220003', 3, 4, 32095.00, 0.00, 15.00, 32110.00, 'alipay', 1, '2024-12-22 13:00:00', '202412220000003', 'shipped', '中通快递', 'ZTO20241222000003', '2024-12-22 16:00:00', NULL, NULL, NULL, '尽快发货', '订单正常', 1, '企业', '2024-12-22 12:00:00', '2024-12-22 16:00:00'),
('ORD202412230004', 4, 5, 4598.00, 0.00, 8.00, 4606.00, 'bank', 0, NULL, NULL, 'pending', NULL, NULL, NULL, NULL, NULL, NULL, '需要发票', '待支付', 1, '个人', '2024-12-23 13:00:00', '2024-12-23 13:00:00'),
('ORD202412240005', 1, 2, 6999.00, 100.00, 0.00, 6899.00, 'alipay', 1, '2024-12-24 14:00:00', '202412240000005', 'completed', '京东快递', 'JD20241224000005', '2024-12-24 17:00:00', '2024-12-25 09:00:00', NULL, NULL, '注意包装', '订单正常', 0, NULL, '2024-12-24 14:00:00', '2024-12-25 09:00:00');

-- 订单项表测试数据
INSERT INTO `order_items` (order_id, order_no, product_id, product_name, product_image, product_specifications, unit_price, quantity, item_amount, created_at) VALUES 
(1, 'ORD202412200001', 1, '联想拯救者R9000P', 'http://localhost:1000/file/r9000p.jpg', '{"颜色": "灰色", "屏幕尺寸": "16英寸", "刷新率": "240Hz"}', 8999.00, 1, 8999.00, '2024-12-20 10:00:00'),
(1, 'ORD202412200001', 7, '联想小新平板', 'http://localhost:1000/file/小新平板.jpg', '{"颜色": "灰色", "屏幕尺寸": "11英寸", "存储": "128GB"}', 2299.00, 1, 2299.00, '2024-12-20 10:00:00'),
(2, 'ORD202412210002', 7, '联想小新平板', 'http://localhost:1000/file/小新平板.jpg', '{"颜色": "灰色", "屏幕尺寸": "11英寸", "存储": "128GB"}', 2299.00, 1, 2299.00, '2024-12-21 11:00:00'),
(3, 'ORD202412220003', 2, '联想Y7000P', 'http://localhost:1000/file/y7000p.jpg', '{"颜色": "黑色", "屏幕尺寸": "15.6英寸", "刷新率": "120Hz"}', 7499.00, 1, 7499.00, '2024-12-22 12:00:00'),
(3, 'ORD202412220003', 4, '联想小新Pro14', 'http://localhost:1000/file/小新pro14.jpg', '{"颜色": "银色", "屏幕尺寸": "14英寸", "刷新率": "120Hz"}', 4599.00, 1, 4599.00, '2024-12-22 12:00:00'),
(3, 'ORD202412220003', 8, '华为Pura70', 'http://localhost:1000/file/手机.jpg', '{"颜色": "白色", "存储": "256GB", "屏幕尺寸": "6.8英寸"}', 6999.00, 3, 20997.00, '2024-12-22 12:00:00'),
(4, 'ORD202412230004', 7, '联想小新平板', 'http://localhost:1000/file/小新平板.jpg', '{"颜色": "灰色", "屏幕尺寸": "11英寸", "存储": "128GB"}', 2299.00, 2, 4598.00, '2024-12-23 13:00:00'),
(5, 'ORD202412240005', 8, '华为Pura70', 'http://localhost:1000/file/手机.jpg', '{"颜色": "白色", "存储": "256GB", "屏幕尺寸": "6.8英寸"}', 6999.00, 1, 6999.00, '2024-12-24 14:00:00');

SET FOREIGN_KEY_CHECKS = 1;
