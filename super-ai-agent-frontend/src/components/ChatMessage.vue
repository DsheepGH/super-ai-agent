<script setup>
import { computed } from 'vue'

import AppIcon from '@/components/AppIcon.vue'
import { formatTime } from '@/utils/chat'

const props = defineProps({
  message: {
    type: Object,
    required: true,
  },
  appName: {
    type: String,
    default: 'AI 助手',
  },
  icon: {
    type: String,
    default: 'spark',
  },
})

const isUser = computed(() => props.message.role === 'user')
const hasContent = computed(() => Boolean(props.message.content?.trim()))
const time = computed(() => formatTime(props.message.createdAt))
</script>

<template>
  <article
    class="message"
    :class="[
      `message--${message.role}`,
      {
        'message--error': message.isError,
        'message--streaming': message.streaming,
      },
    ]"
  >
    <div v-if="!isUser" class="avatar avatar--ai" aria-hidden="true">
      <AppIcon :name="icon" />
    </div>

    <div class="message-body">
      <div class="message-meta">
        <span class="message-name">{{ isUser ? '你' : appName }}</span>
        <span class="message-time">{{ time }}</span>
        <span v-if="!isUser && !message.streaming && hasContent" class="message-kind">
          AI 陪伴
        </span>
      </div>

      <div class="bubble">
        <span v-if="!hasContent && message.streaming" class="thinking" aria-label="AI 正在思考">
          <i></i>
          <i></i>
          <i></i>
        </span>
        <template v-else>
          <span class="bubble-text">{{ message.content }}</span>
          <span v-if="message.streaming" class="stream-cursor" aria-hidden="true"></span>
        </template>
      </div>

      <p v-if="message.isError && hasContent" class="message-error">
        这次回应连接中断了
      </p>
      <p v-else-if="message.isStopped" class="message-stopped">已在这里停下</p>
    </div>

    <div v-if="isUser" class="avatar avatar--user" aria-hidden="true">你</div>
  </article>
</template>

<style scoped>
.message {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  animation: message-in 0.36s cubic-bezier(0.2, 0.75, 0.3, 1) both;
}

.message--user {
  flex-direction: row-reverse;
}

.avatar {
  display: grid;
  flex: 0 0 auto;
  place-items: center;
  width: 34px;
  height: 34px;
  margin-top: 24px;
  border-radius: 13px;
}

.avatar--ai {
  border: 1px solid var(--accent-line, rgba(255, 255, 255, 0.1));
  background: var(--avatar-bg, rgba(255, 255, 255, 0.05));
  color: var(--accent, #fff);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.66),
    0 4px 12px rgba(89, 112, 91, 0.06);
}

.avatar--ai :deep(svg) {
  width: 18px;
  height: 18px;
}

.avatar--user {
  border: 1px solid rgba(122, 111, 91, 0.17);
  background: rgba(249, 237, 220, 0.7);
  color: #826f5c;
  font-size: 11px;
  font-weight: 500;
}

.message-body {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  max-width: min(76%, 650px);
  min-width: 0;
}

.message--user .message-body {
  align-items: flex-end;
}

.message-meta {
  display: flex;
  gap: 8px;
  align-items: center;
  height: 24px;
  padding: 0 2px;
}

.message--user .message-meta {
  flex-direction: row-reverse;
}

.message-name {
  color: #66786b;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.04em;
}

.message--user .message-name {
  color: #8c998e;
}

.message-time {
  color: #a0aaa1;
  font-size: 9px;
  letter-spacing: 0.03em;
}

.message-kind {
  padding: 1px 6px;
  border: 1px solid rgba(101, 128, 105, 0.15);
  border-radius: 99px;
  color: #9ca89e;
  font-size: 7.5px;
  letter-spacing: 0.03em;
}

.bubble {
  position: relative;
  min-width: 48px;
  padding: 14px 17px;
  border: 1px solid rgba(97, 124, 103, 0.15);
  border-radius: 3px 17px 17px;
  background: rgba(255, 253, 247, 0.72);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.82),
    0 8px 23px rgba(76, 101, 82, 0.055);
  color: #506358;
  font-size: 14px;
  line-height: 1.87;
}

.message--user .bubble {
  border-color: rgba(113, 141, 109, 0.13);
  border-radius: 17px 3px 17px 17px;
  background: var(--user-bubble, linear-gradient(135deg, #f4e4d3, #e8cfbf));
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.6),
    0 8px 22px rgba(81, 107, 83, 0.08);
  color: #43594b;
}

.bubble-text {
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  word-break: break-word;
}

.message--error .bubble {
  border-color: rgba(185, 131, 108, 0.25);
}

.message-error,
.message-stopped {
  margin-top: 6px;
  padding-left: 2px;
  font-size: 10px;
}

.message-error {
  color: #ae816d;
}

.message-stopped {
  color: #9ba69d;
}

.thinking {
  display: inline-flex;
  gap: 4px;
  align-items: center;
  height: 18px;
  padding: 0 2px;
}

.thinking i {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--accent, #638170);
  opacity: 0.35;
  animation: thinking 1.2s ease-in-out infinite;
}

.thinking i:nth-child(2) {
  animation-delay: 0.16s;
}

.thinking i:nth-child(3) {
  animation-delay: 0.32s;
}

.stream-cursor {
  display: inline-block;
  width: 2px;
  height: 14px;
  margin-left: 3px;
  border-radius: 2px;
  background: var(--accent, #638170);
  vertical-align: -2px;
  animation: cursor-blink 0.85s steps(2, start) infinite;
}

@keyframes message-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes thinking {
  0%,
  70%,
  100% {
    opacity: 0.22;
    transform: translateY(0);
  }
  35% {
    opacity: 0.9;
    transform: translateY(-3px);
  }
}

@keyframes cursor-blink {
  50% {
    opacity: 0.2;
  }
}

@media (max-width: 640px) {
  .message {
    gap: 8px;
  }

  .avatar {
    width: 30px;
    height: 30px;
    margin-top: 22px;
    border-radius: 10px;
  }

  .avatar--ai :deep(svg) {
    width: 16px;
    height: 16px;
  }

  .message-body {
    max-width: 84%;
  }

  .bubble {
    padding: 11px 13px;
    border-radius: 4px 15px 15px;
    font-size: 13.5px;
    line-height: 1.75;
  }

  .message--user .bubble {
    border-radius: 15px 4px 15px 15px;
  }

  .message-time {
    display: none;
  }

  .message-kind {
    display: none;
  }

  .bubble {
    padding: 12px 14px;
    border-radius: 3px 15px 15px;
    font-size: 13px;
    line-height: 1.82;
  }

  .message--user .bubble {
    border-radius: 15px 3px 15px 15px;
  }
}
</style>
