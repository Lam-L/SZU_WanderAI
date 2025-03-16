<template>
  <div class="user-roles">
    <div class="page-header">
      <h2>用户角色分配</h2>
    </div>

    <el-card class="users-table">
      <el-table
        v-loading="loading"
        :data="users"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="userName" label="用户名" width="150" />
        
        <el-table-column prop="email" label="邮箱" min-width="200" />
        
        <el-table-column label="当前角色" width="200">
          <template #default="{ row }">
            <el-tag 
              v-for="role in getUserRoles(row.id)"
              :key="role.id"
              class="role-tag"
            >
              {{ role.name }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="openRoleDialog(row)"
            >
              分配角色
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- User Role Dialog -->
    <el-dialog
      v-model="roleDialogVisible"
      :title="`分配用户角色 - ${selectedUser?.userName}`"
      width="500px"
    >
      <div class="role-list">
        <template v-if="selectedUser">
          <div 
            v-for="role in roles" 
            :key="role.id"
            class="role-item"
          >
            <el-checkbox 
              :model-value="hasRole(selectedUser.id, role.id)"
              @update:model-value="(checked) => handleRoleChange(selectedUser.id, role.id, checked)"
            >
              <div class="role-info">
                <div class="role-name">{{ role.name }}</div>
                <div class="role-description">{{ role.description }}</div>
              </div>
            </el-checkbox>
          </div>
        </template>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

interface User {
  id: number
  userName: string
  email: string
}

interface Role {
  id: number
  name: string
  description: string
  createdAt: string
}

interface UserRole {
  userId: number
  roleId: number
}

const users = ref<User[]>([])
const roles = ref<Role[]>([])
const userRoles = ref<UserRole[]>([])
const loading = ref(true)
const roleDialogVisible = ref(false)
const selectedUser = ref<User | null>(null)

onMounted(async () => {
  // Simulate loading data
  setTimeout(() => {
    users.value = [
      {
        id: 1,
        userName: 'Admin User',
        email: 'admin@example.com'
      },
      {
        id: 2,
        userName: 'Test Merchant',
        email: 'merchant@example.com'
      },
      {
        id: 3,
        userName: 'Regular User',
        email: 'user@example.com'
      }
    ]

    roles.value = [
      {
        id: 1,
        name: 'admin',
        description: 'System administrator with full access',
        createdAt: '2025-03-15T05:31:52Z'
      },
      {
        id: 2,
        name: 'merchant',
        description: 'Merchant who can manage their own store and products',
        createdAt: '2025-03-15T05:31:52Z'
      },
      {
        id: 3,
        name: 'user',
        description: 'Regular user who can browse and purchase products',
        createdAt: '2025-03-15T05:31:52Z'
      }
    ]

    userRoles.value = [
      { userId: 1, roleId: 1 },
      { userId: 1, roleId: 3 },
      { userId: 2, roleId: 2 },
      { userId: 2, roleId: 3 },
      { userId: 3, roleId: 3 }
    ]

    loading.value = false
  }, 1000)
})

const openRoleDialog = (user: User) => {
  selectedUser.value = user
  roleDialogVisible.value = true
}

const getUserRoles = (userId: number) => {
  const userRoleIds = userRoles.value
    .filter(ur => ur.userId === userId)
    .map(ur => ur.roleId)
  
  return roles.value.filter(role => userRoleIds.includes(role.id))
}

const hasRole = (userId: number, roleId: number) => {
  return userRoles.value.some(
    ur => ur.userId === userId && ur.roleId === roleId
  )
}

const handleRoleChange = (userId: number, roleId: number, checked: boolean) => {
  if (checked) {
    userRoles.value.push({ userId, roleId })
    ElMessage.success('角色已添加')
  } else {
    userRoles.value = userRoles.value.filter(
      ur => !(ur.userId === userId && ur.roleId === roleId)
    )
    ElMessage.success('角色已移除')
  }
}
</script>

<style scoped>
.user-roles {
  padding: 1rem;
}

.page-header {
  margin-bottom: 1.5rem;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.users-table {
  background-color: white;
}

.role-tag {
  margin-right: 0.5rem;
}

.role-list {
  max-height: 400px;
  overflow-y: auto;
}

.role-item {
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
}

.role-item:last-child {
  border-bottom: none;
}

.role-info {
  margin-left: 0.5rem;
}

.role-name {
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.role-description {
  font-size: 0.9rem;
  color: #666;
}
</style>