<template>
  <div class="main-context">
    <el-card class="box-card">
      <el-space direction="vertical" style="width: 100%" size="large">
        <el-space>
          <img src="../assets/logo.png" width="100%" style="width: 55px">
          <el-space direction="vertical" style="width: 100%" size="small">
            <h2 style="font-style: oblique">管理系统</h2>
            <span style="font-style: oblique;font-size: 15px">javadh.com</span>
          </el-space>
        </el-space>
        <el-form :model="formData" label-width="80px" :rules="rules" ref="formRef" style="width: 100%">
          <el-form-item label="类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择用户类型">
              <el-option label="管理员" value="ADMIN"></el-option>
              <el-option label="用户" value="USER"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="手机号" prop="tel"
                        :rules="[{required:true,message:'请输入手机号',trigger:[ 'blur','change']}]">
            <el-input
                style="width: 180px"
                placeholder="请输入手机号"
                v-model.trim="formData.tel"
                clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item label="验证码" prop="code"
                        :rules="[{required:true,message:'请输入验证码',trigger:[ 'blur','change']}]">
            <el-input
                style="width: 180px"
                placeholder="请输入验证码"
                v-model.trim="formData.code"
                clearable
            >
              <template #append>
                <el-button :disabled="countdown > 0" @click="getVerificationCode">
                  {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="password"
                        :rules="[{required:true,message:'请输入密码',trigger:[ 'blur','change']}]">
            <el-input
                style="width: 180px"
                placeholder="请输入密码"
                show-password
                v-model.trim="formData.password"
                clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item label="" style="width: 100%">
            <el-space direction="vertical" alignment="left" style="width: 100%">
              <el-button @click="submitForm()" type="success" style="width: 100%">重置密码</el-button>
              <router-link tag="span" :to="{path:'login'}">
                <el-button type="text" class="button" style="float: right">返回登录</el-button>
              </router-link>
            </el-space>

          </el-form-item>
        </el-form>
      </el-space>
    </el-card>
  </div>
</template>
<script setup>
import {ref} from 'vue';
import {ElMessage} from 'element-plus';
import http from "@/utils/http.js";
import router from "@/router/index.js";

const formRef = ref(null);
const formData = ref({
  type: 'USER',
  tel: '',
  code: '',
  password: ''
});

// 验证码倒计时
const countdown = ref(0);
let timer = null;

// 获取验证码
const getVerificationCode = () => {
  if (!formData.value.tel) {
    ElMessage.error('请输入手机号');
    return;
  }
  
  // 发送获取验证码请求
  http.post("/common/sendVerificationCode", {
    tel: formData.value.tel,
    type: formData.value.type
  }).then(res => {
    if (res && res.success) {
      ElMessage.success('验证码发送成功');
      startCountdown();
    } else {
      ElMessage.error('验证码发送失败');
    }
  }).catch(error => {
    console.error('发送验证码出错:', error);
    ElMessage.error('发送验证码出错');
  });
}

// 开始倒计时
const startCountdown = () => {
  countdown.value = 60;
  timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
      timer = null;
    }
  }, 1000);
}

// 组件销毁时清除定时器
import { onUnmounted } from 'vue';
onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});

const submitForm = () => {
  formRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    http.post("/common/retrievePassword", formData.value).then(res => {
      if (res && res.success) {
        ElMessage({
          message: "重置成功，正在跳转",
          type: "success"
        });
        router.push({path: "/login"})
      } else {
        ElMessage.error(res?.message || '重置密码失败');
      }
    }).catch(error => {
      console.error('重置密码出错:', error);
      ElMessage.error('重置密码出错');
    });
  });
}

</script>

<style scoped>
.main-context {
  height: 100vh; /* 使 .app 高度为视口高度 */
  background: url("../assets/login.png") no-repeat center center fixed;
  background-size: cover; /* 使用 cover 保持图片比例 */
  display: flex;
  justify-content: center;
  align-items: center;
  color: white; /* 根据背景图片调整文字颜色 */
}

.box-card {
  width: 350px;
  margin: 0 auto;
  text-align: center;
}
</style>
