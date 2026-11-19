import axios from 'axios'

/**
 * 接口基础路径统一从环境变量读取：
 *
 * - 开发环境：`.env.development` 使用相对路径 `/api`，
 *   由 Vite 代理到 `VITE_DEV_PROXY_TARGET`（默认 http://localhost:8123），
 *   浏览器只访问同源地址，不会产生跨域。
 * - 生产环境：`.env.production` 使用相对路径 `/api`，
 *   由 Nginx 将 `/api/` 反向代理到后端，浏览器同样只访问同源地址。
 *
 * 如果前后端必须分开域名部署，也可以把 `VITE_API_BASE_URL` 改成完整地址，
 * 例如 `https://api.example.com/api`，此时需要后端自行允许跨域。
 */
function normalizeBaseUrl(value) {
  const baseUrl = String(value ?? '').trim()

  if (!baseUrl) return '/api'

  return baseUrl.replace(/\/+$/, '') || '/api'
}

export const API_BASE_URL = normalizeBaseUrl(import.meta.env.VITE_API_BASE_URL)

const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 0,
})

export default http
