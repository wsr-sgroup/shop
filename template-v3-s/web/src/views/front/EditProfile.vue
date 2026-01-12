<template>
  <div class="edit-profile">
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" circle></el-button>
      <h2>编辑资料</h2>
      <div></div>
    </div>

    <div class="profile-container">
      <!-- 头像编辑区 -->
      <div class="avatar-section">
        <el-avatar :size="120" :src="formData.avatarUrl || defaultAvatar" />
        <el-button type="primary" class="upload-btn">
          <el-icon><Upload /></el-icon>
          更换头像
        </el-button>
      </div>

      <!-- 表单区 -->
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px" class="profile-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" disabled>
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称">
            <template #prefix>
              <el-icon><Postcard /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号">
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
            <el-radio label="other">保密</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="生日" prop="birthday">
          <el-date-picker 
            v-model="formData.birthday" 
            type="date" 
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="个人简介" prop="bio">
          <el-input 
            v-model="formData.bio" 
            type="textarea" 
            :rows="4"
            placeholder="写点什么介绍一下自己吧..."
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="saveProfile" :loading="loading">
            <el-icon><Check /></el-icon>
            保存修改
          </el-button>
          <el-button @click="resetForm">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Upload, User, Postcard, Iphone, Message, Check, RefreshLeft } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const formData = reactive({
  username: '用户123',
  nickname: '阳光少年',
  phone: '13800138000',
  email: 'user@example.com',
  gender: 'male',
  birthday: new Date('1995-06-15'),
  bio: '热爱生活，享受购物的乐趣~',
  avatarUrl: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

onMounted(() => {
  // 这里可以从localStorage或API获取用户信息
  const userStr = localStorage.getItem('currentUser')
  if (userStr) {
    const user = JSON.parse(userStr)
    Object.assign(formData, {
      username: user.username || formData.username,
      nickname: user.nickname || formData.nickname,
      phone: user.phone || formData.phone,
      email: user.email || formData.email,
      avatarUrl: user.avatarUrl || formData.avatarUrl
    })
  }
})

const goBack = () => {
  router.back()
}

const saveProfile = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      // 模拟保存延迟
      setTimeout(() => {
        loading.value = false
        ElMessage.success('资料保存成功！')
      }, 1000)
    }
  })
}

const resetForm = () => {
  formRef.value.resetFields()
}
</script>

<style scoped>
.edit-profile {
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
  margin-bottom: 30px;
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

.profile-container {
  max-width: 800px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(250, 112, 154, 0.3);
}

.avatar-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: linear-gradient(135deg, rgba(250, 112, 154, 0.1) 0%, rgba(255, 107, 107, 0.1) 100%);
  border-radius: 15px;
}

.upload-btn {
  margin-top: 20px;
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  border: none;
  color: white;
  font-weight: bold;
  padding: 12px 30px;
  transition: all 0.3s;
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(250, 112, 154, 0.4);
}

.profile-form {
  margin-top: 20px;
}

.profile-form :deep(.el-form-item__label) {
  font-weight: bold;
  color: #333;
}

.profile-form :deep(.el-input__inner) {
  border-radius: 10px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s;
}

.profile-form :deep(.el-input__inner:focus) {
  border-color: #fa709a;
  box-shadow: 0 0 0 3px rgba(250, 112, 154, 0.1);
}

.profile-form :deep(.el-textarea__inner) {
  border-radius: 10px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s;
}

.profile-form :deep(.el-textarea__inner:focus) {
  border-color: #fa709a;
  box-shadow: 0 0 0 3px rgba(250, 112, 154, 0.1);
}

.profile-form :deep(.el-button--primary) {
  background: linear-gradient(135deg, #fa709a 0%, #ff6b6b 100%);
  border: none;
  padding: 12px 40px;
  font-weight: bold;
  transition: all 0.3s;
}

.profile-form :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(250, 112, 154, 0.4);
}

.profile-form :deep(.el-radio__input.is-checked .el-radio__inner) {
  background: #fa709a;
  border-color: #fa709a;
}

.profile-form :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #fa709a;
}
</style>
