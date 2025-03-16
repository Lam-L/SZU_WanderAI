<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useMerchantStore } from '../../stores/merchant'

const router = useRouter()
const merchantStore = useMerchantStore()

const loginForm = reactive({
  email: '',
  password: ''
})

const errors = reactive({
  email: '',
  password: '',
  general: ''
})

const isLoading = ref(false)

const validateForm = () => {
  let isValid = true
  errors.email = ''
  errors.password = ''
  errors.general = ''
  
  if (!loginForm.email) {
    errors.email = '请输入邮箱'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginForm.email)) {
    errors.email = '请输入有效的邮箱地址'
    isValid = false
  }
  
  if (!loginForm.password) {
    errors.password = '请输入密码'
    isValid = false
  }
  
  return isValid
}

const handleLogin = async () => {
  if (!validateForm()) return
  
  isLoading.value = true
  try {
    const result = await merchantStore.login(loginForm.email, loginForm.password)
    
    if (result.success) {
      ElMessage.success('登录成功！')
      router.push('/merchant')
    } else {
      errors.general = result.error || '登录失败，请稍后再试'
    }
  } catch (error) {
    console.error('Login failed:', error)
    errors.general = '登录失败，请稍后再试'
  } finally {
    isLoading.value = false
  }
}

const goToRegister = () => {
  router.push('/merchant/register')
}
</script>

<template>
  <div class="merchant-login">
    <div class="login-container">
      <div class="login-header">
        <h1>WanderAI</h1>
        <p>商家登录</p>
      </div>
      
      <div class="login-form">
        <div class="form-error" v-if="errors.general">{{ errors.general }}</div>
        
        <div class="form-group">
          <label for="email">邮箱</label>
          <input 
            id="email" 
            type="email" 
            v-model="loginForm.email" 
            placeholder="请输入邮箱"
          />
          <div class="field-error" v-if="errors.email">{{ errors.email }}</div>
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            id="password" 
            type="password" 
            v-model="loginForm.password" 
            placeholder="请输入密码"
          />
          <div class="field-error" v-if="errors.password">{{ errors.password }}</div>
        </div>
        
        <button 
          class="login-button" 
          @click="handleLogin" 
          :disabled="isLoading"
        >
          {{ isLoading ? '登录中...' : '登录' }}
        </button>
        
        <div class="test-account">
          <p>测试账号: merchant@qq.com / wanderai@qq.com</p>
        </div>
        
        <div class="register-link">
          <p>还没有商家账号？<a @click="goToRegister">立即入驻</a></p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.merchant-login {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
}

.login-container {
  width: 100%;
  max-width: 400px;
  padding: 2rem;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-header h1 {
  font-size: 2rem;
  color: #2575fc;
  margin-bottom: 0.5rem;
}

.login-header p {
  color: #666;
  margin: 0;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.login-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  margin-bottom: 1rem;
}

.login-button:disabled {
  background-color: #a0c0f0;
  cursor: not-allowed;
}

.test-account {
  text-align: center;
  padding: 0.5rem;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px dashed #ddd;
  margin-bottom: 1rem;
}

.test-account p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.register-link {
  text-align: center;
}

.register-link a {
  color: #2575fc;
  cursor: pointer;
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