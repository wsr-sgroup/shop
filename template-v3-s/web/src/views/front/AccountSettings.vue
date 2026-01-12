<template>
  <div class="account-settings">
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" circle></el-button>
      <h2>账户设置</h2>
      <div></div>
    </div>

    <div class="settings-container">
      <!-- 安全设置 -->
      <div class="setting-section">
        <div class="section-title">
          <el-icon><Lock /></el-icon>
          <span>安全设置</span>
        </div>
        <div class="setting-items">
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><Key /></el-icon>
              <div class="item-info">
                <div class="item-title">登录密码</div>
                <div class="item-desc">定期更换密码可以保护账户安全</div>
              </div>
            </div>
            <el-button type="primary" @click="changePassword">修改</el-button>
          </div>
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><Iphone /></el-icon>
              <div class="item-info">
                <div class="item-title">绑定手机</div>
                <div class="item-desc">已绑定: 138****8000</div>
              </div>
            </div>
            <el-button @click="changePhone">更换</el-button>
          </div>
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><Message /></el-icon>
              <div class="item-info">
                <div class="item-title">绑定邮箱</div>
                <div class="item-desc">已绑定: user@example.com</div>
              </div>
            </div>
            <el-button @click="changeEmail">更换</el-button>
          </div>
        </div>
      </div>

      <!-- 隐私设置 -->
      <div class="setting-section">
        <div class="section-title">
          <el-icon><View /></el-icon>
          <span>隐私设置</span>
        </div>
        <div class="setting-items">
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><User /></el-icon>
              <div class="item-info">
                <div class="item-title">个人资料展示</div>
                <div class="item-desc">设置哪些信息对其他用户可见</div>
              </div>
            </div>
            <el-switch v-model="privacySettings.showProfile" />
          </div>
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><ShoppingCart /></el-icon>
              <div class="item-info">
                <div class="item-title">购物记录</div>
                <div class="item-desc">是否展示我的购物记录</div>
              </div>
            </div>
            <el-switch v-model="privacySettings.showOrders" />
          </div>
        </div>
      </div>

      <!-- 通知设置 -->
      <div class="setting-section">
        <div class="section-title">
          <el-icon><Bell /></el-icon>
          <span>通知设置</span>
        </div>
        <div class="setting-items">
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><ChatDotRound /></el-icon>
              <div class="item-info">
                <div class="item-title">订单通知</div>
                <div class="item-desc">接收订单状态变更提醒</div>
              </div>
            </div>
            <el-switch v-model="notificationSettings.orderNotify" />
          </div>
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><Present /></el-icon>
              <div class="item-info">
                <div class="item-title">优惠活动</div>
                <div class="item-desc">接收促销活动和优惠信息</div>
              </div>
            </div>
            <el-switch v-model="notificationSettings.promotionNotify" />
          </div>
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><MessageBox /></el-icon>
              <div class="item-info">
                <div class="item-title">系统消息</div>
                <div class="item-desc">接收系统通知和公告</div>
              </div>
            </div>
            <el-switch v-model="notificationSettings.systemNotify" />
          </div>
        </div>
      </div>

      <!-- 其他设置 -->
      <div class="setting-section">
        <div class="section-title">
          <el-icon><Setting /></el-icon>
          <span>其他设置</span>
        </div>
        <div class="setting-items">
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><Monitor /></el-icon>
              <div class="item-info">
                <div class="item-title">深色模式</div>
                <div class="item-desc">开启夜间深色主题</div>
              </div>
            </div>
            <el-switch v-model="otherSettings.darkMode" />
          </div>
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><Cpu /></el-icon>
              <div class="item-info">
                <div class="item-title">智能推荐</div>
                <div class="item-desc">根据浏览记录推荐商品</div>
              </div>
            </div>
            <el-switch v-model="otherSettings.smartRecommend" />
          </div>
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><DeleteLocation /></el-icon>
              <div class="item-info">
                <div class="item-title">清除缓存</div>
                <div class="item-desc">清除本地缓存数据</div>
              </div>
            </div>
            <el-button @click="clearCache">清除</el-button>
          </div>
        </div>
      </div>

      <!-- 账户操作 -->
      <div class="setting-section danger-section">
        <div class="section-title">
          <el-icon><Warning /></el-icon>
          <span>账户操作</span>
        </div>
        <div class="setting-items">
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><SwitchButton /></el-icon>
              <div class="item-info">
                <div class="item-title">退出登录</div>
                <div class="item-desc">退出当前账户</div>
              </div>
            </div>
            <el-button type="warning" @click="logout">退出</el-button>
          </div>
          <div class="setting-item">
            <div class="item-left">
              <el-icon class="item-icon"><Delete /></el-icon>
              <div class="item-info">
                <div class="item-title">注销账户</div>
                <div class="item-desc">永久删除账户，该操作不可恢复</div>
              </div>
            </div>
            <el-button type="danger" @click="deleteAccount">注销</el-button>
          </div>
        </div>
      </div>

      <!-- 保存按钮 -->
      <div class="save-actions">
        <el-button type="primary" size="large" @click="saveSettings" :loading="saving">
          <el-icon><Check /></el-icon>
          保存设置
        </el-button>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="500px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Lock, Key, Iphone, Message, View, User, ShoppingCart, 
  Bell, ChatDotRound, Present, MessageBox, Setting, Monitor, Cpu, 
  DeleteLocation, Warning, SwitchButton, Delete, Check 
} from '@element-plus/icons-vue'

const router = useRouter()
const saving = ref(false)
const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)

// 隐私设置
const privacySettings = reactive({
  showProfile: true,
  showOrders: false
})

// 通知设置
const notificationSettings = reactive({
  orderNotify: true,
  promotionNotify: true,
  systemNotify: true
})

// 其他设置
const otherSettings = reactive({
  darkMode: false,
  smartRecommend: true
})

// 修改密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

const goBack = () => {
  router.back()
}

const changePassword = () => {
  passwordDialogVisible.value = true
  // 重置表单
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

const submitPassword = () => {
  passwordFormRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success('密码修改成功，请重新登录')
      passwordDialogVisible.value = false
      setTimeout(() => {
        logout()
      }, 1500)
    }
  })
}

const changePhone = () => {
  ElMessage.info('功能开发中：更换手机号')
}

const changeEmail = () => {
  ElMessage.info('功能开发中：更换邮箱')
}

const clearCache = () => {
  ElMessageBox.confirm('确定要清除所有缓存数据吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('缓存已清除')
  }).catch(() => {})
}

const saveSettings = () => {
  saving.value = true
  // 模拟保存延迟
  setTimeout(() => {
    saving.value = false
    ElMessage.success('设置已保存')
  }, 1000)
}

const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('currentUser')
    ElMessage.success('已退出登录')
    setTimeout(() => {
      router.push('/login')
    }, 1000)
  }).catch(() => {})
}

const deleteAccount = () => {
  ElMessageBox.confirm(
    '注销账户后将无法恢复，确定要继续吗？', 
    '警告', 
    {
      confirmButtonText: '确定注销',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    }
  ).then(() => {
    ElMessage.success('账户注销申请已提交')
  }).catch(() => {})
}
</script>

<style scoped>
.account-settings {
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

.settings-container {
  max-width: 900px;
  margin: 0 auto;
}

.setting-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 8px 32px rgba(250, 112, 154, 0.3);
}

.danger-section {
  border: 2px solid rgba(255, 77, 79, 0.3);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(250, 112, 154, 0.2);
}

.section-title .el-icon {
  font-size: 24px;
  color: #fa709a;
}

.setting-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, rgba(250, 112, 154, 0.05) 0%, rgba(255, 107, 107, 0.05) 100%);
  border-radius: 15px;
  transition: all 0.3s;
}

.setting-item:hover {
  background: linear-gradient(135deg, rgba(250, 112, 154, 0.1) 0%, rgba(255, 107, 107, 0.1) 100%);
  transform: translateX(5px);
}

.item-left {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.item-icon {
  font-size: 28px;
  color: #fa709a;
  padding: 10px;
  background: rgba(250, 112, 154, 0.1);
  border-radius: 12px;
}

.item-info {
  flex: 1;
}

.item-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.item-desc {
  font-size: 14px;
  color: #999;
}

.setting-item :deep(.el-button) {
  border-radius: 20px;
  padding: 10px 25px;
  font-weight: bold;
}

.setting-item :deep(.el-button--primary) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  border: none;
}

.setting-item :deep(.el-button--warning) {
  background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
  border: none;
}

.setting-item :deep(.el-button--danger) {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  border: none;
}

.setting-item :deep(.el-switch.is-checked .el-switch__core) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
}

.save-actions {
  text-align: center;
  padding: 30px 0;
}

.save-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  border: none;
  padding: 15px 60px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s;
}

.save-actions :deep(.el-button--primary:hover) {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(250, 112, 154, 0.5);
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

:deep(.el-input__inner) {
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
