import { createRouter, createWebHistory } from 'vue-router'

import ChatView from '@/views/ChatView.vue'
import HomeView from '@/views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/counseling',
      name: 'counseling',
      component: ChatView,
      meta: { app: 'counseling' },
    },
    {
      path: '/assistant',
      name: 'assistant',
      component: ChatView,
      meta: { app: 'assistant' },
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
  scrollBehavior: () => ({ top: 0 }),
})

router.afterEach((to) => {
  const titles = {
    home: '心屿 · 给情绪一处安放的地方',
    counseling: '心屿心理咨询 · 慢慢说，我在听',
    assistant: '心理自助助手 · 从今天的一小步开始',
  }

  document.title = titles[to.name] ?? '心屿 · 心理咨询 AI'
})

export default router
