<template>
  <div class="forbidden-page">
    <div class="error-container">
      <h1>403</h1>
      <h2>访问被拒绝</h2>
      <p>抱歉，您没有权限访问此页面</p>
      <button @click="goBack">返回上一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

const goBack = () => {
  router.back();
};
</script>

<style scoped>
.forbidden-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.error-container {
  text-align: center;
  padding: 2rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h1 {
  font-size: 4rem;
  color: #e74c3c;
  margin: 0;
}

h2 {
  margin: 1rem 0;
  color: #333;
}

p {
  color: #666;
  margin-bottom: 2rem;
}

button {
  padding: 0.75rem 1.5rem;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

button:hover {
  background-color: #1a65e0;
}
</style>