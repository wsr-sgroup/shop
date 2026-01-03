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
.shopping-cart {
  padding: 20px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  min-height: 100vh;
}

.cart-title {
  font-size: 28px;
  margin-bottom: 25px;
  color: #333;
  background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold;
  text-align: center;
}

.cart-table {
  background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.cart-table :deep(.el-table__header th) {
  background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  font-weight: bold;
}

.cart-summary {
  position: sticky;
  bottom: 0;
  background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
  padding: 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  border-radius: 15px;
  margin-top: 20px;
}

.summary-content {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 30px;
}

.select-info {
  font-size: 16px;
  background: rgba(102, 126, 234, 0.05);
  padding: 10px 20px;
  border-radius: 20px;
}

.total-price .price {
  font-size: 24px;
  color: #ff6600;
  font-weight: bold;
  background: linear-gradient(90deg, #ff6600 0%, #ff3366 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.checkout-button {
  background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%) !important;
  border: none;
  border-radius: 10px;
  font-weight: bold;
  letter-spacing: 1px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
  padding: 12px 30px;
}

.checkout-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.6);
}

.checkout-button:disabled {
  opacity: 0.6;
  transform: none;
  box-shadow: none;
}

.product-info-cell {
  display: flex;
  align-items: center;
}

.product-thumb {
  width: 80px;
  height: 80px;
  margin-right: 15px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.product-name {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.product-specs {
  color: #999;
  font-size: 14px;
}

.subtotal {
  font-weight: bold;
  color: #ff6600;
}
</style>