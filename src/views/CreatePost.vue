<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const postForm = reactive({
  title: '',
  content: '',
  images: [] as string[]
});

const errors = reactive({
  title: '',
  content: '',
  images: '',
  general: ''
});

const isSubmitting = ref(false);
const previewImages = ref<string[]>([]);

const validateForm = () => {
  let isValid = true;
  errors.title = '';
  errors.content = '';
  errors.images = '';
  errors.general = '';
  
  if (!postForm.title.trim()) {
    errors.title = '请输入标题';
    isValid = false;
  }
  
  if (!postForm.content.trim()) {
    errors.content = '请输入正文内容';
    isValid = false;
  }
  
  if (previewImages.value.length > 3) {
    errors.images = '最多只能上传3张图片';
    isValid = false;
  }
  
  return isValid;
};

const handleImageUpload = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (!input.files || input.files.length === 0) return;
  
  if (previewImages.value.length + input.files.length > 3) {
    errors.images = '最多只能上传3张图片';
    return;
  }
  
  errors.images = '';
  
  Array.from(input.files).forEach(file => {
    const reader = new FileReader();
    reader.onload = (e) => {
      if (e.target && typeof e.target.result === 'string') {
        previewImages.value.push(e.target.result);
      }
    };
    reader.readAsDataURL(file);
  });
  
  // Reset input value to allow selecting the same file again
  input.value = '';
};

const removeImage = (index: number) => {
  previewImages.value.splice(index, 1);
  errors.images = '';
};

const submitPost = async () => {
  if (!validateForm() || isSubmitting.value) return;
  
  isSubmitting.value = true;
  
  // In a real app, you would send the post data to an API
  // For now, we'll simulate a successful submission
  setTimeout(() => {
    // Simulate successful post creation
    router.push('/community');
    isSubmitting.value = false;
  }, 1500);
};

const goBack = () => {
  router.back();
};
</script>

<template>
  <div class="create-post-container">
    <!-- Header -->
    <header class="create-post-header">
      <div class="back-button" @click="goBack">
        <span>← 取消</span>
      </div>
      <h1>发布帖子</h1>
      <button 
        class="publish-button" 
        @click="submitPost" 
        :disabled="isSubmitting"
      >
        {{ isSubmitting ? '发布中...' : '发布' }}
      </button>
    </header>
    
    <!-- Post Form -->
    <div class="post-form-container">
      <div class="form-error" v-if="errors.general">{{ errors.general }}</div>
      
      <div class="form-group">
        <label for="post-title">标题</label>
        <input 
          id="post-title" 
          type="text" 
          v-model="postForm.title" 
          placeholder="请输入标题"
          maxlength="50"
        />
        <div class="field-error" v-if="errors.title">{{ errors.title }}</div>
        <div class="character-count">{{ postForm.title.length }}/50</div>
      </div>
      
      <div class="form-group">
        <label for="post-content">正文</label>
        <textarea 
          id="post-content" 
          v-model="postForm.content" 
          placeholder="分享你的旅行经历..."
          rows="10"
        ></textarea>
        <div class="field-error" v-if="errors.content">{{ errors.content }}</div>
      </div>
      
      <div class="form-group">
        <label>图片 (最多3张)</label>
        <div class="image-upload-container">
          <div 
            v-for="(image, index) in previewImages" 
            :key="index" 
            class="image-preview"
          >
            <img :src="image" alt="预览图片" />
            <button class="remove-image" @click="removeImage(index)">×</button>
          </div>
          
          <div class="image-upload-button" v-if="previewImages.length < 3">
            <input 
              type="file" 
              accept="image/*" 
              @change="handleImageUpload" 
              multiple
            />
            <div class="upload-icon">+</div>
            <div class="upload-text">上传图片</div>
          </div>
        </div>
        <div class="field-error" v-if="errors.images">{{ errors.images }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.create-post-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f5f5;
}

.create-post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.create-post-header h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #2575fc;
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
  padding: 0.5rem 1rem;
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

.post-form-container {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 1.5rem;
  background-color: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: relative;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input[type="text"] {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  resize: vertical;
  font-family: inherit;
}

.character-count {
  position: absolute;
  right: 1.5rem;
  top: 1.5rem;
  color: #666;
  font-size: 0.9rem;
}

.image-upload-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.image-preview {
  width: 150px;
  height: 150px;
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
  width: 150px;
  height: 150px;
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