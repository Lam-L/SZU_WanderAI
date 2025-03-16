import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';

// Configure axios
axios.defaults.baseURL = 'http://localhost:8080' // Backend API URL
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Handle 401 responses (unauthorized)
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      // Clear token and redirect to login page
      localStorage.removeItem('token')
      router.push('/')
    }
    return Promise.reject(error)
  }
)

// Add axios interceptor to handle fake token
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token === 'fake-jwt-token-for-testing') {
    // Skip actual API calls for the fake account
    if (config.url?.includes('/api/')) {
      // This will cause the request to be canceled
      // We'll handle fake responses in the store methods
      return {
        ...config,
        cancelToken: new axios.CancelToken(cancel => cancel('Using fake account'))
      }
    }
  }
  return config
})

const app = createApp(App)


app.use(createPinia().use(piniaPluginPersistedstate))
app.use(router)
app.use(ElementPlus)

app.mount('#app')