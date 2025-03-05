<script setup lang="ts">
import { ref, onMounted, reactive, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../stores/user';

declare global {
  interface Window {
    AMap: any;
  }
}

const router = useRouter();
const userStore = useUserStore();

const mapContainer = ref<HTMLElement | null>(null);
const amapInstance = ref<any>(null);
const markers = ref<any[]>([]);
const destination = ref('');
const showAiChat = ref(false);
const aiMessages = ref<{ role: 'user' | 'ai', content: string }[]>([]);
const userMessage = ref('');
const isProcessing = ref(false);

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

onBeforeUnmount(() => {
  // Clean up map instance when component is unmounted
  if (amapInstance.value) {
    amapInstance.value.destroy();
  }
});

// Handle map click
const handleMapClick = (e: any) => {
  const lng = e.lnglat.getLng();
  const lat = e.lnglat.getLat();
  
  // Clear existing markers
  clearMarkers();
  
  // Add marker at clicked location
  if (amapInstance.value) {
    const marker = new window.AMap.Marker({
      position: [lng, lat],
      map: amapInstance.value
    });
    markers.value.push(marker);
    
    // Show AI chat with travel suggestions
    showAiChat.value = true;
    
    // Simulate AI response
    aiMessages.value = [
      { role: 'ai', content: `我注意到你对 ${lat.toFixed(4)}, ${lng.toFixed(4)} 附近感兴趣。这个地区有什么我可以帮你规划的吗？` }
    ];
    
    // Simulate adding nearby attractions (in a real app, this would come from an API)
    simulateNearbyAttractions(lat, lng);
  }
};

// Simulate adding nearby attractions
const simulateNearbyAttractions = (lat: number, lng: number) => {
  // In a real app, you would fetch this data from an API
  const attractions = [
    { name: '景点 A', lat: lat + 0.02, lng: lng + 0.03 },
    { name: '景点 B', lat: lat - 0.01, lng: lng + 0.02 },
    { name: '景点 C', lat: lat + 0.03, lng: lng - 0.01 }
  ];
  
  if (amapInstance.value) {
    attractions.forEach(attraction => {
      const marker = new window.AMap.Marker({
        position: [attraction.lng, attraction.lat],
        map: amapInstance.value,
        title: attraction.name
      });
      
      // Add info window
      const infoWindow = new window.AMap.InfoWindow({
        content: `<div>${attraction.name}</div>`,
        offset: new window.AMap.Pixel(0, -30)
      });
      
      // Add click event to show info window
      marker.on('click', () => {
        infoWindow.open(amapInstance.value, marker.getPosition());
      });
      
      markers.value.push(marker);
    });
  }
};

// Clear all markers from the map
const clearMarkers = () => {
  markers.value.forEach(marker => {
    marker.setMap(null);
  });
  markers.value = [];
};

// Handle route planning
const planRoute = () => {
  if (!destination.value) return;
  
  showAiChat.value = true;
  aiMessages.value = [
    { role: 'ai', content: `我正在为你规划前往 ${destination.value} 的路线。你有什么特别的偏好吗？例如，你更喜欢公共交通、自驾还是步行？` }
  ];
  
  // In a real app, you would call a routing API here
  if (amapInstance.value) {
    // Clear existing markers and routes
    clearMarkers();
    
    // Use AMap's Geocoder to find the destination
    const geocoder = new window.AMap.Geocoder();
    geocoder.getLocation(destination.value, (status: string, result: any) => {
      if (status === 'complete' && result.info === 'OK') {
        const destinationLocation = result.geocodes[0].location;
        const currentCenter = amapInstance.value.getCenter();
        
        // Add markers for start and end points
        const startMarker = new window.AMap.Marker({
          position: [currentCenter.lng, currentCenter.lat],
          map: amapInstance.value,
          title: '当前位置'
        });
        markers.value.push(startMarker);
        
        const endMarker = new window.AMap.Marker({
          position: [destinationLocation.lng, destinationLocation.lat],
          map: amapInstance.value,
          title: destination.value
        });
        markers.value.push(endMarker);
        
        // Use AMap's Driving plugin to plan a route
        window.AMap.plugin('AMap.Driving', () => {
          const driving = new window.AMap.Driving({
            map: amapInstance.value,
            panel: false
          });
          
          driving.search(
            [currentCenter.lng, currentCenter.lat],
            [destinationLocation.lng, destinationLocation.lat],
            (status: string, result: any) => {
              if (status === 'complete') {
                // Route planned successfully
                const routes = result.routes;
                if (routes.length > 0) {
                  // Fit the map to show the route
                  amapInstance.value.setFitView();
                }
              } else {
                // Route planning failed
                aiMessages.value.push({
                  role: 'ai',
                  content: `抱歉，我无法规划到 ${destination.value} 的路线。请尝试其他目的地。`
                });
              }
            }
          );
        });
      } else {
        // Geocoding failed
        aiMessages.value.push({
          role: 'ai',
          content: `抱歉，我找不到 ${destination.value} 的位置。请尝试更具体的地址或地点名称。`
        });
      }
    });
  }
};

// Send message to AI
const sendMessage = async () => {
  if (!userMessage.value || isProcessing.value) return;
  
  const message = userMessage.value;
  userMessage.value = '';
  
  // Add user message to chat
  aiMessages.value.push({ role: 'user', content: message });
  
  // Simulate AI processing
  isProcessing.value = true;
  
  // In a real app, you would call your AI API here
  setTimeout(() => {
    // Simulate AI response
    if (message.includes('日本')) {
      aiMessages.value.push({ 
        role: 'ai', 
        content: `日本是一个很棒的旅游目的地！以下是我的建议：
        
1. 东京：
   - 浅草寺
   - 东京塔
   - 涩谷十字路口
   - 秋叶原电器街

2. 京都：
   - 伏见稻荷大社
   - 金阁寺
   - 清水寺
   - 岚山竹林

3. 大阪：
   - 大阪城
   - 道顿堀美食街
   - 环球影城

建议行程：东京(3天) → 京都(2天) → 大阪(2天)

最佳旅游季节：春季(3-5月)樱花季或秋季(9-11月)红叶季。

你对这个计划有什么想法吗？` 
      });
      
      // Simulate adding markers for Japan attractions
      if (amapInstance.value) {
        // Clear existing markers
        clearMarkers();
        
        // Set view to Japan
        amapInstance.value.setCenter([139.6503, 35.6762]);
        amapInstance.value.setZoom(6);
        
        // Add markers for key attractions
        const attractions = [
          { name: '东京 - 浅草寺', lat: 35.7147, lng: 139.7967 },
          { name: '京都 - 伏见稻荷大社', lat: 34.9671, lng: 135.7727 },
          { name: '大阪 - 大阪城', lat: 34.6873, lng: 135.5262 }
        ];
        
        attractions.forEach(attraction => {
          const marker = new window.AMap.Marker({
            position: [attraction.lng, attraction.lat],
            map: amapInstance.value,
            title: attraction.name
          });
          
          // Add info window
          const infoWindow = new window.AMap.InfoWindow({
            content: `<div>${attraction.name}</div>`,
            offset: new window.AMap.Pixel(0, -30)
          });
          
          // Add click event to show info window
          marker.on('click', () => {
            infoWindow.open(amapInstance.value, marker.getPosition());
          });
          
          markers.value.push(marker);
        });
      }
    } else {
      aiMessages.value.push({ 
        role: 'ai', 
        content: `感谢你的消息！我可以帮你规划旅行路线、推荐景点和提供旅行建议。请告诉我你想去的目的地，我会为你提供详细的旅游规划。` 
      });
    }
    
    isProcessing.value = false;
  }, 1500);
};

// Export AI travel plan as image
const exportAsPlan = () => {
  alert('导出功能将在实际应用中实现，需要使用html2canvas等库将对话内容转换为图片');
};

// Navigate to community page
const goToCommunity = () => {
  router.push('/community');
};

// Toggle AI chat panel
const toggleAiChat = () => {
  showAiChat.value = !showAiChat.value;
  if (showAiChat.value && aiMessages.value.length === 0) {
    aiMessages.value = [
      { role: 'ai', content: '你好！我是你的AI旅行助手。我可以帮你规划旅行路线、推荐景点和提供旅行建议。请告诉我你想去哪里？' }
    ];
  }
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
        <router-link to="/profile">{{ userStore.user.userName || '个人中心' }}</router-link>
      </div>
    </header>
    
    <!-- Map Container -->
    <div class="map-container" ref="mapContainer"></div>
    
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
  height: 100%;
  position: relative;
}

.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #2575fc;
}

.user-info a {
  color: #333;
  text-decoration: none;
  font-weight: 500;
}

.user-info a:hover {
  color: #2575fc;
}

.map-container {
  flex: 1;
  width: 100%;
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
  flex: 1;
  max-width: 400px;
}

.destination-input input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
  font-size: 1rem;
}

.destination-input button {
  padding: 0.75rem 1rem;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.community-button, .ai-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.community-button:hover, .ai-button:hover {
  background-color: #f0f0f0;
}

.icon {
  font-size: 1.5rem;
  margin-bottom: 0.25rem;
}

.ai-chat-panel {
  position: fixed;
  top: 0;
  right: -400px;
  width: 380px;
  height: 100%;
  background-color: white;
  box-shadow: -5px 0 15px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  transition: right 0.3s ease;
  z-index: 1000;
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
  color: #2575fc;
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
  padding: 1rem;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message {
  max-width: 80%;
  padding: 0.75rem 1rem;
  border-radius: 1rem;
  line-height: 1.4;
}

.message.user {
  align-self: flex-end;
  background-color: #2575fc;
  color: white;
  border-bottom-right-radius: 0.25rem;
}

.message.ai {
  align-self: flex-start;
  background-color: #f0f0f0;
  color: #333;
  border-bottom-left-radius: 0.25rem;
}

.message.ai.processing {
  background-color: #f0f0f0;
  padding: 1rem;
}

.typing-indicator {
  display: flex;
  gap: 0.25rem;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background-color: #aaa;
  border-radius: 50%;
  animation: typing 1s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: 0s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.chat-input {
  display: flex;
  padding: 1rem;
  border-top: 1px solid #eee;
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
  background-color: #4caf50 !important;
}

.message-content {
  white-space: pre-line;
}
</style>