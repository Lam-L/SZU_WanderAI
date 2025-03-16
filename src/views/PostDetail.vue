<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const postId = route.params.id;

interface Comment {
  id: number;
  content: string;
  author: {
    id: number;
    userName: string;
  };
  createdAt: string;
}

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
  comments: Comment[];
}

const post = ref<Post | null>(null);
const isLoading = ref(true);
const newComment = ref('');
const isSubmitting = ref(false);

onMounted(async () => {
  // In a real app, you would fetch post details from an API
  // For now, we'll use mock data
  setTimeout(() => {
    if (postId === '1') {
      post.value = {
        id: 1,
        title: '日本东京三日游记',
        content: `这次去东京的旅行真是太棒了！我们参观了浅草寺、东京塔和涩谷十字路口。

东京是一个充满活力的城市，现代与传统完美融合。第一天，我们参观了浅草寺，这是东京最古老的寺庙之一。寺庙前的雷门非常壮观，仲见世商店街上有各种传统小吃和纪念品。

第二天，我们去了东京塔，从观景台可以俯瞰整个东京市区，景色令人惊叹。晚上，我们在涩谷十字路口体验了世界上最繁忙的人行横道，周围的霓虹灯和人群让人感受到东京的活力。

第三天，我们去了秋叶原电器街，这里是动漫和电子产品的天堂。各种各样的电子产品和动漫周边让人目不暇接。

总的来说，东京是一个值得多次造访的城市，下次我想去京都和大阪看看！`,
        images: [
          'https://images.unsplash.com/photo-1536098561742-ca998e48cbcc?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80',
          'https://images.unsplash.com/photo-1526481280693-3bfa7568e0f3?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80',
          'https://images.unsplash.com/photo-1570521462033-3015e76e7432?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80'
        ],
        author: {
          id: 1,
          userName: '旅行达人'
        },
        createdAt: '2025-05-15T10:30:00Z',
        likes: 24,
        comments: [
          {
            id: 1,
            content: '照片拍得真好！请问你用的是什么相机？',
            author: {
              id: 2,
              userName: '摄影爱好者'
            },
            createdAt: '2025-05-15T14:20:00Z'
          },
          {
            id: 2,
            content: '东京真的很棒，我也去过浅草寺，那里的氛围很特别。',
            author: {
              id: 3,
              userName: '环球旅行者'
            },
            createdAt: '2025-05-16T09:15:00Z'
          },
          {
            id: 3,
            content: '请问你在东京住的是哪家酒店？推荐一下吧！',
            author: {
              id: 4,
              userName: '旅游规划师'
            },
            createdAt: '2025-05-16T16:45:00Z'
          }
        ]
      };
    } else if (postId === '2') {
      post.value = {
        id: 2,
        title: '巴黎浪漫之旅',
        content: `埃菲尔铁塔、卢浮宫、凯旋门，巴黎的每一处都充满了浪漫...

巴黎被称为"光之城"，这里的建筑、艺术和美食都让人流连忘返。我们在埃菲尔铁塔前野餐，在卢浮宫欣赏了《蒙娜丽莎》，还在塞纳河上乘船游览了巴黎的夜景。

巴黎的咖啡馆文化也很有特色，我们在圣日耳曼德佩区的咖啡馆里度过了一个悠闲的下午，观察路人，品尝正宗的法式甜点。

如果你计划去巴黎，一定要留出足够的时间，慢慢体验这座城市的魅力。`,
        images: [
          'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80',
          'https://images.unsplash.com/photo-1499856871958-5b9627545d1a?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80'
        ],
        author: {
          id: 2,
          userName: '摄影爱好者'
        },
        createdAt: '2025-05-10T14:20:00Z',
        likes: 36,
        comments: [
          {
            id: 4,
            content: '巴黎真的很浪漫，你的照片拍得太美了！',
            author: {
              id: 1,
              userName: '旅行达人'
            },
            createdAt: '2025-05-10T16:30:00Z'
          },
          {
            id: 5,
            content: '请问你是几月份去的巴黎？天气怎么样？',
            author: {
              id: 5,
              userName: '季节旅行者'
            },
            createdAt: '2025-05-11T10:45:00Z'
          }
        ]
      };
    } else {
      // Default mock data for other IDs
      post.value = {
        id: Number(postId),
        title: '旅行游记',
        content: '这是一篇旅行游记的详细内容...',
        images: [
          'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80'
        ],
        author: {
          id: 1,
          userName: '旅行者'
        },
        createdAt: '2025-05-01T10:00:00Z',
        likes: 10,
        comments: []
      };
    }
    
    isLoading.value = false;
  }, 1000);
});

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const likePost = () => {
  if (post.value) {
    post.value.likes++;
  }
};

const submitComment = () => {
  if (!newComment.value.trim() || !post.value || isSubmitting.value) return;
  
  isSubmitting.value = true;
  
  // In a real app, you would send the comment to an API
  setTimeout(() => {
    if (post.value) {
      const newCommentObj: Comment = {
        id: post.value.comments.length + 1,
        content: newComment.value,
        author: {
          id: 999, // Assuming current user ID
          userName: '当前用户' // Assuming current user name
        },
        createdAt: new Date().toISOString()
      };
      
      post.value.comments.push(newCommentObj);
      newComment.value = '';
    }
    
    isSubmitting.value = false;
  }, 500);
};

const goBack = () => {
  router.back();
};
</script>

<template>
  <div class="post-detail-container">
    <!-- Header -->
    <header class="post-detail-header">
      <div class="back-button" @click="goBack">
        <span>← 返回</span>
      </div>
      <h1>帖子详情</h1>
      <div class="placeholder"></div>
    </header>
    
    <!-- Post Content -->
    <div class="post-detail-content">
      <div v-if="isLoading" class="loading">
        <p>加载中...</p>
      </div>
      
      <div v-else-if="!post" class="not-found">
        <p>帖子不存在或已被删除</p>
      </div>
      
      <div v-else class="post-detail">
        <div class="post-header">
          <h2 class="post-title">{{ post.title }}</h2>
          <div class="post-meta">
            <span class="post-author">{{ post.author.userName }}</span>
            <span class="post-date">{{ formatDate(post.createdAt) }}</span>
          </div>
        </div>
        
        <div class="post-images" v-if="post.images.length > 0">
          <div class="image-gallery">
            <img 
              v-for="(image, index) in post.images" 
              :key="index" 
              :src="image" 
              :alt="`${post.title} - 图片 ${index + 1}`" 
              class="gallery-image"
            />
          </div>
        </div>
        
        <div class="post-body">
          <p class="post-content">{{ post.content }}</p>
        </div>
        
        <div class="post-actions">
          <div class="action-button" @click="likePost">
            <span class="action-icon">❤️</span>
            <span class="action-count">{{ post.likes }}</span>
          </div>
          <div class="action-button">
            <span class="action-icon">💬</span>
            <span class="action-count">{{ post.comments.length }}</span>
          </div>
        </div>
        
        <div class="post-comments">
          <h3>评论 ({{ post.comments.length }})</h3>
          
          <div class="comment-form">
            <textarea 
              v-model="newComment" 
              placeholder="写下你的评论..." 
              rows="3"
            ></textarea>
            <button 
              @click="submitComment" 
              :disabled="!newComment.trim() || isSubmitting"
            >
              {{ isSubmitting ? '提交中...' : '发表评论' }}
            </button>
          </div>
          
          <div class="comments-list" v-if="post.comments.length > 0">
            <div 
              v-for="comment in post.comments" 
              :key="comment.id" 
              class="comment-item"
            >
              <div class="comment-header">
                <span class="comment-author">{{ comment.author.userName }}</span>
                <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
            </div>
          </div>
          
          <div class="no-comments" v-else>
            <p>暂无评论，快来发表第一条评论吧！</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.post-detail-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f5f5;
}

.post-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.post-detail-header h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #2575fc;
}

.back-button {
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.back-button:hover {
  background-color: #f0f0f0;
}

.placeholder {
  width: 80px;
}

.post-detail-content {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
}

.loading, .not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.post-detail {
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
  font-size: 1.5rem;
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

.image-gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 0 1.5rem;
}

.gallery-image {
  width: calc(33.333% - 6px);
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.post-body {
  padding: 1.5rem;
}

.post-content {
  margin: 0;
  line-height: 1.6;
  white-space: pre-line;
}

.post-actions {
  display: flex;
  gap: 1.5rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #f0f0f0;
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

.post-comments {
  padding: 1.5rem;
  border-top: 1px solid #f0f0f0;
}

.post-comments h3 {
  margin: 0 0 1rem;
  font-size: 1.25rem;
}

.comment-form {
  margin-bottom: 2rem;
}

.comment-form textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  resize: vertical;
  margin-bottom: 1rem;
  font-family: inherit;
}

.comment-form button {
  padding: 0.75rem 1.5rem;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.comment-form button:disabled {
  background-color: #a0c0f0;
  cursor: not-allowed;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.comment-item {
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.comment-author {
  font-weight: 500;
}

.comment-date {
  color: #666;
  font-size: 0.9rem;
}

.comment-content {
  margin: 0;
  line-height: 1.5;
}

.no-comments {
  text-align: center;
  color: #666;
  padding: 2rem 0;
}
</style>