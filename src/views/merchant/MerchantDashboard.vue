<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface DashboardStats {
  totalPackages: number
  totalSales: number
  totalOrders: number
  newOrdersToday: number
  salesToday: number
}

const stats = ref<DashboardStats>({
  totalPackages: 0,
  totalSales: 0,
  totalOrders: 0,
  newOrdersToday: 0,
  salesToday: 0
})

onMounted(() => {
  // Simulate loading dashboard stats
  stats.value = {
    totalPackages: 12,
    totalSales: 156800,
    totalOrders: 328,
    newOrdersToday: 15,
    salesToday: 12600
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
            <span>总团购数</span>
          </div>
        </template>
        <div class="stats-value">{{ stats.totalPackages }}</div>
      </el-card>
      
      <el-card class="stats-card">
        <template #header>
          <div class="card-header">
            <span>总销售额</span>
          </div>
        </template>
        <div class="stats-value">¥{{ stats.totalSales.toLocaleString() }}</div>
        <div class="stats-label">今日: ¥{{ stats.salesToday.toLocaleString() }}</div>
      </el-card>
      
      <el-card class="stats-card">
        <template #header>
          <div class="card-header">
            <span>总订单数</span>
          </div>
        </template>
        <div class="stats-value">{{ stats.totalOrders }}</div>
        <div class="stats-label">今日新增: +{{ stats.newOrdersToday }}</div>
      </el-card>
    </div>
    
    <!-- Charts Section -->
    <div class="charts-section">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>销售趋势</span>
          </div>
        </template>
        <div class="chart-placeholder">
          图表区域 - 销售趋势
        </div>
      </el-card>
      
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>订单趋势</span>
          </div>
        </template>
        <div class="chart-placeholder">
          图表区域 - 订单趋势
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