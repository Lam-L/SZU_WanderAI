<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

declare global {
  interface Window {
    AMap: any;
  }
}

const router = useRouter();
const authStore = useAuthStore();

interface TourPackage {
  id: number;
  title: string;
  description: string;
  price: number;
  originalPrice: number;
  image: string;
  soldCount: number;
  rating: number;
  location: string;
  duration: string;
}

const mapContainer = ref<HTMLElement | null>(null);
const amapInstance = ref<any>(null);
const markers = ref<any[]>([]);
const destination = ref('');
const showAiChat = ref(false);
const aiMessages = ref<{ role: 'user' | 'ai', content: string }[]>([]);
const userMessage = ref('');
const isProcessing = ref(false);
const showTourPanel = ref(true);

const tourPackages = ref<TourPackage[]>([
  {
    id: 1,
    title: '日本东京+大阪6日游',
    description: '东京、大阪精华景点，含机票酒店',
    price: 5999,
    originalPrice: 7999,
    image: 'https://images.unsplash.com/photo-1536098561742-ca998e48cbcc?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
    soldCount: 328,
    rating: 4.8,
    location: '东京/大阪',
    duration: '6天5晚'
  },
  {
    id: 2,
    title: '泰国清迈文艺小城之旅',
    description: '清迈古城+夜市+拜县深度游',
    price: 3999,
    originalPrice: 4999,
    image: 'https://images.unsplash.com/photo-1504214208698-ea446addeb7e?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
    soldCount: 156,
    rating: 4.9,
    location: '清迈',
    duration: '5天4晚'
  },
  {
    id: 3,
    title: '巴厘岛浪漫度假套餐',
    description: '私人沙滩+SPA+美食之旅',
    price: 6999,
    originalPrice: 8999,
    image: 'https://images.unsplash.com/photo-1537996194471-e657df975ab4?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
    soldCount: 89,
    rating: 4.7,
    location: '巴厘岛',
    duration: '7天6晚'
  },
  {
    id: 4,
    title: '新加坡亲子欢乐游',
    description: '环球影城+海洋馆+动物园',
    price: 4999,
    originalPrice: 5999,
    image: 'https://images.unsplash.com/photo-1565967511849-76a60a516170?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
    soldCount: 246,
    rating: 4.9,
    location: '新加坡',
    duration: '5天4晚'
  },
  {
    id: 5,
    title: '马尔代夫奢华度假',
    description: '水上别墅+私人管家+浮潜',
    price: 12999,
    originalPrice: 15999,
    image: 'https://images.unsplash.com/photo-1573843981267-be1999ff37cd?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
    soldCount: 78,
    rating: 4.9,
    location: '马尔代夫',
    duration: '6天5晚'
  },
  {
    id: 6,
    title: '韩国首尔深度游',
    description: '明洞+南山塔+济州岛',
    price: 4599,
    originalPrice: 5599,
    image: 'https://images.unsplash.com/photo-1517154421773-0529f29ea451?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
    soldCount: 189,
    rating: 4.8,
    location: '首尔',
    duration: '5天4晚'
  }
]);

// Initialize map
onMounted(() => {
  if (mapContainer.value) {
    // Create AMap instance
    amapInstance.value = new window.AMap.Map(mapContainer.value, {
      resizeEnable: true,
      zoom: 11,
      center: [116.397428, 39.90923] // Beijing by default
    });
    
    // Add click event to map
    amapInstance.value.on('click', handleMapClick);
  }
});

const handleMapClick = (e: any) => {
  const [lng, lat] = e.lnglat.toArray();
  // Handle map click event
  console.log('Map clicked at:', lng, lat);
};

const planRoute = () => {
  if (!destination.value.trim()) return;
  // Handle route planning
  console.log('Planning route to:', destination.value);
};

const goToCommunity = () => {
  router.push('/community');
};

const toggleAiChat = () => {
  showAiChat.value = !showAiChat.value;
  if (showAiChat.value && aiMessages.value.length === 0) {
    // Add welcome message
    aiMessages.value.push({
      role: 'ai',
      content: '你好！我是你的AI旅行助手。请告诉我你想去哪里，我会为你提供旅行建议和规划。'
    });
  }
};

const sendMessage = async () => {
  if (!userMessage.value.trim() || isProcessing.value) return;
  
  const message = userMessage.value;
  userMessage.value = '';
  
  // Add user message
  aiMessages.value.push({
    role: 'user',
    content: message
  });
  
  isProcessing.value = true;
  
  try {
    // Simulate AI response
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    aiMessages.value.push({
      role: 'ai',
      content: `我理解你想了解关于"${message}"的信息。这是一个很好的选择！让我为你提供一些建议...`
    });
  } catch (error) {
    console.error('Failed to get AI response:', error);
  } finally {
    isProcessing.value = false;
  }
};

const exportAsPlan = () => {
  // Handle exporting chat as a travel plan
  console.log('Exporting chat as travel plan');
};

// Toggle tour panel
const toggleTourPanel = () => {
  showTourPanel.value = !showTourPanel.value;
};

// View tour package details
const viewTourPackage = (packageId: number) => {
  // In a real app, this would navigate to the package detail page
  console.log('Viewing tour package:', packageId);
};

const switchRole = (role: string) => {
  authStore.switchRole(role);
};

const logout = () => {
  authStore.logout();
};
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
              
              <!-- Dynamic role switching options -->
              <template v-if="authStore.roles.length > 1">
                <el-dropdown-item 
                  v-for="role in authStore.roles" 
                  :key="role"
                  v-if="role !== authStore.activeRole"
                  divided
                  @click="switchRole(role)"
                >
                  切换到{{ role === 'admin' ? '管理员' : role === 'merchant' ? '商家' : '用户' }}
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
    
    <div class="main-content">
      <!-- Tour Packages Panel -->
      <div class="tour-panel" :class="{ 'collapsed': !showTourPanel }">
        <div class="tour-panel-header" @click="toggleTourPanel">
          <h2>精选团购</h2>
          <span class="toggle-icon">{{ showTourPanel ? '←' : '→' }}</span>
        </div>
        
        <div class="tour-packages" v-if="showTourPanel">
          <div 
            v-for="pkg in tourPackages" 
            :key="pkg.id" 
            class="tour-package"
            @click="viewTourPackage(pkg.id)"
          >
            <div class="package-image">
              <img :src="pkg.image" :alt="pkg.title">
            </div>
            <div class="package-info">
              <h3>{{ pkg.title }}</h3>
              <p class="package-description">{{ pkg.description }}</p>
              <div class="package-meta">
                <span class="package-location">{{ pkg.location }}</span>
                <span class="package-duration">{{ pkg.duration }}</span>
              </div>
              <div class="package-price">
                <span class="current-price">¥{{ pkg.price }}</span>
                <span class="original-price">¥{{ pkg.originalPrice }}</span>
              </div>
              <div class="package-stats">
                <span class="sold-count">已售 {{ pkg.soldCount }}</span>
                <span class="rating">{{ pkg.rating }}分</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Map Container -->
      <div class="map-container" ref="mapContainer"></div>
    </div>
    
    <!-- Footer Controls -->
    <div class="home-footer">
      <div class="destination-input">
        <input 
          type="text" 
          v-model="destination" 
          placeholder="输入目的地..." 
          @keyup.enter="planRoute"
        />
        <button @click="planRoute">规划路线</button>
      </div>
      
      <div class="community-button" @click="goToCommunity">
        <span class="icon">🌍</span>
        <span>广场</span>
      </div>
      
      <div class="ai-button" @click="toggleAiChat">
        <span class="icon">🤖</span>
        <span>AI助手</span>
      </div>
    </div>
    
    <!-- AI Chat Panel -->
    <div class="ai-chat-panel" :class="{ 'show': showAiChat }">
      <div class="chat-header">
        <h3>AI旅行助手</h3>
        <button class="close-button" @click="toggleAiChat">×</button>
      </div>
      
      <div class="chat-messages">
        <div 
          v-for="(message, index) in aiMessages" 
          :key="index" 
          class="message" 
          :class="message.role"
        >
          <div class="message-content">{{ message.content }}</div>
        </div>
        
        <div v-if="isProcessing" class="message ai processing">
          <div class="typing-indicator">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
      
      <div class="chat-input">
        <input 
          type="text" 
          v-model="userMessage" 
          placeholder="输入你的问题..." 
          @keyup.enter="sendMessage"
        />
        <button @click="sendMessage" :disabled="isProcessing">发送</button>
        <button class="export-button" @click="exportAsPlan" :disabled="aiMessages.length <= 1">
          导出规划
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 10;
  height: 64px;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #2575fc;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-info-text {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  padding: 0.5rem;
  color: #333;
  transition: color 0.3s ease;
}

.user-info-text:hover {
  color: #2575fc;
}

.main-content {
  flex: 1;
  display: flex;
  position: relative;
  overflow: hidden;
}

.tour-panel {
  width: 320px;
  background-color: white;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  z-index: 5;
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.tour-panel.collapsed {
  transform: translateX(-290px);
}

.tour-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  background-color: white;
}

.tour-panel-header h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #333;
}

.toggle-icon {
  font-size: 1.25rem;
  color: #666;
}

.tour-packages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
}

.tour-package {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1rem;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  background-color: white;
}

.tour-package:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.package-image {
  height: 160px;
  overflow: hidden;
}

.package-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.package-info {
  padding: 1rem;
}

.package-info h3 {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
  color: #333;
}

.package-description {
  margin: 0 0 0.5rem;
  font-size: 0.9rem;
  color: #666;
  line-height: 1.4;
}

.package-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  color: #666;
}

.package-price {
  margin-bottom: 0.5rem;
}

.current-price {
  font-size: 1.25rem;
  font-weight: bold;
  color: #f56c6c;
  margin-right: 0.5rem;
}

.original-price {
  font-size: 0.9rem;
  color: #999;
  text-decoration: line-through;
}

.package-stats {
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
  color: #666;
}

.map-container {
  flex: 1;
  height: 100%;
}

.home-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.destination-input {
  display: flex;
  gap: 1rem;
  flex: 1;
  max-width: 500px;
}

.destination-input input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.destination-input button {
  padding: 0.75rem 1.5rem;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.community-button,
.ai-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background-color: #f5f5f5;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 1rem;
}

.icon {
  font-size: 1.25rem;
}

.ai-chat-panel {
  position: fixed;
  right: -400px;
  top: 0;
  width: 400px;
  height: 100vh;
  background-color: white;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  transition: right 0.3s ease;
  z-index: 100;
}

.ai-chat-panel.show {
  right: 0;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.chat-header h3 {
  margin: 0;
  color: #333;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message {
  max-width: 80%;
  padding: 0.75rem 1rem;
  border-radius: 8px;
}

.message.user {
  align-self: flex-end;
  background-color: #2575fc;
  color: white;
}

.message.ai {
  align-self: flex-start;
  background-color: #f5f5f5;
  color: #333;
}

.typing-indicator {
  display: flex;
  gap: 0.5rem;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background-color: #666;
  border-radius: 50%;
  animation: typing 1s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.chat-input {
  padding: 1rem;
  border-top: 1px solid #eee;
  display: flex;
  gap: 0.5rem;
}

.chat-input input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.chat-input button {
  padding: 0.75rem 1rem;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.chat-input button:disabled {
  background-color: #a0c0f0;
  cursor: not-allowed;
}

.export-button {
  background-color: #67c23a !important;
}

.export-button:disabled {
  background-color: #a3e2a3 !important;
}
</style>