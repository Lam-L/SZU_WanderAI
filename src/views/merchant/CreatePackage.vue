<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const packageForm = reactive({
  title: '',
  description: '',
  price: '',
  originalPrice: '',
  location: '',
  duration: '',
  image: ''
})

const errors = reactive({
  title: '',
  description: '',
  price: '',
  originalPrice: '',
  location: '',
  duration: '',
  image: '',
  general: ''
})

const isSubmitting = ref(false)
const previewImage = ref('')

const validateForm = () => {
  let isValid = true
  Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '')
  
  if (!packageForm.title.trim()) {
    errors.title = '请输入标题'
    isValid = false
  }
  
  if (!packageForm.description.trim()) {
    errors.description = '请输入描述'
    isValid = false
  }
  
  if (!packageForm.price) {
    errors.price = '请输入价格'
    isValid = false
  } else if (isNaN(Number(packageForm.price)) || Number(packageForm.price) <= 0) {
    errors.price = '请输入有效的价格'
    isValid = false
  }
  
  if (!packageForm.originalPrice) {
    errors.originalPrice = '请输入原价'
    isValid = false
  } else if (isNaN(Number(packageForm.originalPrice)) || Number(packageForm.originalPrice) <= 0) {
    errors.originalPrice = '请输入有效的原价'
    isValid = false
  }
  
  if (!packageForm.location.trim()) {
    errors.location = '请输入目的地'
    isValid = false
  }
  
  if (!packageForm.duration.trim()) {
    errors.duration = '请输入行程天数'
    isValid = false
  }
  
  if (!packageForm.image) {
    errors.image = '请上传封面图'
    isValid = false
  }
  
  return isValid
}

const handleImageUpload = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (!input.files || input.files.length === 0) return
  
  const file = input.files[0]
  const reader = new FileReader()
  
  reader.onload = (e) => {
    if (e.target && typeof e.target.result === 'string') {
      packageForm.image = e.target.result
      previewImage.value = e.target.result
    }
  }
  
  reader.readAsDataURL(file)
}

const removeImage = () => {
  packageForm.image = ''
  previewImage.value = ''
}

const submitPackage = async () => {
  if (!validateForm() || isSubmitting.value) return
  
  isSubmitting.value = true
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    ElMessage.success('团购发布成功')
    router.push('/merchant/packages')
  } catch (error) {
    console.error('Failed to create package:', error)
    errors.general = '发布失败，请稍后再试'
  } finally {
    isSubmitting.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>

<template>
  <div class="create-package">
    <div class="page-header">
      <div class="back-button" @click="goBack">
        <span>← 返回</span>
      </div>
      <h2>发布团购</h2>
      <button 
        class="publish-button" 
        @click="submitPackage" 
        :disabled="isSubmitting"
      >
        {{ isSubmitting ? '发布中...' : '发布' }}
      </button>
    </div>
    
    <div class="form-container">
      <div class="form-error" v-if="errors.general">{{ errors.general }}</div>
      
      <div class="form-group">
        <label for="title">标题</label>
        <input 
          id="title" 
          type="text" 
          v-model="packageForm.title" 
          placeholder="请输入标题"
        />
        <div class="field-error" v-if="errors.title">{{ errors.title }}</div>
      </div>
      
      <div class="form-group">
        <label for="description">描述</label>
        <textarea 
          id="description" 
          v-model="packageForm.description" 
          placeholder="请输入描述"
          rows="4"
        ></textarea>
        <div class="field-error" v-if="errors.description">{{ errors.description }}</div>
      </div>
      
      <div class="form-row">
        <div class="form-group">
          <label for="price">价格</label>
          <input 
            id="price" 
            type="number" 
            v-model="packageForm.price" 
            placeholder="请输入价格"
          />
          <div class="field-error" v-if="errors.price">{{ errors.price }}</div>
        </div>
        
        <div class="form-group">
          <label for="original-price">原价</label>
          <input 
            id="original-price" 
            type="number" 
            v-model="packageForm.originalPrice" 
            placeholder="请输入原价"
          />
          <div class="field-error" v-if="errors.originalPrice">{{ errors.originalPrice }}</div>
        </div>
      </div>
      
      <div class="form-row">
        <div class="form-group">
          <label for="location">目的地</label>
          <input 
            id="location" 
            type="text" 
            v-model="packageForm.location" 
            placeholder="请输入目的地"
          />
          <div class="field-error" v-if="errors.location">{{ errors.location }}</div>
        </div>
        
        <div class="form-group">
          <label for="duration">行程天数</label>
          <input 
            id="duration" 
            type="text" 
            v-model="packageForm.duration" 
            placeholder="例如：6天5晚"
          />
          <div class="field-error" v-if="errors.duration">{{ errors.duration }}</div>
        </div>
      </div>
      
      <div class="form-group">
        <label>封面图</label>
        <div class="image-upload-container">
          <div 
            v-if="previewImage" 
            class="image-preview"
          >
            <img :src="previewImage" alt="预览图片" />
            <button class="remove-image" @click="removeImage">×</button>
          </div>
          
          <div class="image-upload-button" v-if="!previewImage">
            <input 
              type="file" 
              accept="image/*" 
              @change="handleImageUpload"
            />
            <div class="upload-icon">+</div>
            <div class="upload-text">上传图片</div>
          </div>
        </div>
        <div class="field-error" v-if="errors.image">{{ errors.image }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.create-package {
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

.back-button {
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.back-button:hover {
  background-color: #f0f0f0;
}

.publish-button {
  padding: 0.75rem 1.5rem;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.publish-button:disabled {
  background-color: #a0c0f0;
  cursor: not-allowed;
}

.form-container {
  background-color: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
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

.image-upload-container {
  display: flex;
  gap: 1rem;
}

.image-preview {
  width: 200px;
  height: 200px;
  position: relative;
  border-radius: 4px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  width: 24px;
  height: 24px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  font-size: 1rem;
}

.image-upload-button {
  width: 200px;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.image-upload-button input[type="file"] {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.upload-icon {
  font-size: 2rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.upload-text {
  color: #666;
  font-size: 0.9rem;
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
</style>