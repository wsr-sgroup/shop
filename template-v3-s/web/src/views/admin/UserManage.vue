<template>
  <div class="user-manage">
    <!-- 搜索表单 -->
    <el-form :model="searchForm" label-width="80px" inline>
      <el-form-item label="用户名">
        <el-input v-model="searchForm.username" placeholder="请输入用户名" aria-label="用户名"></el-input>
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="searchForm.phone" placeholder="请输入电话" aria-label="电话"></el-input>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.isActive" placeholder="请选择状态" clearable aria-label="状态">
          <el-option label="启用" :value="1"></el-option>
          <el-option label="禁用" :value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getPageList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div style="margin-bottom: 15px;">
      <el-button type="primary" @click="handleAdd">新增用户</el-button>
    </div>

    <!-- 用户表格 -->
    <el-table :data="listData" border stripe>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column prop="phone" label="电话"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.isActive === 1 ? 'success' : 'danger'">
            {{ scope.row.isActive === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pageInfo.pageNum"
      v-model:page-size="pageInfo.pageSize"
      :page-sizes="[10, 20, 50]"
      :total="pageInfo.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; justify-content: flex-end;"
      aria-label="分页导航"
    />

    <!-- 用户编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="editForm" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入电话"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="passwordHash" v-if="!isEdit">
          <el-input v-model="editForm.passwordHash" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-switch 
            v-model="editForm.isActive" 
            :active-value="1" 
            :inactive-value="0"
            active-text="启用" 
            inactive-text="禁用">
          </el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, toRaw } from 'vue'
import request from '@/utils/http.js'
import { ElMessage, ElMessageBox } from 'element-plus'

    //搜索条件
    const searchForm = ref({
        username:undefined,
        phone:undefined,
        isActive:undefined
    })

    const pageInfo = ref({
        //当前页
        pageNum:1,
        //每页条数
        pageSize:10,
        //总条数
        total:0
    })

    function getPageList(){
        //向后端发起请求，携带参数：分页参数+搜索参数
        request.get('/user/page',{
            params:Object.assign(toRaw(searchForm.value),toRaw(pageInfo.value))
        }).then(res => {
            //获取列表
            listData.value = res.data.list
            //获取总记录数
            pageInfo.value.total = res.data.total 
        })
    }

    const listData = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

const editForm = ref({
  id: undefined,
  username: '',
  phone: '',
  email: '',
  passwordHash: '',
  isActive: 1
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 重置搜索条件
function resetSearch() {
  searchForm.value = {
    username: undefined,
    phone: undefined,
    isActive: undefined
  }
  getPageList()
}

// 处理分页变化
function handleSizeChange(val) {
  pageInfo.value.pageSize = val
  getPageList()
}

function handleCurrentChange(val) {
  pageInfo.value.pageNum = val
  getPageList()
}

// 处理新增
function handleAdd() {
  dialogTitle.value = '新增用户'
  isEdit.value = false
  editForm.value = {
    id: undefined,
    username: '',
    phone: '',
    email: '',
    passwordHash: '',
    isActive: 1
  }
  dialogVisible.value = true
}

// 处理编辑
function handleEdit(row) {
  dialogTitle.value = '编辑用户'
  isEdit.value = true
  // 深拷贝，避免影响原数据
  editForm.value = JSON.parse(JSON.stringify(row))
  dialogVisible.value = true
}

// 保存用户信息
function saveUser() {
  formRef.value.validate((valid) => {
    if (valid) {
      const method = isEdit.value ? 'put' : 'post'
      const url = isEdit.value ? '/user/update' : '/user/add'
      request[method](url, editForm.value).then(res => {
        ElMessage.success(`${isEdit.value ? '编辑' : '新增'}成功`)
        dialogVisible.value = false
        getPageList()
      }).catch(err => {
        ElMessage.error(err.message || `${isEdit.value ? '编辑' : '新增'}失败`)
      })
    }
  })
}

// 处理删除
function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete(`/user/delBatch`, {
      data: [row.id]
    }).then(res => {
      ElMessage.success('删除成功')
      getPageList()
    }).catch(err => {
      ElMessage.error(err.message || '删除失败')
    })
  }).catch(() => {
    // 用户取消删除
  })
}

// 初始化加载数据
getPageList()
</script>

<style scoped>
/* 解决兼容性问题 */
.user-manage ::v-deep(.el-table) {
  /* 为 user-select 添加 Safari 前缀 */
  -webkit-user-select: text;
  -khtml-user-select: text;
  -moz-user-select: text;
  -ms-user-select: text;
  user-select: text;
}

.user-manage ::v-deep(.el-dialog) {
  /* 为 backdrop-filter 添加 Safari 前缀 */
  -webkit-backdrop-filter: blur(10px);
  backdrop-filter: blur(10px);
  
  /* 添加 appearance 属性支持 */
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
}

.user-manage ::v-deep(.el-pagination) {
  min-height: 20px; /* 避免使用 min-height: auto */
}

/* 修复 mask 和 user-select 属性顺序 */
.custom-mask {
  -webkit-mask: none;
  mask: none;
  -webkit-mask-size: auto;
  mask-size: auto;
}

.custom-user-select {
  -webkit-user-select: text;
  -khtml-user-select: text;
  -moz-user-select: text;
  -ms-user-select: text;
  user-select: text;
}
</style>
```

