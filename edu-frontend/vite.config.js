import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    historyApiFallback: true,
    host: true,
    port: 5174,
    proxy: {
      '/auth': { target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/events': { target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/oauth': { target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/profile': { target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/join' : {target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/my-classes' : {target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/class':       { target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/user':       { target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/group': { target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/my-groups': { target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/calendar': { target: 'http://localhost:8080', changeOrigin: true, secure: false },
      '/logout': { target: 'http://localhost:8080', changeOrigin: true, secure: false },
    },
  },
})