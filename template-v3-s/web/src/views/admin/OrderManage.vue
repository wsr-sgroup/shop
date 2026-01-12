<template>
  <div class="order-manage">
    <!-- 搜索表单 -->
    <el-form :model="searchForm" label-width="80px" inline>
      <el-form-item label="订单号">
        <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" aria-label="订单号"></el-input>
      </el-form-item>
      <el-form-item label="用户ID">
        <el-input v-model="searchForm.userId" placeholder="请输入用户ID" aria-label="用户ID"></el-input>
      </el-form-item>
      <el-form-item label="订单状态">
        <el-select v-model="searchForm.orderStatus" placeholder="请选择订单状态" clearable aria-label="订单状态">
          <el-option label="待发货" value="pending"></el-option>
          <el-option label="已发货" value="shipped"></el-option>
          <el-option label="已收货" value="received"></el-option>
          <el-option label="已关闭" value="closed"></el-option>
          <el-option label="已取消" value="cancelled"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="支付状态">
        <el-select v-model="searchForm.paymentStatus" placeholder="请选择支付状态" clearable aria-label="支付状态">
          <el-option label="待支付" value="0"></el-option>
          <el-option label="已支付" value="1"></el-option>
          <el-option label="已退款" value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getPageList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div style="margin-bottom: 15px;">
      <!-- 管理员界面通常不需要新增订单功能，因为订单是由用户在前端创建的 -->
    </div>

    <!-- 订单表格 -->
    <el-table :data="listData" border stripe>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="orderNo" label="订单号"></el-table-column>
      <el-table-column prop="userId" label="用户ID"></el-table-column>
      <el-table-column prop="totalAmount" label="总金额"></el-table-column>
      <el-table-column prop="finalAmount" label="实付金额"></el-table-column>
      <el-table-column prop="paymentStatus" label="支付状态">
        <template #default="scope">
          <span>{{ getPaymentStatusText(scope.row.paymentStatus) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="orderStatus" label="订单状态"></el-table-column>
      <el-table-column prop="createdAt" label="创建时间"></el-table-column>
      <el-table-column label="操作" width="320">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="primary" @click="handleView(scope.row)">查看</el-button>
          <el-button size="small" type="success" @click="showUpdateStatusDialog(scope.row)">更新状态</el-button>
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

    <!-- 订单编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="editForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="editForm.orderNo" placeholder="订单号" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="editForm.userId" placeholder="请输入用户ID"></el-input>
        </el-form-item>
        <el-form-item label="总金额" prop="totalAmount">
          <el-input-number v-model="editForm.totalAmount" :min="0" :precision="2" controls-position="right"></el-input-number>
        </el-form-item>
        <el-form-item label="实付金额" prop="finalAmount">
          <el-input-number v-model="editForm.finalAmount" :min="0" :precision="2" controls-position="right"></el-input-number>
        </el-form-item>
        <el-form-item label="支付状态" prop="paymentStatus">
          <el-select v-model="editForm.paymentStatus" placeholder="请选择支付状态">
            <el-option label="待支付" :value="0"></el-option>
            <el-option label="已支付" :value="1"></el-option>
            <el-option label="已退款" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态" prop="orderStatus">
          <el-select v-model="editForm.orderStatus" placeholder="请选择订单状态">
            <el-option label="待发货" value="pending"></el-option>
            <el-option label="已发货" value="shipped"></el-option>
            <el-option label="已收货" value="received"></el-option>
            <el-option label="已关闭" value="closed"></el-option>
            <el-option label="已取消" value="cancelled"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="adminNote">
          <el-input v-model="editForm.adminNote" type="textarea" placeholder="请输入管理员备注"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveOrder">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="800px">
      <div v-if="orderDetail">
        <h3>基本信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单ID">{{ orderDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ orderDetail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ orderDetail.userId }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ orderDetail.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="优惠金额">¥{{ orderDetail.discountAmount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="运费">¥{{ orderDetail.shippingFee || 0 }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">¥{{ orderDetail.finalAmount }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ orderDetail.paymentMethod || '未指定' }}</el-descriptions-item>
          <el-descriptions-item label="支付状态">{{ getPaymentStatusText(orderDetail.paymentStatus) }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">{{ orderDetail.orderStatus }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ orderDetail.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ orderDetail.paymentTime || '未支付' }}</el-descriptions-item>
        </el-descriptions>

        <h3 style="margin-top: 20px;">订单项</h3>
        <el-table :data="orderDetail.orderItems" border>
          <el-table-column prop="productName" label="商品名称"></el-table-column>
          <el-table-column prop="price" label="单价"></el-table-column>
          <el-table-column prop="quantity" label="数量"></el-table-column>
          <el-table-column prop="subtotal" label="小计"></el-table-column>
        </el-table>

        <h3 style="margin-top: 20px;">备注信息</h3>
        <p><strong>买家备注：</strong>{{ orderDetail.buyerNote || '无' }}</p>
        <p><strong>管理员备注：</strong>{{ orderDetail.adminNote || '无' }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="detailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 订单状态更新对话框 -->
    <el-dialog v-model="statusDialogVisible" title="更新订单状态" width="500px">
      <el-form :model="statusForm" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="statusForm.orderNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="当前状态">
          <el-input v-model="statusForm.currentStatus" disabled></el-input>
        </el-form-item>
        <el-form-item label="新状态" prop="newStatus">
          <el-select v-model="statusForm.newStatus" placeholder="请选择新状态">
            <el-option label="待发货" value="pending"></el-option>
            <el-option label="已发货" value="shipped"></el-option>
            <el-option label="已收货" value="received"></el-option>
            <el-option label="已关闭" value="closed"></el-option>
            <el-option label="已取消" value="cancelled"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateOrderStatus">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, toRaw } from 'vue'
import http from '@/utils/http.js'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索条件
const searchForm = ref({
  orderNo: undefined,
  userId: undefined,
  orderStatus: undefined,
  paymentStatus: undefined
})

const pageInfo = ref({
  // 当前页
  pageNum: 1,
  // 每页条数
  pageSize: 10,
  // 总条数
  total: 0
})

// getPageList()   与后端对接后取消注释
function getPageList() {
  // 向后端发起请求，携带参数：分页参数+搜索参数
  http.get('/order/page', {
    params: Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  }).then(res => {
    // 获取列表
    listData.value = res.data.list
    // 获取总记录数
    pageInfo.value.total = res.data.total
  })
}

const listData = ref([
  {
    id: 1,
    orderNo: 'ORD123456789',
    userId: 1,
    totalAmount: 100.00,
    finalAmount: 95.00,
    paymentStatus: 1,
    orderStatus: 'pending',
    createdAt: '2025-01-01 12:00:00'
  }
])

const dialogVisible = ref(false)
const detailVisible = ref(false)
const statusDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

const editForm = ref({
  id: undefined,
  orderNo: '',
  userId: undefined,
  totalAmount: 0,
  finalAmount: 0,
  paymentStatus: 0,
  orderStatus: 'pending',
  adminNote: ''
})

const statusForm = ref({
  orderNo: '',
  currentStatus: '',
  newStatus: ''
})

const orderDetail = ref(null)

const formRules = {

  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' },
    { type: 'number', message: '用户ID必须为数字', trigger: 'blur' }
  ],
  totalAmount: [
    { required: true, message: '请输入总金额', trigger: 'blur' }
  ],
  finalAmount: [
    { required: true, message: '请输入实付金额', trigger: 'blur' }
  ]
}

// 重置搜索条件
function resetSearch() {
  searchForm.value = {
    orderNo: undefined,
    userId: undefined,
    orderStatus: undefined,
    paymentStatus: undefined
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

// 管理员界面通常不需要新增订单功能
// 因为订单是由用户在前端创建的，管理员只需处理现有订单

// 处理编辑
function handleEdit(row) {
  dialogTitle.value = '编辑订单'
  isEdit.value = true
  // 深拷贝，避免影响原数据
  editForm.value = JSON.parse(JSON.stringify(row))
  dialogVisible.value = true
}

// 显示更新状态对话框
function showUpdateStatusDialog(row) {
  statusForm.value = {
    orderNo: row.orderNo,
    currentStatus: row.orderStatus,
    newStatus: row.orderStatus
  }
  statusDialogVisible.value = true
}

// 更新订单状态
function updateOrderStatus() {
  http.post(`/order/updateStatus/${statusForm.value.orderNo}/${statusForm.value.newStatus}`).then(() => {
    ElMessage.success('订单状态更新成功');
    statusDialogVisible.value = false;
    getPageList(); // 刷新列表
  }).catch(() => {
    ElMessage.error('订单状态更新失败');
  });
}

// 处理查看
function handleView(row) {
  // 获取订单详情（包括订单项）
  http.get(`/order/selectById/${row.id}`).then(res => {
    orderDetail.value = res.data
    detailVisible.value = true
  })
}

// 处理删除
function handleDelete(row) {
  ElMessageBox.confirm(
    `确定要删除订单 ${row.orderNo} 吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    http.delete(`/order/delete/${row.id}`).then(() => {
      ElMessage.success('删除成功')
      getPageList()
    }).catch(() => {
      ElMessage.error('删除失败')
    })
  })
}

// 保存订单（仅更新）
function saveOrder() {
  // 管理员界面只处理订单更新，不创建新订单
  if (!isEdit.value) {
    ElMessage.error('管理员界面不支持创建新订单');
    return;
  }

  http.put('/order/update', editForm.value).then(() => {
    ElMessage.success('更新成功');
    dialogVisible.value = false;
    getPageList();
  }).catch(() => {
    ElMessage.error('更新失败');
  });
}

// 获取支付状态文本
function getPaymentStatusText(status) {
  switch (status) {
    case 0: return '待支付'
    case 1: return '已支付'
    case 2: return '已退款'
    default: return '未知'
  }
}

// 初始化加载数据
getPageList()
</script>

<style scoped>
.order-manage {
  padding: 20px;
}
.dialog-footer {
  text-align: right;
}
h3 {
  margin: 15px 0 10px 0;
  font-size: 16px;
  font-weight: bold;
}
</style>