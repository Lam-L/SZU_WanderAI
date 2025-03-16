<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

interface TourPackage {
  id: number
  title: string
  price: number
  originalPrice: number
  location: string
  duration: string
  soldCount: number
  status: 'active' | 'inactive'
  createdAt: string
}

const packages = ref<TourPackage[]>([])
const loading = ref(true)
const search = ref('')

const tableData = ref<TourPackage[]>([])

onMounted(() => {
  // Simulate loading packages data
  setTimeout(() => {
    packages.value = [
      {
        id: 1,
        title: '日本东京+大阪6日游',
        price: 5999,
        originalPrice: 7999,
        location: '东京/大阪',
        duration: '6天5晚',
        soldCount: 328,
        status: 'active',
        createdAt: '2025-05-15T10:30:00Z'
      },
      {
        id: 2,
        title: '泰国清迈文艺小城之旅',
        price: 3999,
        originalPrice: 4999,
        location: '清迈',
        duration: '5天4晚',
        soldCount: 156,
        status: 'active',
        createdAt: '2025-05-10T14:20:00Z'
      },
      {
        id: 3,
        title: '巴厘岛浪漫度假套餐',
        price: 6999,
        originalPrice: 8999,
        location: '巴厘岛',
        duration: '7天6晚',
        soldCount: 89,
        status: 'inactive',
        createdAt: '2025-05-05T09:15:00Z'
      }
    ]
    tableData.value = [...packages.value]
    loading.value = false
  }, 1000)
})

const handleSearch = () => {
  if (search.value) {
    tableData.value = packages.value.filter(pkg => 
      pkg.title.toLowerCase().includes(search.value.toLowerCase()) ||
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

const handleToggleStatus = async (pkg: TourPackage) => {
  try {
    await ElMessageBox.confirm(
      `确定要${pkg.status === 'active' ? '下架' : '上架'}团购 "${pkg.title}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // Simulate API call
    const newStatus = pkg.status === 'active' ? 'inactive' : 'active'
    pkg.status = newStatus
    ElMessage.success(`团购已${newStatus === 'active' ? '上架' : '下架'}`)
  } catch {
    // User canceled
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
    
    // Simulate API call
    const index = tableData.value.findIndex(p => p.id === pkg.id)
    if (index > -1) {
      tableData.value.splice(index, 1)
      packages.value = packages.value.filter(p => p.id !== pkg.id)
    }
    ElMessage.success('团购已删除')
  } catch {
    // User canceled
  }
}

const createPackage = () => {
  router.push('/merchant/packages/create')
}
</script>

<template>
  <div class="packages-management">
    <div class="page-header">
      <h2>团购管理</h2>
      
      <div class="header-actions">
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
        
        <button class="create-button" @click="createPackage">
          发布团购
        </button>
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
        
        <el-table-column label="价格" width="200">
          <template #default="{ row }">
            <div class="price-info">
              <span class="current-price">¥{{ row.price }}</span>
              <span class="original-price">¥{{ row.originalPrice }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="location" label="目的地" width="120" />
        
        <el-table-column prop="duration" label="行程" width="100" />
        
        <el-table-column prop="soldCount" label="已售" width="100" />
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button
              :type="row.status === 'active' ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'active' ? '下架' : '上架' }}
            </el-button>
            
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
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

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.search-box {
  width: 300px;
}

.create-button {
  padding: 0.75rem 1.5rem;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
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
</style>