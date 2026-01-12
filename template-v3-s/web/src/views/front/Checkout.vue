<template>
  <div class="checkout-page">
    <el-page-header @back="onClickLeft" title="确认订单"></el-page-header>
    
    <!-- 收货地址 -->
    <div class="address-section">
      <h3 class="section-title">收货信息</h3>
      <el-card shadow="never" class="address-card">
        <el-form label-position="top" :model="selectedAddress" label-width="80px" style="max-width: 600px;">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="收货人" required>
                <el-input v-model="selectedAddress.recipient_name" placeholder="请输入收货人姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" required>
                <el-input v-model="selectedAddress.recipient_phone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="省份" required>
                <el-input v-model="selectedAddress.province" placeholder="请输入省份" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="城市" required>
                <el-input v-model="selectedAddress.city" placeholder="请输入城市" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="区县" required>
                <el-input v-model="selectedAddress.district" placeholder="请输入区县" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="详细地址" required>
                <el-input v-model="selectedAddress.street_address" placeholder="请输入详细街道地址" :rows="2" type="textarea" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
    </div>
    
    <!-- 商品列表 -->
    <div class="goods-section">
      <h3 class="section-title">商品信息</h3>
      <el-card
        v-for="item in orderItems"
        :key="item.id"
        shadow="never"
        class="product-card"
      >
        <template #header>
          <div class="card-header">
            <img :src="item.productImage" :alt="item.productName" class="product-thumb" />
            <div class="product-info">
              <h4 class="product-title">{{ item.productName }}</h4>
              <div class="product-price">¥{{ item.unitPrice.toFixed(2) }}</div>
            </div>
          </div>
        </template>
        <div class="card-body">
          <div class="product-quantity">数量: {{ item.quantity }}</div>
          <div class="product-subtotal">小计: ¥{{ (item.unitPrice * item.quantity).toFixed(2) }}</div>
        </div>
      </el-card>
    </div>
    
    <!-- 订单金额 -->
    <div class="order-summary">
      <h3 class="section-title">订单金额</h3>
      <el-card shadow="never">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="商品总额">¥{{ totalAmount.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="运费">¥{{ shippingFee.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="优惠券">-¥{{ coupon.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="实际付款" class="total-amount">¥{{ actualPayment.toFixed(2) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
    
    <!-- 支付方式 -->
    <div class="payment-method">
      <el-card shadow="never">
        <el-row @click="showPaymentPopup = true" class="payment-row">
          <el-col :span="6">支付方式</el-col>
          <el-col :span="16">
            <span :style="{ color: !currentPaymentValue ? 'red' : '#606266' }">
              {{ currentPaymentText || '请选择' }}
            </span>
          </el-col>
          <el-col :span="2"><el-icon><Right /></el-icon></el-col>
        </el-row>
      </el-card>
    </div>
    
    <!-- 付款按钮 -->
    <div class="submit-order">
      <el-button 
        type="primary" 
        block 
        @click="submitOrder"
        :disabled="!selectedAddress.recipient_name || !selectedAddress.recipient_phone || !currentPaymentValue"
        size="large"
      >
        付款 ¥{{ actualPayment.toFixed(2) }}
      </el-button>
    </div>
    
    <!-- 地址选择弹窗 -->
    <el-dialog
      v-model="showAddressList"
      title="收货地址"
      width="90%"
      center
    >
      <div class="address-list">
        <div 
          v-for="item in addressList" 
          :key="item.id"
          class="address-item"
          @click="onSelectAddress(item)"
        >
          <div class="address-item-header">
            <span class="address-name">{{ item.name }}</span>
            <span class="address-phone">{{ item.tel }}</span>
            <el-tag v-if="item.isDefault" size="small" type="primary">默认</el-tag>
          </div>
          <div class="address-item-content">{{ item.address }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" block @click="showAddressList = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 支付方式弹窗 -->
    <el-dialog
      v-model="showPaymentPopup"
      title="选择支付方式"
      width="80%"
      center
    >
      <div class="payment-methods">
        <div 
          v-for="method in paymentMethods" 
          :key="method.value"
          class="payment-method-item"
        >
          <div class="payment-method-info">
            <el-icon :size="24" :color="method.color"><component :is="method.icon" /></el-icon>
            <span>{{ method.text }}</span>
          </div>
          <el-radio v-model="currentPaymentValue" :label="method.value"></el-radio>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPaymentPopup = false">取消</el-button>
          <el-button type="primary" @click="confirmPaymentMethod">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  HomeFilled, User, Search, Setting, Check, Delete, Edit, Plus, Minus, Close, Right 
} from '@element-plus/icons-vue'
import http from '@/utils/http'

const router = useRouter()
const route = useRoute()

// 地址相关数据
const showAddressList = ref(false)
const addressList = ref([])
const chosenAddressId = ref('')
// 初始化选中地址，确保包含所有需要的字段
const selectedAddress = ref({
  recipient_name: '',
  recipient_phone: '',
  province: '',
  city: '',
  district: '',
  street_address: ''
})

// 支付方式相关数据
const showPaymentPopup = ref(false)
const currentPaymentValue = ref('')

// 支付方式列表
const paymentMethods = ref([
  { value: 'wechat', text: '微信支付', icon: HomeFilled, color: '#07c160' },
  { value: 'alipay', text: '支付宝', icon: User, color: '#1677ff' },
  { value: 'balance', text: '余额支付', icon: Setting, color: '#ff9800' }
])

// 获取当前支付方式的文本
const currentPaymentText = computed(() => {
  const method = paymentMethods.value.find(m => m.value === currentPaymentValue.value)
  return method ? method.text : ''
})

// 订单相关数据
const orderItems = ref([])

// 运费（10到50元之间的随机数）
const shippingFee = ref(0)

// 优惠券金额（固定为0）
const coupon = ref(0)

// 获取订单总金额
const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + (item.unitPrice * item.quantity)
  }, 0)
})

// 计算实际付款金额
const actualPayment = computed(() => {
  return totalAmount.value + shippingFee.value - coupon.value
})

// 返回上一页
const onClickLeft = () => {
  router.go(-1)
}

// 显示地址选择器
const showAddressPicker = () => {
  showAddressList.value = true
}

// 添加新地址
const onAddAddress = () => {
  // 这里应该跳转到添加地址页面
  router.push('/address/edit')
}

// 编辑地址
const onEditAddress = (item, index) => {
  // 这里应该跳转到编辑地址页面
  router.push(`/address/edit/${item.id}`)
}

// 选择地址
const onSelectAddress = (item) => {
  selectedAddress.value = {
    id: item.id,
    recipient_name: item.name,
    recipient_phone: item.tel,
    province: item.province,
    city: item.city,
    district: item.district,
    street_address: item.street_address
  }
  chosenAddressId.value = item.id
  showAddressList.value = false
}

// 确认支付方式
const confirmPaymentMethod = () => {
  showPaymentPopup.value = false
}

// 提交订单
const submitOrder = async () => {
  // 验证收货人姓名
  if (!selectedAddress.value.recipient_name) {
    ElMessage.warning('请填写收货人姓名')
    return
  }
  
  // 验证联系电话
  if (!selectedAddress.value.recipient_phone) {
    ElMessage.warning('请填写联系电话')
    return
  }
  
  // 验证支付方式
  if (!currentPaymentValue.value) {
    ElMessage.warning('请选择支付方式')
    return
  }
  
  // 验证商品列表
  if (orderItems.value.length === 0) {
    ElMessage.warning('订单中没有商品')
    return
  }
  
  try {
    // 显示提交中的提示
    ElMessage.info('正在提交订单...')
    
    // 构造订单数据
    const orderData = {
      addressId: selectedAddress.value.id || 0, // 使用默认地址ID或0
      paymentMethod: currentPaymentValue.value, // 获取ref的值
      buyerNote: '',
      invoiceNeeded: 0,
      orderItems: orderItems.value.map(item => ({
        productId: parseInt(item.productId),
        quantity: parseInt(item.quantity),
        unitPrice: parseFloat(item.unitPrice),
        productSpec: item.productSpec || ''
      }))
    }
    
    console.log('提交订单数据:', orderData)
    
    // 提交订单到后端
    const res = await http.post('/order/create', orderData)
    
    console.log('订单创建响应:', res)
    
    if (res && res.code === 200) {
      ElMessage.success('订单创建成功，正在跳转到订单中心')
      // 跳转到用户订单界面
      router.push('/user/center')
      
      // 清空购物车等相关操作
      // 从sessionStorage中移除订单商品信息
      sessionStorage.removeItem('orderItems')
    } else {
      ElMessage.error(res && res.msg || '提交订单失败')
    }
  } catch (error) {
    console.error('提交订单出错:', error)
    ElMessage.error('提交订单异常，请查看控制台日志')
  }
}

// 获取用户地址列表
const fetchAddresses = async () => {
  // 使用模拟地址数据，避免调用不存在的API
  addressList.value = [
    {
      id: 1,
      name: '张三',
      tel: '13800138000',
      province: '广东省',
      city: '深圳市',
      district: '福田区',
      street_address: '福华路3001号会展中心',
      address: '广东省深圳市福田区福华路3001号会展中心',
      isDefault: true
    },
    {
      id: 2,
      name: '李四',
      tel: '13800138001',
      province: '北京市',
      city: '北京市',
      district: '朝阳区',
      street_address: '建国门外大街1号国贸大厦',
      address: '北京市朝阳区建国门外大街1号国贸大厦',
      isDefault: false
    }
  ]
  
  // 设置默认地址
  const defaultAddress = addressList.value.find(addr => addr.isDefault)
  if (defaultAddress) {
    chosenAddressId.value = defaultAddress.id
    selectedAddress.value = {
      id: defaultAddress.id,
      recipient_name: defaultAddress.name,
      recipient_phone: defaultAddress.tel,
      province: defaultAddress.province,
      city: defaultAddress.city,
      district: defaultAddress.district,
      street_address: defaultAddress.street_address
    }
  }
}

// 初始化订单项
const initOrderItems = () => {
  console.log('初始化订单项')
  
  // 检查URL参数中是否有商品信息（立即购买场景）
  const productId = route.query.productId
  const quantity = route.query.quantity
  
  if (productId && quantity) {
    // 立即购买场景：根据URL参数创建订单商品
    console.log('从URL参数获取商品信息:', productId, quantity)
    
    // 调用API获取商品详情
    http.get(`/product/${productId}`).then(res => {
      if (res && res.code === 200 && res.data) {
        const product = res.data
        // 创建订单商品
        orderItems.value = [{
          id: 1,
          productId: parseInt(productId),
          productName: product.name,
          productImage: product.mainImage || '@/assets/default-product.jpg',
          unitPrice: product.price,
          quantity: parseInt(quantity)
        }]
        
        // 生成10到50元之间的随机运费
        shippingFee.value = parseFloat((Math.random() * (50 - 10) + 10).toFixed(2))
        console.log('生成的随机运费:', shippingFee.value)
        
        // 优惠券金额固定为0
        coupon.value = 0
        console.log('优惠券金额:', coupon.value)
      } else {
        console.error('获取商品详情失败:', res)
        ElMessage.error('获取商品详情失败')
        useMockData()
      }
    }).catch(error => {
      console.error('获取商品详情出错:', error)
      ElMessage.error('获取商品详情出错')
      useMockData()
    })
  } else {
    // 从sessionStorage获取订单商品信息（购物车结算场景）
    const orderItemsStr = sessionStorage.getItem('orderItems')
    
    if (orderItemsStr) {
      try {
        orderItems.value = JSON.parse(orderItemsStr)
        console.log('从sessionStorage获取订单商品信息:', orderItems.value)
        
        // 生成10到50元之间的随机运费
        shippingFee.value = parseFloat((Math.random() * (50 - 10) + 10).toFixed(2))
        console.log('生成的随机运费:', shippingFee.value)
        
        // 优惠券金额固定为0
        coupon.value = 0
        console.log('优惠券金额:', coupon.value)
      } catch (error) {
        console.error('解析订单商品信息失败:', error)
        ElMessage.error('获取订单商品信息失败')
        // 使用模拟数据
        useMockData()
      }
    } else {
      console.log('sessionStorage中没有订单商品信息，使用模拟数据')
      useMockData()
    }
  }
}

// 使用模拟数据
const useMockData = () => {
  // 模拟购物车选中的商品
  orderItems.value = [
    {
      id: 1,
      productId: 1,
      productName: '商品1',
      productImage: 'https://via.placeholder.com/150',
      unitPrice: 3,
      quantity: 1
    },
    {
      id: 2,
      productId: 2,
      productName: '商品2',
      productImage: 'https://via.placeholder.com/150',
      unitPrice: 5,
      quantity: 3
    }
  ]
  
  // 生成10到50元之间的随机运费
  shippingFee.value = parseFloat((Math.random() * (50 - 10) + 10).toFixed(2))
  
  // 优惠券金额固定为0
  coupon.value = 0
  
  console.log('使用模拟数据，订单商品:', orderItems.value)
  console.log('模拟数据，运费:', shippingFee.value)
}

// 页面加载时初始化数据
onMounted(() => {
  fetchAddresses()
  initOrderItems()
})
</script>

<style scoped>
/* 结算页主容器 - 炫彩流动背景 */
.checkout-page {
  padding: 30px;
  padding-bottom: 140px;
  background: linear-gradient(135deg, 
    #fa709a 0%, 
    #40fef1 25%, 
    #ffdde1 50%, 
    #feca57 75%, 
    #fa709a 100%);
  background-size: 400% 400%;
  animation: checkoutGradient 25s ease infinite;
  min-height: 100vh;
}

@keyframes checkoutGradient {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 区块标题 - 彩虹渐变 */
.section-title {
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(90deg, 
    #fa709a 0%, 
    #fee140 50%, 
    #ff6b6b 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 25px 20px 15px;
  letter-spacing: 1px;
  text-transform: uppercase;
  animation: titleSlide 2s ease-in-out infinite;
}

@keyframes titleSlide {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(5px); }
}

/* 收货地址区块 */
.address-section {
  margin-bottom: 25px;
}

/* 地址卡片 - 玻璃拟态 */
.address-card {
  margin: 0 20px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.15),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border: 2px solid rgba(255, 255, 255, 0.5);
  overflow: hidden;
  transition: all 0.4s ease;
  position: relative;
}

.address-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(250, 112, 154, 0.1), 
    transparent);
  transition: left 0.6s;
}

.address-card:hover::before {
  left: 100%;
}

.address-card:hover {
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25),
              0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  transform: translateY(-5px);
}

/* 商品列表区块 */
.goods-section {
  margin-bottom: 25px;
}

/* 商品卡片 - 玻璃拟态 */
.product-card {
  margin: 0 20px 15px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.12),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border: 2px solid rgba(255, 255, 255, 0.5);
  overflow: hidden;
  transition: all 0.4s ease;
  position: relative;
}

.product-card::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, 
    #fa709a 0%, 
    #fee140 50%, 
    #ff6b6b 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.product-card:hover::after {
  opacity: 1;
}

.product-card:hover {
  box-shadow: 0 15px 50px rgba(250, 112, 154, 0.25),
              0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  transform: translateY(-3px) translateX(3px);
}

.card-header {
  display: flex;
  align-items: center;
  padding: 15px;
}

/* 商品缩略图 - 彩色边框 */
.product-thumb {
  width: 90px;
  height: 90px;
  object-fit: cover;
  border-radius: 12px;
  margin-right: 15px;
  border: 3px solid transparent;
  background: linear-gradient(white, white) padding-box,
              linear-gradient(135deg, #fa709a, #fee140, #ff6b6b) border-box;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.product-thumb:hover {
  transform: scale(1.1) rotate(3deg);
  box-shadow: 0 12px 30px rgba(250, 112, 154, 0.3);
}

.product-info {
  flex: 1;
}

/* 商品标题 - 渐变文字 */
.product-title {
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0 0 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 商品价格 - 炫彩渐变 */
.product-price {
  font-size: 18px;
  font-weight: 900;
  background: linear-gradient(135deg, 
    #ff6b6b 0%, 
    #ff8e53 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.card-body {
  text-align: right;
  color: #606266;
  font-size: 14px;
  padding: 0 15px 15px;
  font-weight: 600;
}

/* 订单金额区块 */
.order-summary {
  margin-bottom: 25px;
}

.order-summary .el-card {
  margin: 0 20px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.15),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border: 2px solid rgba(255, 255, 255, 0.5);
  overflow: hidden;
}

/* 总金额 - 金色闪耀 */
.total-amount {
  font-weight: 900;
  font-size: 22px;
  background: linear-gradient(135deg, 
    #ff6b6b 0%, 
    #ff8e53 25%, 
    #ffd93d 50%, 
    #ff8e53 75%, 
    #ff6b6b 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: amountShine 3s linear infinite;
}

@keyframes amountShine {
  0% { background-position: 0% center; }
  100% { background-position: 200% center; }
}

/* 支付方式区块 */
.payment-method {
  margin-bottom: 25px;
}

.payment-method .el-card {
  margin: 0 20px;
  border-radius: 20px;
  background: linear-gradient(135deg, 
    rgba(250, 112, 154, 0.15) 0%, 
    rgba(254, 225, 64, 0.15) 100%);
  backdrop-filter: blur(20px);
  box-shadow: 0 10px 40px rgba(250, 112, 154, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border: 2px solid rgba(250, 112, 154, 0.3);
  overflow: hidden;
  transition: all 0.3s ease;
}

.payment-method .el-card:hover {
  box-shadow: 0 15px 50px rgba(250, 112, 154, 0.3);
  transform: translateY(-2px);
}

.payment-row {
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 15px 0;
  border-radius: 10px;
}

.payment-row:hover {
  background: linear-gradient(90deg, 
    rgba(250, 112, 154, 0.1) 0%, 
    rgba(254, 225, 64, 0.1) 100%);
  transform: translateX(5px);
}

/* 付款按钮栏 - 玻璃拟态 */
.submit-order {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(30px);
  box-shadow: 0 -10px 50px rgba(0, 0, 0, 0.2),
              0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  border-top-left-radius: 30px;
  border-top-right-radius: 30px;
  border-top: 2px solid rgba(255, 255, 255, 0.5);
  z-index: 1000;
  animation: submitSlideUp 0.5s ease-out;
}

@keyframes submitSlideUp {
  from {
    opacity: 0;
    transform: translateY(100%);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 付款按钮 - 多彩渐变 */
.submit-order .el-button {
  background: linear-gradient(135deg, 
    #fa709a 0%, 
    #fee140 33%, 
    #ff6b6b 66%, 
    #feca57 100%) !important;
  background-size: 200% auto;
  border: none;
  height: 56px;
  font-size: 18px;
  font-weight: 900;
  border-radius: 28px;
  transition: all 0.4s ease;
  letter-spacing: 2px;
  text-transform: uppercase;
  box-shadow: 0 10px 30px rgba(250, 112, 154, 0.5);
  position: relative;
  overflow: hidden;
}

.submit-order .el-button::before {
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

.submit-order .el-button:hover::before {
  left: 100%;
}

.submit-order .el-button:hover {
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 15px 40px rgba(250, 112, 154, 0.7);
  background-position: right center;
}

.submit-order .el-button:active {
  transform: translateY(-2px) scale(1);
}

.submit-order .el-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 支付方式弹窗 */
.payment-methods {
  padding: 20px;
}

/* 支付方式选项 - 渐变卡片 */
.payment-method-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-radius: 15px;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.9) 0%, 
    rgba(240, 242, 245, 0.9) 100%);
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.4s ease;
  border: 2px solid transparent;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.payment-method-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, 
    #fa709a 0%, 
    #fee140 50%, 
    #ff6b6b 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.payment-method-item:hover::before {
  opacity: 1;
}

.payment-method-item:hover {
  background: linear-gradient(135deg, 
    rgba(250, 112, 154, 0.08) 0%, 
    rgba(254, 225, 64, 0.08) 100%);
  border-color: rgba(250, 112, 154, 0.3);
  transform: translateX(5px);
  box-shadow: 0 8px 25px rgba(250, 112, 154, 0.2);
}

.payment-method-item.active {
  background: linear-gradient(135deg, 
    rgba(250, 112, 154, 0.15) 0%, 
    rgba(254, 225, 64, 0.15) 100%);
  border: 2px solid transparent;
  background-origin: border-box;
  background-clip: padding-box, border-box;
  background-image: linear-gradient(135deg, 
                    rgba(250, 112, 154, 0.15) 0%, 
                    rgba(254, 225, 64, 0.15) 100%),
                    linear-gradient(135deg, #fa709a, #fee140);
  box-shadow: 0 10px 30px rgba(250, 112, 154, 0.3);
}

.payment-method-item.active::before {
  opacity: 1;
}

.payment-method-info {
  display: flex;
  align-items: center;
}

.payment-method-info span {
  margin-left: 15px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 表单样式优化 */
.el-form-item {
  margin-bottom: 18px;
}

.el-form-item__label {
  font-weight: 700;
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 15px;
}

/* 输入框美化 */
.el-input__wrapper {
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 2px solid rgba(250, 112, 154, 0.2);
  background: rgba(255, 255, 255, 0.8);
}

.el-input__wrapper:hover {
  border-color: rgba(250, 112, 154, 0.4);
  background: rgba(255, 255, 255, 1);
}

.el-input__wrapper:focus-within {
  box-shadow: 0 0 0 3px rgba(250, 112, 154, 0.15);
  border-color: #fa709a;
  background: rgba(255, 255, 255, 1);
}

.el-select__wrapper {
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 2px solid rgba(250, 112, 154, 0.2);
}

.el-select__wrapper:hover {
  border-color: rgba(250, 112, 154, 0.4);
}

.el-select__wrapper:focus-within {
  box-shadow: 0 0 0 3px rgba(250, 112, 154, 0.15);
  border-color: #fa709a;
}

/* 文本域美化 */
:deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid rgba(102, 126, 234, 0.2);
  background: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

:deep(.el-textarea__inner):hover {
  border-color: rgba(250, 112, 154, 0.4);
  background: rgba(255, 255, 255, 1);
}

:deep(.el-textarea__inner):focus {
  box-shadow: 0 0 0 3px rgba(250, 112, 154, 0.15);
  border-color: #fa709a;
  background: rgba(255, 255, 255, 1);
}

/* 描述列表美化 */
:deep(.el-descriptions__label) {
  background: linear-gradient(135deg, 
    rgba(250, 112, 154, 0.1) 0%, 
    rgba(254, 225, 64, 0.1) 100%) !important;
  font-weight: 700;
  color: #fa709a !important;
}

:deep(.el-descriptions__content) {
  background: rgba(255, 255, 255, 0.8) !important;
  color: #333;
  font-weight: 500;
}
</style>