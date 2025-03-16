<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const registerForm = reactive({
  name: '',
  email: '',
  password: '',
  passwordConfirm: '',
  inviteCode: ''
})

const errors = reactive({
  name: '',
  email: '',
  password: '',
  passwordConfirm: '',
  inviteCode: '',
  general: ''
})

const isLoading = ref(false)

const validateForm = () => {
  let isValid = true
  Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '')
  
  if (!registerForm.name) {
    errors.name = '请输入管理员名称'
    isValid = false
  }
  
  if (!registerForm.email) {
    errors.email = '请输入邮箱'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    errors.email = '请输入有效的邮箱地址'
    isValid = false
  }
  
  if (!registerForm.password) {
    errors.password = '请输入密码'
    isValid = false
  } else if (registerForm.password.length < 6) {
    errors.password = '密码长度至少为6位'
    isValid = false
  }
  
  if (!registerForm.passwordConfirm) {
    errors.passwordConfirm = '请确认密码'
    isValid = false
  } else if (registerForm.password !== registerForm.passwordConfirm) {
    errors.passwordConfirm = '两次输入的密码不一致'
    isValid = false
  }
  
  if (!registerForm.inviteCode) {
    errors.inviteCode = '请输入邀请码'
    isValid = false
  }
  
  return isValid
}

const handleRegister = async () => {
  if (!validateForm()) return
  
  isLoading.value = true
  try {
    // In a real app, you would make an API call here
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('注册成功！请登录')
    router.push('/admin/login')
  } catch (error) {
    console.error('Registration failed:', error)
    errors.general = '注册失败，请稍后再试'
  } finally {
    isLoading.value = false
  }
}

const goToLogin = () => {
  router.push('/admin/login')
}
</script>

<template>
  <div class="admin-register">
    <div class="register-container">
      <div class="register-header">
        <h1>WanderAI</h1>
        <p>管理员注册</p>
      </div>
      
      <div class="register-form">
        <div class="form-error" v-if="errors.general">{{ errors.general }}</div>
        
        <div class="form-group">
          <label for="name">管理员名称</label>
          <input 
            id="name" 
            type="text" 
            v-model="registerForm.name" 
            placeholder="请输入管理员名称"
          />
          <div class="field-error" v-if="errors.name">{{ errors.name }}</div>
        </div>
        
        <div class="form-group">
          <label for="email">邮箱</label>
          <input 
            id="email" 
            type="email" 
            v-model="registerForm.email" 
            placeholder="请输入邮箱"
          />
          <div class="field-error" v-if="errors.email">{{ errors.email }}</div>
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            id="password" 
            type="password" 
            v-model="registerForm.password" 
            placeholder="请输入密码"
          />
          <div class="field-error" v-if="errors.password">{{ errors.password }}</div>
        </div>
        
        <div class="form-group">
          <label for="password-confirm">确认密码</label>
          <input 
            id="password-confirm" 
            type="password" 
            v-model="registerForm.passwordConfirm" 
            placeholder="请再次输入密码"
          />
          <div class="field-error" v-if="errors.passwordConfirm">{{ errors.passwordConfirm }}</div>
        </div>
        
        <div class="form-group">
          <label for="invite-code">邀请码</label>
          <input 
            id="invite-code" 
            type="text" 
            v-model="registerForm.inviteCode" 
            placeholder="请输入邀请码"
          />
          <div class="field-error" v-if="errors.inviteCode">{{ errors.inviteCode }}</div>
        </div>
        
        <button 
          class="register-button" 
          @click="handleRegister" 
          :disabled="isLoading"
        >
          {{ isLoading ? '注册中...' : '注册' }}
        </button>
        
        <div class="login-link">
          <p>已有管理员账号？<a @click="goToLogin">立即登录</a></p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-register {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  padding: 2rem 0;
}

.register-container {
  width: 100%;
  max-width: 400px;
  padding: 2rem;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  margin: 0 1rem;
}

.register-header {
  text-align: center;
  margin-bottom: 2rem;
}

.register-header h1 {
  font-size: 2rem;
  color: #2575fc;
  margin-bottom: 0.5rem;
}

.register-header p {
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

.register-button {
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

.register-button:disabled {
  background-color: #a0c0f0;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
}

.login-link a {
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