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

SET FOREIGN_KEY_CHECKS = 1;
