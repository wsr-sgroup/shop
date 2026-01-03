<template>
  <div class="user-center">
    <!-- 用户信息卡片 -->
    <div class="user-info-card">
      <div class="avatar-section">
        <el-avatar :size="80" :src="userInfo.avatarUrl || '@/assets/default_avatar.png'" />
        <div class="user-details">
          <h2 class="username">{{ userInfo.nickname || userInfo.username }}</h2>
          <p class="user-type">{{ userInfo.type === 'ADMIN' ? '管理员' : '普通用户' }}</p>
        </div>
      </div>
      <el-button type="primary" @click="editUserInfo">编辑资料</el-button>
    </div>

    <!-- 功能菜单 -->
    <div class="function-menu">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="menu-card" @click="goToOrders">
            <div class="menu-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="menu-text">我的订单</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="menu-card" @click="goToAddresses">
            <div class="menu-icon">
              <el-icon><Location /></el-icon>
            </div>
            <div class="menu-text">收货地址</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="menu-card" @click="goToSettings">
            <div class="menu-icon">
              <el-icon><Setting /></el-icon>
            </div>
            <div class="menu-text">账户设置</div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近订单 -->
    <div class="recent-orders">
      <h3>最近订单</h3>
      <div v-if="orders.length > 0" class="orders-list">
        <el-card 
          v-for="order in orders" 
          :key="order.id" 
          class="order-card"
          @click="viewOrderDetail(order.id)"
        >
          <div class="order-header">
            <span class="order-id">订单号: {{ order.order_number }}</span>
            <span :class="`order-status status-${order.order_status}`">
              {{ getOrderStatusText(order.order_status) }}
            </span>
          </div>
          <div class="order-items">
            <div 
              v-for="item in order.order_items" 
              :key="item.id" 
              class="order-item"
            >
              <img 
                :src="item.product_image || '@/assets/default-product.jpg'" 
                class="item-image"
              />
              <div class="item-info">
                <div class="item-name">{{ item.product_name }}</div>
                <div class="item-quantity">x{{ item.quantity }}</div>
              </div>
            </div>
          </div>
          <div class="order-footer">
            <div class="order-total">
              总计: <span class="total-price">¥{{ order.final_amount }}</span>
            </div>
            <div class="order-actions">
              <el-button 
                v-if="order.order_status === 'pending'" 
                type="primary" 
                size="small"
                @click.stop="payOrder(order.id)"
              >
                立即支付
              </el-button>
              <el-button 
                v-if="order.order_status === 'pending'" 
                type="danger" 
                size="small"
                @click.stop="cancelOrder(order.id)"
              >
                取消订单
              </el-button>
              <el-button 
                v-if="order.order_status === 'paid'" 
                type="success" 
                size="small"
                @click.stop="confirmReceipt(order.id)"
              >
                确认收货
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
      <div v-else class="no-orders">
        <el-empty description="暂无订单" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElEmpty, ElAvatar, ElCard, ElButton } from 'element-plus'
import { Document, Location, Setting } from '@element-plus/icons-vue'
import http from '@/utils/http.js'

const router = useRouter()
const userInfo = ref({})
const orders = ref([])

// 页面加载时获取用户信息和订单列表
onMounted(() => {
  getCurrentUser()
  getRecentOrders()
})

// 获取当前用户信息
const getCurrentUser = () => {
  const userStr = localStorage.getItem('currentUser')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
}

// 获取最近订单
const getRecentOrders = () => {
  http.get('/order/myOrders', {
    params: {
      pageNum: 1,
      pageSize: 5
    }
  }).then(res => {
    if (res && res.success) {
      orders.value = res.data.records
    } else {
      ElMessage.error('获取订单列表失败')
    }
  }).catch(error => {
    console.error('请求订单列表出错:', error)
    ElMessage.error('请求订单列表出错')
  })
}

// 跳转到订单列表
const goToOrders = () => {
  // 这里可以跳转到专门的订单列表页面
  ElMessage.info('功能开发中')
}

// 跳转到地址管理
const goToAddresses = () => {
  // 这里可以跳转到专门的地址管理页面
  ElMessage.info('功能开发中')
}

// 跳转到账户设置
const goToSettings = () => {
  // 这里可以跳转到专门的账户设置页面
  ElMessage.info('功能开发中')
}

// 查看订单详情
const viewOrderDetail = (orderId) => {
  // 这里可以跳转到专门的订单详情页面
  ElMessage.info('功能开发中')
}

// 支付订单
const payOrder = (orderId) => {
  http.post('/order/updatePayment', { 
    orderNo: orderId, 
    paymentMethod: 'online' 
  }).then(res => {
    if (res && res.success) {
      ElMessage.success('支付成功')
      getRecentOrders() // 刷新订单列表
    } else {
      ElMessage.error('支付失败')
    }
  }).catch(error => {
    console.error('支付请求出错:', error)
    ElMessage.error('支付请求出错')
  })
}

// 取消订单
const cancelOrder = (orderId) => {
  http.post(`/order/cancel/${orderId}`).then(res => {
    if (res && res.success) {
      ElMessage.success('订单已取消')
      getRecentOrders() // 刷新订单列表
    } else {
      ElMessage.error('取消订单失败')
    }
  }).catch(error => {
    console.error('取消订单请求出错:', error)
    ElMessage.error('取消订单请求出错')
  })
}

// 确认收货
const confirmReceipt = (orderId) => {
  http.post(`/order/confirmReceipt/${orderId}`).then(res => {
    if (res && res.success) {
      ElMessage.success('已确认收货')
      getRecentOrders() // 刷新订单列表
    } else {
      ElMessage.error('确认收货失败')
    }
  }).catch(error => {
    console.error('确认收货请求出错:', error)
    ElMessage.error('确认收货请求出错')
  })
}

// 编辑用户信息
const editUserInfo = () => {
  // 这里可以跳转到编辑资料页面
  ElMessage.info('功能开发中')
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    'pending': '待支付',
    'paid': '已支付',
    'shipped': '已发货',
    'delivered': '已送达',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || '未知状态'
}
</script>

<style scoped>
.user-center {
  padding: 20px;
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  min-height: 100vh;
}

.user-info-card {
  background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  display: flex;
  align-items: center;
}

.user-details {
  margin-left: 20px;
}

.username {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
  background: linear-gradient(90deg, #fa709a 0%, #fee140 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.user-type {
  color: #666;
  font-size: 14px;
}

.function-menu {
  margin-bottom: 30px;
}

.menu-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  text-align: center;
  padding: 30px 20px;
  background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
  border-radius: 15px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.menu-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(250, 112, 154, 0.3);
}

.menu-icon {
  font-size: 48px;
  color: #fa709a;
  margin-bottom: 10px;
}

.menu-text {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.recent-orders h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
  background: linear-gradient(90deg, #fa709a 0%, #fee140 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold;
}

.orders-list {
  display: grid;
  gap: 20px;
}

.order-card {
  background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
  border-radius: 15px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
  cursor: pointer;
}

.order-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

.order-id {
  color: #666;
  font-size: 14px;
}

.order-status {
  font-size: 14px;
  font-weight: bold;
}

.status-pending {
  color: #ff6600;
}

.status-paid {
  color: #1989fa;
}

.status-shipped {
  color: #722ed1;
}

.status-delivered {
  color: #eb2f96;
}

.status-completed {
  color: #52c41a;
}

.status-cancelled {
  color: #999;
}

.order-items {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
}

.order-item {
  display: flex;
  align-items: center;
  width: calc(25% - 15px);
}

.item-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 10px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.item-quantity {
  font-size: 12px;
  color: #666;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.order-total {
  font-size: 14px;
  color: #666;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff6600;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.no-orders {
  text-align: center;
  padding: 60px 0;
  background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
  border-radius: 15px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}
</style>