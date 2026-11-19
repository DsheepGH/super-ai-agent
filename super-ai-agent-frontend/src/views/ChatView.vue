<script setup>
import {
  computed,
  nextTick,
  onBeforeUnmount,
  onMounted,
  reactive,
  ref,
  shallowRef,
  watch,
} from 'vue'
import { useRoute } from 'vue-router'

import AppIcon from '@/components/AppIcon.vue'
import ChatComposer from '@/components/ChatComposer.vue'
import ChatMessage from '@/components/ChatMessage.vue'
import { APPS } from '@/config/apps'
import { streamSse } from '@/services/sse'
import {
  copyText,
  createChatId,
  getFriendlyError,
  isAbortError,
} from '@/utils/chat'

const route = useRoute()
const app = APPS[route.meta.app] ?? APPS.counseling

let messageSeed = 0

function makeMessage(role, content, extra = {}) {
  messageSeed += 1

  return {
    id: `${Date.now()}-${messageSeed}`,
    role,
    content,
    createdAt: Date.now(),
    streaming: false,
    ...extra,
  }
}

const chatId = ref(app.requiresChatId ? createChatId() : '')
const messages = ref([makeMessage('assistant', app.greeting)])
const input = ref('')
const isStreaming = ref(false)
const errorMessage = ref('')
const copied = ref(false)
const showScrollButton = ref(false)

const scrollContainer = ref(null)
const composerRef = ref(null)
const activeController = shallowRef(null)

let copyTimer = null
let scrollFrame = 0
let isPinnedToBottom = true
let isUnmounted = false

const displayChatId = computed(() => {
  if (!chatId.value) return ''
  if (chatId.value.length <= 19) return chatId.value
  return `${chatId.value.slice(0, 10)}…${chatId.value.slice(-4)}`
})

const showPrompts = computed(
  () =>
    messages.value.length === 1 &&
    messages.value[0].role === 'assistant' &&
    !isStreaming.value,
)

const placeholder = computed(() =>
  app.requiresChatId
    ? '慢慢说。此刻，你心里在想什么？'
    : '想从哪一个小目标开始？',
)

const disclaimer = computed(() =>
  app.requiresChatId
    ? 'AI 倾听不能替代专业诊断或治疗 · 请避免填写敏感个人信息'
    : 'AI 自助建议仅供参考 · 必要时请寻求专业帮助',
)

function buildRequestUrl(message) {
  const params = new URLSearchParams({ message })

  if (app.requiresChatId) {
    params.set('chatId', chatId.value)
  }

  return `${app.endpoint}?${params.toString()}`
}

function handleScroll() {
  const container = scrollContainer.value
  if (!container) return

  const distanceToBottom =
    container.scrollHeight - container.scrollTop - container.clientHeight

  isPinnedToBottom = distanceToBottom < 96
  showScrollButton.value = distanceToBottom > 260
}

function scheduleScroll(force = false) {
  if (isUnmounted) return
  if (force) isPinnedToBottom = true
  if (!isPinnedToBottom) return

  cancelAnimationFrame(scrollFrame)
  scrollFrame = requestAnimationFrame(() => {
    const container = scrollContainer.value
    if (!container) return

    container.scrollTop = container.scrollHeight
    showScrollButton.value = false
  })
}

function scrollToBottom() {
  nextTick(() => scheduleScroll(true))
}

async function sendMessage(rawMessage) {
  const message = String(rawMessage ?? '').trim()

  if (!message || isStreaming.value) return

  errorMessage.value = ''
  input.value = ''
  isStreaming.value = true

  const userMessage = makeMessage('user', message)
  const assistantMessage = reactive(
    makeMessage('assistant', '', { streaming: true }),
  )

  messages.value.push(userMessage, assistantMessage)
  scrollToBottom()

  const controller = new AbortController()
  activeController.value = controller

  try {
    await streamSse({
      url: buildRequestUrl(message),
      signal: controller.signal,
      onMessage(chunk) {
        if (isUnmounted || !chunk) return
        assistantMessage.content += chunk
      },
    })

    if (isUnmounted) return

    if (!assistantMessage.content.trim()) {
      assistantMessage.content = '我暂时没有生成有效回应，请再试一次。'
      assistantMessage.isError = true
    }
  } catch (error) {
    if (isUnmounted) return

    if (isAbortError(error)) {
      if (!assistantMessage.content.trim()) {
        const index = messages.value.findIndex(
          (item) => item.id === assistantMessage.id,
        )
        if (index !== -1) messages.value.splice(index, 1)
      } else {
        assistantMessage.isStopped = true
      }
    } else {
      console.error('[chat] SSE 请求失败：', error)
      errorMessage.value = getFriendlyError(error, app.endpoint)
      assistantMessage.isError = true

      if (!assistantMessage.content.trim()) {
        assistantMessage.content = '这次回应没有完成，请稍后再试。'
      }
    }
  } finally {
    if (!isUnmounted) {
      assistantMessage.streaming = false
      isStreaming.value = false
      activeController.value = null

      await nextTick()
      scheduleScroll()

      if (window.matchMedia?.('(pointer: fine)').matches) {
        composerRef.value?.focus()
      }
    }
  }
}

function handleSend() {
  sendMessage(input.value)
}

function handlePrompt(prompt) {
  sendMessage(prompt)
}

function stopGeneration() {
  activeController.value?.abort()
}

async function handleCopyChatId() {
  if (!chatId.value) return

  try {
    await copyText(chatId.value)
    copied.value = true
    window.clearTimeout(copyTimer)
    copyTimer = window.setTimeout(() => {
      copied.value = false
    }, 1800)
  } catch {
    errorMessage.value = '复制失败，请手动选中会话编号后复制。'
  }
}

watch(messages, () => scheduleScroll(), { deep: true })

onMounted(async () => {
  await nextTick()
  scheduleScroll(true)

  if (window.matchMedia?.('(pointer: fine)').matches) {
    composerRef.value?.focus()
  }
})

onBeforeUnmount(() => {
  isUnmounted = true
  activeController.value?.abort()
  cancelAnimationFrame(scrollFrame)
  window.clearTimeout(copyTimer)
})
</script>

<template>
  <div class="chat" :class="`chat--${app.theme}`">
    <div class="chat-wash" aria-hidden="true"></div>
    <div class="chat-orb chat-orb--one" aria-hidden="true"></div>
    <div class="chat-orb chat-orb--two" aria-hidden="true"></div>

    <header class="chat-header">
      <div class="header-left">
        <RouterLink class="back-link" to="/" aria-label="返回心屿首页">
          <span class="back-icon">
            <svg viewBox="0 0 20 20" fill="none" aria-hidden="true">
              <path
                d="M11.5 4.5 6 10l5.5 5.5M6.3 10h9"
                stroke="currentColor"
                stroke-width="1.35"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </span>
          <span class="back-label">回到首页</span>
        </RouterLink>
      </div>

      <div class="identity">
        <span class="identity-avatar"><AppIcon :name="app.icon" /></span>
        <div class="identity-copy">
          <p class="identity-english">{{ app.english }}</p>
          <h1>{{ app.name }}</h1>
        </div>
      </div>

      <div class="header-tools">
        <button
          v-if="app.requiresChatId"
          type="button"
          class="session-chip"
          :class="{ 'session-chip--copied': copied }"
          :title="`${chatId}\n点击复制会话编号`"
          @click="handleCopyChatId"
        >
          <span class="session-dot" aria-hidden="true"></span>
          <span class="session-id">{{ copied ? '已复制会话编号' : displayChatId }}</span>
          <svg class="session-copy" viewBox="0 0 16 16" fill="none" aria-hidden="true">
            <path
              v-if="!copied"
              d="M5.8 5.2V3.8c0-.6.4-1 1-1h5.4c.6 0 1 .4 1 1v5.4c0 .6-.4 1-1 1h-1.4M3.8 5.8h5.4c.6 0 1 .4 1 1v5.4c0 .6-.4 1-1 1H3.8c-.6 0-1-.4-1-1V6.8c0-.6.4-1 1-1Z"
              stroke="currentColor"
              stroke-width="1.05"
              stroke-linecap="round"
            />
            <path
              v-else
              d="m3.5 8.2 2.7 2.7 6.2-6.2"
              stroke="currentColor"
              stroke-width="1.25"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </button>

        <span class="live-pill">
          <i aria-hidden="true"></i>
          <span>{{ app.onlineLabel }}</span>
        </span>
      </div>
    </header>

    <main
      ref="scrollContainer"
      class="chat-scroll"
      aria-label="对话内容"
      @scroll.passive="handleScroll"
    >
      <div class="thread">
        <div class="thread-intro">
          <span class="intro-line" aria-hidden="true"></span>
          <p>{{ app.requiresChatId ? '这里没有标准答案' : '从一小步开始' }}</p>
          <span class="intro-line" aria-hidden="true"></span>
        </div>

        <ChatMessage
          v-for="message in messages"
          :key="message.id"
          :message="message"
          :app-name="app.shortName"
          :icon="app.icon"
        />

        <Transition name="prompt">
          <section v-if="showPrompts" class="prompt-block" aria-label="可以这样开始">
            <div class="prompt-head">
              <span class="prompt-mark" aria-hidden="true">
                <svg viewBox="0 0 16 16" fill="none">
                  <path
                    d="M8 2v3.1m0 5.8V14M2 8h3.1m5.8 0H14"
                    stroke="currentColor"
                    stroke-width="1.1"
                    stroke-linecap="round"
                  />
                  <circle cx="8" cy="8" r="1.6" fill="currentColor" />
                </svg>
              </span>
              <p>不知道从哪里开始，可以先聊聊这些</p>
              <span class="prompt-english">START GENTLY</span>
            </div>

            <div class="prompt-list">
              <button
                v-for="(prompt, index) in app.prompts"
                :key="prompt"
                type="button"
                class="prompt-item"
                :disabled="isStreaming"
                @click="handlePrompt(prompt)"
              >
                <span class="prompt-index">0{{ index + 1 }}</span>
                <span class="prompt-text">{{ prompt }}</span>
                <svg viewBox="0 0 16 16" fill="none" aria-hidden="true">
                  <path
                    d="M3 8h10m-4-4 4 4-4 4"
                    stroke="currentColor"
                    stroke-width="1.2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </button>
            </div>
          </section>
        </Transition>

        <aside v-if="app.requiresChatId && isStreaming" class="gentle-reminder">
          <span aria-hidden="true">·</span>
          如果现在不想继续，也可以先停下来，等准备好再说。
        </aside>
      </div>
    </main>

    <footer class="composer-area">
      <div class="composer-shell">
        <Transition name="notice">
          <div v-if="errorMessage" class="error-banner" role="alert">
            <svg viewBox="0 0 18 18" fill="none" aria-hidden="true">
              <circle cx="9" cy="9" r="6.7" stroke="currentColor" stroke-width="1.2" />
              <path
                d="M9 5.5v3.7m0 2.5v.2"
                stroke="currentColor"
                stroke-width="1.3"
                stroke-linecap="round"
              />
            </svg>
            <span>{{ errorMessage }}</span>
            <button type="button" aria-label="关闭提示" @click="errorMessage = ''">
              <svg viewBox="0 0 14 14" fill="none" aria-hidden="true">
                <path
                  d="m3.5 3.5 7 7m0-7-7 7"
                  stroke="currentColor"
                  stroke-width="1.2"
                  stroke-linecap="round"
                />
              </svg>
            </button>
          </div>
        </Transition>

        <ChatComposer
          ref="composerRef"
          v-model="input"
          :streaming="isStreaming"
          :placeholder="placeholder"
          @send="handleSend"
          @stop="stopGeneration"
        />

        <div class="composer-hint">
          <span>{{ disclaimer }}</span>
          <span class="key-hint">
            <kbd>Enter</kbd> 发送
            <i aria-hidden="true"></i>
            <kbd>Shift</kbd><span>+</span><kbd>Enter</kbd> 换行
          </span>
        </div>
      </div>
    </footer>

    <Transition name="scroll-fade">
      <button
        v-if="showScrollButton"
        type="button"
        class="scroll-down"
        aria-label="回到最新消息"
        @click="scrollToBottom"
      >
        <svg viewBox="0 0 18 18" fill="none" aria-hidden="true">
          <path
            d="M9 3.5v11m0 0 4.2-4.2M9 14.5 4.8 10.3"
            stroke="currentColor"
            stroke-width="1.3"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
    </Transition>
  </div>
</template>

<style scoped>
.chat {
  --ink: #35463d;
  --ink-soft: #64756b;
  --muted: #8a978e;
  --page: #f5f1e9;
  --line: rgba(72, 98, 80, 0.13);
  --accent: #638170;
  --accent-light: #d7e2d5;
  --accent-soft: rgba(187, 207, 186, 0.23);
  --accent-line: rgba(104, 137, 111, 0.26);
  --avatar-bg: linear-gradient(145deg, rgba(223, 233, 216, 0.86), rgba(250, 231, 214, 0.68));
  --user-bubble: linear-gradient(135deg, #e1ead9, #d4e1d2);
  --header-glow: rgba(216, 225, 199, 0.56);
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100svh;
  overflow: hidden;
  background: var(--page);
  color: var(--ink);
  isolation: isolate;
}

.chat--assistant {
  --accent: #a4735e;
  --accent-light: #f0ddce;
  --accent-soft: rgba(238, 213, 195, 0.3);
  --accent-line: rgba(186, 139, 113, 0.27);
  --avatar-bg: linear-gradient(145deg, rgba(249, 230, 212, 0.92), rgba(226, 234, 212, 0.72));
  --user-bubble: linear-gradient(135deg, #f0dfd0, #e6d9ca);
  --header-glow: rgba(243, 219, 198, 0.54);
}

.chat-wash {
  position: absolute;
  z-index: -3;
  inset: 0;
  background:
    radial-gradient(ellipse 46% 40% at 74% 2%, var(--header-glow), transparent 72%),
    radial-gradient(ellipse 35% 31% at 5% 60%, rgba(248, 225, 208, 0.28), transparent 76%),
    linear-gradient(155deg, #f8f4eb 0%, #f4efe6 54%, #f0f3e9 100%);
}

.chat-orb {
  position: absolute;
  z-index: -2;
  width: 440px;
  height: 440px;
  border-radius: 50%;
  filter: blur(98px);
  opacity: 0.4;
  pointer-events: none;
}

.chat-orb--one {
  top: -190px;
  right: 2%;
  background: rgba(191, 212, 185, 0.6);
}

.chat-orb--two {
  bottom: -250px;
  left: -140px;
  background: rgba(245, 211, 184, 0.48);
}

.chat-header {
  position: relative;
  z-index: 10;
  display: grid;
  flex: 0 0 auto;
  grid-template-columns: minmax(90px, 1fr) auto minmax(90px, 1fr);
  gap: 18px;
  align-items: center;
  height: 83px;
  padding: 0 clamp(18px, 4vw, 48px);
  border-bottom: 1px solid var(--line);
  background: rgba(248, 245, 237, 0.69);
  backdrop-filter: blur(21px);
}

.header-left {
  display: flex;
  justify-self: start;
}

.back-link {
  display: inline-flex;
  gap: 7px;
  align-items: center;
  padding: 5px 8px 5px 4px;
  border-radius: 10px;
  color: #87958a;
  font-size: 10px;
  letter-spacing: 0.03em;
  transition:
    color 0.25s ease,
    background-color 0.25s ease;
}

.back-link:hover {
  background: rgba(255, 255, 255, 0.38);
  color: var(--ink);
}

.back-icon {
  display: grid;
  width: 29px;
  height: 29px;
  place-items: center;
  border: 1px solid rgba(91, 121, 98, 0.16);
  border-radius: 9px;
}

.back-icon svg {
  width: 15px;
  height: 15px;
}

.identity {
  display: flex;
  gap: 11px;
  align-items: center;
  justify-self: center;
  min-width: 0;
}

.identity-avatar {
  display: grid;
  flex: 0 0 auto;
  width: 38px;
  height: 38px;
  place-items: center;
  border: 1px solid var(--accent-line);
  border-radius: 13px;
  background: var(--avatar-bg);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.66);
  color: var(--accent);
}

.identity-avatar :deep(svg) {
  width: 22px;
  height: 22px;
}

.identity-copy {
  min-width: 0;
}

.identity-english {
  margin-bottom: 1px;
  color: var(--accent);
  font-size: 7.5px;
  font-weight: 650;
  letter-spacing: 0.17em;
  line-height: 1.3;
  opacity: 0.8;
}

.identity-copy h1 {
  overflow: hidden;
  color: var(--ink);
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 550;
  letter-spacing: -0.015em;
  line-height: 1.45;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-tools {
  display: flex;
  gap: 9px;
  align-items: center;
  justify-self: end;
  min-width: 0;
}

.session-chip {
  display: inline-flex;
  gap: 7px;
  align-items: center;
  max-width: 210px;
  height: 30px;
  padding: 0 10px;
  border: 1px solid rgba(91, 118, 98, 0.15);
  border-radius: 9px;
  background: rgba(255, 255, 255, 0.31);
  color: #8a988d;
  cursor: pointer;
  transition:
    border-color 0.25s ease,
    color 0.25s ease,
    background-color 0.25s ease;
}

.session-chip:hover,
.session-chip--copied {
  border-color: var(--accent-line);
  background: rgba(255, 255, 255, 0.48);
  color: var(--accent);
}

.session-dot {
  width: 5px;
  height: 5px;
  flex: 0 0 auto;
  border-radius: 50%;
  background: var(--accent);
  opacity: 0.72;
}

.session-copy {
  width: 13px;
  height: 13px;
  flex: 0 0 auto;
}

.session-id {
  overflow: hidden;
  font-family: 'Cascadia Code', 'SFMono-Regular', Consolas, monospace;
  font-size: 9px;
  letter-spacing: 0.015em;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.live-pill {
  display: inline-flex;
  gap: 7px;
  align-items: center;
  height: 30px;
  padding: 0 10px;
  border: 1px solid rgba(91, 118, 98, 0.13);
  border-radius: 9px;
  color: #86958a;
  font-size: 9px;
  letter-spacing: 0.025em;
  white-space: nowrap;
}

.live-pill i {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #86a789;
  box-shadow: 0 0 0 3px rgba(134, 167, 137, 0.14);
  animation: live-pulse 2.8s ease-in-out infinite;
}

.chat-scroll {
  flex: 1;
  padding: 28px clamp(16px, 4vw, 32px) 11px;
  overflow-x: hidden;
  overflow-y: auto;
  overscroll-behavior: contain;
  scrollbar-gutter: stable;
}

.thread {
  display: flex;
  flex-direction: column;
  gap: 22px;
  width: min(790px, 100%);
  min-height: 100%;
  margin: 0 auto;
  padding-bottom: 5px;
}

.thread-intro {
  display: flex;
  gap: 11px;
  align-items: center;
  justify-content: center;
  padding: 1px 0 7px;
}

.intro-line {
  width: 27px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(106, 135, 110, 0.25));
}

.intro-line:last-child {
  background: linear-gradient(90deg, rgba(106, 135, 110, 0.25), transparent);
}

.thread-intro p {
  color: #929e94;
  font-size: 9px;
  letter-spacing: 0.08em;
}

.prompt-block {
  width: calc(100% - 46px);
  margin-left: 46px;
  padding-top: 3px;
}

.prompt-head {
  display: flex;
  gap: 7px;
  align-items: center;
  margin-bottom: 10px;
  padding-left: 1px;
}

.prompt-mark {
  display: grid;
  width: 15px;
  height: 15px;
  place-items: center;
  color: var(--accent);
}

.prompt-mark svg {
  width: 14px;
  height: 14px;
}

.prompt-head p {
  color: #839086;
  font-size: 10px;
  letter-spacing: 0.015em;
}

.prompt-english {
  margin-left: auto;
  color: #a5aea6;
  font-size: 7.5px;
  font-weight: 650;
  letter-spacing: 0.16em;
}

.prompt-list {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.prompt-item {
  display: flex;
  gap: 11px;
  align-items: center;
  width: 100%;
  min-height: 43px;
  padding: 0 13px;
  border: 1px solid rgba(93, 123, 100, 0.14);
  border-radius: 11px;
  background: rgba(255, 253, 247, 0.44);
  color: #6f8074;
  text-align: left;
  cursor: pointer;
  transition:
    transform 0.25s ease,
    border-color 0.25s ease,
    background-color 0.25s ease,
    color 0.25s ease;
}

.prompt-item:hover {
  border-color: var(--accent-line);
  background: rgba(255, 255, 250, 0.72);
  color: var(--ink);
  transform: translateX(3px);
}

.prompt-index {
  color: #ad9885;
  font-family: var(--font-display);
  font-size: 8.5px;
  letter-spacing: 0.06em;
}

.prompt-text {
  flex: 1;
  font-size: 11.5px;
  letter-spacing: 0.015em;
}

.prompt-item > svg {
  width: 13px;
  height: 13px;
  color: #a8b2a8;
  transition:
    color 0.25s ease,
    transform 0.25s ease;
}

.prompt-item:hover > svg {
  color: var(--accent);
  transform: translateX(2px);
}

.prompt-item:disabled {
  cursor: not-allowed;
  opacity: 0.52;
}

.gentle-reminder {
  align-self: flex-start;
  margin: -6px 0 0 46px;
  color: #a0aaa1;
  font-size: 9.5px;
  line-height: 1.7;
}

.gentle-reminder span {
  margin-right: 5px;
  color: #c29679;
}

.composer-area {
  position: relative;
  z-index: 8;
  flex: 0 0 auto;
  padding: 7px clamp(16px, 4vw, 32px) max(15px, env(safe-area-inset-bottom));
  background: linear-gradient(180deg, rgba(245, 241, 233, 0) 0%, rgba(245, 241, 233, 0.82) 25%, rgba(245, 241, 233, 0.97) 100%);
}

.composer-shell {
  width: min(790px, 100%);
  margin: 0 auto;
}

.error-banner {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 9px;
  padding: 9px 11px;
  border: 1px solid rgba(184, 127, 105, 0.22);
  border-radius: 11px;
  background: rgba(246, 225, 211, 0.58);
  color: #9d7561;
  font-size: 10px;
  line-height: 1.55;
}

.error-banner > svg {
  width: 15px;
  height: 15px;
  flex: 0 0 auto;
}

.error-banner span {
  flex: 1;
}

.error-banner button {
  display: grid;
  width: 22px;
  height: 22px;
  flex: 0 0 auto;
  place-items: center;
  border-radius: 6px;
  background: transparent;
  color: #b28c78;
  cursor: pointer;
}

.error-banner button:hover {
  background: rgba(255, 255, 255, 0.36);
  color: #8e6957;
}

.error-banner button svg {
  width: 12px;
  height: 12px;
}

.composer-hint {
  display: flex;
  gap: 14px;
  align-items: center;
  justify-content: space-between;
  padding: 8px 4px 0;
  color: #9aa59b;
  font-size: 9px;
  line-height: 1.5;
}

.key-hint {
  display: inline-flex;
  gap: 4px;
  align-items: center;
  color: #a2aca3;
  white-space: nowrap;
}

.key-hint kbd {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 17px;
  padding: 0 4px;
  border: 1px solid rgba(96, 125, 101, 0.16);
  border-bottom-color: rgba(96, 125, 101, 0.25);
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.28);
  color: #8d998f;
  font-family: var(--font-sans);
  font-size: 8px;
}

.key-hint i {
  width: 2px;
  height: 2px;
  margin: 0 2px;
  border-radius: 50%;
  background: #b7c0b5;
}

.scroll-down {
  position: absolute;
  z-index: 9;
  right: max(20px, calc((100vw - 790px) / 2 + 10px));
  bottom: 153px;
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  border: 1px solid rgba(97, 125, 102, 0.2);
  border-radius: 50%;
  background: rgba(255, 252, 245, 0.91);
  box-shadow: 0 8px 22px rgba(78, 101, 82, 0.14);
  color: #7b8b7e;
  cursor: pointer;
  backdrop-filter: blur(12px);
  transition:
    color 0.25s ease,
    border-color 0.25s ease,
    transform 0.25s ease;
}

.scroll-down:hover {
  border-color: var(--accent-line);
  color: var(--accent);
  transform: translateY(-2px);
}

.scroll-down svg {
  width: 16px;
  height: 16px;
}

.notice-enter-active,
.notice-leave-active {
  transition:
    opacity 0.22s ease,
    transform 0.22s ease;
}

.notice-enter-from,
.notice-leave-to {
  opacity: 0;
  transform: translateY(5px);
}

.prompt-enter-active,
.prompt-leave-active {
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
}

.prompt-enter-from,
.prompt-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

.scroll-fade-enter-active,
.scroll-fade-leave-active {
  transition:
    opacity 0.2s ease,
    transform 0.2s ease;
}

.scroll-fade-enter-from,
.scroll-fade-leave-to {
  opacity: 0;
  transform: translateY(4px);
}

@keyframes live-pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
}

@media (max-width: 900px) {
  .chat-header {
    grid-template-columns: minmax(74px, 0.8fr) auto minmax(74px, 0.8fr);
    gap: 10px;
    height: 75px;
    padding: 0 18px;
  }

  .live-pill {
    display: none;
  }

  .session-chip {
    max-width: 150px;
  }

  .prompt-block {
    width: calc(100% - 10px);
    margin-left: 10px;
  }
}

@media (max-width: 640px) {
  .chat-header {
    grid-template-columns: auto minmax(0, 1fr) auto;
    gap: 8px;
    height: 68px;
    padding: 0 12px;
  }

  .back-link {
    padding: 4px;
  }

  .back-icon {
    width: 29px;
    height: 29px;
  }

  .back-label {
    display: none;
  }

  .identity {
    justify-self: start;
    gap: 8px;
    margin-left: 2px;
  }

  .identity-avatar {
    width: 33px;
    height: 33px;
    border-radius: 10px;
  }

  .identity-avatar :deep(svg) {
    width: 19px;
    height: 19px;
  }

  .identity-english {
    display: none;
  }

  .identity-copy h1 {
    font-size: 14px;
  }

  .header-tools {
    gap: 0;
  }

  .session-chip {
    max-width: 104px;
    height: 29px;
    padding: 0 7px;
  }

  .session-copy {
    display: none;
  }

  .session-id {
    font-size: 8px;
  }

  .chat-scroll {
    padding: 20px 12px 8px;
    scrollbar-gutter: auto;
  }

  .thread {
    gap: 20px;
  }

  .thread-intro {
    padding-bottom: 2px;
  }

  .prompt-block {
    width: 100%;
    margin-left: 0;
  }

  .prompt-english {
    display: none;
  }

  .prompt-item {
    min-height: 41px;
    padding: 0 11px;
  }

  .prompt-text {
    font-size: 11px;
  }

  .gentle-reminder {
    margin-left: 0;
    font-size: 9px;
  }

  .composer-area {
    padding: 7px 10px max(10px, env(safe-area-inset-bottom));
  }

  .composer-hint {
    justify-content: center;
    padding-top: 7px;
    text-align: center;
  }

  .key-hint {
    display: none;
  }

  .scroll-down {
    right: 16px;
    bottom: 132px;
    width: 32px;
    height: 32px;
  }
}

@media (max-width: 400px) {
  .session-chip {
    max-width: 88px;
  }

  .session-id {
    font-size: 7.5px;
  }
}
</style>
