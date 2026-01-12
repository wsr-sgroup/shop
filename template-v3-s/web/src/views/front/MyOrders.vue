<template>
  <div class="my-orders">
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" circle></el-button>
      <h2>我的订单</h2>
      <div></div>
    </div>

    <!-- 订单状态筛选 -->
    <div class="filter-tabs">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部订单" name="all"></el-tab-pane>
        <el-tab-pane label="待发货" name="pending"></el-tab-pane>
        <el-tab-pane label="待收货" name="shipped"></el-tab-pane>
        <el-tab-pane label="已完成" name="received"></el-tab-pane>
        <el-tab-pane label="已取消" name="cancelled"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list">
      <div v-if="filteredOrders.length > 0">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <span class="order-time">{{ order.createTime }}</span>
            </div>
            <el-tag :type="getStatusType(order.status)" size="large">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>

          <!-- 订单商品列表 -->
          <div class="order-items">
            <div v-for="item in order.items" :key="item.id" class="order-item">
              <img :src="item.image" class="item-image" />
              <div class="item-details">
                <div class="item-name">{{ item.name }}</div>
                <div class="item-spec">{{ item.spec }}</div>
              </div>
              <div class="item-price">
                <div class="price">¥{{ item.price }}</div>
                <div class="quantity">x{{ item.quantity }}</div>
              </div>
            </div>
          </div>

          <!-- 订单金额 -->
          <div class="order-amount">
            <div class="amount-item">
              <span>商品总额:</span>
              <span>¥{{ order.totalAmount }}</span>
            </div>
            <div class="amount-item" v-if="order.discount > 0">
              <span>优惠:</span>
              <span class="discount">-¥{{ order.discount }}</span>
            </div>
            <div class="amount-item" v-if="order.shippingFee > 0">
              <span>运费:</span>
              <span>¥{{ order.shippingFee }}</span>
            </div>
            <div class="amount-item total">
              <span>实付金额:</span>
              <span class="final-amount">¥{{ order.finalAmount }}</span>
            </div>
          </div>

          <!-- 订单操作 -->
          <div class="order-actions">
            <el-button size="small" @click="viewDetail(order.id)">
              查看详情
            </el-button>
            <el-button 
              v-if="order.status === 'pending'" 
              type="danger" 
              size="small"
              @click="cancelOrder(order.id)"
            >
              取消订单
            </el-button>
            <el-button 
              v-if="order.status === 'shipped'" 
              type="success" 
              size="small"
              @click="confirmReceipt(order.id)"
            >
              确认收货
            </el-button>
            <el-button 
              v-if="order.status === 'received'" 
              type="primary" 
              size="small"
              @click="reviewOrder(order.id)"
            >
              评价晒单
            </el-button>
            <el-button 
              v-if="['received', 'cancelled'].includes(order.status)" 
              size="small"
              @click="deleteOrder(order.id)"
            >
              删除订单
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty description="暂无订单数据">
          <el-button type="primary" @click="goShopping">去购物</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="filteredOrders.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const activeTab = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(50)

// 模拟订单数据
const orders = ref([
  {
    id: 1,
    orderNo: 'ORD2026011200001',
    createTime: '2026-01-12 10:30:25',
    status: 'pending',
    items: [
      {
        id: 1,
        name: 'ThinkPad X1 Carbon Gen 11',
        spec: '14英寸 / i7-1355U / 16GB / 512GB',
        image: 'https://via.placeholder.com/100',
        price: 8999,
        quantity: 1
      },
      {
        id: 2,
        name: '罗技MX Master 3S鼠标',
        spec: '无线蓝牙 / 黑色',
        image: 'https://via.placeholder.com/100',
        price: 699,
        quantity: 1
      }
    ],
    totalAmount: 9698,
    discount: 200,
    shippingFee: 0,
    finalAmount: 9498
  },
  {
    id: 2,
    orderNo: 'ORD2026011100002',
    createTime: '2026-01-11 15:20:10',
    status: 'shipped',
    items: [
      {
        id: 3,
        name: 'MacBook Pro 14英寸',
        spec: 'M3 Pro / 18GB / 512GB / 深空黑色',
        image: 'https://via.placeholder.com/100',
        price: 16999,
        quantity: 1
      }
    ],
    totalAmount: 16999,
    discount: 500,
    shippingFee: 0,
    finalAmount: 16499
  },
  {
    id: 3,
    orderNo: 'ORD2026011000003',
    createTime: '2026-01-10 09:15:33',
    status: 'received',
    items: [
      {
        id: 4,
        name: 'Dell XPS 15',
        spec: '15.6英寸 / i7-13700H / 32GB / 1TB',
        image: 'https://via.placeholder.com/100',
        price: 12999,
        quantity: 1
      }
    ],
    totalAmount: 12999,
    discount: 0,
    shippingFee: 20,
    finalAmount: 13019
  },
  {
    id: 4,
    orderNo: 'ORD2026010900004',
    createTime: '2026-01-09 14:45:20',
    status: 'cancelled',
    items: [
      {
        id: 5,
        name: 'HP战99笔记本',
        spec: '15.6英寸 / i5-12500H / 16GB / 512GB',
        image: 'https://via.placeholder.com/100',
        price: 5999,
        quantity: 1
      }
    ],
    totalAmount: 5999,
    discount: 100,
    shippingFee: 0,
    finalAmount: 5899
  }
])

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') {
    return orders.value
  }
  return orders.value.filter(order => order.status === activeTab.value)
})

const goBack = () => {
  router.back()
}

const handleTabClick = () => {
  currentPage.value = 1
}

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    shipped: 'primary',
    received: 'success',
    cancelled: 'info'
  }
  return typeMap[status] || ''
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待发货',
    shipped: '待收货',
    received: '已完成',
    cancelled: '已取消'
  }
  return textMap[status] || '未知'
}

const viewDetail = (orderId) => {
  ElMessage.info(`查看订单详情: ${orderId}`)
}

const cancelOrder = (orderId) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('订单已取消')
    // 更新订单状态
    const order = orders.value.find(o => o.id === orderId)
    if (order) order.status = 'cancelled'
  }).catch(() => {})
}

const confirmReceipt = (orderId) => {
  ElMessageBox.confirm('确认已收到商品吗？', '提示', {
    confirmButtonText: '确认收货',
    cancelButtonText: '取消',
    type: 'success'
  }).then(() => {
    ElMessage.success('确认收货成功')
    // 更新订单状态
    const order = orders.value.find(o => o.id === orderId)
    if (order) order.status = 'received'
  }).catch(() => {})
}

const reviewOrder = (orderId) => {
  ElMessage.info(`评价订单: ${orderId}`)
}

const deleteOrder = (orderId) => {
  ElMessageBox.confirm('确定要删除该订单吗？删除后无法恢复。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('订单已删除')
    // 删除订单
    const index = orders.value.findIndex(o => o.id === orderId)
    if (index > -1) orders.value.splice(index, 1)
  }).catch(() => {})
}

const goShopping = () => {
  router.push('/')
}
</script>

<style scoped>
.my-orders {
  min-height: 100vh;
  background: linear-gradient(135deg, 
    #fa709a 0%, 
    #06caf7da 25%, 
    #fa709a 50%, 
    #ff6b6b 75%, 
    #f304cb 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  padding: 20px;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 20px 30px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(250, 112, 154, 0.3);
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold;
}

.filter-tabs {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 20px 30px 0;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(250, 112, 154, 0.3);
  margin-bottom: 20px;
}

.filter-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.filter-tabs :deep(.el-tabs__item.is-active) {
  color: #fa709a;
  font-weight: bold;
}

.filter-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #fa709a 0%, #ff6b6b 100%);
  height: 3px;
}

.orders-list {
  margin-bottom: 20px;
}

.order-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 8px 32px rgba(250, 112, 154, 0.3);
  transition: all 0.3s;
}

.order-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(250, 112, 154, 0.4);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(250, 112, 154, 0.2);
  margin-bottom: 20px;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.order-no {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.order-time {
  font-size: 14px;
  color: #999;
}

.order-items {
  margin-bottom: 20px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px dashed rgba(250, 112, 154, 0.2);
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 12px;
  margin-right: 20px;
  border: 2px solid rgba(250, 112, 154, 0.2);
}

.item-details {
  flex: 1;
}

.item-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.item-spec {
  font-size: 14px;
  color: #666;
}

.item-price {
  text-align: right;
  min-width: 100px;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #fa709a;
  margin-bottom: 5px;
}

.quantity {
  font-size: 14px;
  color: #999;
}

.order-amount {
  background: linear-gradient(135deg, rgba(250, 112, 154, 0.1) 0%, rgba(255, 107, 107, 0.1) 100%);
  padding: 15px 20px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.amount-item:last-child {
  margin-bottom: 0;
}

.amount-item.total {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  padding-top: 10px;
  border-top: 2px dashed rgba(250, 112, 154, 0.3);
}

.discount {
  color: #fa709a;
}

.final-amount {
  font-size: 20px;
  color: #fa709a;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.order-actions :deep(.el-button) {
  border-radius: 20px;
  padding: 10px 25px;
  font-weight: bold;
  transition: all 0.3s;
}

.order-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  border: none;
}

.order-actions :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(250, 112, 154, 0.4);
}

.order-actions :deep(.el-button--success) {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  border: none;
}

.order-actions :deep(.el-button--danger) {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  border: none;
}

.empty-state {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 60px 20px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(250, 112, 154, 0.3);
}

.empty-state :deep(.el-button--primary) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  border: none;
  padding: 12px 40px;
  border-radius: 20px;
  font-weight: bold;
}

.pagination {
  display: flex;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 20px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(250, 112, 154, 0.3);
}

.pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  color: white;
}

.pagination :deep(.el-pager li:hover) {
  color: #fa709a;
}
</style>
