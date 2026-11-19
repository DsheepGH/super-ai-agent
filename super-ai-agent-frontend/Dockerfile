# ---------- 构建阶段 ----------
FROM node:20-alpine AS build

WORKDIR /app

# 先复制依赖清单，充分利用 Docker 层缓存。
# 显式安装 devDependencies，避免构建机设置了 NODE_ENV=production 时缺少 Vite。
COPY package.json package-lock.json ./
RUN npm ci --include=dev --no-audit --no-fund

COPY . .

# 生产环境默认使用相对路径 /api，由 Nginx 反向代理到后端。
# 如需在构建时覆盖，可执行：
#   docker build --build-arg VITE_API_BASE_URL=/api .
ARG VITE_API_BASE_URL=/api
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

RUN npm run build

# ---------- 运行阶段 ----------
FROM nginx:1.27-alpine AS runtime

# 后端服务地址，微信云托管可在“环境变量”中覆盖。
# 示例：http://super-ai-backend:8123 或服务的内网访问地址。
# 不要以 / 结尾。
ENV BACKEND_UPSTREAM=http://backend:8123

# 官方镜像启动时会用 envsubst 渲染模板到 /etc/nginx/conf.d/default.conf。
COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget -q -O /dev/null http://127.0.0.1/healthz || exit 1

CMD ["nginx", "-g", "daemon off;"]
