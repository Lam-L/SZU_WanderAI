// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import type { RouteLocationNormalized, NavigationGuardNext } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'landing',
      component: () => import('../views/LandingPage.vue'),
      meta: { requiresGuest: true }
    },
    // Admin routes
    {
      path: '/admin',
      component: () => import('../views/admin/AdminLayout.vue'),
      meta: { 
        requiresAuth: true,
        roles: ['ADMIN']
      },
      children: [
        {
          path: '',
          name: 'admin-dashboard',
          component: () => import('../views/admin/AdminDashboard.vue')
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('../views/admin/AdminUsers.vue')
        },
        {
          path: 'merchants',
          name: 'admin-merchants',
          component: () => import('../views/admin/AdminMerchants.vue')
        },
        {
          path: 'packages',
          name: 'admin-packages',
          component: () => import('../views/admin/AdminPackages.vue')
        },
        {
          path: 'posts',
          name: 'admin-posts',
          component: () => import('../views/admin/AdminPosts.vue')
        },
        {
          path: 'roles',
          children: [
            {
              path: '',
              name: 'admin-roles',
              redirect: { name: 'admin-roles-list' }
            },
            {
              path: 'list',
              name: 'admin-roles-list',
              component: () => import('../views/admin/roles/RolesList.vue')
            },
            {
              path: 'permissions',
              name: 'admin-permissions',
              component: () => import('../views/admin/roles/Permissions.vue')
            },
            {
              path: 'role-permissions',
              name: 'admin-role-permissions',
              component: () => import('../views/admin/roles/RolePermissions.vue')
            },
            {
              path: 'user-roles',
              name: 'admin-user-roles',
              component: () => import('../views/admin/roles/UserRoles.vue')
            }
          ]
        }
      ]
    },
    // Merchant routes
    {
      path: '/merchant',
      component: () => import('../views/merchant/MerchantLayout.vue'),
      meta: { 
        requiresAuth: true,
        roles: ['MERCHANT']
      },
      children: [
        {
          path: '',
          name: 'merchant-dashboard',
          component: () => import('../views/merchant/MerchantDashboard.vue')
        },
        {
          path: 'packages',
          name: 'merchant-packages',
          component: () => import('../views/merchant/MerchantPackages.vue')
        },
        {
          path: 'packages/create',
          name: 'create-package',
          component: () => import('../views/merchant/CreatePackage.vue')
        }
      ]
    },
    // User routes
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/HomePage.vue'),
      meta: { 
        requiresAuth: true
      }
    },
    {
      path: '/community',
      name: 'community',
      component: () => import('../views/CommunityPage.vue'),
      meta: { 
        requiresAuth: true
      }
    },
    {
      path: '/create-post',
      name: 'create-post',
      component: () => import('../views/CreatePost.vue'),
      meta: { 
        requiresAuth: true
      }
    },
    {
      path: '/post/:id',
      name: 'post-detail',
      component: () => import('../views/PostDetail.vue'),
      meta: { 
        requiresAuth: true
      }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/UserProfile.vue'),
      meta: { 
        requiresAuth: true
      }
    },
    // Error pages
    {
      path: '/403',
      name: 'forbidden',
      component: () => import('../views/errors/403.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ]
});

// Navigation guard
router.beforeEach(async (to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
  const authStore = useAuthStore();
   // 等待 Store 初始化完成
  // if (!authStore.isReady) {
  //   await new Promise(resolve => {
  //     const interval = setInterval(() => {
  //       if (authStore.isReady) {
  //         clearInterval(interval);
  //         resolve(null);
  //       }
  //     }, 100);
  //   });
  // }

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest);
  const requiredRoles = to.meta.roles as string[] | undefined;

  // 如果用户已登录但访问游客页面（如登录页）
  if (requiresGuest && authStore.isAuthenticated) {
    return next(authStore.homePath);
  }

  // 如果路由需要认证但用户未登录
  if (requiresAuth && !authStore.isAuthenticated) {
    return next('/');
  }

  // 如果路由需要特定角色
  if (requiresAuth && requiredRoles && requiredRoles.length > 0) {
    // 检查用户是否有任何所需角色
    console.log(authStore.activeRole)
    console.log(authStore.roles)
    const hasRequiredRole = authStore.hasAnyRole(requiredRoles);
    console.log("require,", requiredRoles)
    console.log("hasrequire", hasRequiredRole)
    if (!hasRequiredRole) {
      // 如果用户有其他角色，重定向到对应的首页
      if (authStore.roles.length > 0) {
        console.log("have")
        return next(authStore.homePath);
      }
      // 如果用户没有任何角色，重定向到首页
      return next('/');
    }
  }

  // 如果都通过了，继续导航
  next();
});

export default router;
