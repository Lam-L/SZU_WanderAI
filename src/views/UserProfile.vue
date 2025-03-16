<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

interface Post {
  id: number
  title: string
  createdAt: string
  likes: number
  comments: number
}

const userPosts = ref<Post[]>([])
const isLoading = ref(true)
const isEditing = ref(false)
const showMerchantDialog = ref(false)

const userForm = reactive({
  userName: '',
  email: '',
  bio: '',
  avatar: ''
})

const merchantForm = reactive({
  name: '',
  phone: '',
  address: '',
  businessLicense: ''
})

const errors = reactive({
  userName: '',
  bio: '',
  general: '',
  merchant: {
    name: '',
    phone: '',
    address: '',
    businessLicense: '',
    general: ''
  }
})

const isSubmitting = ref(false)
const isMerchantSubmitting = ref(false)

// 计算属性：判断是否已经是商家
const isMerchant = computed(() => {
  return authStore.roles.includes('merchant')
})

// 计算属性：判断是否正在等待商家审核
const isPendingMerchant = computed(() => {
  return authStore.user?.merchantStatus === 'pending'
})

onMounted(async () => {
  isLoading.value = true
  try {
    // Get user profile
    const userProfile = authStore.user
    if (userProfile) {
      userForm.userName = userProfile.userName
      userForm.email = userProfile.email
      userForm.bio = userProfile.bio || ''
      userForm.avatar = userProfile.avatar || 'https://i.pravatar.cc/150?img=3'
    }

    // Get user posts
    if (authStore.token === 'fake-jwt-token') {
      // Mock data for fake account
      userPosts.value = [
        {
          id: 1,
          title: '日本东京三日游记',
          createdAt: '2025-05-15T10:30:00Z',
          likes: 24,
          comments: 5
        },
        {
          id: 2,
          title: '巴黎浪漫之旅',
          createdAt: '2025-05-10T14:20:00Z',
          likes: 36,
          comments: 8
        }
      ]
    } else {
      // Real API call for user posts
      const response = await axios.get('/api/posts/user')
      userPosts.value = response.data
    }
  } catch (error) {
    console.error('Failed to load user data:', error)
    ElMessage.error('加载用户数据失败')
  } finally {
    isLoading.value = false
  }
})

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const toggleEdit = () => {
  isEditing.value = !isEditing.value
  if (!isEditing.value) {
    // Reset form when canceling edit
    const currentUser = authStore.user
    if (currentUser) {
      userForm.userName = currentUser.userName
      userForm.bio = currentUser.bio || ''
    }
  }
}

const validateForm = () => {
  let isValid = true
  errors.userName = ''
  errors.bio = ''
  errors.general = ''
  
  if (!userForm.userName.trim()) {
    errors.userName = '请输入昵称'
    isValid = false
  }
  
  if (userForm.bio && userForm.bio.length > 500) {
    errors.bio = '简介不能超过500字'
    isValid = false
  }
  
  return isValid
}

const validateMerchantForm = () => {
  let isValid = true
  errors.merchant.name = ''
  errors.merchant.phone = ''
  errors.merchant.address = ''
  errors.merchant.businessLicense = ''
  errors.merchant.general = ''

  if (!merchantForm.name.trim()) {
    errors.merchant.name = '请输入商家名称'
    isValid = false
  }

  if (!merchantForm.phone.trim()) {
    errors.merchant.phone = '请输入联系电话'
    isValid = false
  } else if (!/^1[3-9]\d{9}$/.test(merchantForm.phone)) {
    errors.merchant.phone = '请输入有效的手机号'
    isValid = false
  }

  if (!merchantForm.address.trim()) {
    errors.merchant.address = '请输入商家地址'
    isValid = false
  }

  if (!merchantForm.businessLicense.trim()) {
    errors.merchant.businessLicense = '请输入营业执照号'
    isValid = false
  }

  return isValid
}

const saveProfile = async () => {
  if (!validateForm() || isSubmitting.value) return
  
  isSubmitting.value = true
  try {
    const result = await authStore.updateProfile({
      userName: userForm.userName.trim(),
      bio: userForm.bio.trim()
    })

    if (result.success) {
      ElMessage.success('个人信息更新成功')
      isEditing.value = false
    } else {
      errors.general = result.error
    }
  } catch (error) {
    console.error('Failed to update profile:', error)
    errors.general = '更新失败，请稍后再试'
  } finally {
    isSubmitting.value = false
  }
}

const submitMerchantApplication = async () => {
  if (!validateMerchantForm() || isMerchantSubmitting.value) return

  isMerchantSubmitting.value = true
  try {
    const response = await axios.post('/api/auth/apply-merchant', {
      name: merchantForm.name,
      phone: merchantForm.phone,
      address: merchantForm.address,
      businessLicense: merchantForm.businessLicense
    })

    if (response.data.success) {
      ElMessage.success('商家申请已提交，请等待审核')
      showMerchantDialog.value = false
      // 更新用户信息
      await authStore.getUserProfile()
    } else {
      errors.merchant.general = response.data.message || '申请提交失败，请稍后再试'
    }
  } catch (error: any) {
    console.error('Failed to submit merchant application:', error)
    errors.merchant.general = error.response?.data?.message || '申请提交失败，请稍后再试'
  } finally {
    isMerchantSubmitting.value = false
  }
}

const goToPostDetail = (postId: number) => {
  router.push(`/post/${postId}`)
}

const logout = () => {
  authStore.logout()
}

const goToHome = () => {
  router.push('/home')
}
</script>

<template>
  <div class="profile-container">
    <!-- Header -->
    <header class="profile-header">
      <div class="back-button" @click="goToHome">
        <span>← 返回首页</span>
      </div>
      <h1>个人中心</h1>
      <button class="logout-button" @click="logout">退出登录</button>
    </header>
    
    <!-- Profile Content -->
    <div class="profile-content">
      <div v-if="isLoading" class="loading">
        <p>加载中...</p>
      </div>
      
      <div v-else class="profile-sections">
        <!-- User Info Section -->
        <div class="profile-section user-info-section">
          <div class="section-header">
            <h2>个人信息</h2>
            <div class="header-actions">
              <!-- 申请成为商家按钮 -->
              <button
                v-if="!isMerchant && !isPendingMerchant"
                class="merchant-button"
                @click="showMerchantDialog = true"
              >
                申请成为商家
              </button>
              <div
                v-if="isPendingMerchant"
                class="pending-status"
              >
                商家申请审核中
              </div>
              <button 
                v-if="!isEditing" 
                class="edit-button" 
                @click="toggleEdit"
              >
                编辑
              </button>
            </div>
          </div>
          
          <div v-if="!isEditing" class="user-info-display">
            <div class="avatar-container">
              <img :src="userForm.avatar" alt="用户头像" class="user-avatar" />
            </div>
            
            <div class="user-details">
              <div class="detail-item">
                <span class="detail-label">昵称:</span>
                <span class="detail-value">{{ userForm.userName }}</span>
              </div>
              
              <div class="detail-item">
                <span class="detail-label">邮箱:</span>
                <span class="detail-value">{{ userForm.email }}</span>
              </div>
              
              <div class="detail-item">
                <span class="detail-label">简介:</span>
                <span class="detail-value">{{ userForm.bio || '暂无简介' }}</span>
              </div>
            </div>
          </div>
          
          <div v-else class="user-info-edit">
            <div class="form-error" v-if="errors.general">{{ errors.general }}</div>
            
            <div class="form-group">
              <label for="user-name">昵称</label>
              <input 
                id="user-name" 
                type="text" 
                v-model="userForm.userName" 
                placeholder="请输入昵称"
              />
              <div class="field-error" v-if="errors.userName">{{ errors.userName }}</div>
            </div>
            
            <div class="form-group">
              <label for="user-email">邮箱</label>
              <input 
                id="user-email" 
                type="email" 
                v-model="userForm.email" 
                disabled
                class="disabled-input"
              />
              <div class="field-hint">邮箱不可修改</div>
            </div>
            
            <div class="form-group">
              <label for="user-bio">简介</label>
              <textarea 
                id="user-bio" 
                v-model="userForm.bio" 
                placeholder="请输入个人简介"
                rows="3"
              ></textarea>
              <div class="field-error" v-if="errors.bio">{{ errors.bio }}</div>
            </div>
            
            <div class="form-actions">
              <button 
                class="cancel-button" 
                @click="toggleEdit"
              >
                取消
              </button>
              <button 
                class="save-button" 
                @click="saveProfile" 
                :disabled="isSubmitting"
              >
                {{ isSubmitting ? '保存中...' : '保存' }}
              </button>
            </div>
          </div>
        </div>
        
        <!-- User Posts Section -->
        <div class="profile-section user-posts-section">
          <div class="section-header">
            <h2>我的帖子</h2>
          </div>
          
          <div v-if="userPosts.length === 0" class="no-posts">
            <p>你还没有发布过帖子</p>
            <button class="create-post-button" @click="router.push('/create-post')">
              发布第一篇帖子
            </button>
          </div>
          
          <div v-else class="user-posts-list">
            <div 
              v-for="post in userPosts" 
              :key="post.id" 
              class="post-item"
              @click="goToPostDetail(post.id)"
            >
              <div class="post-title">{{ post.title }}</div>
              <div class="post-meta">
                <span class="post-date">{{ formatDate(post.createdAt) }}</span>
                <div class="post-stats">
                  <span class="post-likes">❤️ {{ post.likes }}</span>
                  <span class="post-comments">💬 {{ post.comments }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 申请成为商家对话框 -->
    <el-dialog
      v-model="showMerchantDialog"
      title="申请成为商家"
      width="500px"
    >
      <div class="merchant-form">
        <div class="form-error" v-if="errors.merchant.general">
          {{ errors.merchant.general }}
        </div>

        <div class="form-group">
          <label for="merchant-name">商家名称</label>
          <input
            id="merchant-name"
            type="text"
            v-model="merchantForm.name"
            placeholder="请输入商家名称"
          />
          <div class="field-error" v-if="errors.merchant.name">
            {{ errors.merchant.name }}
          </div>
        </div>

        <div class="form-group">
          <label for="merchant-phone">联系电话</label>
          <input
            id="merchant-phone"
            type="tel"
            v-model="merchantForm.phone"
            placeholder="请输入联系电话"
          />
          <div class="field-error" v-if="errors.merchant.phone">
            {{ errors.merchant.phone }}
          </div>
        </div>

        <div class="form-group">
          <label for="merchant-address">商家地址</label>
          <input
            id="merchant-address"
            type="text"
            v-model="merchantForm.address"
            placeholder="请输入商家地址"
          />
          <div class="field-error" v-if="errors.merchant.address">
            {{ errors.merchant.address }}
          </div>
        </div>

        <div class="form-group">
          <label for="merchant-license">营业执照号</label>
          <input
            id="merchant-license"
            type="text"
            v-model="merchantForm.businessLicense"
            placeholder="请输入营业执照号"
          />
          <div class="field-error" v-if="errors.merchant.businessLicense">
            {{ errors.merchant.businessLicense }}
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showMerchantDialog = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitMerchantApplication"
            :loading="isMerchantSubmitting"
          >
            {{ isMerchantSubmitting ? '提交中...' : '提交申请' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f5f5;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.profile-header h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #2575fc;
}

.back-button, .logout-button {
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.back-button:hover {
  background-color: #f0f0f0;
}

.logout-button {
  color: #e74c3c;
  border: none;
  background: none;
  font-size: 1rem;
}

.logout-button:hover {
  background-color: #fff0f0;
}

.profile-content {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.profile-sections {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.profile-section {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 1.5rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.merchant-button {
  padding: 0.5rem 1rem;
  background-color: #67c23a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.merchant-button:hover {
  background-color: #5daf34;
}

.pending-status {
  color: #e6a23c;
  font-size: 0.9rem;
}

.edit-button {
  padding: 0.5rem 1rem;
  background-color: #f0f0f0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.edit-button:hover {
  background-color: #e0e0e0;
}

.user-info-display {
  display: flex;
  gap: 2rem;
}

.avatar-container {
  flex-shrink: 0;
}

.user-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details {
  flex: 1;
}

.detail-item {
  margin-bottom: 1rem;
}

.detail-label {
  font-weight: 500;
  margin-right: 0.5rem;
  color: #666;
}

.detail-value {
  color: #333;
}

.user-info-edit {
  max-width: 600px;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input, .form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
}

.form-group textarea {
  resize: vertical;
}

.disabled-input {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.field-hint {
  color: #666;
  font-size: 0.85rem;
  margin-top: 0.25rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.cancel-button, .save-button {
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.cancel-button {
  background-color: #f0f0f0;
  border: none;
}

.save-button {
  background-color: #2575fc;
  color: white;
  border: none;
}

.save-button:disabled {
  background-color: #a0c0f0;
  cursor: not-allowed;
}

.no-posts {
  text-align: center;
  padding: 2rem 0;
}

.create-post-button {
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.user-posts-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.post-item {
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.post-item:hover {
  background-color: #f9f9f9;
}

.post-title {
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 0.9rem;
}

.post-stats {
  display: flex;
  gap: 1rem;
}

.form-error {
  background-color: #fff0f0;
  color: #e74c3c;
  padding: 0.75rem;
  border-radius: 4px;
  margin-bottom: 1.5rem;
  text-align: center;
}

.field-error {
  color: #e74c3c;
  font-size: 0.85rem;
  margin-top: 0.25rem;
}

.merchant-form {
  padding: 1rem 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding-top: 1rem;
}
</style>
