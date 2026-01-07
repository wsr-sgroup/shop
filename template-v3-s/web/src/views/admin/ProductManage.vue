<template>
  <div>
    <!-- 搜索表单 -->
    <el-form :model="searchForm" label-width="80px" inline>
      <el-form-item label="商品名称">
        <el-input v-model="searchForm.name" placeholder="请输入商品名称" aria-label="商品名称"></el-input>
      </el-form-item>
      <el-form-item label="SKU">
        <el-input v-model="searchForm.sku" placeholder="请输入SKU" aria-label="SKU"></el-input>
      </el-form-item>
      <el-form-item label="系列">
        <el-input v-model="searchForm.series" placeholder="请输入系列" aria-label="系列"></el-input>
      </el-form-item>
      <el-form-item label="上架状态">
        <el-select v-model="searchForm.isOnSale" placeholder="请选择上架状态" clearable aria-label="上架状态">
          <el-option label="上架" :value="1"></el-option>
          <el-option label="下架" :value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="库存状态">
        <el-select v-model="searchForm.stockStatus" placeholder="请选择库存状态" clearable aria-label="库存状态">
          <el-option label="库存充足" value="adequate"></el-option>
          <el-option label="库存不足" value="low"></el-option>
          <el-option label="缺货" value="out"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchPage">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div style="margin-bottom: 15px;">
      <el-button type="primary" @click="openAdd">新增商品</el-button>
      <el-button type="danger" @click="deleteBatch" :disabled="!multipleSelection.length">批量删除</el-button>
      <el-button type="success" @click="batchOnSale" :disabled="!multipleSelection.length">批量上架</el-button>
      <el-button type="warning" @click="batchOffSale" :disabled="!multipleSelection.length">批量下架</el-button>
    </div>

    <el-table :data="list" style="width:100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="sku" label="SKU" width="160"></el-table-column>
      <el-table-column prop="price" label="价格" width="120"></el-table-column>
      <el-table-column prop="originalPrice" label="原价" width="120"></el-table-column>
      <el-table-column prop="stock" label="库存" width="100">
        <template #default="{ row }">
          <span :class="{
            'text-success': row.stock > row.stockLowThreshold,
            'text-warning': row.stock <= row.stockLowThreshold && row.stock > 0,
            'text-danger': row.stock === 0
          }">
            {{ row.stock }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="series" label="系列" width="120"></el-table-column>
      <el-table-column prop="isOnSale" label="上架状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.isOnSale"
            :active-value="1"
            :inactive-value="0"
            active-text="上架"
            inactive-text="下架"
            @change="toggleSaleStatus(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="库存状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStockStatusType(row)">
            {{ getStockStatusText(row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="mini" @click="openEdit(row)">编辑</el-button>
          <el-button size="mini" type="primary" @click="adjustStock(row)">调库存</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="onPageChange"
      :current-page="pageNum"
      :page-sizes="[10, 20, 50]"
      :page-size="pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>

    <!-- 商品编辑对话框 -->
    <el-dialog :title="editing.id ? '编辑商品' : '新增商品'" v-model="dialogVisible" width="800px">
      <el-form :model="editing" label-width="120px">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="名称">
                  <el-input v-model="editing.name" placeholder="请输入商品名称"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="SKU">
                  <el-input v-model="editing.sku" placeholder="请输入SKU" :disabled="editing.id"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="价格">
                  <el-input-number v-model="editing.price" :min="0" :precision="2" controls-position="right" style="width: 100%"></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="原价">
                  <el-input-number v-model="editing.originalPrice" :min="0" :precision="2" controls-position="right" style="width: 100%"></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="成本价">
                  <el-input-number v-model="editing.costPrice" :min="0" :precision="2" controls-position="right" style="width: 100%"></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="库存">
                  <el-input-number v-model="editing.stock" :min="0" controls-position="right" style="width: 100%"></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="库存预警值">
                  <el-input-number v-model="editing.stockLowThreshold" :min="0" controls-position="right" style="width: 100%"></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="上架状态">
                  <el-select v-model="editing.isOnSale" placeholder="请选择上架状态">
                    <el-option label="上架" :value="1"></el-option>
                    <el-option label="下架" :value="0"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="副标题">
              <el-input v-model="editing.subtitle" placeholder="请输入副标题"></el-input>
            </el-form-item>
            <el-form-item label="主图">
              <my-upload v-model="editing.mainImage"></my-upload>
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane label="规格参数" name="spec">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="系列">
                  <el-input v-model="editing.series" placeholder="请输入系列"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="型号">
                  <el-input v-model="editing.model" placeholder="请输入型号"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="CPU">
                  <el-input v-model="editing.cpu" placeholder="请输入CPU信息"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="GPU">
                  <el-input v-model="editing.gpu" placeholder="请输入GPU信息"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="内存">
                  <el-input v-model="editing.ram" placeholder="请输入内存信息"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="存储">
                  <el-input v-model="editing.storage" placeholder="请输入存储信息"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="屏幕尺寸">
                  <el-input-number v-model="editing.screenSize" :min="0" :precision="1" controls-position="right" style="width: 100%"></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="重量">
                  <el-input-number v-model="editing.weight" :min="0" :precision="2" controls-position="right" style="width: 100%"></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="规格参数(JSON)">
              <el-input v-model="editing.specifications" type="textarea" :rows="4" placeholder="请输入规格参数(JSON格式)"></el-input>
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane label="详情描述" name="desc">
            <el-form-item label="描述">
              <my-editor v-model="editing.description"></my-editor>
            </el-form-item>
            <el-form-item label="图库(JSON)">
              <el-input v-model="editing.imageGallery" type="textarea" :rows="4" placeholder="请输入图库(JSON格式)"></el-input>
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 库存调整对话框 -->
    <el-dialog title="调整库存" v-model="stockDialogVisible" width="500px">
      <el-form :model="stockAdjustment" label-width="100px">
        <el-form-item label="商品名称">
          <el-input v-model="stockAdjustment.productName" disabled></el-input>
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input v-model.number="stockAdjustment.currentStock" disabled></el-input>
        </el-form-item>
        <el-form-item label="调整数量">
          <el-input-number v-model="stockAdjustment.adjustment" :min="-stockAdjustment.currentStock" :max="99999" controls-position="right"></el-input-number>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stockAdjustment.note" type="textarea" :rows="3" placeholder="请输入调整原因"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stockDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmStockAdjustment">确认调整</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import MyUpload from '@/components/MyUpload.vue'
import MyEditor from '@/components/MyEditor.vue'
import http from '@/utils/http'

export default {
  components: { MyUpload, MyEditor },
  data() {
    return {
      list: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      stockDialogVisible: false,
      editing: {},
      multipleSelection: [],
      activeTab: 'basic',
      searchForm: {
        name: undefined,
        sku: undefined,
        series: undefined,
        isOnSale: undefined,
        stockStatus: undefined
      },
      stockAdjustment: {
        productId: null,
        productName: '',
        currentStock: 0,
        adjustment: 0,
        note: ''
      }
    }
  },
  mounted() { 
    this.fetchPage() 
  },
  methods: {
    fetchPage() {
      const params = {
        pageNum: this.pageNum, 
        pageSize: this.pageSize,
        name: this.searchForm.name,
        sku: this.searchForm.sku,
        series: this.searchForm.series,
        isOnSale: this.searchForm.isOnSale
      };
      
      // 根据库存状态添加筛选条件
      if (this.searchForm.stockStatus === 'low') {
        params.isLowStock = true;
      } else if (this.searchForm.stockStatus === 'out') {
        params.isOutOfStock = true;
      } else if (this.searchForm.stockStatus === 'adequate') {
        params.isAdequateStock = true;
      }

      // 过滤掉空值参数
      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === '') {
          delete params[key];
        }
      });

      http.get('/product/page', { params })
        .then(res => {
          this.list = res.data.list || []
          this.total = res.data.total || 0
        })
    },
    onPageChange(page) {
      this.pageNum = page
      this.fetchPage()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.pageNum = 1
      this.fetchPage()
    },
    openAdd() {
      this.editing = {
        name: '',
        sku: '',
        price: 0,
        originalPrice: 0,
        costPrice: 0,
        stock: 0,
        stockLowThreshold: 10,
        isOnSale: 1,
        subtitle: '',
        mainImage: '',
        description: '',
        series: '',
        model: '',
        cpu: '',
        gpu: '',
        ram: '',
        storage: '',
        screenSize: 0,
        weight: 0,
        specifications: '',
        imageGallery: ''
      }
      this.activeTab = 'basic'
      this.dialogVisible = true
    },
    openEdit(row) {
      http.get(`/product/${row.id}`).then(res => {
        this.editing = { ...res.data }
        this.activeTab = 'basic'
        this.dialogVisible = true
      })
    },
    save() {
      const api = this.editing.id ? '/product/update' : '/product/add'
      const method = this.editing.id ? http.put : http.post
      method(api, this.editing).then(() => {
        this.dialogVisible = false
        this.fetchPage()
      }).catch(error => {
        console.error('保存失败:', error)
      })
    },
    handleSelectionChange(val) { 
      this.multipleSelection = val 
    },
    deleteBatch() {
      const ids = this.multipleSelection.map(i => i.id)
      http.delete('/product/delBatch', { data: ids }).then(() => {
        this.fetchPage()
      }).catch(error => {
        console.error('批量删除失败:', error)
      })
    },
    resetSearch() {
      this.searchForm = {
        name: undefined,
        sku: undefined,
        series: undefined,
        isOnSale: undefined,
        stockStatus: undefined
      }
      this.pageNum = 1
      this.fetchPage()
    },
    // 切换上架/下架状态
    toggleSaleStatus(row) {
      const newStatus = row.isOnSale === 1 ? '下架' : '上架';
      this.$confirm(`确定要将商品 "${row.name}" ${newStatus}吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        http.put('/product/update', {
          id: row.id,
          isOnSale: row.isOnSale
        }).then(() => {
          this.$message.success(`${newStatus}成功`);
          // 不需要重新加载，因为状态已经更新
        }).catch(error => {
          // 如果失败，恢复原状态
          row.isOnSale = row.isOnSale === 1 ? 0 : 1;
          console.error(`${newStatus}失败:`, error);
        });
      }).catch(() => {
        // 如果用户取消，恢复原状态
        row.isOnSale = row.isOnSale === 1 ? 0 : 1;
      });
    },
    // 批量上架
    batchOnSale() {
      const ids = this.multipleSelection.map(i => i.id);
      this.$confirm(`确定要将选中的 ${ids.length} 个商品上架吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        http.put('/product/batchUpdateSaleStatus', {
          ids: ids,
          isOnSale: 1
        }).then(() => {
          this.$message.success('批量上架成功');
          this.fetchPage();
        }).catch(error => {
          console.error('批量上架失败:', error);
        });
      });
    },
    // 批量下架
    batchOffSale() {
      const ids = this.multipleSelection.map(i => i.id);
      this.$confirm(`确定要将选中的 ${ids.length} 个商品下架吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        http.put('/product/batchUpdateSaleStatus', {
          ids: ids,
          isOnSale: 0
        }).then(() => {
          this.$message.success('批量下架成功');
          this.fetchPage();
        }).catch(error => {
          console.error('批量下架失败:', error);
        });
      });
    },
    // 获取库存状态文本
    getStockStatusText(row) {
      if (row.stock === 0) {
        return '缺货';
      } else if (row.stock <= row.stockLowThreshold) {
        return '库存不足';
      } else {
        return '库存充足';
      }
    },
    // 获取库存状态类型
    getStockStatusType(row) {
      if (row.stock === 0) {
        return 'danger';
      } else if (row.stock <= row.stockLowThreshold) {
        return 'warning';
      } else {
        return 'success';
      }
    },
    // 调整库存
    adjustStock(row) {
      this.stockAdjustment = {
        productId: row.id,
        productName: row.name,
        currentStock: row.stock,
        adjustment: 0,
        note: ''
      };
      this.stockDialogVisible = true;
    },
    // 确认库存调整
    confirmStockAdjustment() {
      if (this.stockAdjustment.adjustment === 0) {
        this.$message.warning('请输入调整数量');
        return;
      }
      
      const newStock = this.stockAdjustment.currentStock + this.stockAdjustment.adjustment;
      if (newStock < 0) {
        this.$message.error('库存不能为负数');
        return;
      }
      
      http.put('/product/update', {
        id: this.stockAdjustment.productId,
        stock: newStock
      }).then(() => {
        this.$message.success('库存调整成功');
        this.stockDialogVisible = false;
        this.fetchPage();
      }).catch(error => {
        console.error('库存调整失败:', error);
        this.$message.error('库存调整失败');
      });
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
.text-success {
  color: #67C23A;
}
.text-warning {
  color: #E6A23C;
}
.text-danger {
  color: #F56C6C;
}
</style>