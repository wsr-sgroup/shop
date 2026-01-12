<template>
  <div>
    <el-button type="primary" @click="openAdd">新增商品</el-button>
    <el-button type="danger" @click="deleteBatch" :disabled="!multipleSelection.length">批量删除</el-button>
    <el-button type="success" @click="openCategoryAdd">新增类别</el-button>
    <el-button type="danger" @click="deleteCategoryBatch" :disabled="!multipleCategorySelection.length">批量删除类别</el-button>

    <el-table :data="list" style="width:100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="sku" label="SKU" width="160"></el-table-column>
      <el-table-column prop="price" label="价格" width="120"></el-table-column>
      <el-table-column prop="stock" label="库存" width="100"></el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="mini" @click="openEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @current-change="onPageChange"
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next">
    </el-pagination>

    <el-dialog :title="editing.id ? '编辑商品' : '新增商品'" :visible.sync="dialogVisible" width="800px">
      <el-form :model="editing" label-width="100px">
        <el-form-item label="名称"><el-input v-model="editing.name"></el-input></el-form-item>
        <el-form-item label="SKU"><el-input v-model="editing.sku"></el-input></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="editing.price" :min="0"></el-input-number></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="editing.stock" :min="0"></el-input-number></el-form-item>
        <el-form-item label="主图">
          <my-upload v-model="editing.mainImage"></my-upload>
        </el-form-item>
        <el-form-item label="描述">
          <my-editor v-model="editing.description"></my-editor>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </span>
    </el-dialog>

    <el-table :data="categoryList" style="width:100%" @selection-change="handleCategorySelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="slug" label="Slug" width="160"></el-table-column>
      <el-table-column prop="parentId" label="父类别ID" width="120"></el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="mini" @click="openCategoryEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @current-change="onCategoryPageChange"
      :current-page="categoryPageNum"
      :page-size="categoryPageSize"
      :total="categoryTotal"
      layout="prev, pager, next">
    </el-pagination>

    <el-dialog :title="editingCategory.id ? '编辑类别' : '新增类别'" :visible.sync="categoryDialogVisible" width="600px">
      <el-form :model="editingCategory" label-width="100px">
        <el-form-item label="名称"><el-input v-model="editingCategory.name"></el-input></el-form-item>
        <el-form-item label="Slug"><el-input v-model="editingCategory.slug"></el-input></el-form-item>
        <el-form-item label="父类别ID">
          <el-input-number v-model="editingCategory.parentId" :min="0"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import MyUpload from '@/components/MyUpload.vue'
import MyEditor from '@/components/MyEditor.vue'
import http from '@/utils/http'
import { ref } from 'vue'

export default {
  components: { MyUpload, MyEditor },
  data() {
    return {
      list: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      editing: {},
      multipleSelection: [],
      categoryList: [],
      categoryPageNum: 1,
      categoryPageSize: 10,
      categoryTotal: 0,
      categoryDialogVisible: false,
      editingCategory: {},
      multipleCategorySelection: []
    }
  },
  mounted() {
    this.fetchPage()
    this.fetchCategoryPage()
  },
  methods: {
    fetchPage() {
      http.get('/product/page', { params: { pageNum: this.pageNum, pageSize: this.pageSize } })
        .then(res => {
          this.list = res.data.list || []
          this.total = res.data.total || 0
        })
    },
    onPageChange(page) {
      this.pageNum = page
      this.fetchPage()
    },
    openAdd() {
      this.editing = {}
      this.dialogVisible = true
    },
    openEdit(row) {
      http.get(`/product/${row.id}`).then(res => {
        this.editing = res.data || {}
        this.dialogVisible = true
      })
    },
    save() {
      const api = this.editing.id ? '/product/update' : '/product/add'
      const method = this.editing.id ? http.put : http.post
      method(api, this.editing).then(() => {
        this.dialogVisible = false
        this.fetchPage()
      })
    },
    handleSelectionChange(val) { this.multipleSelection = val },
    deleteBatch() {
      const ids = this.multipleSelection.map(i => i.id)
      http.delete('/product/delBatch', { data: ids }).then(() => this.fetchPage())
    },

    fetchCategoryPage() {
      http.get('/category/page', { params: { pageNum: this.categoryPageNum, pageSize: this.categoryPageSize } })
        .then(res => {
          this.categoryList = res.data.list || []
          this.categoryTotal = res.data.total || 0
        })
    },
    onCategoryPageChange(page) {
      this.categoryPageNum = page
      this.fetchCategoryPage()
    },
    openCategoryAdd() {
      this.editingCategory = {}
      this.categoryDialogVisible = true
    },
    openCategoryEdit(row) {
      http.get(`/category/${row.id}`).then(res => {
        this.editingCategory = res.data || {}
        this.categoryDialogVisible = true
      })
    },
    saveCategory() {
      const api = this.editingCategory.id ? '/category/update' : '/category/add'
      const method = this.editingCategory.id ? http.put : http.post
      method(api, this.editingCategory).then(() => {
        this.categoryDialogVisible = false
        this.fetchCategoryPage()
      })
    },
    handleCategorySelectionChange(val) { this.multipleCategorySelection = val },
    deleteCategoryBatch() {
      const ids = this.multipleCategorySelection.map(i => i.id)
      http.delete('/category/delBatch', { data: ids }).then(() => this.fetchCategoryPage())
    }
  }
}
</script>
