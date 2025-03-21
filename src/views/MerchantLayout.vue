<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const activeMenu = ref(router.currentRoute.value.name)

const handleMenuSelect = (index: string) => {
  router.push({ name: index })
}

const switchRole = (role: string) => {
  authStore.switchRole(role)
}

const logout = () => {
  authStore.logout()
}

// Get available roles excluding current active role
const availableRoles = computed(() => {
  return authStore.roles.filter(role => role !== authStore.activeRole)
})
</script>

<template>
  <div class="merchant-layout">
    <!-- Sidebar -->
    <div class="sidebar">
      <div class="logo">
        <h1>WanderAI</h1>
        <p>商家中心</p>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="merchant-dashboard">
          <el-icon><el-icon-monitor /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        
        <el-menu-item index="merchant-packages">
          <el-icon><el-icon-tickets /></el-icon>
          <span>团购管理</span>
        </el-menu-item>
      </el-menu>
    </div>
    
    <!-- Main Content -->
    <div class="main-content">
      <!-- Header -->
      <div class="header">
        <div class="breadcrumb">
          <el-breadcrumb>
            <el-breadcrumb-item>商家中心</el-breadcrumb-item>
            <el-breadcrumb-item>{{ activeMenu }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="user-actions">
          <el-dropdown>
            <span class="user-info">
              {{ authStore.user?.userName }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <!-- Role switching options -->
                <template v-if="availableRoles.length > 0">
                  <el-dropdown-item 
                    v-for="role in availableRoles" 
                    :key="role"
                    @click="switchRole(role)"
                  >
                    切换到{{ role === 'ADMIN' ? '管理员' : role === 'MERCHANT' ? '商家' : '用户' }}
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="logout">
                    退出登录
                  </el-dropdown-item>
                </template>
                <el-dropdown-item v-else @click="logout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- Page Content -->
      <div class="page-content">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<style scoped>
.merchant-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 240px;
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 1rem;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #fff;
}

.logo p {
  margin: 0.5rem 0 0;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
}

.sidebar-menu {
  border-right: none;
  background-color: transparent;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
}

.header {
  height: 60px;
  background-color: white;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1rem;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0.5rem;
}

.page-content {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
}
</style>