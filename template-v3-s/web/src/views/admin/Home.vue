<template>
  <div class="admin-home">
    <h1 class="page-title">
      <el-icon><DataAnalysis /></el-icon>
      数据统计
    </h1>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card user-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="50"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">普通用户</div>
              <div class="stat-number">{{ statistics.userCount }}</div>
            </div>
          </div>
          <div class="stat-footer">
            <el-button text @click="goToUserManage">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card admin-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="50"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">管理员</div>
              <div class="stat-number">{{ statistics.adminCount }}</div>
            </div>
          </div>
          <div class="stat-footer">
            <el-button text @click="goToAdminManage">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card product-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="50"><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">商品数量</div>
              <div class="stat-number">{{ statistics.productCount }}</div>
            </div>
          </div>
          <div class="stat-footer">
            <el-button text @click="goToProductManage">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card order-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="50"><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">订单数量</div>
              <div class="stat-number">{{ statistics.orderCount }}</div>
            </div>
          </div>
          <div class="stat-footer">
            <el-button text @click="goToOrderManage">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h2 class="section-title">
        <el-icon><Operation /></el-icon>
        快捷操作
      </h2>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="action-card" shadow="hover" @click="goToProductManage">
            <div class="action-content">
              <el-icon :size="40" color="#409EFF"><Plus /></el-icon>
              <div class="action-text">新增商品</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="action-card" shadow="hover" @click="goToOrderManage">
            <div class="action-content">
              <el-icon :size="40" color="#67C23A"><Document /></el-icon>
              <div class="action-text">订单管理</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="action-card" shadow="hover" @click="goToUserManage">
            <div class="action-content">
              <el-icon :size="40" color="#E6A23C"><User /></el-icon>
              <div class="action-text">用户管理</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="action-card" shadow="hover" @click="goToAdminManage">
            <div class="action-content">
              <el-icon :size="40" color="#F56C6C"><Setting /></el-icon>
              <div class="action-text">系统设置</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  DataAnalysis, User, UserFilled, Goods, List, ArrowRight, 
  Operation, Plus, Document, Setting 
} from '@element-plus/icons-vue'
import request from '@/utils/http.js'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 统计数据
const statistics = ref({
  userCount: 0,
  adminCount: 0,
  productCount: 0,
  orderCount: 0
})

// 获取统计数据
const getStatistics = async () => {
  try {
    // 获取用户数量
    const userRes = await request.get('/user/page', {
      params: { pageNum: 1, pageSize: 1 }
    })
    if (userRes && userRes.code === 200) {
      statistics.value.userCount = userRes.data.total || 0
    }
    
    // 获取管理员数量
    const adminRes = await request.get('/admin/page', {
      params: { pageNum: 1, pageSize: 1 }
    })
    if (adminRes && adminRes.code === 200) {
      statistics.value.adminCount = adminRes.data.total || 0
    }
    
    // 获取商品数量
    const productRes = await request.get('/product/page', {
      params: { pageNum: 1, pageSize: 1 }
    })
    if (productRes && productRes.code === 200) {
      statistics.value.productCount = productRes.data.total || 0
    }
    
    // 获取订单数量
    const orderRes = await request.get('/order/page', {
      params: { pageNum: 1, pageSize: 1 }
    })
    if (orderRes && orderRes.code === 200) {
      statistics.value.orderCount = orderRes.data.total || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 跳转到用户管理
const goToUserManage = () => {
  router.push('/admin/user')
}

// 跳转到管理员管理
const goToAdminManage = () => {
  router.push('/admin/admin')
}

// 跳转到商品管理
const goToProductManage = () => {
  router.push('/admin/product')
}

// 跳转到订单管理
const goToOrderManage = () => {
  router.push('/admin/order')
}

onMounted(() => {
  getStatistics()
})
</script>

<style scoped>
.admin-home {
  padding: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #303133;
}

.stats-row {
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s;
  border: none;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.user-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.admin-card {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.product-card {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.order-card {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
}

.stat-icon {
  opacity: 0.8;
}

.stat-info {
  text-align: right;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
}

.stat-footer {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.3);
}

.stat-footer .el-button {
  color: white;
  font-weight: 500;
}

.stat-footer .el-button:hover {
  color: rgba(255, 255, 255, 0.8);
}

.quick-actions {
  margin-top: 40px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}

.action-card {
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid #f0f0f0;
}

.action-card:hover {
  border-color: #409EFF;
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.action-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 20px;
  gap: 15px;
}

.action-text {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
}

.action-card:hover .action-text {
  color: #409EFF;
}
</style>
