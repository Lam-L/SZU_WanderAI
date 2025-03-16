import { defineStore } from 'pinia'
import axios from 'axios'

interface MerchantState {
  token: string | null
  merchant: {
    id: number | null
    name: string
    email: string
  }
  isAuthenticated: boolean
}

export const useMerchantStore = defineStore('merchant', {
  state: (): MerchantState => ({
    token: localStorage.getItem('merchantToken'),
    merchant: {
      id: null,
      name: '',
      email: ''
    },
    isAuthenticated: localStorage.getItem('merchantToken') !== null
  }),
  
  actions: {
    async login(email: string, password: string) {
      try {
        // Check for test merchant account
        if (email === 'merchant@qq.com' && password === 'wanderai@qq.com') {
          const fakeMerchant = {
            id: 1,
            name: '测试商家',
            email: 'merchant@qq.com'
          }
          
          this.token = 'fake-merchant-token'
          this.merchant = fakeMerchant
          this.isAuthenticated = true
          
          localStorage.setItem('merchantToken', 'fake-merchant-token')
          return { success: true }
        }
        
        // Real API call for other accounts
        const response = await axios.post('/api/merchant/login', { email, password })
        
        this.token = response.data.token
        this.merchant = response.data.merchant
        this.isAuthenticated = true
        
        localStorage.setItem('merchantToken', response.data.token)
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
      this.merchant = {
        id: null,
        name: '',
        email: ''
      }
      this.isAuthenticated = false
      
      localStorage.removeItem('merchantToken')
    }
  }
})