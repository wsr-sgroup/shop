<template>
  <div class="address-manage">
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" circle></el-button>
      <h2>收货地址管理</h2>
      <el-button type="primary" @click="addAddress" :icon="Plus">
        新增地址
      </el-button>
    </div>

    <!-- 地址列表 -->
    <div class="address-list">
      <div v-if="addresses.length > 0">
        <div 
          v-for="address in addresses" 
          :key="address.id" 
          class="address-card"
          :class="{ 'default-address': address.isDefault }"
        >
          <!-- 默认标签 -->
          <div class="default-tag" v-if="address.isDefault">
            <el-tag type="danger" effect="dark" size="large">默认地址</el-tag>
          </div>

          <!-- 地址信息 -->
          <div class="address-info">
            <div class="info-row">
              <span class="label">
                <el-icon><User /></el-icon>
                收货人:
              </span>
              <span class="value name">{{ address.name }}</span>
              <span class="phone">{{ address.phone }}</span>
            </div>
            <div class="info-row">
              <span class="label">
                <el-icon><Location /></el-icon>
                地址:
              </span>
              <span class="value address-detail">
                {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail }}
              </span>
            </div>
            <div class="info-row" v-if="address.postcode">
              <span class="label">
                <el-icon><Postcard /></el-icon>
                邮编:
              </span>
              <span class="value">{{ address.postcode }}</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="address-actions">
            <el-button 
              v-if="!address.isDefault" 
              size="small" 
              type="primary"
              @click="setDefault(address.id)"
            >
              <el-icon><Check /></el-icon>
              设为默认
            </el-button>
            <el-button size="small" @click="editAddress(address)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger"
              @click="deleteAddress(address.id)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty description="还没有收货地址">
          <el-button type="primary" @click="addAddress">添加地址</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 编辑地址对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="addressForm" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="收货人" prop="name">
          <el-input v-model="addressForm.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所在地区" prop="region">
          <el-cascader
            v-model="addressForm.region"
            :options="regionOptions"
            placeholder="请选择省/市/区"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input 
            v-model="addressForm.detail" 
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址，如道路、门牌号、小区、楼栋号、单元等信息"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="邮政编码" prop="postcode">
          <el-input v-model="addressForm.postcode" placeholder="选填，邮政编码" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus, User, Location, Postcard, Check, Edit, Delete } from '@element-plus/icons-vue'

const router = useRouter()
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const saving = ref(false)
const isEdit = ref(false)

// 模拟地址数据
const addresses = ref([
  {
    id: 1,
    name: '张三',
    phone: '13800138000',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    detail: '望京SOHO T3座 2008室',
    postcode: '100000',
    isDefault: true
  },
  {
    id: 2,
    name: '李四',
    phone: '13900139000',
    province: '上海市',
    city: '上海市',
    district: '浦东新区',
    detail: '陆家嘴环路1000号恒生银行大厦10楼',
    postcode: '200120',
    isDefault: false
  },
  {
    id: 3,
    name: '王五',
    phone: '13700137000',
    province: '广东省',
    city: '深圳市',
    district: '南山区',
    detail: '科技园南区深圳湾科技生态园5栋A座',
    postcode: '518000',
    isDefault: false
  }
])

const addressForm = reactive({
  id: null,
  name: '',
  phone: '',
  region: [],
  detail: '',
  postcode: '',
  isDefault: false
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  region: [
    { required: true, message: '请选择所在地区', trigger: 'change' }
  ],
  detail: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 100, message: '详细地址长度在5-100个字符', trigger: 'blur' }
  ]
}

// 地区选项（简化版）
const regionOptions = [
  {
    value: '北京市',
    label: '北京市',
    children: [
      {
        value: '北京市',
        label: '北京市',
        children: [
          { value: '东城区', label: '东城区' },
          { value: '西城区', label: '西城区' },
          { value: '朝阳区', label: '朝阳区' },
          { value: '海淀区', label: '海淀区' }
        ]
      }
    ]
  },
  {
    value: '上海市',
    label: '上海市',
    children: [
      {
        value: '上海市',
        label: '上海市',
        children: [
          { value: '黄浦区', label: '黄浦区' },
          { value: '徐汇区', label: '徐汇区' },
          { value: '浦东新区', label: '浦东新区' },
          { value: '闵行区', label: '闵行区' }
        ]
      }
    ]
  },
  {
    value: '广东省',
    label: '广东省',
    children: [
      {
        value: '广州市',
        label: '广州市',
        children: [
          { value: '天河区', label: '天河区' },
          { value: '越秀区', label: '越秀区' }
        ]
      },
      {
        value: '深圳市',
        label: '深圳市',
        children: [
          { value: '福田区', label: '福田区' },
          { value: '南山区', label: '南山区' }
        ]
      }
    ]
  }
]

const goBack = () => {
  router.back()
}

const addAddress = () => {
  dialogTitle.value = '新增地址'
  isEdit.value = false
  // 重置表单
  Object.assign(addressForm, {
    id: null,
    name: '',
    phone: '',
    region: [],
    detail: '',
    postcode: '',
    isDefault: false
  })
  dialogVisible.value = true
}

const editAddress = (address) => {
  dialogTitle.value = '编辑地址'
  isEdit.value = true
  // 填充表单
  Object.assign(addressForm, {
    id: address.id,
    name: address.name,
    phone: address.phone,
    region: [address.province, address.city, address.district],
    detail: address.detail,
    postcode: address.postcode,
    isDefault: address.isDefault
  })
  dialogVisible.value = true
}

const saveAddress = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      saving.value = true
      // 模拟保存延迟
      setTimeout(() => {
        saving.value = false
        
        const [province, city, district] = addressForm.region
        const newAddress = {
          id: addressForm.id || Date.now(),
          name: addressForm.name,
          phone: addressForm.phone,
          province,
          city,
          district,
          detail: addressForm.detail,
          postcode: addressForm.postcode,
          isDefault: addressForm.isDefault
        }

        if (isEdit.value) {
          // 编辑
          const index = addresses.value.findIndex(a => a.id === addressForm.id)
          if (index > -1) {
            // 如果设为默认，取消其他默认地址
            if (newAddress.isDefault) {
              addresses.value.forEach(a => a.isDefault = false)
            }
            addresses.value[index] = newAddress
            ElMessage.success('地址修改成功')
          }
        } else {
          // 新增
          if (newAddress.isDefault) {
            addresses.value.forEach(a => a.isDefault = false)
          }
          addresses.value.push(newAddress)
          ElMessage.success('地址添加成功')
        }
        
        dialogVisible.value = false
      }, 800)
    }
  })
}

const setDefault = (id) => {
  addresses.value.forEach(a => {
    a.isDefault = a.id === id
  })
  ElMessage.success('已设为默认地址')
}

const deleteAddress = (id) => {
  ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = addresses.value.findIndex(a => a.id === id)
    if (index > -1) {
      addresses.value.splice(index, 1)
      ElMessage.success('地址已删除')
    }
  }).catch(() => {})
}
</script>

<style scoped>
.address-manage {
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
  flex: 1;
  text-align: center;
}

.page-header :deep(.el-button--primary) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  border: none;
  padding: 12px 30px;
  border-radius: 20px;
  font-weight: bold;
}

.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 20px;
}

.address-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(250, 112, 154, 0.3);
  transition: all 0.3s;
  position: relative;
  border: 2px solid transparent;
}

.address-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(250, 112, 154, 0.4);
}

.address-card.default-address {
  border-color: #fa709a;
}

.default-tag {
  position: absolute;
  top: 0;
  right: 0;
  transform: translate(10px, -10px);
}

.address-info {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
  line-height: 1.6;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  display: flex;
  align-items: center;
  gap: 5px;
  font-weight: bold;
  color: #666;
  min-width: 80px;
  flex-shrink: 0;
}

.value {
  color: #333;
  flex: 1;
}

.name {
  font-size: 18px;
  font-weight: bold;
  color: #fa709a;
}

.phone {
  font-size: 16px;
  color: #666;
  margin-left: 20px;
}

.address-detail {
  font-size: 15px;
  line-height: 1.8;
}

.address-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 2px dashed rgba(250, 112, 154, 0.2);
}

.address-actions :deep(.el-button) {
  border-radius: 20px;
  padding: 8px 20px;
  font-weight: bold;
  transition: all 0.3s;
}

.address-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  border: none;
}

.address-actions :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(250, 112, 154, 0.4);
}

.address-actions :deep(.el-button--danger) {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  border: none;
}

.empty-state {
  grid-column: 1 / -1;
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

/* 对话框样式 */
:deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  padding: 20px;
  margin: 0;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: bold;
  font-size: 18px;
}

:deep(.el-dialog__close) {
  color: white;
  font-size: 20px;
}

:deep(.el-dialog__body) {
  padding: 30px;
}

:deep(.el-dialog__footer) {
  padding: 20px 30px;
  border-top: 1px solid #f0f0f0;
}

:deep(.el-form-item__label) {
  font-weight: bold;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  border-radius: 10px;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  border: none;
  border-radius: 20px;
  padding: 10px 30px;
  font-weight: bold;
}
</style>
