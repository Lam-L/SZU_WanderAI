<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

interface Post {
  id: number;
  title: string;
  content: string;
  images: string[];
  author: {
    id: number;
    userName: string;
  };
  createdAt: string;
  likes: number;
  comments: number;
}

const posts = ref<Post[]>([]);
const isLoading = ref(true);

onMounted(async () => {
  // In a real app, you would fetch posts from an API
  // For now, we'll use mock data
  setTimeout(() => {
    posts.value = [
      {
        id: 1,
        title: '日本东京三日游记',
        content: '这次去东京的旅行真是太棒了！我们参观了浅草寺、东京塔和涩谷十字路口...',
        images: [
          'https://images.unsplash.com/photo-1536098561742-ca998e48cbcc?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
          'https://images.unsplash.com/photo-1526481280693-3bfa7568e0f3?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60'
        ],
        author: {
          id: 1,
          userName: '旅行达人'
        },
        createdAt: '2025-05-15T10:30:00Z',
        likes: 24,
        comments: 5
      },
      {
        id: 2,
        title: '巴黎浪漫之旅',
        content: '埃菲尔铁塔、卢浮宫、凯旋门，巴黎的每一处都充满了浪漫...',
        images: [
          'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60'
        ],
        author: {
          id: 2,
          userName: '摄影爱好者'
        },
        createdAt: '2025-05-10T14:20:00Z',
        likes: 36,
        comments: 8
      },
      {
        id: 3,
        title: '泰国清迈美食之旅',
        content: '清迈的美食真是让人难以忘怀，特别是那些街边小吃...',
        images: [
          'https://images.unsplash.com/photo-1504674900247-0877df9cc836?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
          'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
          'https://images.unsplash.com/photo-1476224203421-9ac39bcb3327?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60'
        ],
        author: {
          id: 3,
          userName: '美食探险家'
        },
        createdAt: '2025-05-05T09:15:00Z',
        likes: 42,
        comments: 12
      }
    ];
    isLoading.value = false;
  }, 1000);
});

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

const goToPostDetail = (postId: number) => {
  router.push(`/post/${postId}`);
};

const goToCreatePost = () => {
  router.push('/create-post');
};

const likePost = (postId: number) => {
  const post = posts.value.find(p => p.id === postId);
  if (post) {
    post.likes++;
  }
};

const goToHome = () => {
  router.push('/home');
};
</script>

<template>
  <div class="community-container">
    <!-- Header -->
    <header class="community-header">
      <div class="back-button" @click="goToHome">
        <span>← 返回地图</span>
      </div>
      <h1>旅游广场</h1>
      <div class="create-post-button" @click="goToCreatePost">
        <span>✏️ 发帖</span>
      </div>
    </header>
    
    <!-- Posts List -->
    <div class="posts-container">
      <div v-if="isLoading" class="loading">
        <p>加载中...</p>
      </div>
      
      <div v-else-if="posts.length === 0" class="no-posts">
        <p>暂无帖子</p>
      </div>
      
      <div v-else class="posts-list">
        <div 
          v-for="post in posts" 
          :key="post.id" 
          class="post-card"
        >
          <div class="post-header">
            <h2 class="post-title" @click="goToPostDetail(post.id)">{{ post.title }}</h2>
            <div class="post-meta">
              <span class="post-author">{{ post.author.userName }}</span>
              <span class="post-date">{{ formatDate(post.createdAt) }}</span>
            </div>
          </div>
          
          <div class="post-images" v-if="post.images.length > 0">
            <div class="image-grid" :class="`grid-${Math.min(post.images.length, 3)}`">
              <div 
                v-for="(image, index) in post.images.slice(0, 3)" 
                :key="index" 
                class="post-image"
                @click="goToPostDetail(post.id)"
              >
                <img :src="image" :alt="`${post.title} - 图片 ${index + 1}`" />
              </div>
            </div>
          </div>
          
          <div class="post-footer">
            <div class="post-actions">
              <div class="action-button" @click="likePost(post.id)">
                <span class="action-icon">❤️</span>
                <span class="action-count">{{ post.likes }}</span>
              </div>
              <div class="action-button" @click="goToPostDetail(post.id)">
                <span class="action-icon">💬</span>
                <span class="action-count">{{ post.comments }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.community-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f5f5;
}

.community-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.community-header h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #2575fc;
}

.back-button, .create-post-button {
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.back-button:hover, .create-post-button:hover {
  background-color: #f0f0f0;
}

.create-post-button {
  color: #2575fc;
  font-weight: 500;
}

.posts-container {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
}

.loading, .no-posts {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.post-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.post-header {
  padding: 1.5rem 1.5rem 1rem;
}

.post-title {
  margin: 0 0 0.5rem;
  font-size: 1.25rem;
  cursor: pointer;
}

.post-title:hover {
  color: #2575fc;
}

.post-meta {
  display: flex;
  gap: 1rem;
  color: #666;
  font-size: 0.9rem;
}

.post-images {
  width: 100%;
}

.image-grid {
  display: grid;
  gap: 4px;
  height: 200px;
}

.grid-1 {
  grid-template-columns: 1fr;
}

.grid-2 {
  grid-template-columns: 1fr 1fr;
}

.grid-3 {
  grid-template-columns: 1fr 1fr 1fr;
}

.post-image {
  height: 100%;
  overflow: hidden;
  cursor: pointer;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.post-image img:hover {
  transform: scale(1.05);
}

.post-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #f0f0f0;
}

.post-actions {
  display: flex;
  gap: 1.5rem;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.action-icon {
  font-size: 1.25rem;
}

.action-count {
  color: #666;
}
</style>