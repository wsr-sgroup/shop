<template>
  <div class="category-manage">
    <div class="header">
      <h2>分类管理</h2>
      <el-button type="primary" @click="showAddDialog">添加分类</el-button>
    </div>

    <el-table
      :data="categoryList"
      style="width: 100%"
      row-key="id"
      lazy
      :load="loadChildren"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="name" label="分类名称" width="200" />
      <el-table-column prop="slug" label="标识符" width="200" />
      <el-table-column label="分类图片" width="150">
        <template #default="{ row }">
          <el-image
            v-if="row.image"
            :src="row.image"
            :preview-src-list="[row.image]"
            fit="cover"
            style="width: 80px; height: 80px;"
            :preview-teleported="true"
          />
          <span v-else>暂无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column prop="isVisible" label="是否可见" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isVisible ? 'success' : 'danger'">
            {{ row.isVisible ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteCategory(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分类编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="currentCategory" :rules="categoryRules" ref="categoryFormRef" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="currentCategory.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="标识符" prop="slug">
          <el-input v-model="currentCategory.slug" placeholder="请输入分类标识符（英文）" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="currentCategory.parentId" placeholder="请选择父分类" clearable>
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类图片">
          <my-upload
            v-model="currentCategory.image"
            :show-preview="true"
            :max-size="2"
            accept="image/*"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="currentCategory.description"
            type="textarea"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number
            v-model="currentCategory.sortOrder"
            :min="0"
            :max="999"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="是否可见">
          <el-switch
            v-model="currentCategory.isVisible"
            :active-value="true"
            :inactive-value="false"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCategory">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/http.js'
import MyUpload from '@/components/MyUpload.vue'

// 分类列表
const categoryList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const currentCategory = ref({
  id: null,
  name: '',
  slug: '',
  parentId: null,
  image: '',
  description: '',
  sortOrder: 0,
  isVisible: true
})

// 分类选项（用于父分类选择）
const categoryOptions = ref([])

// 表单验证规则
const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  slug: [
    { required: true, message: '请输入分类标识符', trigger: 'blur' }
  ]
}

// 表格引用
const categoryFormRef = ref(null)

// 获取分类列表
const getCategoryList = async () => {
  try {
    const res = await http.get('/category/page', {
      params: {
        pageNum: 1,
        pageSize: 100
      }
    })
    if (res && res.code === 200) {
      categoryList.value = res.data.list || []
      categoryOptions.value = [
        { id: null, name: '顶级分类' },
        ...(res.data.list || [])
      ]
    } else {
      ElMessage.error(res.msg || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 显示添加对话框
const showAddDialog = () => {
  dialogTitle.value = '添加分类'
  currentCategory.value = {
    id: null,
    name: '',
    slug: '',
    parentId: null,
    image: '',
    description: '',
    sortOrder: 0,
    isVisible: true
  }
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  dialogTitle.value = '编辑分类'
  currentCategory.value = { ...row }
  dialogVisible.value = true
}

// 保存分类
const saveCategory = async () => {
  try {
    // 验证表单
    await categoryFormRef.value.validate()
    
    let res
    if (currentCategory.value.id) {
      // 更新分类
      res = await http.put('/category/update', currentCategory.value)
    } else {
      // 添加分类
      res = await http.post('/category/add', currentCategory.value)
    }
    
    if (res && res.code === 200) {
      ElMessage.success(currentCategory.value.id ? '更新成功' : '添加成功')
      dialogVisible.value = false
      getCategoryList() // 刷新列表
    } else {
      ElMessage.error(res.msg || (currentCategory.value.id ? '更新失败' : '添加失败'))
    }
  } catch (error) {
    console.error('保存分类失败:', error)
    ElMessage.error('保存分类失败')
  }
}

// 删除分类
const deleteCategory = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await http.delete('/category/delBatch', {
      data: [id]
    })
    
    if (res && res.code === 200) {
      ElMessage.success('删除成功')
      getCategoryList() // 刷新列表
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除分类失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 加载子分类（用于表格树形结构）
const loadChildren = (row, treeNode, resolve) => {
  // 这里可以根据需要加载子分类
  // 目前简单返回空数组
  resolve([])
}

onMounted(() => {
  getCategoryList()
})
</script>

<style scoped>
.category-manage {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>