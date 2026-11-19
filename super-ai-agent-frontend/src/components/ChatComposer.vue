<script setup>
import { computed, nextTick, onMounted, ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  streaming: {
    type: Boolean,
    default: false,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  placeholder: {
    type: String,
    default: '输入你的问题…',
  },
  maxlength: {
    type: Number,
    default: 2000,
  },
})

const emit = defineEmits(['update:modelValue', 'send', 'stop'])

const textareaRef = ref(null)
const focused = ref(false)
let composing = false

const text = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

const canSend = computed(
  () => props.modelValue.trim().length > 0 && !props.streaming && !props.disabled,
)

const showCount = computed(() => props.modelValue.length >= props.maxlength * 0.85)

function resizeTextarea() {
  const textarea = textareaRef.value
  if (!textarea) return

  textarea.style.height = '0px'
  const height = Math.min(textarea.scrollHeight, 148)
  textarea.style.height = `${Math.max(height, 26)}px`
}

function handleInput(event) {
  text.value = event.target.value
  resizeTextarea()
}

function handleKeydown(event) {
  if (event.key !== 'Enter' || event.shiftKey) return
  if (composing || event.isComposing) return

  event.preventDefault()

  if (canSend.value) emit('send')
}

function handleCompositionStart() {
  composing = true
}

function handleCompositionEnd() {
  composing = false
}

function focus() {
  textareaRef.value?.focus()
}

defineExpose({ focus })

watch(
  () => props.modelValue,
  () => nextTick(resizeTextarea),
)

onMounted(() => {
  resizeTextarea()
})
</script>

<template>
  <div class="composer" :class="{ 'composer--focused': focused }">
    <textarea
      ref="textareaRef"
      :value="modelValue"
      :maxlength="maxlength"
      :placeholder="placeholder"
      class="composer-input"
      rows="1"
      aria-label="心理咨询对话输入框"
      @input="handleInput"
      @keydown="handleKeydown"
      @compositionstart="handleCompositionStart"
      @compositionend="handleCompositionEnd"
      @focus="focused = true"
      @blur="focused = false"
    ></textarea>

    <div class="composer-actions">
      <span v-if="showCount" class="char-count">{{ modelValue.length }} / {{ maxlength }}</span>

      <button
        v-if="streaming"
        type="button"
        class="send-button send-button--stop"
        aria-label="暂停回应"
        @click="emit('stop')"
      >
        <span class="stop-icon" aria-hidden="true"></span>
        <span class="send-label">暂停</span>
      </button>

      <button
        v-else
        type="button"
        class="send-button"
        :disabled="!canSend"
        aria-label="发送心里话"
        @click="emit('send')"
      >
        <span class="send-label">发送</span>
        <svg viewBox="0 0 20 20" fill="none" aria-hidden="true">
          <path
            d="M10 16V4m0 0L5.2 8.8M10 4l4.8 4.8"
            stroke="currentColor"
            stroke-width="1.55"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.composer {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  padding: 13px 13px 13px 18px;
  border: 1px solid rgba(91, 122, 99, 0.18);
  border-radius: 18px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.44), transparent),
    rgba(255, 252, 246, 0.84);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.86),
    0 12px 32px rgba(75, 102, 82, 0.09);
  backdrop-filter: blur(19px);
  transition:
    border-color 0.28s ease,
    box-shadow 0.28s ease;
}

.composer--focused {
  border-color: var(--accent-line, rgba(91, 122, 99, 0.28));
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.88),
    0 14px 36px rgba(75, 102, 82, 0.1),
    0 0 0 3px var(--accent-soft, rgba(187, 207, 186, 0.23));
}

.composer-input {
  flex: 1;
  width: 100%;
  min-height: 26px;
  max-height: 148px;
  padding: 6px 0 3px;
  overflow-y: auto;
  border: 0;
  outline: 0;
  background: transparent;
  color: #40564a;
  font-size: 13.5px;
  line-height: 1.7;
  scrollbar-width: thin;
}

.composer-input::placeholder {
  color: #9ba69c;
}

.composer-actions {
  display: flex;
  flex: 0 0 auto;
  gap: 10px;
  align-items: center;
  padding-bottom: 1px;
}

.char-count {
  color: #a0aaa1;
  font-size: 9.5px;
  font-variant-numeric: tabular-nums;
}

.send-button {
  display: inline-flex;
  gap: 6px;
  align-items: center;
  justify-content: center;
  height: 36px;
  min-width: 36px;
  padding: 0 13px;
  border: 1px solid transparent;
  border-radius: 11px;
  background: var(--accent, #638170);
  box-shadow: 0 5px 12px rgba(88, 117, 94, 0.12);
  color: #fcfaf5;
  cursor: pointer;
  transition:
    opacity 0.2s ease,
    transform 0.2s ease,
    background-color 0.2s ease;
}

.send-button svg {
  width: 16px;
  height: 16px;
}

.send-button:hover:not(:disabled) {
  transform: translateY(-1px);
  filter: brightness(0.96);
}

.send-button:active:not(:disabled) {
  transform: translateY(0);
}

.send-button:disabled {
  background: rgba(145, 164, 145, 0.18);
  box-shadow: none;
  color: #a9b4aa;
  cursor: not-allowed;
}

.send-label {
  font-size: 10.5px;
  font-weight: 600;
  letter-spacing: 0.03em;
}

.send-button--stop {
  border-color: var(--accent-line, rgba(104, 137, 111, 0.22));
  background: var(--accent-soft, rgba(187, 207, 186, 0.23));
  box-shadow: none;
  color: var(--accent, #638170);
}

.stop-icon {
  width: 9px;
  height: 9px;
  border-radius: 2px;
  background: currentColor;
}

@media (max-width: 640px) {
  .composer {
    gap: 8px;
    padding: 10px 10px 10px 14px;
    border-radius: 17px;
  }

  .composer-input {
    padding-top: 8px;
    font-size: 13px;
  }

  .send-button {
    width: 36px;
    padding: 0;
    border-radius: 11px;
  }

  .send-label {
    display: none;
  }
}
</style>
