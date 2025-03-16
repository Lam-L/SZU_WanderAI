```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

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

const roleDialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const rolePermissionDialogVisible = ref(false)

const selectedRole = ref<Role | null>(null)
const selectedPermission = ref<Permission | null>(null)

const roleForm = ref({
  name: '',
  description: ''
})

const permissionForm = ref({
  name: '',
  description: ''
})

const roleErrors = ref({
  name: '',
  description: ''
})

const permissionErrors = ref({
  name: '',
  description: ''
})

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

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// Role management
const openRoleDialog = (role?: Role) => {
  if (role) {
    selectedRole.value = role
    roleForm.value = {
      name: role.name,
      description: role.description
    }
  } else {
    selectedRole.value = null
    roleForm.value = {
      name: '',
      description: ''
    }
  }
  roleDialogVisible.value = true
}

const validateRoleForm = () => {
  let isValid = true
  roleErrors.value.name = ''
  roleErrors.value.description = ''

  if (!roleForm.value.name.trim()) {
    roleErrors.value.name = '请输入角色名称'
    isValid = false
  }

  if (!roleForm.value.description.trim()) {
    roleErrors.value.description = '请输入角色描述'
    isValid = false
  }

  return isValid
}

const handleRoleSubmit = async () => {
  if (!validateRoleForm()) return

  try {
    if (selectedRole.value) {
      // Update existing role
      const index = roles.value.findIndex(r => r.id === selectedRole.value?.id)
      if (index > -1) {
        roles.value[index] = {
          ...selectedRole.value,
          name: roleForm.value.name,
          description: roleForm.value.description
        }
      }
      ElMessage.success('角色更新成功')
    } else {
      // Create new role
      const newRole: Role = {
        id: roles.value.length + 1,
        name: roleForm.value.name,
        description: roleForm.value.description,
        createdAt: new Date().toISOString()
      }
      roles.value.push(newRole)
      ElMessage.success('角色创建成功')
    }
    roleDialogVisible.value = false
  } catch (error) {
    console.error('Failed to save role:', error)
    ElMessage.error('操作失败，请稍后再试')
  }
}

const handleDeleteRole = async (role: Role) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色 "${role.name}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    roles.value = roles.value.filter(r => r.id !== role.id)
    rolePermissions.value = rolePermissions.value.filter(rp => rp.roleId !== role.id)
    ElMessage.success('角色已删除')
  } catch {
    // User canceled
  }
}

// Permission management
const openPermissionDialog = (permission?: Permission) => {
  if (permission) {
    selectedPermission.value = permission
    permissionForm.value = {
      name: permission.name,
      description: permission.description
    }
  } else {
    selectedPermission.value = null
    permissionForm.value = {
      name: '',
      description: ''
    }
  }
  permissionDialogVisible.value = true
}

const validatePermissionForm = () => {
  let isValid = true
  permissionErrors.value.name = ''
  permissionErrors.value.description = ''

  if (!permissionForm.value.name.trim()) {
    permissionErrors.value.name = '请输入权限名称'
    isValid = false
  }

  if (!permissionForm.value.description.trim()) {
    permissionErrors.value.description = '请输入权限描述'
    isValid = false
  }

  return isValid
}

const handlePermissionSubmit = async () => {
  if (!validatePermissionForm()) return

  try {
    if (selectedPermission.value) {
      // Update existing permission
      const index = permissions.value.findIndex(p => p.id === selectedPermission.value?.id)
      if (index > -1) {
        permissions.value[index] = {
          ...selectedPermission.value,
          name: permissionForm.value.name,
          description: permissionForm.value.description
        }
      }
      ElMessage.success('权限更新成功')
    } else {
      // Create new permission
      const newPermission: Permission = {
        id: permissions.value.length + 1,
        name: permissionForm.value.name,
        description: permissionForm.value.description,
        createdAt: new Date().toISOString()
      }
      permissions.value.push(newPermission)
      ElMessage.success('权限创建成功')
    }
    permissionDialogVisible.value = false
  } catch (error) {
    console.error('Failed to save permission:', error)
    ElMessage.error('操作失败，请稍后再试')
  }
}

const handleDeletePermission = async (permission: Permission) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除权限 "${permission.name}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    permissions.value = permissions.value.filter(p => p.id !== permission.id)
    rolePermissions.value = rolePermissions.value.filter(rp => rp.permissionId !== permission.id)
    ElMessage.success('权限已删除')
  } catch {
    // User canceled
  }
}

// Role permission management
const openRolePermissionDialog = (role: Role) => {
  selectedRole.value = role
  rolePermissionDialogVisible.value = true
}

const getRolePermissions = (roleId: number) => {
  return rolePermissions.value
    .filter(rp => rp.roleId === roleId)
    .map(rp => rp.permissionId)
}

const handlePermissionChange = (roleId: number, permissionId: number, checked: boolean) => {
  if (checked) {
    rolePermissions.value.push({ roleId, permissionId })
  } else {
    rolePermissions.value = rolePermissions.value.filter(
      rp => !(rp.roleId === roleId && rp.permissionId === permissionId)
    )
  }
}
</script>

<template>
  <div class="role-management">
    <div class="page-header">
      <h2>权限管理</h2>
    </div>
    
    <!-- Roles Section -->
    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <h3>角色管理</h3>
          <el-button type="primary" @click="openRoleDialog()">
            新建角色
          </el-button>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="roles"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="name" label="角色名称" width="150" />
        
        <el-table-column prop="description" label="描述" min-width="200" />
        
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="openRolePermissionDialog(row)"
            >
              权限配置
            </el-button>
            
            <el-button
              type="warning"
              size="small"
              @click="openRoleDialog(row)"
            >
              编辑
            </el-button>
            
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteRole(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- Permissions Section -->
    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <h3>权限管理</h3>
          <el-button type="primary" @click="openPermissionDialog()">
            新建权限
          </el-button>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="permissions"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="name" label="权限名称" width="200" />
        
        <el-table-column prop="description" label="描述" min-width="200" />
        
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button
              type="warning"
              size="small"
              @click="openPermissionDialog(row)"
            >
              编辑
            </el-button>
            
            <el-button
              type="danger"
              size="small"
              @click="handleDeletePermission(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- Role Dialog -->
    <el-dialog
      v-model="roleDialogVisible"
      :title="selectedRole ? '编辑角色' : '新建角色'"
      width="500px"
    >
      <div class="dialog-form">
        <div class="form-group">
          <label>角色名称</label>
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
          <div class="field-error" v-if="roleErrors.name">{{ roleErrors.name }}</div>
        </div>
        
        <div class="form-group">
          <label>描述</label>
          <el-input
            v-model="roleForm.description"
            type="textarea"
            rows="3"
            placeholder="请输入角色描述"
          />
          <div class="field-error" v-if="roleErrors.description">{{ roleErrors.description }}</div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRoleSubmit">
            {{ selectedRole ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- Permission Dialog -->
    <el-dialog
      v-model="permissionDialogVisible"
      :title="selectedPermission ? '编辑权限' : '新建权限'"
      width="500px"
    >
      <div class="dialog-form">
        <div class="form-group">
          <label>权限名称</label>
          <el-input v-model="permissionForm.name" placeholder="请输入权限名称" />
          <div class="field-error" v-if="permissionErrors.name">{{ permissionErrors.name }}</div>
        </div>
        
        <div class="form-group">
          <label>描述</label>
          <el-input
            v-model="permissionForm.description"
            type="textarea"
            rows="3"
            placeholder="请输入权限描述"
          />
          <div class="field-error" v-if="permissionErrors.description">{{ permissionErrors.description }}</div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePermissionSubmit">
            {{ selectedPermission ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- Role Permission Dialog -->
    <el-dialog
      v-model="rolePermissionDialogVisible"
      :title="`配置角色权限 - ${selectedRole?.name}`"
      width="600px"
    >
      <div class="permission-list">
        <el-checkbox-group 
          v-if="selectedRole"
          v-model="getRolePermissions(selectedRole.id)"
          @change="(values) => {
            permissions.forEach(permission => {
              const checked = values.includes(permission.id)
              handlePermissionChange(selectedRole.id, permission.id, checked)
            })
          }"
        >
          <div 
            v-for="permission in permissions" 
            :key="permission.id"
            class="permission-item"
          >
            <el-checkbox :label="permission.id">
              <div class="permission-info">
                <div class="permission-name">{{ permission.name }}</div>
                <div class="permission-description">{{ permission.description }}</div>
              </div>
            </el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rolePermissionDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.role-management {
  padding: 1rem;
}

.page-header {
  margin-bottom: 1.5rem;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.section-card {
  margin-bottom: 1.5rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #303133;
}

.dialog-form {
  padding: 1rem 0;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.field-error {
  color: #f56c6c;
  font-size: 0.85rem;
  margin-top: 0.25rem;
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
```