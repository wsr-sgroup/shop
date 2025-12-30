-- 插入商品分类数据（使用INSERT IGNORE避免主键冲突）
INSERT IGNORE INTO `categories` (`id`, `name`, `slug`, `parent_id`, `description`, `sort_order`, `is_visible`) VALUES
(1, '笔记本电脑', 'laptops', NULL, '各种品牌和型号的笔记本电脑', 1, 1),
(2, '手机', 'smartphones', NULL, '智能手机和平板电脑', 2, 1),
(3, '配件', 'accessories', NULL, '电脑和手机配件', 3, 1),
(4, '游戏本', 'gaming-laptops', 1, '高性能游戏笔记本电脑', 1, 1),
(5, '商务本', 'business-laptops', 1, '适合办公的商务笔记本电脑', 2, 1),
(6, '轻薄本', 'ultrabooks', 1, '轻薄便携的笔记本电脑', 3, 1),
(7, 'iPhone', 'iphone', 2, '苹果iPhone手机', 1, 1),
(8, '华为', 'huawei', 2, '华为手机和平板电脑', 2, 1),
(9, '小米', 'xiaomi', 2, '小米手机和平板电脑', 3, 1),
(10, '键盘鼠标', 'keyboards-mice', 3, '各种键盘和鼠标', 1, 1),
(11, '屏幕', 'monitors', 3, '显示器和显示屏', 2, 1),
(12, '电池电源', 'batteries-power', 3, '电池和电源适配器', 3, 1);

-- 插入商品数据（使用INSERT IGNORE避免主键冲突）
INSERT IGNORE INTO `products` (`id`, `sku`, `name`, `subtitle`, `description`, `price`, `original_price`, `cost_price`, `stock`, `stock_low_threshold`, `main_image`, `image_gallery`, `specifications`, `series`, `model`, `cpu`, `gpu`, `ram`, `storage`, `screen_size`, `weight`, `is_on_sale`, `is_featured`, `is_hot`, `sales_count`, `view_count`, `rating_avg`, `review_count`)
VALUES
(1001, 'PROD-1001', 'ThinkPad X1 Carbon 2024款', '14英寸商务轻薄笔记本电脑', '联想ThinkPad X1 Carbon 2024款，14英寸OLED屏，Intel i7处理器，16GB内存，512GB SSD', 12999.00, 14999.00, 9999.00, 50, 10, 'https://example.com/images/thinkpad-x1.jpg', '["https://example.com/images/thinkpad-x1-1.jpg", "https://example.com/images/thinkpad-x1-2.jpg"]', '{"屏幕分辨率":"2.8K","屏幕刷新率":"60Hz","显卡":"集成显卡","操作系统":"Windows 11 Pro","颜色":"黑色"}', 'ThinkPad X1', 'X1 Carbon 2024', 'Intel i7-1360P', 'Intel Iris Xe Graphics', '16GB LPDDR5', '512GB PCIe 4.0 SSD', 14.0, 1.12, 1, 1, 1, 250, 5000, 4.8, 120),
(1002, 'PROD-1002', 'MacBook Air M3 2024款', '13英寸视网膜屏笔记本电脑', '苹果MacBook Air M3 2024款，13英寸视网膜屏，M3芯片，8GB内存，256GB SSD', 9999.00, 10999.00, 7999.00, 30, 5, 'https://example.com/images/macbook-air-m3.jpg', '["https://example.com/images/macbook-air-m3-1.jpg", "https://example.com/images/macbook-air-m3-2.jpg"]', '{"屏幕分辨率":"2560x1600","屏幕刷新率":"60Hz","显卡":"M3集成显卡","操作系统":"macOS Sonoma","颜色":"银色"}', 'MacBook Air', 'MacBook Air M3', 'Apple M3', 'Apple M3 GPU', '8GB Unified Memory', '256GB SSD', 13.6, 1.24, 1, 1, 1, 180, 4200, 4.9, 95),
(1003, 'PROD-1003', 'iPhone 16 Pro Max', '6.7英寸智能手机', '苹果iPhone 16 Pro Max，6.7英寸Super Retina XDR屏，A18 Pro芯片，256GB存储', 9999.00, 10999.00, 7999.00, 40, 10, 'https://example.com/images/iphone-16-pro-max.jpg', '["https://example.com/images/iphone-16-pro-max-1.jpg", "https://example.com/images/iphone-16-pro-max-2.jpg"]', '{"屏幕分辨率":"2796x1290","屏幕刷新率":"120Hz","摄像头":"48MP+12MP+12MP","电池容量":"5000mAh","颜色":"深空黑色"}', 'iPhone', 'iPhone 16 Pro Max', 'Apple A18 Pro', 'Apple A18 Pro GPU', '8GB', '256GB', 6.7, 0.240, 1, 1, 1, 320, 6500, 4.7, 180),
(1004, 'PROD-1004', '华为 MateBook X Pro 2024款', '14.2英寸触屏笔记本电脑', '华为MateBook X Pro 2024款，14.2英寸3.1K触屏，13代酷睿i7处理器', 8999.00, 9999.00, 6999.00, 25, 5, 'https://example.com/images/huawei-matebook-x-pro.jpg', '["https://example.com/images/huawei-matebook-x-pro-1.jpg", "https://example.com/images/huawei-matebook-x-pro-2.jpg"]', '{"屏幕分辨率":"3120x2080","屏幕刷新率":"90Hz","显卡":"RTX 3050","操作系统":"Windows 11 Home","颜色":"深空灰"}', 'MateBook', 'MateBook X Pro 2024', 'Intel i7-1360P', 'NVIDIA GeForce RTX 3050', '16GB LPDDR5', '512GB PCIe 4.0 SSD', 14.2, 1.26, 1, 1, 1, 150, 3800, 4.6, 75),
(1005, 'PROD-1005', '罗技 MX Master 3S 无线鼠标', '高性能无线办公鼠标', '罗技MX Master 3S无线鼠标，支持多设备连接，高精度传感器', 699.00, 799.00, 499.00, 100, 20, 'https://example.com/images/logitech-mx-master-3s.jpg', '["https://example.com/images/logitech-mx-master-3s-1.jpg", "https://example.com/images/logitech-mx-master-3s-2.jpg"]', '{"连接方式":"无线/蓝牙","传感器":"25K DPI","电池续航":"70天","兼容系统":"Windows/Mac/Linux","颜色":"石墨黑"}', 'MX Master', 'MX Master 3S', NULL, NULL, NULL, NULL, NULL, 0.141, 1, 1, 1, 420, 8500, 4.9, 250);

-- 插入商品分类关联数据（使用INSERT IGNORE避免主键冲突）
INSERT IGNORE INTO `product_category` (`product_id`, `category_id`) VALUES
(1001, 1), -- ThinkPad X1 Carbon 属于笔记本电脑
(1001, 5), -- ThinkPad X1 Carbon 属于商务本
(1002, 1), -- MacBook Air 属于笔记本电脑
(1002, 6), -- MacBook Air 属于轻薄本
(1003, 2), -- iPhone 16 Pro Max 属于手机
(1003, 7), -- iPhone 16 Pro Max 属于iPhone
(1004, 1), -- 华为MateBook X Pro 属于笔记本电脑
(1004, 5), -- 华为MateBook X Pro 属于商务本
(1004, 6), -- 华为MateBook X Pro 属于轻薄本
(1005, 3), -- 罗技MX Master 3S 属于配件
(1005, 10); -- 罗技MX Master 3S 属于键盘鼠标

-- 更新自增ID起始值
ALTER TABLE `categories` AUTO_INCREMENT = 13;
ALTER TABLE `products` AUTO_INCREMENT = 1006;
