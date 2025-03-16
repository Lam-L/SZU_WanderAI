<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

interface User {
  id: number
  userName: string
  email: string
  createdAt: string
  lastLogin: string
  status: 'active' | 'blocked'
  isBlocked: boolean
  postsCount: number
}

const users = ref<User[]>([])
const loading = ref(true)
const search = ref('')

const tableData = ref<User[]>([])

const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/admin/users')
    users.value = response.data
    tableData.value = [...users.value]
  } catch (error: any) {
    console.error('Failed to fetch users:', error)
    ElMessage.error(error.response?.data?.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUsers()
})

const handleSearch = () => {
  if (search.value) {
    tableData.value = users.value.filter(user => 
      user.userName.toLowerCase().includes(search.value.toLowerCase()) ||
      user.email.toLowerCase().includes(search.value.toLowerCase())
    )
  } else {
    tableData.value = [...users.value]
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

const handleBlock = async (user: User) => {
  try {
    await ElMessageBox.confirm(
      `确定要${user.status === 'active' ? '封禁' : '解封'}用户 "${user.userName}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const action = user.status === 'active' ? 'block' : 'unblock'
    await axios.post(`/api/admin/users/${user.id}/${action}`)
    
    // Update local state
    user.status = user.status === 'active' ? 'blocked' : 'active'
    ElMessage.success(`已${user.status === 'active' ? '解封' : '封禁'}用户`)
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Operation failed:', error)
    ElMessage.error(error.response?.data?.message || '操作失败，请稍后再试')
  }
}

const handleDelete = async (user: User) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.userName}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await axios.delete(`/api/admin/users/${user.id}`)
    
    // Update local state
    const index = tableData.value.findIndex(u => u.id === user.id)
    if (index > -1) {
      tableData.value.splice(index, 1)
      users.value = users.value.filter(u => u.id !== user.id)
    }
    ElMessage.success('用户已删除')
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Delete failed:', error)
    ElMessage.error(error.response?.data?.message || '删除失败，请稍后再试')
  }
}
</script>

<template>
  <div class="users-management">
    <div class="page-header">
      <h2>用户管理</h2>
      
      <div class="search-box">
        <el-input
          v-model="search"
          placeholder="搜索用户..."
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    
    <el-card class="users-table">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="userName" label="用户名" width="150" />
        
        <el-table-column prop="email" label="邮箱" width="200" />
        
        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="最后登录" width="180">
          <template #default="{ row }">
            {{ row.lastLogin ? formatDate(row.lastLogin) : '从未登录' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="postsCount" label="发帖数" width="100" />
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isBlocked ?  'danger' : 'success'">
              {{ row.isBlocked ?  '已封禁' : '正常'}}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button
              :type="!row.isBlocked ? 'warning' : 'success'"
              size="small"
              @click="handleBlock(row)"
            >
              {{ !row.isBlocked  ? '封禁' : '解封' }}
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
.users-management {
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

.users-table {
  background-color: white;
}
</style>