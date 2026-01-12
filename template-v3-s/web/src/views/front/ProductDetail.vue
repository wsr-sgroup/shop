<template>
  <div class="product-detail">
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="image-gallery">
          <el-image
            :src="currentImage"
            class="main-image"
            fit="contain"
          ></el-image>
          <div class="thumbnail-list">
            <el-image
              v-for="(image, index) in imageList"
              :key="index"
              :src="image"
              class="thumbnail"
              :class="{ active: currentImage === image }"
              @click="currentImage = image"
              fit="cover"
            ></el-image>
          </div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="product-info">
          <h1 class="product-title">{{ product.name }}</h1>
          <div class="product-price">¥{{ product.price }}</div>
          
          <div class="product-meta">
            <div class="meta-item">
              <span class="label">商品编号：</span>
              <span class="value">{{ product.sku }}</span>
            </div>
            <div class="meta-item">
              <span class="label">库存状态：</span>
              <span class="value">{{ product.stock > 0 ? '有货' : '缺货' }}</span>
            </div>
          </div>
          
          <div class="product-specs">
            <div class="spec-item">
              <span class="label">处理器：</span>
              <span class="value">{{ product.cpu }}</span>
            </div>
            <div class="spec-item">
              <span class="label">内存：</span>
              <span class="value">{{ product.ram }}</span>
            </div>
            <div class="spec-item">
              <span class="label">存储：</span>
              <span class="value">{{ product.storage }}</span>
            </div>
            <div class="spec-item">
              <span class="label">显卡：</span>
              <span class="value">{{ product.gpu }}</span>
            </div>
            <div class="spec-item">
              <span class="label">屏幕：</span>
              <span class="value">{{ product.screenSize }}英寸</span>
            </div>
          </div>
          
          <div class="purchase-section">
            <div class="quantity-control">
              <span class="label">数量：</span>
              <el-input-number 
                v-model="quantity" 
                :min="1" 
                :max="Math.max(1, product.stock)"
                size="large"
              />
            </div>
            
            <div class="action-buttons">
              <el-button 
                type="primary" 
                size="large" 
                class="buy-button"
                @click="buyNow"
              >
                立即购买
              </el-button>
              <el-button 
                type="warning" 
                size="large" 
                class="cart-button"
                @click="addToCart"
              >
                加入购物车
              </el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <div class="product-description">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="商品介绍" name="description">
          <div class="description-content" v-html="product.description"></div>
        </el-tab-pane>
        <el-tab-pane label="规格参数" name="specs">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="处理器">{{ product.cpu }}</el-descriptions-item>
            <el-descriptions-item label="内存">{{ product.ram }}</el-descriptions-item>
            <el-descriptions-item label="存储">{{ product.storage }}</el-descriptions-item>
            <el-descriptions-item label="显卡">{{ product.gpu }}</el-descriptions-item>
            <el-descriptions-item label="屏幕">{{ product.screenSize }}英寸</el-descriptions-item>
            <el-descriptions-item label="重量">{{ product.weight }}kg</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '@/utils/http.js'

const route = useRoute()
const router = useRouter()

// 当前选中的图片
const currentImage = ref('')

// 数量
const quantity = ref(1)

// 激活的标签页
const activeTab = ref('description')

// 商品信息
const product = ref({
  id: '',
  name: '',
  price: '',
  sku: '',
  stock: 0,
  cpu: '',
  ram: '',
  storage: '',
  gpu: '',
  screenSize: '',
  weight: '',
  mainImage: '',
  imageGallery: '',
  description: ''
})

// 图片列表（从imageGallery解析或使用默认图片）
const imageList = computed(() => {
  if (product.value.imageGallery) {
    try {
      const images = JSON.parse(product.value.imageGallery)
      return Array.isArray(images) ? images : []
    } catch (e) {
      console.error('解析图片列表失败:', e)
      return []
    }
  }
  // 如果没有图片列表，使用主图或默认图片
  return product.value.mainImage ? [product.value.mainImage] : []
})

// 立即购买
const buyNow = () => {
  // 应该跳转到订单确认页面
  router.push({
    path: '/checkout',
    query: {
      productId: product.value.id,
      quantity: quantity.value
    }
  })
}

// 加入购物车
const addToCart = () => {
  // 检查用户是否已登录
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
  if (!currentUser || !currentUser.id) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 调用后端API将商品添加到购物车
  http.post('/api/cart/add', {
    userId: currentUser.id,
    productId: product.value.id,
    quantity: quantity.value
  }).then(res => {
    if (res && res.code === 200) {
      ElMessage.success(`已将 ${quantity.value} 件商品添加到购物车`)
    } else {
      ElMessage.error(res ? res.msg : '添加购物车失败')
    }
  }).catch(error => {
    ElMessage.error('请求添加购物车出错')
    console.error(error)
  })
}

// 获取商品详情
const getProductDetail = (id) => {
  http.get(`/product/${id}`).then(res => {
    console.log('商品详情响应:', res)
    if (res && res.code === 200) {
      product.value = res.data
      // 设置默认显示第一张图片
      if (imageList.value.length > 0) {
        currentImage.value = imageList.value[0]
      }
    } else {
      ElMessage.error('获取商品详情失败')
    }
  }).catch(error => {
    ElMessage.error('请求商品详情出错')
    console.error(error)
  })
}

onMounted(() => {
  // 根据路由参数获取商品ID，并请求商品详情
  const productId = route.params.id
  console.log('获取商品详情，商品ID:', productId)
  getProductDetail(productId)
})
</script>

<style scoped>
/* 商品详情页主容器 - 炫彩流动背景 */
.product-detail {
  padding: 30px;
  background: linear-gradient(135deg, 
    #fa709a 0%, 
    #40f8fefe 25%, 
    #ff6b6b 50%, 
    #fe57e2 75%, 
    #fa709a 100%);
  background-size: 400% 400%;
  animation: gradientFlow 20s ease infinite;
  min-height: 100vh;
}

@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 图片画廊 - 玻璃拟态效果 */
.image-gallery {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border: 2px solid rgba(255, 255, 255, 0.5);
  transition: all 0.4s ease;
}

.image-gallery:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

/* 主图展示区 */
.main-image {
  width: 100%;
  height: 450px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 15px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15) inset;
}

.main-image::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, 
    transparent 30%, 
    rgba(255, 255, 255, 0.1) 50%, 
    transparent 70%);
  animation: shimmerEffect 3s infinite;
}

@keyframes shimmerEffect {
  0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
  100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
}

/* 缩略图列表 */
.thumbnail-list {
  display: flex;
  margin-top: 20px;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
}

/* 缩略图样式 - 彩虹边框 */
.thumbnail {
  width: 90px;
  height: 90px;
  cursor: pointer;
  border: 3px solid transparent;
  border-radius: 12px;
  transition: all 0.4s ease;
  background: linear-gradient(white, white) padding-box,
              linear-gradient(135deg, #fa709a, #fee140, #ff6b6b) border-box;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.thumbnail:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 8px 25px rgba(250, 112, 154, 0.4);
}

.thumbnail.active {
  border: 3px solid transparent;
  background: linear-gradient(white, white) padding-box,
              linear-gradient(135deg, #ffd700, #ffed4e, #ffd700) border-box;
  transform: scale(1.15);
  box-shadow: 0 0 30px rgba(255, 215, 0, 0.6),
              0 8px 25px rgba(255, 215, 0, 0.3);
  animation: activePulse 2s ease-in-out infinite;
}

@keyframes activePulse {
  0%, 100% { box-shadow: 0 0 30px rgba(255, 215, 0, 0.6); }
  50% { box-shadow: 0 0 50px rgba(255, 215, 0, 0.8); }
}

/* 商品信息卡片 - 玻璃拟态 */
.product-info {
  padding: 35px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border: 2px solid rgba(255, 255, 255, 0.5);
  transition: all 0.4s ease;
}

.product-info:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

/* 商品标题 - 彩虹渐变 */
.product-title {
  font-size: 32px;
  margin-bottom: 20px;
  background: linear-gradient(90deg, 
    #fa709a 0%, 
    #fee140 33%, 
    #ff6b6b 66%, 
    #feca57 100%);
  background-size: 200% auto;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 900;
  letter-spacing: 1px;
  animation: titleShine 3s linear infinite;
}

@keyframes titleShine {
  0% { background-position: 0% center; }
  100% { background-position: 200% center; }
}

/* 价格标签 - 闪耀金色 */
.product-price {
  font-size: 42px;
  font-weight: 900;
  margin-bottom: 30px;
  background: linear-gradient(135deg, 
    #ff6b6b 0%, 
    #ff8e53 25%, 
    #ffd93d 50%, 
    #ff8e53 75%, 
    #ff6b6b 100%);
  background-size: 200% auto;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: priceGlow 3s ease-in-out infinite;
  text-shadow: 0 0 30px rgba(255, 107, 107, 0.5);
  position: relative;
}

.product-price::before {
  content: '¥';
  font-size: 28px;
  margin-right: 5px;
}

@keyframes priceGlow {
  0%, 100% { filter: brightness(1); }
  50% { filter: brightness(1.3); }
}

/* 商品元信息和规格 */
.product-meta, .product-specs {
  margin-bottom: 30px;
}

/* 信息项 - 渐变卡片 */
.meta-item, .spec-item {
  margin-bottom: 12px;
  padding: 15px;
  border-radius: 12px;
  background: linear-gradient(135deg, 
    rgba(250, 112, 154, 0.08) 0%, 
    rgba(254, 225, 64, 0.08) 100%);
  border-left: 4px solid transparent;
  background-origin: border-box;
  background-clip: padding-box, border-box;
  background-image: linear-gradient(white, white),
                    linear-gradient(135deg, #fa709a, #fee140);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.meta-item::before, .spec-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 0;
  height: 100%;
  background: linear-gradient(90deg, 
    rgba(250, 112, 154, 0.15) 0%, 
    rgba(254, 225, 64, 0.15) 100%);
  transition: width 0.4s ease;
  z-index: 0;
}

.meta-item:hover::before, .spec-item:hover::before {
  width: 100%;
}

.meta-item:hover, .spec-item:hover {
  transform: translateX(10px);
  box-shadow: 0 5px 20px rgba(250, 112, 154, 0.2);
}

.label {
  font-weight: 700;
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  position: relative;
  z-index: 1;
}

.value {
  color: #333;
  font-weight: 500;
  position: relative;
  z-index: 1;
}

/* 数量控制区 */
.quantity-control {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  padding: 20px;
  background: linear-gradient(135deg, 
    rgba(250, 112, 154, 0.1) 0%, 
    rgba(254, 225, 64, 0.1) 100%);
  border-radius: 15px;
  border: 2px solid rgba(250, 112, 154, 0.3);
  box-shadow: 0 4px 15px rgba(250, 112, 154, 0.2);
}

.quantity-control .label {
  margin-right: 20px;
  font-weight: 700;
  font-size: 16px;
}

/* 数量输入框美化 */
.quantity-control :deep(.el-input-number) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.quantity-control :deep(.el-input-number__decrease),
.quantity-control :deep(.el-input-number__increase) {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
  border: none;
  transition: all 0.3s ease;
}

.quantity-control :deep(.el-input-number__decrease):hover,
.quantity-control :deep(.el-input-number__increase):hover {
  background: linear-gradient(135deg, #fee140 0%, #ff6b6b 100%);
  transform: scale(1.1);
}

/* 操作按钮区 */
.action-buttons {
  display: flex;
  gap: 20px;
}

.buy-button, .cart-button {
  flex: 1;
  border: none;
  border-radius: 15px;
  font-weight: bold;
  font-size: 16px;
  letter-spacing: 2px;
  text-transform: uppercase;
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;
  padding: 15px 30px;
}

/* 立即购买按钮 - 橙红渐变 */
.buy-button {
  background: linear-gradient(135deg, 
    #fa709a 0%, 
    #fee140 50%, 
    #ff6b6b 100%) !important;
  background-size: 200% auto;
  box-shadow: 0 8px 25px rgba(250, 112, 154, 0.5);
}

.buy-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(255, 255, 255, 0.4), 
    transparent);
  transition: left 0.6s;
}

.buy-button:hover::before {
  left: 100%;
}

.buy-button:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 15px 35px rgba(250, 112, 154, 0.7);
  background-position: right center;
}

/* 加入购物车按钮 - 粉橙渐变 */
.cart-button {
  background: linear-gradient(135deg, 
    #ff9a9e 0%, 
    #fecfef 50%, 
    #fad0c4 100%) !important;
  background-size: 200% auto;
  box-shadow: 0 8px 25px rgba(255, 154, 158, 0.5);
}

.cart-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(255, 255, 255, 0.4), 
    transparent);
  transition: left 0.6s;
}

.cart-button:hover::before {
  left: 100%;
}

.cart-button:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 15px 35px rgba(255, 154, 158, 0.7);
  background-position: right center;
}

/* 商品描述区 - 玻璃拟态 */
.product-description {
  margin-top: 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border: 2px solid rgba(255, 255, 255, 0.5);
}

/* 标签页样式美化 */
:deep(.el-tabs__nav-wrap)::after {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  height: 3px;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 600;
  color: #666;
  transition: all 0.3s ease;
}

:deep(.el-tabs__item:hover) {
  color: #667eea;
  transform: translateY(-2px);
}

:deep(.el-tabs__item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 800;
}

:deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  height: 4px;
  border-radius: 2px;
}

/* 描述内容区 */
.description-content {
  line-height: 2;
  padding: 25px;
  background: linear-gradient(135deg, 
    rgba(102, 126, 234, 0.05) 0%, 
    rgba(118, 75, 162, 0.05) 100%);
  border-radius: 15px;
  color: #333;
  font-size: 15px;
  border-left: 4px solid #667eea;
}

/* 规格参数表格美化 */
:deep(.el-descriptions) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-descriptions__label) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
  font-weight: bold;
}

:deep(.el-descriptions__content) {
  background: rgba(255, 255, 255, 0.8);
  color: #333;
  font-weight: 500;
}
</style>