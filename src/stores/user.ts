import { defineStore } from 'pinia'
import axios from 'axios'
import type { UserProfile } from '../types/auth'

interface UserState {
  token: string | null
  user: UserProfile | null
  isAuthenticated: boolean
}

export const useUserStore = defineStore('user', {
    persist: true,
  state: (): UserState => ({
    token: localStorage.getItem('token'),
    user: null,
    isAuthenticated: localStorage.getItem('token') !== null
  }),
  
  actions: {
    async login(email: string, password: string) {
      try {
        // Check for fake account first
        if (email === 'admin@qq.com' && password === 'wanderai@qq.com') {
          // Fake login for testing
          const fakeUser: UserProfile = {
            id: 999,
            userName: '管理员',
            email: 'admin@qq.com',
            bio: '平台管理员账号',
            avatar: 'https://i.pravatar.cc/150?img=1',
            createdAt: new Date().toISOString(),
            roles: [
              { id: 1, name: 'admin', description: 'System administrator' }
            ],
            permissions: [
              { name: 'manage_users', description: 'Can manage all users' },
              { name: 'manage_merchants', description: 'Can manage merchant accounts' },
              { name: 'manage_roles', description: 'Can manage roles and permissions' },
              { name: 'view_admin_dashboard', description: 'Can view admin dashboard' }
            ]
          }
          
          this.token = 'fake-jwt-token-for-testing'
          this.user = fakeUser
          this.isAuthenticated = true
          
          localStorage.setItem('token', 'fake-jwt-token-for-testing')
          return true
        }
        
        // Real API call for other accounts
        const response = await axios.post('/api/auth/login', { 
          email, 
          password
        })
        
        this.token = response.data.token
        this.user = response.data.user
        this.isAuthenticated = true
        
        localStorage.setItem('token', response.data.token)
        return true
      } catch (error) {
        console.error('Login failed:', error)
        return false
      }
    },
    
    async register(userData: { 
      userName: string
      email: string
      password: string
      verifyCode: string
      isMerchant?: boolean
      phone?: string
      address?: string
      businessLicense?: string
    }) {
      try {
        const response = await axios.post('/api/auth/register', userData)
        
        this.token = response.data.token
        this.user = response.data.user
        this.isAuthenticated = true
        
        localStorage.setItem('token', response.data.token)
        return true
      } catch (error) {
        console.error('Registration failed:', error)
        return false
      }
    },
    
    logout() {
      this.token = null
      this.user = null
      this.isAuthenticated = false
      
      localStorage.removeItem('token')
    },
    
    async getUserProfile() {
      try {
        // Check if using fake account
        if (this.token === 'fake-jwt-token-for-testing') {
          return {
            id: 999,
            userName: '管理员',
            email: 'admin@qq.com',
            bio: '平台管理员账号',
            avatar: 'https://i.pravatar.cc/150?img=1',
            createdAt: new Date().toISOString(),
            roles: [
              { id: 1, name: 'admin', description: 'System administrator' }
            ],
            permissions: [
              { name: 'manage_users', description: 'Can manage all users' },
              { name: 'manage_merchants', description: 'Can manage merchant accounts' },
              { name: 'manage_roles', description: 'Can manage roles and permissions' },
              { name: 'view_admin_dashboard', description: 'Can view admin dashboard' }
            ]
          }
        }

        // Add test merchant account
        if (this.user?.email === 'merchant@qq.com') {
          return {
            id: 2,
            userName: '测试商家',
            email: 'merchant@qq.com',
            bio: '商家测试账号',
            avatar: 'https://i.pravatar.cc/150?img=2',
            merchantStatus: 'approved',
            createdAt: new Date().toISOString(),
            roles: [
              { id: 2, name: 'merchant', description: 'Merchant account' },
              { id: 3, name: 'user', description: 'Regular user' }
            ],
            permissions: [
              { name: 'manage_products', description: 'Can manage own products' },
              { name: 'view_merchant_dashboard', description: 'Can view merchant dashboard' },
              { name: 'manage_orders', description: 'Can manage orders' },
              { name: 'browse_products', description: 'Can browse products' },
              { name: 'create_posts', description: 'Can create posts' }
            ]
          }
        }

        // Add test user account
        if (this.user?.email === 'user@qq.com') {
          return {
            id: 3,
            userName: '测试用户',
            email: 'user@qq.com',
            bio: '用户测试账号',
            avatar: 'https://i.pravatar.cc/150?img=3',
            createdAt: new Date().toISOString(),
            roles: [
              { id: 3, name: 'user', description: 'Regular user' }
            ],
            permissions: [
              { name: 'browse_products', description: 'Can browse products' },
              { name: 'create_posts', description: 'Can create posts' }
            ]
          }
        }
        
        // Real API call for other accounts
        const response = await axios.get('/api/user/profile')
        this.user = response.data
        return response.data
      } catch (error) {
        console.error('Failed to get user profile:', error)
        return null
      }
    },
    
    async updateUserProfile(userData: { 
      userName: string
      bio: string
      phone?: string
      address?: string
    }) {
      try {
        // Check if using fake account
        if (this.token === 'fake-jwt-token-for-testing') {
          const updatedUser = {
            ...this.user,
            ...userData,
            id: 999,
            email: 'admin@qq.com'
          }
          this.user = updatedUser
          return { success: true, user: updatedUser }
        }
        
        // Real API call for other accounts
        const response = await axios.put('/api/user/profile', userData)
        this.user = response.data
        return { success: true, user: response.data }
      } catch (error: any) {
        console.error('Failed to update profile:', error)
        return { 
          success: false, 
          error: error.response?.data?.message || '更新失败，请稍后再试' 
        }
      }
    }
  }
})