import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const proxyTarget = (
    env.VITE_DEV_PROXY_TARGET || 'http://localhost:8123'
  ).replace(/\/+$/, '')

  // 开发/预览环境统一走同源代理，避免浏览器直连后端产生 CORS。
  const apiProxy = {
    '/api': {
      target: proxyTarget,
      changeOrigin: true,
    },
  }

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    server: {
      port: 5173,
      watch: {
        // 避免本地测试产物、截图和浏览器缓存触发文件监听异常。
        ignored: [
          '**/.tmp/**',
          '**/artifacts/**',
          '**/.npm-cache/**',
          '**/dist/**',
          '**/*.tmpdir/**',
        ],
      },
      proxy: apiProxy,
    },
    preview: {
      port: 4173,
      proxy: apiProxy,
    },
  }
})
