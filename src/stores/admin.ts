import { defineStore } from 'pinia'
import axios from 'axios'

interface AdminState {
  token: string | null
  admin: {
    id: number | null
    name: string
    email: string
  }
  isAuthenticated: boolean
}

export const useAdminStore = defineStore('admin', {
  state: (): AdminState => ({
    token: localStorage.getItem('adminToken'),
    admin: {
      id: null,
      name: '',
      email: ''
    },
    isAuthenticated: localStorage.getItem('adminToken') !== null
  }),
  
  actions: {
    async login(email: string, password: string) {
      try {
        // Check for test admin account
        if (email === 'admin@qq.com' && password === 'wanderai@qq.com') {
          const fakeAdmin = {
            id: 1,
            name: '管理员',
            email: 'admin@qq.com'
          }
          
          this.token = 'fake-admin-token'
          this.admin = fakeAdmin
          this.isAuthenticated = true
          
          localStorage.setItem('adminToken', 'fake-admin-token')
          return { success: true }
        }
        
        // Real API call for other accounts
        const response = await axios.post('/api/admin/login', { email, password })
        
        this.token = response.data.token
        this.admin = response.data.admin
        this.isAuthenticated = true
        
        localStorage.setItem('adminToken', response.data.token)
        return { success: true }
      } catch (error: any) {
        console.error('Login failed:', error)
        return { 
          success: false, 
          error: error.response?.data?.message || '登录失败，请稍后再试' 
        }
      }
    },
    
    logout() {
      this.token = null
      this.admin = {
        id: null,
        name: '',
        email: ''
      }
      this.isAuthenticated = false
      
      localStorage.removeItem('adminToken')
    }
  }
})