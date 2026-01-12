<template>
  <div class="mall-home">
    <div class="header">
      <div class="logo">
        <img src="@/assets/7-20092H1324Oa.jpg" alt="联想商城" />
        <h1>联想笔记本商城</h1>
      </div>
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索联想笔记本..."
          size="large"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
      <div class="user-actions">
        <el-button type="primary" @click="goToCart">
          <el-icon><ShoppingCart /></el-icon>
          购物车
        </el-button>
        <template v-if="checkLogin()">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-avatar :size="32" :src="userInfo.avatarUrl || '@/assets/default-avatar.png'" />
              {{ userInfo.nickname || userInfo.username }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToUserCenter">个人中心</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button @click="goToLogin">
            <el-icon><User /></el-icon>
            登录
          </el-button>
        </template>
      </div>
    </div>

    <div class="categories">
      <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
        <el-tab-pane label="全部商品" name="all">
          <template #label>
            <span><el-icon><Goods /></el-icon> 全部商品</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="ThinkPad系列" name="thinkpad">
          <template #label>
            <span><el-icon><Monitor /></el-icon> ThinkPad系列</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="拯救者系列" name="legion">
          <template #label>
            <span><el-icon><SetUp /></el-icon> 拯救者系列</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="小新系列" name="xiaoxin">
          <template #label>
            <span><el-icon><OfficeBuilding /></el-icon> 小新系列</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="Yoga系列" name="yoga">
          <template #label>
            <span><el-icon><Refresh /></el-icon> Yoga系列</span>
          </template>
        </el-tab-pane>
      </el-tabs>
    </div>

    <div class="product-list">
      <el-row :gutter="20">
        <el-col 
          v-for="product in productList" 
          :key="product.id" 
          :span="6" 
          class="product-item"
        >
          <el-card :body-style="{ padding: '0px' }" @click="viewProductDetail(product)" class="product-card">
            <img 
              :src="product.mainImage || '@/assets/default-product.jpg'" 
              class="product-image" 
              :alt="product.name"
            />
            <div style="padding: 14px;">
              <span class="product-name">{{ product.name }}</span>
              <div class="product-price">¥{{ product.price }}</div>
              <div class="product-description">{{ product.description }}</div>
              <div class="bottom clearfix">
                <el-button type="primary" @click.stop="addToCart(product)">
                  <el-icon><ShoppingCart /></el-icon>
                  加入购物车
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pageInfo.pageNum"
        v-model:page-size="pageInfo.pageSize"
        :page-sizes="[8, 16, 24, 32]"
        :small="false"
        :disabled="false"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pageInfo.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ShoppingCart, User, Goods, Monitor, SetUp, OfficeBuilding, Refresh, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import http from '@/utils/http.js'

const router = useRouter()

// 搜索关键词
const searchKeyword = ref('')

// 当前分类
const activeCategory = ref('all')

// 分页信息
const pageInfo = ref({
  pageNum: 1,
  pageSize: 8,
  total: 0
})

// 商品列表
const productList = ref([
  {
    id: 1,
    name: '联想ThinkPad X1 Carbon 2023',
    price: '12999.00',
    description: '轻薄商务本，碳纤维材质',
    mainImage: ''
  },
  {
    id: 2,
    name: '联想拯救者Y7000P 2023',
    price: '9999.00',
    description: '游戏本，高性能配置',
    mainImage: ''
  },
  {
    id: 3,
    name: '联想小新Pro14 2023',
    price: '5999.00',
    description: '轻薄办公本，高性价比',
    mainImage: ''
  },
  {
    id: 4,
    name: '联想YOGA 14s 2023',
    price: '7999.00',
    description: '翻转触控屏，创意设计本',
    mainImage: ''
  }
])

// 搜索处理
const handleSearch = () => {
  console.log('搜索:', searchKeyword.value)
  getPageList()
}

// 分类切换处理
const handleCategoryChange = (category) => {
  console.log('切换分类:', category)
  pageInfo.value.pageNum = 1
  getPageList()
}

// 获取商品列表
const getPageList = () => {
  // 调用后端接口获取商品数据
  http.get('/product/page', {
    params: {
      name: searchKeyword.value,
      series: activeCategory.value === 'all' ? '' : activeCategory.value,
      pageNum: pageInfo.value.pageNum,
      pageSize: pageInfo.value.pageSize
    }
  }).then(res => {
    console.log('商品列表响应:', res)
    if (res && res.code === 200) {
      productList.value = res.data.list || []
      pageInfo.value.total = res.data.total || 0
      console.log('商品列表:', productList.value)
      console.log('总数量:', pageInfo.value.total)
    } else {
      ElMessage.error('获取商品列表失败')
    }
  }).catch(error => {
    ElMessage.error('请求商品列表出错')
    console.error(error)
  })
}

// 查看商品详情
const viewProductDetail = (product) => {
  router.push(`/product/${product.id}`)
}

// 加入购物车
const addToCart = (product) => {
  // 检查用户是否已登录
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
  if (!currentUser || !currentUser.id) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 调用后端接口添加到购物车
  http.post('/api/cart/add', {
    userId: currentUser.id,
    productId: product.id,
    quantity: 1
  }).then(res => {
    if (res && res.code === 200) {
      ElMessage.success('已添加到购物车')
    } else {
      ElMessage.error(res ? res.msg : '添加购物车失败')
    }
  }).catch(error => {
    ElMessage.error('请求添加购物车出错')
    console.error(error)
  })
}

// 页面大小改变
const handleSizeChange = (val) => {
  pageInfo.value.pageSize = val
  pageInfo.value.pageNum = 1
  getPageList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pageInfo.value.pageNum = val
  getPageList()
}

// 跳转到购物车
const goToCart = () => {
  router.push('/cart')
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 检查用户是否已登录
const checkLogin = () => {
  return localStorage.getItem('token') !== null
}

// 用户信息
const userInfo = ref(JSON.parse(localStorage.getItem('currentUser') || '{}'))

// 跳转到个人中心
const goToUserCenter = () => {
  router.push('/user/center')
}

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('currentUser')
  ElMessage.success('退出登录成功')
  window.location.reload()
}

onMounted(() => {
  getPageList()
})
</script>

<style scoped>
/* 主页容器 - 保留背景图片 */
.mall-home {
  padding: 30px;
  background-image: url('@/assets/主页背景.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  min-height: 100vh;
  position: relative;
}

/* 添加多彩叠加层 */
.mall-home::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(250, 112, 154, 0.1) 0%, 
    rgba(254, 225, 64, 0.1) 25%, 
    rgba(255, 107, 107, 0.1) 50%, 
    rgba(254, 202, 87, 0.1) 75%, 
    rgba(250, 112, 154, 0.1) 100%);
  background-size: 400% 400%;
  animation: overlayFlow 20s ease infinite;
  pointer-events: none;
  z-index: 0;
}

@keyframes overlayFlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 确保内容在叠加层之上 */
.mall-home > * {
  position: relative;
  z-index: 1;
}

/* 头部导航 - 玻璃拟态 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding: 25px 30px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(30px);
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.15),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border: 2px solid rgba(255, 255, 255, 0.5);
  transition: all 0.4s ease;
}

.header:hover {
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  transform: translateY(-3px);
}

/* Logo区域 */
.logo {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 3px solid transparent;
  background: linear-gradient(white, white) padding-box,
              linear-gradient(135deg, #fa709a, #fee140, #ff6b6b) border-box;
  box-shadow: 0 8px 20px rgba(250, 112, 154, 0.3);
  transition: all 0.4s ease;
}

.logo img:hover {
  transform: rotate(360deg) scale(1.1);
  box-shadow: 0 12px 30px rgba(250, 112, 154, 0.5);
}

.logo h1 {
  font-size: 28px;
  font-weight: 900;
  background: linear-gradient(135deg, 
    #fa709a 0%, 
    #fee140 50%, 
    #ff6b6b 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 1px;
  animation: logoFloat 3s ease-in-out infinite;
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

/* 搜索栏 */
.search-bar {
  flex: 1;
  margin: 0 40px;
}

.search-bar :deep(.el-input__wrapper) {
  border-radius: 25px;
  border: 2px solid rgba(250, 112, 154, 0.3);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 15px rgba(250, 112, 154, 0.1);
  transition: all 0.3s ease;
}

.search-bar :deep(.el-input__wrapper):hover {
  border-color: rgba(250, 112, 154, 0.5);
  box-shadow: 0 6px 20px rgba(250, 112, 154, 0.2);
}

.search-bar :deep(.el-input__wrapper.is-focus) {
  border-color: #fa709a;
  box-shadow: 0 0 0 3px rgba(250, 112, 154, 0.15);
}

.search-bar :deep(.el-input-group__append) {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  border: none;
  border-radius: 0 25px 25px 0;
}

.search-bar :deep(.el-input-group__append .el-button) {
  background: transparent;
  border: none;
  color: white;
}

/* 用户操作区 */
.user-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.user-actions :deep(.el-button) {
  border-radius: 20px;
  font-weight: 700;
  padding: 12px 24px;
  transition: all 0.3s ease;
}

.user-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(250, 112, 154, 0.4);
}

.user-actions :deep(.el-button--primary):hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(250, 112, 154, 0.6);
}

.user-actions :deep(.el-button:not(.el-button--primary)) {
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(250, 112, 154, 0.3);
  color: #fa709a;
  font-weight: 700;
}

.user-actions :deep(.el-button:not(.el-button--primary)):hover {
  background: rgba(250, 112, 154, 0.1);
  border-color: #fa709a;
  transform: translateY(-2px);
}

/* 用户下拉菜单 */
.el-dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(250, 112, 154, 0.3);
  font-weight: 700;
  color: #fa709a;
  transition: all 0.3s ease;
}

.el-dropdown-link:hover {
  background: rgba(250, 112, 154, 0.1);
  border-color: #fa709a;
  transform: translateY(-2px);
}

/* 分类标签栏 - 玻璃拟态 */
.categories {
  margin-bottom: 25px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(30px);
  padding: 15px 25px;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.12),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border: 2px solid rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.categories:hover {
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.18);
  transform: translateY(-2px);
}

.categories :deep(.el-tabs__item) {
  font-weight: 700;
  font-size: 15px;
  color: #666;
  transition: all 0.3s ease;
  padding: 0 20px;
}

.categories :deep(.el-tabs__item:hover) {
  color: #fa709a;
  transform: translateY(-2px);
}

.categories :deep(.el-tabs__item.is-active) {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 900;
}

.categories :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #fa709a 0%, #fee140 100%);
  height: 3px;
  border-radius: 2px;
}

.categories :deep(.el-tabs__nav-wrap::after) {
  background: linear-gradient(90deg, 
    rgba(250, 112, 154, 0.2) 0%, 
    rgba(254, 225, 64, 0.2) 100%);
}

/* 商品列表 */
.product-list {
  margin-bottom: 30px;
}

.product-item {
  margin-bottom: 25px;
}

/* 商品卡片 - 玻璃拟态 */
.product-card {
  transition: all 0.4s ease;
  cursor: pointer;
  border-radius: 20px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 2px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.12),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  position: relative;
}

.product-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, 
    #fa709a 0%, 
    #fee140 50%, 
    #ff6b6b 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.product-card:hover::before {
  opacity: 1;
}

.product-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 60px rgba(250, 112, 154, 0.3),
              0 0 0 1px rgba(255, 255, 255, 0.5) inset;
}

/* 商品图片 */
.product-image {
  width: 100%;
  height: 220px;
  object-fit: cover;
  cursor: pointer;
  transition: all 0.4s ease;
  position: relative;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

/* 商品名称 */
.product-name {
  font-weight: 800;
  font-size: 17px;
  display: block;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #333 0%, #667eea 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  transition: all 0.3s ease;
}

.product-card:hover .product-name {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 商品价格 */
.product-price {
  font-size: 22px;
  font-weight: 900;
  margin-bottom: 12px;
  background: linear-gradient(135deg, 
    #ff6b6b 0%, 
    #ff8e53 50%, 
    #ffd93d 100%);
  background-size: 200% auto;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: priceShimmer 3s linear infinite;
}

@keyframes priceShimmer {
  0% { background-position: 0% center; }
  100% { background-position: 200% center; }
}

/* 商品描述 */
.product-description {
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
  height: 40px;
  overflow: hidden;
  line-height: 1.5;
}

/* 加入购物车按钮 */
.bottom {
  text-align: center;
  padding: 10px 0;
}

.bottom :deep(.el-button) {
  width: 100%;
  border-radius: 15px;
  font-weight: 700;
  padding: 12px 20px;
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(250, 112, 154, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.bottom :deep(.el-button::before) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(255, 255, 255, 0.3), 
    transparent);
  transition: left 0.5s;
}

.bottom :deep(.el-button:hover::before) {
  left: 100%;
}

.bottom :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(250, 112, 154, 0.5);
}

/* 分页器 - 玻璃拟态 */
.pagination {
  display: flex;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(30px);
  padding: 25px;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.12),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.pagination :deep(.el-pagination) {
  font-weight: 600;
}

.pagination :deep(.el-pager li) {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  margin: 0 3px;
  font-weight: 700;
  transition: all 0.3s ease;
}

.pagination :deep(.el-pager li:hover) {
  background: linear-gradient(135deg, 
    rgba(250, 112, 154, 0.2) 0%, 
    rgba(254, 225, 64, 0.2) 100%);
  transform: translateY(-2px);
}

.pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(250, 112, 154, 0.4);
}

.pagination :deep(.btn-prev),
.pagination :deep(.btn-next) {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  font-weight: 700;
  transition: all 0.3s ease;
}

.pagination :deep(.btn-prev):hover,
.pagination :deep(.btn-next):hover {
  background: linear-gradient(135deg, 
    rgba(250, 112, 154, 0.2) 0%, 
    rgba(254, 225, 64, 0.2) 100%);
  transform: translateY(-2px);
}

/* 下拉菜单美化 */
:deep(.el-dropdown-menu) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 2px solid rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

:deep(.el-dropdown-menu__item) {
  color: #333;
  font-weight: 600;
  transition: all 0.3s ease;
}

:deep(.el-dropdown-menu__item:hover) {
  background: linear-gradient(135deg, 
    rgba(250, 112, 154, 0.1) 0%, 
    rgba(254, 225, 64, 0.1) 100%);
  color: #fa709a;
}
</style>