import { defineStore } from 'pinia'
import axios from 'axios'

interface UserState {
  token: string | null
  user: {
    id: number | null
    userName: string
    email: string
    bio?: string
    avatar?: string
  }
  isAuthenticated: boolean
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token'),
    user: {
      id: null,
      userName: '',
      email: ''
    },
    isAuthenticated: localStorage.getItem('token') !== null
  }),
  
  actions: {
    async login(email: string, password: string) {
      try {
        // Check for fake account first
        if (email === 'admin@qq.com' && password === 'wanderai@qq.com') {
          // Fake login for testing
          const fakeUser = {
            id: 999,
            userName: '管理员',
            email: 'admin@qq.com',
            bio: '平台管理员账号',
            avatar: 'https://i.pravatar.cc/150?img=1'
          }
          
          this.token = 'fake-jwt-token-for-testing'
          this.user = fakeUser
          this.isAuthenticated = true
          
          localStorage.setItem('token', 'fake-jwt-token-for-testing')
          return true
        }
        
        // Real API call for other accounts
        const response = await axios.post('/api/auth/login', { email, password })
        
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
    
    async register(userData: { userName: string, email: string, password: string, verifyCode: string }) {
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
      this.user = {
        id: null,
        userName: '',
        email: ''
      }
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
            createdAt: new Date().toISOString()
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
    
    async updateUserProfile(userData: any) {
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
          return true
        }
        
        // Real API call for other accounts
        const response = await axios.put('/api/user/profile', userData)
        this.user = response.data
        return true
      } catch (error) {
        console.error('Failed to update profile:', error)
        return false
      }
    }
  }
})