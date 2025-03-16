<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

interface Merchant {
  id: number
  name: string
  email: string
  phone: string
  address: string
  businessLicense: string
  status: 'pending' | 'approved' | 'rejected' | 'blocked'
  createdAt: string
  packageCount: number
  totalSales: number
}

const merchants = ref<Merchant[]>([])
const loading = ref(true)
const search = ref('')
const detailDialogVisible = ref(false)
const selectedMerchant = ref<Merchant | null>(null)

const tableData = ref<Merchant[]>([])

const fetchMerchants = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/admin/merchants')
    merchants.value = response.data
    tableData.value = [...merchants.value]
  } catch (error: any) {
    console.error('Failed to fetch merchants:', error)
    ElMessage.error(error.response?.data?.message || '获取商家列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchMerchants()
})

const handleSearch = () => {
  if (search.value) {
    tableData.value = merchants.value.filter(merchant => 
      merchant.name.toLowerCase().includes(search.value.toLowerCase()) ||
      merchant.email.toLowerCase().includes(search.value.toLowerCase()) ||
      merchant.businessLicense.toLowerCase().includes(search.value.toLowerCase())
    )
  } else {
    tableData.value = [...merchants.value]
  }
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatMoney = (amount: number) => {
  return `¥${amount.toLocaleString()}`
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'approved':
      return 'success'
    case 'pending':
      return 'warning'
    case 'rejected':
      return 'danger'
    case 'blocked':
      return 'info'
    default:
      return ''
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'approved':
      return '已审核'
    case 'pending':
      return '待审核'
    case 'rejected':
      return '已拒绝'
    case 'blocked':
      return '已封禁'
    default:
      return status
  }
}

const handleApprove = async (merchant: Merchant) => {
  try {
    await ElMessageBox.confirm(
      `确定要审核通过商家 "${merchant.name}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await axios.post(`/api/admin/merchants/${merchant.id}/approve`)
    merchant.status = 'approved'
    ElMessage.success('已审核通过商家')
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Operation failed:', error)
    ElMessage.error(error.response?.data?.message || '操作失败，请稍后再试')
  }
}

const handleReject = async (merchant: Merchant) => {
  try {
    await ElMessageBox.confirm(
      `确定要拒绝商家 "${merchant.name}" 的申请吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await axios.post(`/api/admin/merchants/${merchant.id}/reject`)
    merchant.status = 'rejected'
    ElMessage.success('已拒绝商家申请')
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Operation failed:', error)
    ElMessage.error(error.response?.data?.message || '操作失败，请稍后再试')
  }
}

const handleBlock = async (merchant: Merchant) => {
  try {
    const action = merchant.status === 'blocked' ? 'unblock' : 'block'
    const actionText = merchant.status === 'blocked' ? '解封' : '封禁'
    
    await ElMessageBox.confirm(
      `确定要${actionText}商家 "${merchant.name}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await axios.post(`/api/admin/merchants/${merchant.id}/${action}`)
    merchant.status = merchant.status === 'blocked' ? 'approved' : 'blocked'
    ElMessage.success(`已${actionText}商家`)
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Operation failed:', error)
    ElMessage.error(error.response?.data?.message || '操作失败，请稍后再试')
  }
}

const handleDelete = async (merchant: Merchant) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商家 "${merchant.name}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await axios.delete(`/api/admin/merchants/${merchant.id}`)
    
    // Update local state
    const index = tableData.value.findIndex(m => m.id === merchant.id)
    if (index > -1) {
      tableData.value.splice(index, 1)
      merchants.value = merchants.value.filter(m => m.id !== merchant.id)
    }
    ElMessage.success('商家已删除')
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Delete failed:', error)
    ElMessage.error(error.response?.data?.message || '删除失败，请稍后再试')
  }
}

const showDetail = (merchant: Merchant) => {
  selectedMerchant.value = merchant
  detailDialogVisible.value = true
}
</script>

<template>
  <div class="merchants-management">
    <div class="page-header">
      <h2>商家管理</h2>
      
      <div class="search-box">
        <el-input
          v-model="search"
          placeholder="搜索商家..."
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    
    <el-card class="merchants-table">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="name" label="商家名称" min-width="150" />
        
        <el-table-column prop="email" label="邮箱" min-width="180" />
        
        <el-table-column prop="phone" label="电话" width="120" />
        
        <el-table-column prop="businessLicense" label="营业执照号" width="120" />
        
        <el-table-column prop="packageCount" label="团购数" width="100" />
        
        <el-table-column label="总销售额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.totalSales) }}
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="showDetail(row)"
            >
              详情
            </el-button>
            
            <template v-if="row.status === 'pending'">
              <el-button
                type="success"
                size="small"
                @click="handleApprove(row)"
              >
                通过
              </el-button>
              
              <el-button
                type="danger"
                size="small"
                @click="handleReject(row)"
              >
                拒绝
              </el-button>
            </template>
            
            <template v-else>
              <el-button
                :type="row.status === 'blocked' ? 'success' : 'warning'"
                size="small"
                @click="handleBlock(row)"
              >
                {{ row.status === 'blocked' ? '解封' : '封禁' }}
              </el-button>
              
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- Detail Dialog -->
    <el-dialog
      v-model="detailDialogVisible"
      title="商家详情"
      width="600px"
    >
      <div v-if="selectedMerchant" class="merchant-detail">
        <div class="detail-section">
          <h3>基本信息</h3>
          <div class="detail-item">
            <span class="label">商家名称：</span>
            <span class="value">{{ selectedMerchant.name }}</span>
          </div>
          <div class="detail-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ selectedMerchant.email }}</span>
          </div>
          <div class="detail-item">
            <span class="label">联系电话：</span>
            <span class="value">{{ selectedMerchant.phone }}</span>
          </div>
          <div class="detail-item">
            <span class="label">地址：</span>
            <span class="value">{{ selectedMerchant.address }}</span>
          </div>
          <div class="detail-item">
            <span class="label">营业执照号：</span>
            <span class="value">{{ selectedMerchant.businessLicense }}</span>
          </div>
        </div>
        
        <div class="detail-section">
          <h3>经营数据</h3>
          <div class="detail-item">
            <span class="label">团购数量：</span>
            <span class="value">{{ selectedMerchant.packageCount }} 个</span>
          </div>
          <div class="detail-item">
            <span class="label">总销售额：</span>
            <span class="value">{{ formatMoney(selectedMerchant.totalSales) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">注册时间：</span>
            <span class="value">{{ formatDate(selectedMerchant.createdAt) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">状态：</span>
            <el-tag :type="getStatusType(selectedMerchant.status)">
              {{ getStatusText(selectedMerchant.status) }}
            </el-tag>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.merchants-management {
  padding: 1rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.search-box {
  width: 300px;
}

.merchants-table {
  background-color: white;
}

.merchant-detail {
  padding: 1rem;
}

.detail-section {
  margin-bottom: 2rem;
}

.detail-section h3 {
  margin: 0 0 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #eee;
  color: #303133;
}

.detail-item {
  display: flex;
  margin-bottom: 1rem;
}

.detail-item .label {
  width: 100px;
  color: #606266;
}

.detail-item .value {
  flex: 1;
  color: #303133;
}
</style>