import { defineStore } from 'pinia';
import axios from 'axios';
import type { AuthState, LoginRequest, RegisterRequest, UserProfile } from '../types/auth';
import router from '../router';

export const useAuthStore = defineStore('auth', {
  persist: true,
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
    user: null,
    isAuthenticated: localStorage.getItem('token') !== null,
    roles: [],
    permissions: [],
    activeRole: localStorage.getItem('activeRole') || '',
    isReady: false
  }),

  getters: {
    isAdmin: (state) => state.roles.includes('ADMIN'),
    isMerchant: (state) => state.roles.includes('MERCHANT'),
    isUser: (state) => state.roles.includes('USER'),
    
    hasPermission: (state) => (permission: string) => 
      state.permissions.includes(permission),
    
    hasAnyRole: (state) => (roles: string[]) => 
      roles.some(role => state.roles.includes(role)),
    
    hasAllRoles: (state) => (roles: string[]) => 
      roles.every(role => state.roles.includes(role)),

    primaryRole: (state) => state.activeRole,

    homePath: (state): string => {
      if (state.activeRole === 'ADMIN') return '/admin';
      if (state.activeRole === 'MERCHANT') return '/merchant';
      return '/home';
    }
  },

  actions: {
    async login(credentials: LoginRequest) {
      try {
        // Handle test accounts
        if (credentials.email === 'admin@qq.com' && credentials.password === 'wanderai@qq.com') {
          const fakeAdmin: UserProfile = {
            id: 1,
            userName: '管理员',
            email: 'admin@qq.com',
            createdAt: new Date().toISOString(),
            roles: [
              { id: 1, name: 'ADMIN', description: 'System administrator' },
              { id: 3, name: 'USER', description: 'Regular user' }
            ],
            permissions: []
          };
          
          this.setAuth('fake-jwt-token', fakeAdmin, 'ADMIN');
          return { success: true };
        }

        if (credentials.email === 'merchant@qq.com' && credentials.password === 'wanderai@qq.com') {
          const fakeMerchant: UserProfile = {
            id: 2,
            userName: '测试商家',
            email: 'merchant@qq.com',
            merchantStatus: 'approved',
            createdAt: new Date().toISOString(),
            roles: [
              { id: 2, name: 'MERCHANT', description: 'Merchant account' },
              { id: 3, name: 'USER', description: 'Regular user' }
            ],
            permissions: []
          };
          
          this.setAuth('fake-merchant-token', fakeMerchant, 'MERCHANT');
          return { success: true };
        }

        if (credentials.email === 'user@qq.com' && credentials.password === 'wanderai@qq.com') {
          const fakeUser: UserProfile = {
            id: 3,
            userName: '测试用户',
            email: 'user@qq.com',
            createdAt: new Date().toISOString(),
            roles: [{ id: 3, name: 'USER', description: 'Regular user' }],
            permissions: []
          };
          
          this.setAuth('fake-user-token', fakeUser, 'USER');
          return { success: true };
        }
        console.log("waht",this.roles || "救命");


        // Real API call  
        const response = await axios.post('/api/auth/login', credentials);
        console.log(response.data);
        let tempRole = response.data.user.roles[0].name;
        tempRole = tempRole.toUpperCase();
        this.setAuth(response.data.token, response.data.user, tempRole);
        console.log("what",this.roles || "救命");
        return { success: true };
      } catch (error: any) {
        console.error('Login failed:', error);
        return { 
          success: false, 
          error: error.response?.data?.message || '登录失败，请稍后再试' 
        };
      }
    },

    async register(data: RegisterRequest) {
      try {
        // Send verification code first if not already sent
        if (!data.verifyCode) {
          await axios.post('/api/auth/verify-code', { email: data.email });
          return { 
            success: true, 
            message: '验证码已发送到您的邮箱' 
          };
        }

        // Register user
        const registerData = {
          userName: data.userName,
          email: data.email,
          password: data.password,
          verifyCode: data.verifyCode,
          role: data.isMerchant ? 'MERCHANT' : 'USER',
          phone: data.phone,
          address: data.address,
          businessLicense: data.businessLicense
        };

        const response = await axios.post('/api/auth/register', registerData);
        this.setAuth(response.data.token, response.data.user);
        return { success: true };
      } catch (error: any) {
        console.error('Registration failed:', error);
        return { 
          success: false, 
          error: error.response?.data?.message || '注册失败，请稍后再试' 
        };
      }
    },

    async updateProfile(data: { userName: string; bio: string }) {
      try {
        if (!this.user) {
          throw new Error('No user logged in');
        }

        // Handle test accounts
        if (this.token === 'fake-jwt-token' || 
            this.token === 'fake-merchant-token' || 
            this.token === 'fake-user-token') {
          const updatedUser = {
            ...this.user,
            userName: data.userName,
            bio: data.bio
          };
          this.user = updatedUser;
          return { success: true };
        }

        // Real API call
        const response = await axios.put('/api/user/profile', data);
        this.user = response.data;
        
        return { success: true };
      } catch (error: any) {
        console.error('Failed to update profile:', error);
        return { 
          success: false, 
          error: error.response?.data?.message || '更新失败，请稍后再试' 
        };
      }
    },

    switchRole(role: string) {
      if (!this.hasAnyRole([role])) {
        return false;
      }
      
      this.activeRole = role;
      localStorage.setItem('activeRole', role);
      router.push(this.homePath);
      return true;
    },


  logout(this: AuthState & { logout: () => void }) {
    this.token = null;
    this.user = null;
    this.isAuthenticated = false;
    this.roles = [];
    this.permissions = [];
    this.activeRole = '';

    localStorage.removeItem('token');
    localStorage.removeItem('activeRole');

    // 清除 axios 默认请求头
    delete axios.defaults.headers.common['Authorization'];

    // 跳转到登录页
    router.push('/');
  },

    setAuth(token: string, user: UserProfile, initialRole?: string) {
      this.token = token;
      this.user = user;
      this.isAuthenticated = true;
      this.roles = user.roles.map(role => role.name);
      this.permissions = user.permissions ? user.permissions.map(perm => perm.name) : [];
      console.log("yes", this.roles);
      this.isReady = true; 
      // Set initial active role based on priority
      if (initialRole) {
        this.activeRole = initialRole;
      } else if (this.roles.includes('ADMIN')) {
        this.activeRole = 'ADMIN';
      } else if (this.roles.includes('MERCHANT')) {
        this.activeRole = 'MERCHANT';
      } else {
        this.activeRole = 'USER';
      }
      
      localStorage.setItem('token', token);
      localStorage.setItem('activeRole', this.activeRole);

      // Configure axios default headers
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    },

    // logout() {
    //   this.token = null;
    //   this.user = null;
    //   this.isAuthenticated = false;
    //   this.roles = [];
    //   this.permissions = [];
    //   this.activeRole = '';
      
    //   localStorage.removeItem('token');
    //   localStorage.removeItem('activeRole');
      
    //   // Remove axios default headers
    //   delete axios.defaults.headers.common['Authorization'];
      
    //   router.push('/');
    // }
  },
  // persist: {
  //   enabled: true, // 启用持久化
  //   strategies: [
  //     {
  //       key: 'auth', // 存储的 key
  //       storage: localStorage, // 使用 localStorage
  //       paths: ['token', 'user', 'isAuthenticated', 'roles', 'permissions', 'activeRole'] // 指定需要持久化的字段
  //     }
  //   ]
  // }
});
