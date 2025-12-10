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
              v-for="(image, index) in product.images"
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
              <span class="value">{{ product.code }}</span>
            </div>
            <div class="meta-item">
              <span class="label">库存状态：</span>
              <span class="value">{{ product.stock > 0 ? '有货' : '缺货' }}</span>
            </div>
          </div>
          
          <div class="product-specs">
            <div class="spec-item">
              <span class="label">处理器：</span>
              <span class="value">{{ product.processor }}</span>
            </div>
            <div class="spec-item">
              <span class="label">内存：</span>
              <span class="value">{{ product.memory }}</span>
            </div>
            <div class="spec-item">
              <span class="label">存储：</span>
              <span class="value">{{ product.storage }}</span>
            </div>
            <div class="spec-item">
              <span class="label">显卡：</span>
              <span class="value">{{ product.graphics }}</span>
            </div>
            <div class="spec-item">
              <span class="label">屏幕：</span>
              <span class="value">{{ product.display }}</span>
            </div>
          </div>
          
          <div class="purchase-section">
            <div class="quantity-control">
              <span class="label">数量：</span>
              <el-input-number 
                v-model="quantity" 
                :min="1" 
                :max="product.stock"
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
            <el-descriptions-item label="处理器">{{ product.processor }}</el-descriptions-item>
            <el-descriptions-item label="内存">{{ product.memory }}</el-descriptions-item>
            <el-descriptions-item label="存储">{{ product.storage }}</el-descriptions-item>
            <el-descriptions-item label="显卡">{{ product.graphics }}</el-descriptions-item>
            <el-descriptions-item label="屏幕">{{ product.display }}</el-descriptions-item>
            <el-descriptions-item label="操作系统">{{ product.os }}</el-descriptions-item>
            <el-descriptions-item label="重量">{{ product.weight }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

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
  id: 1,
  name: '联想ThinkPad X1 Carbon 2023',
  price: '12999.00',
  code: 'X1CARBON2023',
  stock: 100,
  processor: 'Intel Core i7-1365U',
  memory: '16GB LPDDR5',
  storage: '512GB SSD',
  graphics: 'Intel Iris Xe',
  display: '14英寸 2.8K OLED触摸屏',
  os: 'Windows 11 家庭版',
  weight: '约1.12kg',
  images: [
    '@/assets/products/x1carbon_1.jpg',
    '@/assets/products/x1carbon_2.jpg',
    '@/assets/products/x1carbon_3.jpg'
  ],
  description: `<p>ThinkPad X1 Carbon 2023款是联想最新推出的高端商务笔记本电脑，采用碳纤维机身设计，轻至1.12kg，薄至14.9mm，
                  是一款集便携性与性能于一体的理想商务伴侣。</p>
                <p>搭载第13代英特尔酷睿处理器，性能相较上一代提升显著，无论是日常办公还是复杂任务处理都能轻松应对。
                  配备14英寸2.8K OLED触摸屏，支持杜比视界HDR，色彩表现更加出色。</p>`
})

// 立即购买
const buyNow = () => {
  ElMessage.info('立即购买功能待实现')
  // 应该跳转到订单确认页面
}

// 加入购物车
const addToCart = () => {
  ElMessage.success(`已将 ${quantity.value} 件商品添加到购物车`)
  // 应该调用后端API将商品添加到购物车
}

onMounted(() => {
  // 根据路由参数获取商品ID，并请求商品详情
  const productId = route.params.id
  console.log('获取商品详情，商品ID:', productId)
  
  // 设置默认显示第一张图片
  if (product.value.images && product.value.images.length > 0) {
    currentImage.value = product.value.images[0]
  }
})
</script>

<style scoped>
.product-detail {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.image-gallery {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.main-image {
  width: 100%;
  height: 400px;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  backdrop-filter: blur(10px);
}

.thumbnail-list {
  display: flex;
  margin-top: 20px;
  gap: 10px;
  justify-content: center;
}

.thumbnail {
  width: 80px;
  height: 80px;
  cursor: pointer;
  border: 2px solid transparent;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.thumbnail.active {
  border-color: #ffd700;
  transform: scale(1.1);
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.5);
}

.product-info {
  padding: 30px;
  background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.product-title {
  font-size: 28px;
  margin-bottom: 15px;
  color: #333;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold;
}

.product-price {
  font-size: 32px;
  color: #ff6600;
  font-weight: bold;
  margin-bottom: 25px;
  background: linear-gradient(90deg, #ff6600 0%, #ff3366 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}


.product-meta, .product-specs {
  margin-bottom: 25px;
}

.meta-item, .spec-item {
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 8px;
  background: rgba(102, 126, 234, 0.05);
  transition: all 0.3s ease;
}

.meta-item:hover, .spec-item:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateX(5px);
}

.label {
  font-weight: bold;
  color: #667eea;
}

.value {
  color: #333;
}

.quantity-control {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  padding: 15px;
  background: linear-gradient(90deg, #f0f2f5 0%, #e2e8f0 100%);
  border-radius: 10px;
}

.quantity-control .label {
  margin-right: 15px;
  font-weight: bold;
  color: #667eea;
}

.action-buttons {
  display: flex;
  gap: 20px;
}

.buy-button, .cart-button {
  flex: 1;
  border: none;
  border-radius: 10px;
  font-weight: bold;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.buy-button {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%) !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.buy-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.6);
}

.cart-button {
  background: linear-gradient(90deg, #ff9a9e 0%, #fad0c4 100%) !important;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.4);
}

.cart-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(255, 154, 158, 0.6);
}

.product-description {
  margin-top: 30px;
  background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.el-tabs__nav-wrap)::after {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

:deep(.el-tabs__item.is-active) {
  color: #667eea !important;
  font-weight: bold;
}

:deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.description-content {
  line-height: 1.8;
  padding: 20px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 10px;
}
</style>