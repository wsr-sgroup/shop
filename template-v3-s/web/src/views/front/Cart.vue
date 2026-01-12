<template>
  <div class="shopping-cart">
    <h1 class="cart-title">我的购物车</h1>
    
    <el-table 
      :data="cartItems" 
      style="width: 100%" 
      @selection-change="handleSelectionChange"
      class="cart-table"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      
      <el-table-column label="商品信息" width="400">
        <template #default="scope">
          <div class="product-info-cell">
            <el-image 
              :src="scope.row.productImage || '@/assets/default-product.jpg'" 
              class="product-thumb" 
              fit="cover"
            ></el-image>
            <div class="product-details">
              <div class="product-name">{{ scope.row.productName }}</div>
              <div class="product-specs">{{ scope.row.productSpecs }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column prop="unitPrice" label="单价" width="120">
        <template #default="scope">
          ¥{{ scope.row.unitPrice }}
        </template>
      </el-table-column>
      
      <el-table-column label="数量" width="150">
        <template #default="scope">
          <el-input-number 
            v-model="scope.row.quantity" 
            :min="1" 
            :max="scope.row.productStock"
            @change="updateQuantity(scope.row)"
            size="small"
          />
        </template>
      </el-table-column>
      
      <el-table-column label="小计" width="120">
        <template #default="scope">
          <span class="subtotal">¥{{ (scope.row.unitPrice * scope.row.quantity).toFixed(2) }}</span>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button 
            type="danger" 
            link 
            @click="removeItem(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="cart-summary">
      <div class="summary-content">
        <div class="select-info">
          已选择 {{ selectedItems.length }} 件商品
        </div>
        <div class="total-price">
          合计：<span class="price">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <el-button 
          type="primary" 
          size="large" 
          :disabled="selectedItems.length === 0"
          @click="checkout"
          class="checkout-button"
        >
          去结算
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/http.js'

const router = useRouter()

// 购物车项目
const cartItems = ref([])

// 选中的项目
const selectedItems = ref([])

// 计算总价
const totalPrice = computed(() => {
  return selectedItems.value.reduce((total, item) => {
    return total + (item.unitPrice * item.quantity)
  }, 0)
})

// 获取购物车列表
const getCartItems = () => {
  // 检查用户是否已登录
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
  if (!currentUser || !currentUser.id) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 调用后端接口获取购物车列表
  http.get('/api/cart/list', {
    params: {
      userId: currentUser.id
    }
  }).then(res => {
    if (res && res.code === 200) {
      cartItems.value = res.data || []
    } else {
      ElMessage.error(res ? res.msg : '获取购物车列表失败')
    }
  }).catch(error => {
    ElMessage.error('请求购物车列表出错')
    console.error(error)
  })
}

// 处理选中项变化
const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

// 更新数量
const updateQuantity = (item) => {
  // 检查用户是否已登录
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
  if (!currentUser || !currentUser.id) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 调用后端接口更新购物车商品数量
  http.put('/api/cart/updateQuantity', {
    cartItemId: item.id,
    userId: currentUser.id,
    quantity: item.quantity
  }).then(res => {
    if (res && res.code === 200) {
      ElMessage.success('更新成功')
    } else {
      ElMessage.error(res ? res.msg : '更新失败')
      // 如果更新失败，恢复原来的数量
      getCartItems()
    }
  }).catch(error => {
    ElMessage.error('请求更新出错')
    console.error(error)
    getCartItems()
  })
}

// 删除商品
const removeItem = (item) => {
  // 检查用户是否已登录
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
  if (!currentUser || !currentUser.id) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除 "${item.productName}" 吗?`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    // 调用后端接口删除购物车商品
    http.delete('/api/cart/remove', {
      params: {
        cartItemId: item.id,
        userId: currentUser.id
      }
    }).then(res => {
      if (res && res.code === 200) {
        const index = cartItems.value.findIndex(i => i.id === item.id)
        if (index !== -1) {
          cartItems.value.splice(index, 1)
          selectedItems.value = selectedItems.value.filter(i => i.id !== item.id)
          ElMessage.success('删除成功')
        }
      } else {
        ElMessage.error(res ? res.msg : '删除失败')
      }
    }).catch(error => {
      ElMessage.error('请求删除出错')
      console.error(error)
    })
  })
}

// 结算
const checkout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  
  // 跳转到订单确认页面，传递选中的商品详细信息
  // 将商品信息转换为JSON字符串传递
  const orderItems = selectedItems.value.map(item => ({
    id: item.id,
    productId: item.productId,
    productName: item.productName,
    productImage: item.productImage,
    unitPrice: item.unitPrice,
    quantity: item.quantity
  }))
  
  // 使用sessionStorage存储订单商品信息，避免URL过长
  sessionStorage.setItem('orderItems', JSON.stringify(orderItems))
  
  router.push({
    path: '/checkout'
  })
}

onMounted(() => {
  getCartItems()
})
</script>

<style scoped>
/* 购物车主容器 - 炫彩渐变背景 */
.shopping-cart {
  padding: 30px;
  background: linear-gradient(135deg, 
    #fa709a 0%, 
    #06caf7da 25%, 
    #fa709a 50%, 
    #ff6b6b 75%, 
    #f304cb 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  min-height: 100vh;
  position: relative;
}

/* 背景动画 */
@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 标题样式 - 彩虹渐变 */
.cart-title {
  font-size: 36px;
  margin-bottom: 30px;
  background: linear-gradient(90deg, 
    #ff6b6b 0%, 
    #ff8e53 20%, 
    #ffd93d 40%, 
    #6bcf7f 60%, 
    #4d96ff 80%, 
    #c44fff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 900;
  text-align: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: 2px;
  animation: titleFloat 3s ease-in-out infinite;
}

@keyframes titleFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

/* 表格样式 - 玻璃拟态效果 */
.cart-table {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.cart-table:hover {
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3),
              0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  transform: translateY(-3px);
}

/* 表头样式 - 多彩渐变 */
.cart-table :deep(.el-table__header th) {
  background: linear-gradient(135deg, #f10519 0%, #faab01cc 100%);
  color: white;
  font-weight: bold;
  font-size: 16px;
  text-transform: uppercase;
  letter-spacing: 1px;
  border: none;
  padding: 20px 0;
}

/* 表格行悬停效果 */
.cart-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.cart-table :deep(.el-table__row:hover) {
  background: linear-gradient(90deg, 
    rgba(250, 112, 154, 0.08) 0%, 
    rgba(254, 225, 64, 0.08) 100%) !important;
  transform: scale(1.01);
}

/* 底部结算栏 - 玻璃拟态 */
.cart-summary {
  position: sticky;
  bottom: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 25px;
  box-shadow: 0 -10px 40px rgba(0, 0, 0, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border-radius: 20px;
  margin-top: 30px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  animation: summarySlideUp 0.5s ease-out;
}

@keyframes summarySlideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.summary-content {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 30px;
}

/* 选择信息 - 彩色胶囊 */
.select-info {
  font-size: 16px;
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
  padding: 12px 25px;
  border-radius: 25px;
  font-weight: bold;
  box-shadow: 0 4px 15px rgba(250, 112, 154, 0.4);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

/* 总价显示 - 炫彩渐变 */
.total-price {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.total-price .price {
  font-size: 32px;
  font-weight: 900;
  background: linear-gradient(135deg, 
    #ff6b6b 0%, 
    #ff8e53 25%, 
    #ffd93d 50%, 
    #ff6b6b 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: shimmer 3s linear infinite;
  text-shadow: 0 0 20px rgba(255, 107, 107, 0.5);
}

@keyframes shimmer {
  0% { background-position: 0% center; }
  100% { background-position: 200% center; }
}

/* 结算按钮 - 多彩动态渐变 */
.checkout-button {
  background: linear-gradient(135deg, 
    #f093fb 0%, 
    #f5576c 50%, 
    #4facfe 100%) !important;
  background-size: 200% auto;
  border: none;
  border-radius: 15px;
  font-weight: bold;
  letter-spacing: 2px;
  font-size: 16px;
  transition: all 0.4s ease;
  box-shadow: 0 8px 25px rgba(245, 87, 108, 0.5);
  padding: 15px 40px;
  position: relative;
  overflow: hidden;
  text-transform: uppercase;
}

.checkout-button::before {
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
  transition: left 0.5s;
}

.checkout-button:hover::before {
  left: 100%;
}

.checkout-button:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 15px 35px rgba(245, 87, 108, 0.7);
  background-position: right center;
}

.checkout-button:active {
  transform: translateY(-2px) scale(1.02);
}

.checkout-button:disabled {
  opacity: 0.5;
  transform: none;
  box-shadow: none;
  cursor: not-allowed;
}

/* 商品信息单元格 */
.product-info-cell {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

/* 商品缩略图 - 彩色边框 */
.product-thumb {
  width: 90px;
  height: 90px;
  margin-right: 20px;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  border: 3px solid transparent;
  background: linear-gradient(white, white) padding-box,
              linear-gradient(135deg, #fa709a, #fee140, #ff6b6b) border-box;
  transition: all 0.3s ease;
}

.product-thumb:hover {
  transform: scale(1.1) rotate(3deg);
  box-shadow: 0 12px 30px rgba(250, 112, 154, 0.4);
}

/* 商品详情 */
.product-details {
  flex: 1;
}

.product-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
  color: #333;
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  transition: all 0.3s ease;
}

.product-name:hover {
  transform: translateX(5px);
}

.product-specs {
  color: #888;
  font-size: 14px;
  font-style: italic;
}

/* 小计金额 - 醒目渐变 */
.subtotal {
  font-weight: bold;
  font-size: 18px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 数量输入框美化 */
.cart-table :deep(.el-input-number) {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.cart-table :deep(.el-input-number__decrease),
.cart-table :deep(.el-input-number__increase) {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
  border: none;
  transition: all 0.3s ease;
}

.cart-table :deep(.el-input-number__decrease):hover,
.cart-table :deep(.el-input-number__increase):hover {
  background: linear-gradient(135deg, #fee140 0%, #ff6b6b 100%);
  transform: scale(1.1);
}

/* 删除按钮美化 */
.cart-table :deep(.el-button--danger) {
  color: #ff6b6b;
  font-weight: bold;
  transition: all 0.3s ease;
}

.cart-table :deep(.el-button--danger:hover) {
  color: #ff4757;
  transform: scale(1.1);
  text-shadow: 0 0 10px rgba(255, 107, 107, 0.5);
}

/* 空购物车提示 */
.cart-table :deep(.el-table__empty-block) {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 15px;
  padding: 40px;
}
</style>