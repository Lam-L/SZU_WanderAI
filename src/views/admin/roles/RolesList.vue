<template>
  <div class="roles-list">
    <div class="page-header">
      <h2>角色管理</h2>
      <el-button type="primary" @click="openRoleDialog()">
        新建角色
      </el-button>
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
        
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Role {
  id: number
  name: string
  description: string
  createdAt: string
}

const roles = ref<Role[]>([])
const loading = ref(true)
const roleDialogVisible = ref(false)
const selectedRole = ref<Role | null>(null)

const roleForm = ref({
  name: '',
  description: ''
})

const roleErrors = ref({
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
    ElMessage.success('角色已删除')
  } catch {
    // User canceled
  }
}
</script>

<style scoped>
.roles-list {
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

.roles-table {
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