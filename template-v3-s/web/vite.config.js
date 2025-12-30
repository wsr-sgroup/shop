import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // 添加开发服务器配置
  server: {
    host: '0.0.0.0',
    port: 5174,
    open: true,
    cors: true,

    // 代理配置 - 这是关键，将前端请求转发到后端
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:1000', // 后端地址
        changeOrigin: true, // 允许跨域
        rewrite: (path) => path.replace(/^\/api/, ''), // 重写路径，去掉 /api 前缀
      },
      // 如果需要，可以添加更多代理规则
      '/common': {
        target: 'http://127.0.0.1:1000',
        changeOrigin: true,
      }
    }
  },

  // 可选：构建配置
  build: {
    outDir: 'dist',
    sourcemap: false,
  },

  // 可选：环境变量前缀
  envPrefix: ['VITE_', 'VUE_APP_'],
})
