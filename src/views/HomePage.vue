<script setup lang="ts">
// ... (保持其他导入和代码不变)

const switchRole = (role: string) => {
  authStore.switchRole(role)
}

// Get available roles excluding current active role
const availableRoles = computed(() => {
  return authStore.roles.filter(role => role !== authStore.activeRole)
})

// ... (保持其他代码不变)
</script>

<template>
  <div class="home-container">
    <!-- Header -->
    <header class="home-header">
      <div class="logo">
        <h1>WanderAI</h1>
      </div>
      <div class="user-info">
        <el-dropdown>
          <span class="user-info-text">
            {{ authStore.user?.userName || '个人中心' }}
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/profile')">
                个人中心
              </el-dropdown-item>
              
              <!-- Role switching options -->
              <template v-if="availableRoles.length > 0">
                <el-dropdown-item 
                  v-for="role in availableRoles" 
                  :key="role"
                  divided
                  @click="switchRole(role)"
                >
                  切换到{{ role === 'ADMIN' ? '管理员' : role === 'MERCHANT' ? '商家' : '用户' }}
                </el-dropdown-item>
              </template>
              
              <el-dropdown-item divided @click="logout">
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    
    <!-- 保持其他模板内容不变 -->
  </div>
</template>

<!-- 保持样式不变 -->