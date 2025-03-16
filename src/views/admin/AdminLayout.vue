<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const activeMenu = ref(router.currentRoute.value.name)
const roles = authStore.roles; 

const handleMenuSelect = (index: string) => {
  router.push({ name: index })
}

const switchRole = (role: 'admin' | 'user') => {
  authStore.switchRole(role)
}

const logout = () => {
  authStore.logout()
}

// Compute if current route is under roles management
const isRolesRoute = computed(() => {
  return router.currentRoute.value.path.startsWith('/admin/roles')
})
</script>

<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <div class="sidebar">
      <div class="logo">
        <h1>WanderAI</h1>
        <p>管理平台 {{ authStore.user?.userName }}</p>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="admin-dashboard">
          <el-icon><el-icon-monitor /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        
        <el-menu-item index="admin-users">
          <el-icon><el-icon-user /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="admin-merchants">
          <el-icon><el-icon-shop /></el-icon>
          <span>商家管理</span>
        </el-menu-item>
        
        <el-menu-item index="admin-packages">
          <el-icon><el-icon-tickets /></el-icon>
          <span>团购管理</span>
        </el-menu-item>
        
        <el-menu-item index="admin-posts">
          <el-icon><el-icon-document /></el-icon>
          <span>帖子管理</span>
        </el-menu-item>

        <el-sub-menu index="roles">
          <template #title>
            <el-icon><el-icon-key /></el-icon>
            <span>权限管理</span>
          </template>
          <el-menu-item index="admin-roles-list">角色管理</el-menu-item>
          <el-menu-item index="admin-permissions">权限管理</el-menu-item>
          <el-menu-item index="admin-role-permissions">角色权限分配</el-menu-item>
          <el-menu-item index="admin-user-roles">用户角色分配</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>
    
    <!-- Main Content -->
    <div class="main-content">
      <!-- Header -->
      <div class="header">
        <div class="breadcrumb">
          <el-breadcrumb>
            <el-breadcrumb-item>管理平台</el-breadcrumb-item>
            <template v-if="isRolesRoute">
              <el-breadcrumb-item>权限管理</el-breadcrumb-item>
              <el-breadcrumb-item>{{ activeMenu }}</el-breadcrumb-item>
            </template>
            <el-breadcrumb-item v-else>{{ activeMenu }}</el-breadcrumb-item>
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
                <el-dropdown-item
                  v-for="role in roles"
                  :key="role"
                  @click.native="switchRole('user')"
                >
                {{ role }}
              </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
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
.admin-layout {
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