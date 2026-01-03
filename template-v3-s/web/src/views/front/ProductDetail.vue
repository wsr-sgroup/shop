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
.product-detail {
  padding: 20px;
  background: linear-gradient(135deg, #00dbde 0%, #fc00ff 100%);
  min-height: 100vh;
}

.image-gallery {
  background: linear-gradient(135deg, #00dbde 0%, #fc00ff 100%);
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
  background: linear-gradient(90deg, #00dbde 0%, #fc00ff 100%);
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
  background: linear-gradient(90deg, #00dbde 0%, #fc00ff 100%);
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