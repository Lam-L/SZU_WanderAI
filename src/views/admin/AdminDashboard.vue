<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface DashboardStats {
  totalUsers: number
  totalPosts: number
  totalComments: number
  newUsersToday: number
  newPostsToday: number
}

const stats = ref<DashboardStats>({
  totalUsers: 0,
  totalPosts: 0,
  totalComments: 0,
  newUsersToday: 0,
  newPostsToday: 0
})

onMounted(() => {
  // Simulate loading dashboard stats
  stats.value = {
    totalUsers: 1250,
    totalPosts: 3456,
    totalComments: 12890,
    newUsersToday: 25,
    newPostsToday: 128
  }
})
</script>

<template>
  <div class="dashboard">
    <h2>控制台</h2>
    
    <!-- Stats Cards -->
    <div class="stats-grid">
      <el-card class="stats-card">
        <template #header>
          <div class="card-header">
            <span>总用户数</span>
          </div>
        </template>
        <div class="stats-value">{{ stats.totalUsers }}</div>
        <div class="stats-label">今日新增: +{{ stats.newUsersToday }}</div>
      </el-card>
      
      <el-card class="stats-card">
        <template #header>
          <div class="card-header">
            <span>总帖子数</span>
          </div>
        </template>
        <div class="stats-value">{{ stats.totalPosts }}</div>
        <div class="stats-label">今日新增: +{{ stats.newPostsToday }}</div>
      </el-card>
      
      <el-card class="stats-card">
        <template #header>
          <div class="card-header">
            <span>总评论数</span>
          </div>
        </template>
        <div class="stats-value">{{ stats.totalComments }}</div>
      </el-card>
    </div>
    
    <!-- Charts Section -->
    <div class="charts-section">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>用户增长趋势</span>
          </div>
        </template>
        <div class="chart-placeholder">
          图表区域 - 用户增长趋势
        </div>
      </el-card>
      
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>内容发布趋势</span>
          </div>
        </template>
        <div class="chart-placeholder">
          图表区域 - 内容发布趋势
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 1rem;
}

.dashboard h2 {
  margin: 0 0 1.5rem;
  color: #303133;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stats-card {
  background-color: white;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-value {
  font-size: 2rem;
  font-weight: bold;
  color: #303133;
  margin: 1rem 0;
}

.stats-label {
  color: #909399;
  font-size: 0.9rem;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 1rem;
}

.chart-card {
  background-color: white;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
  border-radius: 4px;
}
</style>