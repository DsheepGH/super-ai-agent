# Super AI Agent

一个基于 Spring Boot 与 Vue 3 的 AI 智能体项目，提供心理咨询对话与心理自助助手两个前端应用，并集成 Spring AI、DashScope、RAG、工具调用、SSE 流式响应和 MCP。

> 本项目用于学习和技术演示。AI 输出不能替代医生诊断、专业心理治疗或紧急援助。

## 功能

- 心屿心理咨询：多轮对话、SSE 流式回复、会话编号与危机提示。
- 心理自助助手：围绕睡眠、压力和日常情绪生成自助练习与行动计划。
- Spring AI Agent：ReAct、工具调用、RAG 检索增强和聊天记忆。
- 前端体验：响应式布局、流式打字效果、暂停回复与移动端适配。
- 部署支持：后端 Dockerfile、前端 Nginx Docker 镜像与微信云托管配置说明。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 21、Spring Boot 3.5、Spring AI、DashScope、LangChain4j |
| 前端 | Vue 3、Vite、Vue Router、Axios |
| 数据 | PostgreSQL/PgVector（可选）、内存向量库（默认） |
| 部署 | Docker、Nginx、微信云托管 |

## 项目结构

```text
.
├─ src/                         Spring Boot 后端
├─ super-ai-agent-frontend/     Vue 3 前端
├─ Dockerfile                   后端镜像
├─ pom.xml                      Maven 配置
└─ mvnw / mvnw.cmd              Maven Wrapper
```

## 环境变量

运行 AI 对话至少需要 DashScope API Key：

| 变量 | 必需 | 说明 |
| --- | --- | --- |
| `DASHSCOPE_API_KEY` | 是 | 阿里云 DashScope API Key |
| `AI_PG_URL` | 否 | PostgreSQL JDBC 地址 |
| `AI_PG_USERNAME` | 否 | PostgreSQL 用户名 |
| `AI_PG_PASSWORD` | 否 | PostgreSQL 密码 |
| `SEARCH_API_KEY` | 否 | 图片搜索 MCP 服务配置预留项 |

请通过操作系统环境变量或未纳入 Git 的 `application-local.yml` 提供凭据，不要将密钥写入源码或提交到仓库。

## 启动后端

前置条件：JDK 21、Maven 3.9+。

```bash
# Windows
set DASHSCOPE_API_KEY=your-key
.\mvnw.cmd spring-boot:run
```

```bash
# macOS / Linux
export DASHSCOPE_API_KEY=your-key
./mvnw spring-boot:run
```

服务默认监听 `http://localhost:8123/api`，健康检查地址为 `http://localhost:8123/api/health`。

## 启动前端

前置条件：Node.js 20+。

```bash
cd super-ai-agent-frontend
npm install
npm run dev
```

浏览器打开 `http://localhost:5173`。Vite 默认把 `/api` 代理到 `http://localhost:8123`。

## 主要接口

| 方法 | 地址 | 说明 |
| --- | --- | --- |
| GET | `/api/health` | 健康检查 |
| GET | `/api/ai/counseling_app/chat/sse` | 心理咨询 SSE 对话 |
| GET | `/api/ai/super_manus/chat` | 心理自助助手 |

## 构建与测试

```bash
.\mvnw.cmd test
.\mvnw.cmd package
```

```bash
cd super-ai-agent-frontend
npm run build
```

前端部署与 Nginx 配置说明见 [super-ai-agent-frontend/README.md](super-ai-agent-frontend/README.md)。

## 安全说明

- `.env`、`application-local.yml`、本地数据库、构建产物和归档文件不会提交到 Git。
- 前端 `.env.development` 与 `.env.production` 仅包含相对接口地址和本地代理目标。
- 如凭据曾进入 Git 历史，应立即撤销或轮换，并避免发布包含旧提交的历史。

## 许可证

仓库目前未添加开源许可证。在明确代码来源和复用意愿前，不默认授予额外使用许可。
