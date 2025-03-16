<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

interface TourPackage {
  id: number
  title: string
  description: string
  price: number
  originalPrice: number
  location: string
  duration: string
  merchant: {
    id: number
    name: string
  }
  status: 'pending' | 'active' | 'rejected' | 'expired'
  soldCount: number
  createdAt: string
  features: string[]
  itinerary: {
    day: number
    title: string
    description: string
  }[]
  includes: string[]
  excludes: string[]
  notes: string[]
}

const packages = ref<TourPackage[]>([])
const loading = ref(true)
const search = ref('')
const detailDialogVisible = ref(false)
const selectedPackage = ref<TourPackage | null>(null)

const tableData = ref<TourPackage[]>([])

const fetchPackages = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/admin/packages')
    packages.value = response.data
    tableData.value = [...packages.value]
  } catch (error: any) {
    console.error('Failed to fetch packages:', error)
    ElMessage.error(error.response?.data?.message || '获取团购列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPackages()
})

const handleSearch = () => {
  if (search.value) {
    tableData.value = packages.value.filter(pkg => 
      pkg.title.toLowerCase().includes(search.value.toLowerCase()) ||
      pkg.merchant.name.toLowerCase().includes(search.value.toLowerCase()) ||
      pkg.location.toLowerCase().includes(search.value.toLowerCase())
    )
  } else {
    tableData.value = [...packages.value]
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
    case 'active':
      return 'success'
    case 'pending':
      return 'warning'
    case 'rejected':
      return 'danger'
    case 'expired':
      return 'info'
    default:
      return ''
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'active':
      return '已上架'
    case 'pending':
      return '待审核'
    case 'rejected':
      return '已拒绝'
    case 'expired':
      return '已过期'
    default:
      return status
  }
}

const handleApprove = async (pkg: TourPackage) => {
  try {
    await ElMessageBox.confirm(
      `确定要审核通过团购 "${pkg.title}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await axios.post(`/api/admin/packages/${pkg.id}/approve`)
    pkg.status = 'active'
    ElMessage.success('已审核通过团购')
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Operation failed:', error)
    ElMessage.error(error.response?.data?.message || '操作失败，请稍后再试')
  }
}

const handleReject = async (pkg: TourPackage) => {
  try {
    await ElMessageBox.confirm(
      `确定要拒绝团购 "${pkg.title}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await axios.post(`/api/admin/packages/${pkg.id}/reject`)
    pkg.status = 'rejected'
    ElMessage.success('已拒绝团购')
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Operation failed:', error)
    ElMessage.error(error.response?.data?.message || '操作失败，请稍后再试')
  }
}

const handleDelete = async (pkg: TourPackage) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除团购 "${pkg.title}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await axios.delete(`/api/admin/packages/${pkg.id}`)
    
    // Update local state
    const index = tableData.value.findIndex(p => p.id === pkg.id)
    if (index > -1) {
      tableData.value.splice(index, 1)
      packages.value = packages.value.filter(p => p.id !== pkg.id)
    }
    ElMessage.success('团购已删除')
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Delete failed:', error)
    ElMessage.error(error.response?.data?.message || '删除失败，请稍后再试')
  }
}

const showDetail = (pkg: TourPackage) => {
  selectedPackage.value = pkg
  detailDialogVisible.value = true
}
</script>

<template>
  <div class="packages-management">
    <div class="page-header">
      <h2>团购管理</h2>
      
      <div class="search-box">
        <el-input
          v-model="search"
          placeholder="搜索团购..."
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    
    <el-card class="packages-table">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="title" label="标题" min-width="200" />
        
        <el-table-column prop="merchant.name" label="商家" width="150" />
        
        <el-table-column label="价格" width="200">
          <template #default="{ row }">
            <div class="price-info">
              <span class="current-price">{{ formatMoney(row.price) }}</span>
              <span class="original-price">{{ formatMoney(row.originalPrice) }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="location" label="目的地" width="120" />
        
        <el-table-column prop="duration" label="行程" width="100" />
        
        <el-table-column prop="soldCount" label="已售" width="100" />
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="发布时间" width="180">
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
      title="团购详情"
      width="800px"
    >
      <div v-if="selectedPackage" class="package-detail">
        <div class="detail-section">
          <h3>基本信息</h3>
          <div class="detail-item">
            <span class="label">标题：</span>
            <span class="value">{{ selectedPackage.title }}</span>
          </div>
          <div class="detail-item">
            <span class="label">商家：</span>
            <span class="value">{{ selectedPackage.merchant.name }}</span>
          </div>
          <div class="detail-item">
            <span class="label">目的地：</span>
            <span class="value">{{ selectedPackage.location }}</span>
          </div>
          <div class="detail-item">
            <span class="label">行程：</span>
            <span class="value">{{ selectedPackage.duration }}</span>
          </div>
          <div class="detail-item">
            <span class="label">价格：</span>
            <div class="value">
              <span class="current-price">{{ formatMoney(selectedPackage.price) }}</span>
              <span class="original-price">{{ formatMoney(selectedPackage.originalPrice) }}</span>
            </div>
          </div>
          <div class="detail-item">
            <span class="label">状态：</span>
            <el-tag :type="getStatusType(selectedPackage.status)">
              {{ getStatusText(selectedPackage.status) }}
            </el-tag>
          </div>
        </div>
        
        <div class="detail-section">
          <h3>产品特色</h3>
          <div class="features-list">
            <div v-for="(feature, index) in selectedPackage.features" :key="index" class="feature-item">
              <el-tag size="small">{{ feature }}</el-tag>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h3>行程安排</h3>
          <el-timeline>
            <el-timeline-item
              v-for="day in selectedPackage.itinerary"
              :key="day.day"
              :timestamp="`第${day.day}天`"
            >
              <h4>{{ day.title }}</h4>
              <p>{{ day.description }}</p>
            </el-timeline-item>
          </el-timeline>
        </div>
        
        <div class="detail-section">
          <h3>费用说明</h3>
          <div class="cost-details">
            <div class="cost-section">
              <h4>费用包含</h4>
              <ul>
                <li v-for="(item, index) in selectedPackage.includes" :key="index">
                  {{ item }}
                </li>
              </ul>
            </div>
            <div class="cost-section">
              <h4>费用不含</h4>
              <ul>
                <li v-for="(item, index) in selectedPackage.excludes" :key="index">
                  {{ item }}
                </li>
              </ul>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h3>预订须知</h3>
          <ul class="notes-list">
            <li v-for="(note, index) in selectedPackage.notes" :key="index">
              {{ note }}
            </li>
          </ul>
        </div>
        
        <div class="detail-section">
          <h3>销售数据</h3>
          <div class="detail-item">
            <span class="label">已售数量：</span>
            <span class="value">{{ selectedPackage.soldCount }} 份</span>
          </div>
          <div class="detail-item">
            <span class="label">发布时间：</span>
            <span class="value">{{ formatDate(selectedPackage.createdAt) }}</span>
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
.packages-management {
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

.packages-table {
  background-color: white;
}

.price-info {
  display: flex;
  flex-direction: column;
}

.current-price {
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 0.9rem;
}

.package-detail {
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

.detail-section h4 {
  margin: 0 0 0.5rem;
  color: #606266;
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

.features-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.cost-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

.cost-section ul,
.notes-list {
  margin: 0;
  padding-left: 1.5rem;
  color: #606266;
}

.cost-section li,
.notes-list li {
  margin-bottom: 0.5rem;
}
</style>