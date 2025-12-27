<template>
  <div class="checkout-page">
    <van-nav-bar
      title="确认订单"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />
    
    <!-- 收货地址 -->
    <div class="address-section" @click="showAddressPicker">
      <div v-if="selectedAddress" class="address-info">
        <van-icon name="location-o" size="20" />
        <div class="address-details">
          <div class="recipient-info">
            <span class="name">{{ selectedAddress.recipient_name }}</span>
            <span class="phone">{{ selectedAddress.recipient_phone }}</span>
          </div>
          <div class="address-full">
            {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.street_address }}
          </div>
        </div>
        <van-icon name="arrow" />
      </div>
      <div v-else class="no-address">
        <van-icon name="location-o" size="20" />
        <span>请选择收货地址</span>
        <van-icon name="arrow" />
      </div>
    </div>
    
    <!-- 商品列表 -->
    <div class="goods-section">
      <van-card
        v-for="item in orderItems"
        :key="item.id"
        :num="item.quantity"
        :price="item.unit_price.toFixed(2)"
        :title="item.product_name"
        :thumb="item.product_image"
      />
    </div>
    
    <!-- 订单金额 -->
    <div class="order-summary">
      <van-cell-group>
        <van-cell title="商品总额" :value="`¥${totalAmount.toFixed(2)}`" />
        <van-cell title="运费" value="¥0.00" />
        <van-cell title="优惠券" value="-¥0.00" />
        <van-cell title="实际付款" :value="`¥${totalAmount.toFixed(2)}`" class="total-amount" />
      </van-cell-group>
    </div>
    
    <!-- 支付方式 -->
    <div class="payment-method">
      <van-cell title="支付方式" is-link @click="showPaymentPopup = true">
        <span>{{ currentPaymentMethod.text || '请选择' }}</span>
      </van-cell>
    </div>
    
    <!-- 提交订单 -->
    <div class="submit-order">
      <van-button 
        type="primary" 
        block 
        @click="submitOrder"
        :disabled="!selectedAddress || !currentPaymentMethod.value"
      >
        提交订单 ¥{{ totalAmount.toFixed(2) }}
      </van-button>
    </div>
    
    <!-- 地址选择弹窗 -->
    <van-popup v-model:show="showAddressList" position="bottom" round style="height: 80%; padding-top: 12px;">
      <div class="address-popup">
        <div class="popup-header">
          <span>收货地址</span>
          <van-icon name="cross" @click="showAddressList = false" />
        </div>
        <van-address-list
          v-model="chosenAddressId"
          :list="addressList"
          default-tag-text="默认"
          @add="onAddAddress"
          @edit="onEditAddress"
          @select="onSelectAddress"
        />
      </div>
    </van-popup>
    
    <!-- 支付方式弹窗 -->
    <van-popup v-model:show="showPaymentPopup" position="bottom" round style="padding: 20px;">
      <div class="payment-popup">
        <div class="popup-title">选择支付方式</div>
        <van-radio-group v-model="currentPaymentMethod.value">
          <van-cell-group>
            <van-cell clickable @click="currentPaymentMethod = { value: 'wechat', text: '微信支付' }">
              <div class="payment-option">
                <van-icon name="wechat" color="#07c160" size="24" />
                <span>微信支付</span>
              </div>
              <template #right-icon>
                <van-radio name="wechat" />
              </template>
            </van-cell>
            <van-cell clickable @click="currentPaymentMethod = { value: 'alipay', text: '支付宝' }">
              <div class="payment-option">
                <van-icon name="alipay" color="#1677ff" size="24" />
                <span>支付宝</span>
              </div>
              <template #right-icon>
                <van-radio name="alipay" />
              </template>
            </van-cell>
            <van-cell clickable @click="currentPaymentMethod = { value: 'balance', text: '余额支付' }">
              <div class="payment-option">
                <van-icon name="balance-pay" size="24" />
                <span>余额支付</span>
              </div>
              <template #right-icon>
                <van-radio name="balance" />
              </template>
            </van-cell>
          </van-cell-group>
        </van-radio-group>
        <van-button 
          type="primary" 
          block 
          @click="confirmPaymentMethod"
          style="margin-top: 20px;"
        >
          确认
        </van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import http from '@/utils/http'

const router = useRouter()
const route = useRoute()

// 地址相关数据
const showAddressList = ref(false)
const addressList = ref([])
const chosenAddressId = ref('')
const selectedAddress = ref(null)

// 支付方式相关数据
const showPaymentPopup = ref(false)
const currentPaymentMethod = ref({ value: '', text: '' })

// 订单相关数据
const orderItems = ref([])

// 获取订单总金额
const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + (item.unit_price * item.quantity)
  }, 0)
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
const onSelectAddress = (item, index) => {
  selectedAddress.value = item
  chosenAddressId.value = item.id
  showAddressList.value = false
}

// 确认支付方式
const confirmPaymentMethod = () => {
  showPaymentPopup.value = false
}

// 提交订单
const submitOrder = async () => {
  if (!selectedAddress.value) {
    showToast('请选择收货地址')
    return
  }
  
  if (!currentPaymentMethod.value.value) {
    showToast('请选择支付方式')
    return
  }
  
  try {
    // 构造订单数据
    const orderData = {
      user_id: 1, // 实际应用中应从用户信息获取
      address_id: selectedAddress.value.id,
      total_amount: totalAmount.value,
      discount_amount: 0,
      shipping_fee: 0,
      final_amount: totalAmount.value,
      payment_method: currentPaymentMethod.value.value,
      order_status: 'pending',
      order_items: orderItems.value.map(item => ({
        product_id: item.product_id,
        product_name: item.product_name,
        product_image: item.product_image,
        unit_price: item.unit_price,
        quantity: item.quantity,
        item_amount: item.unit_price * item.quantity
      }))
    }
    
    // 如果选择的是余额支付，需要检查余额
    if (currentPaymentMethod.value.value === 'balance') {
      // 这里应该调用获取用户余额的接口
      // 由于接口尚未实现，暂时留空
      // const balance = await getUserBalance()
      // if (balance < totalAmount.value) {
      //   showToast('余额不足')
      //   return
      // }
    }
    
    // 提交订单到后端
    const res = await http.post('/api/orders', orderData)
    
    if (res.data.success) {
      // 根据支付方式跳转
      switch (currentPaymentMethod.value.value) {
        case 'wechat':
          // 跳转到微信支付页面（模拟）
          window.open('https://pay.weixin.qq.com/', '_blank')
          break
        case 'alipay':
          // 跳转到支付宝页面（模拟）
          window.open('https://www.alipay.com/', '_blank')
          break
        case 'balance':
          // 余额支付，直接完成订单
          showToast('支付成功')
          router.push('/order-success')
          break
      }
      
      // 清空购物车等相关操作
    } else {
      showToast('提交订单失败')
    }
  } catch (error) {
    console.error('提交订单出错:', error)
    showToast('提交订单异常')
  }
}

// 获取用户地址列表
const fetchAddresses = async () => {
  try {
    const res = await http.get('/api/user/addresses')
    if (res.data.success) {
      addressList.value = res.data.data.map(addr => ({
        id: addr.id,
        name: addr.recipient_name,
        tel: addr.recipient_phone,
        address: `${addr.province}${addr.city}${addr.district}${addr.street_address}`,
        isDefault: addr.is_default
      }))
      
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
          street_address: defaultAddress.address_detail
        }
      }
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
  }
}

// 初始化订单项（这里是从路由参数获取，实际可根据需求调整）
const initOrderItems = () => {
  // 模拟订单项数据
  const items = route.query.items ? JSON.parse(route.query.items) : []
  orderItems.value = items
  
  // 如果没有传入订单项，则可能是从购物车进入
  if (orderItems.value.length === 0) {
    // 这里可以调用获取购物车数据的接口
    orderItems.value = [
      {
        id: 1,
        product_id: 1,
        product_name: '联想ThinkPad X1 Carbon 2023',
        product_image: 'https://example.com/product.jpg',
        unit_price: 9999.00,
        quantity: 1
      }
    ]
  }
}

// 页面加载时初始化数据
onMounted(() => {
  fetchAddresses()
  initOrderItems()
})
</script>

<style scoped>
.checkout-page {
  padding-bottom: 60px;
}

.address-section {
  background: white;
  margin: 10px;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.address-info {
  display: flex;
  align-items: center;
}

.address-details {
  flex: 1;
  margin-left: 10px;
}

.recipient-info {
  display: flex;
  margin-bottom: 5px;
}

.recipient-info .name {
  font-weight: bold;
  margin-right: 15px;
}

.address-full {
  color: #666;
  font-size: 14px;
}

.no-address {
  display: flex;
  align-items: center;
  color: #999;
}

.no-address span {
  margin: 0 10px;
}

.goods-section {
  background: white;
  margin: 10px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.order-summary {
  background: white;
  margin: 10px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.total-amount {
  font-weight: bold;
  font-size: 16px;
  color: #ee0a24;
}

.payment-method {
  background: white;
  margin: 10px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.submit-order {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 10px;
  background: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.address-popup .popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 15px 10px;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #f5f5f5;
  margin-bottom: 10px;
}

.payment-popup .popup-title {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
}

.payment-option {
  display: flex;
  align-items: center;
}

.payment-option span {
  margin-left: 10px;
}
</style>