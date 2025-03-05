<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')

const loginForm = reactive({
  email: '',
  password: ''
})

const registerForm = reactive({
  userName: '',
  email: '',
  verifyCode: '',
  password: '',
  passwordAgain: ''
})

const loginErrors = reactive({
  email: '',
  password: '',
  general: ''
})

const registerErrors = reactive({
  userName: '',
  email: '',
  verifyCode: '',
  password: '',
  passwordAgain: '',
  general: ''
})

const isLoading = ref(false)
const isVerifyCodeLoading = ref(false)
const verifyCodeSent = ref(false)
const verifyCodeCountdown = ref(0)

const validateLoginForm = () => {
  let isValid = true
  loginErrors.email = ''
  loginErrors.password = ''
  loginErrors.general = ''
  
  if (!loginForm.email) {
    loginErrors.email = '请输入邮箱'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginForm.email)) {
    loginErrors.email = '请输入有效的邮箱地址'
    isValid = false
  }
  
  if (!loginForm.password) {
    loginErrors.password = '请输入密码'
    isValid = false
  }
  
  return isValid
}

const validateRegisterForm = () => {
  let isValid = true
  registerErrors.userName = ''
  registerErrors.email = ''
  registerErrors.verifyCode = ''
  registerErrors.password = ''
  registerErrors.passwordAgain = ''
  registerErrors.general = ''
  
  if (!registerForm.userName) {
    registerErrors.userName = '请输入昵称'
    isValid = false
  }
  
  if (!registerForm.email) {
    registerErrors.email = '请输入邮箱'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    registerErrors.email = '请输入有效的邮箱地址'
    isValid = false
  }
  
  if (!registerForm.verifyCode) {
    registerErrors.verifyCode = '请输入验证码'
    isValid = false
  }
  
  if (!registerForm.password) {
    registerErrors.password = '请输入密码'
    isValid = false
  } else if (registerForm.password.length < 6) {
    registerErrors.password = '密码长度至少为6位'
    isValid = false
  }
  
  if (!registerForm.passwordAgain) {
    registerErrors.passwordAgain = '请确认密码'
    isValid = false
  } else if (registerForm.password !== registerForm.passwordAgain) {
    registerErrors.passwordAgain = '两次输入的密码不一致'
    isValid = false
  }
  
  return isValid
}

const handleLogin = async () => {
  if (!validateLoginForm()) return
  
  isLoading.value = true
  try {
    // Check for fake account first
    if (loginForm.email === 'admin@qq.com' && loginForm.password === 'wanderai@qq.com') {
      // Fake login for testing
      const fakeUser = {
        id: 999,
        userName: '管理员',
        email: 'admin@qq.com',
        bio: '平台管理员账号',
        avatar: 'https://i.pravatar.cc/150?img=1',
        createdAt: new Date().toISOString()
      }
      
      // Update store with fake data
      userStore.token = 'fake-jwt-token-for-testing'
      userStore.user = fakeUser
      userStore.isAuthenticated = true
      
      // Save token to localStorage
      localStorage.setItem('token', 'fake-jwt-token-for-testing')
      
      // Show success message
      ElMessage.success('登录成功！')
      
      // Redirect to home page
      router.push('/home')
      return
    }
    
    // Real API call for other accounts
    const response = await axios.post('/api/auth/login', {
      email: loginForm.email,
      password: loginForm.password
    })
    
    const { token, user } = response.data
    
    // Update store
    userStore.token = token
    userStore.user = user
    userStore.isAuthenticated = true
    
    // Save token to localStorage
    localStorage.setItem('token', token)
    
    // Redirect to home page
    router.push('/home')
  } catch (error: any) {
    console.error('Login failed:', error.message || 'Unknown error')
    if (error.response) {
      if (error.response.status === 401) {
        loginErrors.general = '邮箱或密码错误'
      } else {
        loginErrors.general = error.response.data?.message || '登录失败，请稍后再试'
      }
    } else {
      loginErrors.general = '登录失败，请检查网络连接'
    }
  } finally {
    isLoading.value = false
  }
}

const handleRegister = async () => {
  if (!validateRegisterForm()) return
  
  isLoading.value = true
  try {
    const response = await axios.post('/api/auth/register', {
      userName: registerForm.userName,
      email: registerForm.email,
      password: registerForm.password,
      verifyCode: registerForm.verifyCode
    })
    
    const { token, user } = response.data
    
    // Update store
    userStore.token = token
    userStore.user = user
    userStore.isAuthenticated = true
    
    // Save token to localStorage
    localStorage.setItem('token', token)
    
    // Redirect to home page
    router.push('/home')
  } catch (error: any) {
    console.error('Registration failed:', error.message || 'Unknown error')
    if (error.response) {
      if (error.response.data?.message) {
        registerErrors.general = error.response.data.message
      } else if (error.response.data?.verifyCode) {
        registerErrors.verifyCode = error.response.data.verifyCode
      } else if (error.response.data?.email) {
        registerErrors.email = error.response.data.email
      } else if (error.response.data?.userName) {
        registerErrors.userName = error.response.data.userName
      } else if (error.response.data?.password) {
        registerErrors.password = error.response.data.password
      } else {
        registerErrors.general = '注册失败，请稍后再试'
      }
    } else {
      registerErrors.general = '注册失败，请检查网络连接'
    }
  } finally {
    isLoading.value = false
  }
}

const sendVerificationCode = async () => {
  if (!registerForm.email) {
    registerErrors.email = '请输入邮箱'
    return
  }
  
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    registerErrors.email = '请输入有效的邮箱地址'
    return
  }
  
  if (isVerifyCodeLoading.value || verifyCodeCountdown.value > 0) return
  
  isVerifyCodeLoading.value = true
  try {
    await axios.post('/api/auth/verify-code', { email: registerForm.email })
    
    verifyCodeSent.value = true
    verifyCodeCountdown.value = 60
    
    // Start countdown
    const countdownInterval = setInterval(() => {
      verifyCodeCountdown.value--
      if (verifyCodeCountdown.value <= 0) {
        clearInterval(countdownInterval)
      }
    }, 1000)
    
    ElMessage.success('验证码已发送到您的邮箱')
  } catch (error: any) {
    console.error('Failed to send verification code:', error.message || 'Unknown error')
    if (error.response && error.response.data?.message) {
      registerErrors.email = error.response.data.message
    } else {
      registerErrors.email = '验证码发送失败，请稍后再试'
    }
  } finally {
    isVerifyCodeLoading.value = false
  }
}

const switchTab = (tab: string) => {
  activeTab.value = tab
  // Reset errors when switching tabs
  loginErrors.general = ''
  registerErrors.general = ''
}
</script>

<template>
  <div class="landing-container">
    <div class="landing-content">
      <div class="landing-header">
        <h1>WanderAI</h1>
        <p>AI智能旅游规划平台</p>
      </div>
      
      <div class="auth-container">
        <div class="auth-tabs">
          <div 
            class="auth-tab" 
            :class="{ active: activeTab === 'login' }"
            @click="switchTab('login')"
          >
            登录
          </div>
          <div 
            class="auth-tab" 
            :class="{ active: activeTab === 'register' }"
            @click="switchTab('register')"
          >
            注册
          </div>
        </div>
        
        <div class="auth-form" v-if="activeTab === 'login'">
          <div class="form-error" v-if="loginErrors.general">{{ loginErrors.general }}</div>
          
          <div class="form-group">
            <label for="login-email">邮箱</label>
            <input 
              id="login-email" 
              type="email" 
              v-model="loginForm.email" 
              placeholder="请输入邮箱"
            />
            <div class="field-error" v-if="loginErrors.email">{{ loginErrors.email }}</div>
          </div>
          
          <div class="form-group">
            <label for="login-password">密码</label>
            <input 
              id="login-password" 
              type="password" 
              v-model="loginForm.password" 
              placeholder="请输入密码"
            />
            <div class="field-error" v-if="loginErrors.password">{{ loginErrors.password }}</div>
          </div>
          
          <button 
            class="auth-button" 
            @click="handleLogin" 
            :disabled="isLoading"
          >
            {{ isLoading ? '登录中...' : '登录' }}
          </button>
          
          <div class="test-account">
            <p>测试账号: admin@qq.com / wanderai@qq.com</p>
          </div>
          
          <div class="social-login">
            <p>或者使用以下方式登录</p>
            <button class="wechat-login">微信登录</button>
          </div>
        </div>
        
        <div class="auth-form" v-if="activeTab === 'register'">
          <div class="form-error" v-if="registerErrors.general">{{ registerErrors.general }}</div>
          
          <div class="form-group">
            <label for="register-username">昵称</label>
            <input 
              id="register-username" 
              type="text" 
              v-model="registerForm.userName" 
              placeholder="请输入昵称"
            />
            <div class="field-error" v-if="registerErrors.userName">{{ registerErrors.userName }}</div>
          </div>
          
          <div class="form-group">
            <label for="register-email">邮箱</label>
            <input 
              id="register-email" 
              type="email" 
              v-model="registerForm.email" 
              placeholder="请输入邮箱"
            />
            <div class="field-error" v-if="registerErrors.email">{{ registerErrors.email }}</div>
          </div>
          
          <div class="form-group verification-group">
            <label for="register-verify-code">验证码</label>
            <div class="verification-input">
              <input 
                id="register-verify-code" 
                type="text" 
                v-model="registerForm.verifyCode" 
                placeholder="请输入验证码"
              />
              <button 
                class="verify-button" 
                @click="sendVerificationCode"
                :disabled="isVerifyCodeLoading || verifyCodeCountdown > 0"
              >
                <span v-if="verifyCodeCountdown > 0">{{ verifyCodeCountdown }}秒后重新获取</span>
                <span v-else-if="isVerifyCodeLoading">发送中...</span>
                <span v-else>获取验证码</span>
              </button>
            </div>
            <div class="field-error" v-if="registerErrors.verifyCode">{{ registerErrors.verifyCode }}</div>
          </div>
          
          <div class="form-group">
            <label for="register-password">密码</label>
            <input 
              id="register-password" 
              type="password" 
              v-model="registerForm.password" 
              placeholder="请输入密码"
            />
            <div class="field-error" v-if="registerErrors.password">{{ registerErrors.password }}</div>
          </div>
          
          <div class="form-group">
            <label for="register-password-again">确认密码</label>
            <input 
              id="register-password-again" 
              type="password" 
              v-model="registerForm.passwordAgain" 
              placeholder="请再次输入密码"
            />
            <div class="field-error" v-if="registerErrors.passwordAgain">{{ registerErrors.passwordAgain }}</div>
          </div>
          
          <button 
            class="auth-button" 
            @click="handleRegister" 
            :disabled="isLoading"
          >
            {{ isLoading ? '注册中...' : '注册' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.landing-container {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.landing-content {
  width: 100%;
  max-width: 500px;
  padding: 2rem;
}

.landing-header {
  text-align: center;
  color: white;
  margin-bottom: 2rem;
}

.landing-header h1 {
  font-size: 3rem;
  margin-bottom: 0.5rem;
}

.auth-container {
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.auth-tabs {
  display: flex;
  border-bottom: 1px solid #eee;
}

.auth-tab {
  flex: 1;
  text-align: center;
  padding: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.auth-tab.active {
  background-color: #f9f9f9;
  font-weight: bold;
  color: #2575fc;
  border-bottom: 2px solid #2575fc;
}

.auth-form {
  padding: 2rem;
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
  box-sizing: border-box;
}

.verification-group .verification-input {
  display: flex;
  gap: 10px;
}

.verification-group input {
  flex: 1;
}

.verify-button {
  padding: 0.75rem 1rem;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
  min-width: 120px;
}

.verify-button:disabled {
  background-color: #f9f9f9;
  color: #aaa;
  cursor: not-allowed;
}

.auth-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.auth-button:hover {
  background-color: #1a65e0;
}

.auth-button:disabled {
  background-color: #a0c0f0;
  cursor: not-allowed;
}

.test-account {
  margin-top: 1rem;
  text-align: center;
  padding: 0.5rem;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px dashed #ddd;
}

.test-account p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.social-login {
  margin-top: 1.5rem;
  text-align: center;
}

.social-login p {
  margin-bottom: 1rem;
  color: #666;
}

.wechat-login {
  padding: 0.75rem 1.5rem;
  background-color: #07c160;
  color: white;
  border: none;
  border-radius: 4px;
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