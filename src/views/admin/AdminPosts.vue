<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const router = useRouter()

interface Post {
  id: number
  title: string
  content: string
  author: {
    id: number
    userName: string
  }
  createdAt: string
  likes: number
  comments: number
  hidden: boolean
}

const posts = ref<Post[]>([])
const loading = ref(true)
const search = ref('')

const tableData = ref<Post[]>([])

const fetchPosts = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/admin/posts')
    posts.value = response.data
    tableData.value = [...posts.value]
  } catch (error: any) {
    console.error('Failed to fetch posts:', error)
    ElMessage.error(error.response?.data?.message || '获取帖子列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPosts()
})

const handleSearch = () => {
  if (search.value) {
    tableData.value = posts.value.filter(post => 
      post.title.toLowerCase().includes(search.value.toLowerCase()) ||
      post.author.userName.toLowerCase().includes(search.value.toLowerCase())
    )
  } else {
    tableData.value = [...posts.value]
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

const handleToggleVisibility = async (post: Post) => {
  try {
    const action = post.hidden ? 'show' : 'hide'
    const actionText = post.hidden ? '显示' : '隐藏'
    
    await ElMessageBox.confirm(
      `确定要${actionText}帖子 "${post.title}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await axios.post(`/api/admin/posts/${post.id}/${action}`)
    post.hidden = !post.hidden
    ElMessage.success(`帖子已${actionText}`)
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Operation failed:', error)
    ElMessage.error(error.response?.data?.message || '操作失败，请稍后再试')
  }
}

const handleDelete = async (post: Post) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除帖子 "${post.title}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await axios.delete(`/api/admin/posts/${post.id}`)
    
    // Update local state
    const index = tableData.value.findIndex(p => p.id === post.id)
    if (index > -1) {
      tableData.value.splice(index, 1)
      posts.value = posts.value.filter(p => p.id !== post.id)
    }
    ElMessage.success('帖子已删除')
  } catch (error: any) {
    if (error.message === 'cancel') return
    console.error('Delete failed:', error)
    ElMessage.error(error.response?.data?.message || '删除失败，请稍后再试')
  }
}

const handleView = (post: Post) => {
  router.push(`/post/${post.id}`)
}
</script>

<template>
  <div class="posts-management">
    <div class="page-header">
      <h2>帖子管理</h2>
      
      <div class="search-box">
        <el-input
          v-model="search"
          placeholder="搜索帖子..."
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    
    <el-card class="posts-table">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="title" label="标题" min-width="200" />
        
        <el-table-column label="作者" width="150">
          <template #default="{ row }">
            {{ row.author.userName }}
          </template>
        </el-table-column>
        
        <el-table-column label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="likes" label="点赞数" width="100" />
        
        <el-table-column prop="comments" label="评论数" width="100" />
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.hidden ? 'info' : 'success'">
              {{ row.hidden ? '已隐藏' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleView(row)"
            >
              查看
            </el-button>
            
            <el-button
              :type="row.hidden ? 'success' : 'warning'"
              size="small"
              @click="handleToggleVisibility(row)"
            >
              {{ row.hidden ? '显示' : '隐藏' }}
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
.posts-management {
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

.posts-table {
  background-color: white;
}
</style>