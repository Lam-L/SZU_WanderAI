<template>
  <div class="permissions-list">
    <div class="page-header">
      <h2>权限管理</h2>
      <el-button type="primary" @click="openPermissionDialog()">
        新建权限
      </el-button>
    </div>

    <el-card class="permissions-table">
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Permission {
  id: number
  name: string
  description: string
  createdAt: string
}

const permissions = ref<Permission[]>([])
const loading = ref(true)
const permissionDialogVisible = ref(false)
const selectedPermission = ref<Permission | null>(null)

const permissionForm = ref({
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
    ElMessage.success('权限已删除')
  } catch {
    // User canceled
  }
}
</script>

<style scoped>
.permissions-list {
  padding: 1rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.permissions-table {
  background-color: white;
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
</style>