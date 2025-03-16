<template>
  <div class="role-permissions">
    <div class="page-header">
      <h2>角色权限分配</h2>
    </div>

    <el-card class="roles-table">
      <el-table
        v-loading="loading"
        :data="roles"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="name" label="角色名称" width="150" />
        
        <el-table-column prop="description" label="描述" min-width="200" />
        
        <el-table-column label="当前权限" min-width="200">
          <template #default="{ row }">
            <el-tag 
              v-for="permission in getPermissionsForRole(row.id)"
              :key="permission.id"
              class="permission-tag"
            >
              {{ permission.name }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="openPermissionDialog(row)"
            >
              配置权限
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Role Permission Dialog -->
    <el-dialog
      v-model="permissionDialogVisible"
      :title="`配置角色权限 - ${selectedRole?.name}`"
      width="600px"
    >
      <div class="permission-list">
        <template v-if="selectedRole">
          <div 
            v-for="permission in permissions" 
            :key="permission.id"
            class="permission-item"
          >
            <el-checkbox 
              v-model="selectedPermissions"
              :label="permission.id"
            >
              <div class="permission-info">
                <div class="permission-name">{{ permission.name }}</div>
                <div class="permission-description">{{ permission.description }}</div>
              </div>
            </el-checkbox>
          </div>
        </template>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closePermissionDialog">关闭</el-button>
          <el-button type="primary" @click="savePermissions">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

interface Role {
  id: number
  name: string
  description: string
  createdAt: string
}

interface Permission {
  id: number
  name: string
  description: string
  createdAt: string
}

interface RolePermission {
  roleId: number
  permissionId: number
}

const roles = ref<Role[]>([])
const permissions = ref<Permission[]>([])
const rolePermissions = ref<RolePermission[]>([])
const loading = ref(true)
const permissionDialogVisible = ref(false)
const selectedRole = ref<Role | null>(null)
const selectedPermissions = ref<number[]>([])

onMounted(async () => {
  // Simulate loading data
  setTimeout(() => {
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

    permissions.value = [
      {
        id: 1,
        name: 'manage_users',
        description: 'Can manage all users',
        createdAt: '2025-03-15T05:31:52Z'
      },
      {
        id: 2,
        name: 'manage_merchants',
        description: 'Can manage merchant accounts',
        createdAt: '2025-03-15T05:31:52Z'
      },
      {
        id: 3,
        name: 'manage_roles',
        description: 'Can manage roles and permissions',
        createdAt: '2025-03-15T05:31:52Z'
      }
    ]

    rolePermissions.value = [
      { roleId: 1, permissionId: 1 },
      { roleId: 1, permissionId: 2 },
      { roleId: 1, permissionId: 3 }
    ]

    loading.value = false
  }, 1000)
})

const getPermissionsForRole = (roleId: number) => {
  const permissionIds = rolePermissions.value
    .filter(rp => rp.roleId === roleId)
    .map(rp => rp.permissionId)
  
  return permissions.value.filter(permission => permissionIds.includes(permission.id))
}

const openPermissionDialog = (role: Role) => {
  selectedRole.value = role
  selectedPermissions.value = rolePermissions.value
    .filter(rp => rp.roleId === role.id)
    .map(rp => rp.permissionId)
  permissionDialogVisible.value = true
}

const closePermissionDialog = () => {
  selectedRole.value = null
  selectedPermissions.value = []
  permissionDialogVisible.value = false
}

const savePermissions = () => {
  if (!selectedRole.value) return

  // Remove all existing permissions for this role
  rolePermissions.value = rolePermissions.value.filter(
    rp => rp.roleId !== selectedRole.value!.id
  )

  // Add new permissions
  selectedPermissions.value.forEach(permissionId => {
    rolePermissions.value.push({
      roleId: selectedRole.value!.id,
      permissionId
    })
  })

  ElMessage.success('权限配置已保存')
  closePermissionDialog()
}
</script>

<style scoped>
.role-permissions {
  padding: 1rem;
}

.page-header {
  margin-bottom: 1.5rem;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.roles-table {
  background-color: white;
}

.permission-tag {
  margin-right: 0.5rem;
  margin-bottom: 0.5rem;
}

.permission-list {
  max-height: 400px;
  overflow-y: auto;
}

.permission-item {
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
}

.permission-item:last-child {
  border-bottom: none;
}

.permission-info {
  margin-left: 0.5rem;
}

.permission-name {
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.permission-description {
  font-size: 0.9rem;
  color: #666;
}
</style>