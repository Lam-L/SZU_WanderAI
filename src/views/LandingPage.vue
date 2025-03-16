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
          
          <div class="test-accounts">
            <h4>测试账号：</h4>
            <div class="account-item">
              <p>管理员账号：admin@qq.com / wanderai@qq.com</p>
            </div>
            <div class="account-item">
              <p>商家账号：merchant@qq.com / wanderai@qq.com</p>
            </div>
            <div class="account-item">
              <p>用户账号：user@qq.com / wanderai@qq.com</p>
            </div>
          </div>
        </div>
        
        <div class="auth-form" v-if="activeTab === 'register'">
          <div class="form-error" v-if="registerErrors.general">{{ registerErrors.general }}</div>
          
          <div class="form-group">
            <label>注册类型</label>
            <div class="role-selector">
              <div 
                class="role-option" 
                :class="{ active: !isMerchantRegistration }"
                @click="isMerchantRegistration = false"
              >
                普通用户
              </div>
              <div 
                class="role-option" 
                :class="{ active: isMerchantRegistration }"
                @click="isMerchantRegistration = true"
              >
                商家入驻
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label for="register-username">{{ isMerchantRegistration ? '商家名称' : '用户昵称' }}</label>
            <input 
              id="register-username" 
              type="text" 
              v-model="registerForm.userName" 
              :placeholder="isMerchantRegistration ? '请输入商家名称' : '请输入昵称'"
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
          
          <!-- Merchant specific fields -->
          <template v-if="isMerchantRegistration">
            <div class="form-group">
              <label for="register-phone">联系电话</label>
              <input 
                id="register-phone" 
                type="tel" 
                v-model="registerForm.phone" 
                placeholder="请输入联系电话"
              />
              <div class="field-error" v-if="registerErrors.phone">{{ registerErrors.phone }}</div>
            </div>
            
            <div class="form-group">
              <label for="register-address">商家地址</label>
              <input 
                id="register-address" 
                type="text" 
                v-model="registerForm.address" 
                placeholder="请输入商家地址"
              />
              <div class="field-error" v-if="registerErrors.address">{{ registerErrors.address }}</div>
            </div>
            
            <div class="form-group">
              <label for="register-license">营业执照号</label>
              <input 
                id="register-license" 
                type="text" 
                v-model="registerForm.businessLicense" 
                placeholder="请输入营业执照号"
              />
              <div class="field-error" v-if="registerErrors.businessLicense">{{ registerErrors.businessLicense }}</div>
            </div>
          </template>
          
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

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const router = useRouter();
const authStore = useAuthStore();

const activeTab = ref('login');
const isMerchantRegistration = ref(false);

const loginForm = reactive({
  email: '',
  password: ''
});

const registerForm = reactive({
  userName: '',
  email: '',
  verifyCode: '',
  password: '',
  passwordAgain: '',
  phone: '',
  address: '',
  businessLicense: ''
});

const loginErrors = reactive({
  email: '',
  password: '',
  general: ''
});

const registerErrors = reactive({
  userName: '',
  email: '',
  verifyCode: '',
  password: '',
  passwordAgain: '',
  phone: '',
  address: '',
  businessLicense: '',
  general: ''
});

const isLoading = ref(false);
const isVerifyCodeLoading = ref(false);
const verifyCodeSent = ref(false);
const verifyCodeCountdown = ref(0);

const validateLoginForm = () => {
  let isValid = true;
  loginErrors.email = '';
  loginErrors.password = '';
  loginErrors.general = '';
  
  if (!loginForm.email) {
    loginErrors.email = '请输入邮箱';
    isValid = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginForm.email)) {
    loginErrors.email = '请输入有效的邮箱地址';
    isValid = false;
  }
  
  if (!loginForm.password) {
    loginErrors.password = '请输入密码';
    isValid = false;
  }
  
  return isValid;
};

const validateRegisterForm = () => {
  let isValid = true;
  Object.keys(registerErrors).forEach(key => registerErrors[key as keyof typeof registerErrors] = '');
  
  if (!registerForm.userName) {
    registerErrors.userName = '请输入昵称';
    isValid = false;
  }
  
  if (!registerForm.email) {
    registerErrors.email = '请输入邮箱';
    isValid = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    registerErrors.email = '请输入有效的邮箱地址';
    isValid = false;
  }
  
  if (!registerForm.verifyCode) {
    registerErrors.verifyCode = '请输入验证码';
    isValid = false;
  }
  
  if (!registerForm.password) {
    registerErrors.password = '请输入密码';
    isValid = false;
  } else if (registerForm.password.length < 6) {
    registerErrors.password = '密码长度至少为6位';
    isValid = false;
  }
  
  if (!registerForm.passwordAgain) {
    registerErrors.passwordAgain = '请确认密码';
    isValid = false;
  } else if (registerForm.password !== registerForm.passwordAgain) {
    registerErrors.passwordAgain = '两次输入的密码不一致';
    isValid = false;
  }

  if (isMerchantRegistration.value) {
    if (!registerForm.phone) {
      registerErrors.phone = '请输入联系电话';
      isValid = false;
    }
    
    if (!registerForm.address) {
      registerErrors.address = '请输入商家地址';
      isValid = false;
    }
    
    if (!registerForm.businessLicense) {
      registerErrors.businessLicense = '请输入营业执照号';
      isValid = false;
    }
  }
  
  return isValid;
};

const handleLogin = async () => {
  if (!validateLoginForm()) return;
  
  isLoading.value = true;
  try {
    const result = await authStore.login({
      email: loginForm.email,
      password: loginForm.password
    });
    
    if (result.success) {
      ElMessage.success('登录成功！');
      router.push(authStore.homePath);
    } else {
      loginErrors.general = result.error || '登录失败，请稍后再试';
    }
  } catch (error) {
    console.error('Login failed:', error);
    loginErrors.general = '登录失败，请稍后再试';
  } finally {
    isLoading.value = false;
  }
};

const handleRegister = async () => {
  if (!validateRegisterForm()) return;
  
  isLoading.value = true;
  try {
    const result = await authStore.register({
      userName: registerForm.userName,
      email: registerForm.email,
      password: registerForm.password,
      verifyCode: registerForm.verifyCode,
      isMerchant: isMerchantRegistration.value,
      phone: registerForm.phone,
      address: registerForm.address,
      businessLicense: registerForm.businessLicense
    });
    
    if (result.success) {
      ElMessage.success('注册成功！');
      if (isMerchantRegistration.value) {
        ElMessage.info('商家账号需要等待管理员审核后才能登录');
        activeTab.value = 'login';
      } else {
        router.push('/home');
      }
    } else {
      registerErrors.general = result.error || '注册失败，请稍后再试';
    }
  } catch (error) {
    console.error('Registration failed:', error);
    registerErrors.general = '注册失败，请稍后再试';
  } finally {
    isLoading.value = false;
  }
};

const sendVerificationCode = async () => {
  if (!registerForm.email) {
    registerErrors.email = '请输入邮箱';
    return;
  }
  
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    registerErrors.email = '请输入有效的邮箱地址';
    return;
  }
  
  if (isVerifyCodeLoading.value || verifyCodeCountdown.value > 0) return;
  
  isVerifyCodeLoading.value = true;
  try {
    await axios.post('/api/auth/verify-code', { email: registerForm.email });
    
    verifyCodeSent.value = true;
    verifyCodeCountdown.value = 60;
    
    // Start countdown
    const countdownInterval = setInterval(() => {
      verifyCodeCountdown.value--;
      if (verifyCodeCountdown.value <= 0) {
        clearInterval(countdownInterval);
      }
    }, 1000);
    
    ElMessage.success('验证码已发送到您的邮箱');
  } catch (error: any) {
    console.error('Failed to send verification code:', error);
    if (error.response && error.response.data?.message) {
      registerErrors.email = error.response.data.message;
    } else {
      registerErrors.email = '验证码发送失败，请稍后再试';
    }
  } finally {
    isVerifyCodeLoading.value = false;
  }
};

const switchTab = (tab: string) => {
  activeTab.value = tab;
  // Reset errors when switching tabs
  loginErrors.general = '';
  registerErrors.general = '';
};
</script>

<style scoped>
.landing-container {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem 0;
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

.role-selector {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.role-option {
  flex: 1;
  padding: 0.75rem;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.role-option.active {
  background-color: #2575fc;
  color: white;
  border-color: #2575fc;
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
  margin-bottom: 1rem;
}

.auth-button:hover {
  background-color: #1a65e0;
}

.auth-button:disabled {
  background-color: #a0c0f0;
  cursor: not-allowed;
}

.test-accounts {
  margin-top: 1.5rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px dashed #ddd;
}

.test-accounts h4 {
  margin: 0 0 0.5rem;
  color: #333;
}

.account-item {
  margin-bottom: 0.5rem;
}

.account-item:last-child {
  margin-bottom: 0;
}

.account-item p {
  margin: 0;
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