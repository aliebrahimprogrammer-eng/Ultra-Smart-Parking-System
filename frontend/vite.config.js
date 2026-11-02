import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// The browser only talks to Vite. Vite forwards each path to the service that
// owns it, so the Spring services don't need CORS rules for local development.
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const auth = env.AUTH_SERVICE_URL || 'http://localhost:8081'
  const parking = env.PARKING_SERVICE_URL || 'http://localhost:8082'
  const reservation = env.RESERVATION_SERVICE_URL || 'http://localhost:8083'

  return {
    plugins: [react()],
    server: {
      port: 5173,
      proxy: {
        '/auth': auth,
        '/lots': parking,
        '/spots': parking,
        '/api': reservation,
      },
    },
  }
})
