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
              :src="product.image || '@/assets/default-product.jpg'" 
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
    image: ''
  },
  {
    id: 2,
    name: '联想拯救者Y7000P 2023',
    price: '9999.00',
    description: '游戏本，高性能配置',
    image: ''
  },
  {
    id: 3,
    name: '联想小新Pro14 2023',
    price: '5999.00',
    description: '轻薄办公本，高性价比',
    image: ''
  },
  {
    id: 4,
    name: '联想YOGA 14s 2023',
    price: '7999.00',
    description: '翻转触控屏，创意设计本',
    image: ''
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
.mall-home {
  padding: 20px;
  background-image: url('@/assets/主页背景.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.logo {
  display: flex;
  align-items: center;
}

.logo img {
  width: 50px;
  height: 50px;
  margin-right: 15px;
}

.logo h1 {
  font-size: 24px;
  color: #333;
}

.search-bar {
  flex: 1;
  margin: 0 30px;
}

.user-actions {
  display: flex;
  gap: 10px;
}

.categories {
  margin-bottom: 20px;
  background-color: rgba(255, 255, 255, 0.9);
  padding: 10px 20px;
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.categories :deep(.el-tabs__item) {
  font-weight: bold;
}

.categories :deep(.el-tabs__item.is-active) {
  color: #409eff !important;
}

.product-list {
  margin-bottom: 20px;
}

.product-item {
  margin-bottom: 20px;
}

.product-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  border-radius: 10px;
  overflow: hidden;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.2);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  cursor: pointer;
}

.product-name {
  font-weight: bold;
  font-size: 16px;
  display: block;
  margin-bottom: 10px;
}

.product-price {
  color: #ff6600;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.product-description {
  color: #999;
  font-size: 14px;
  margin-bottom: 15px;
  height: 40px;
  overflow: hidden;
}

.bottom {
  text-align: center;
}

.pagination {
  display: flex;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.9);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}
</style>