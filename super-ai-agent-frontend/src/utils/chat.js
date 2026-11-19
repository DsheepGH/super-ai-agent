export function createChatId() {
  const randomPart =
    globalThis.crypto?.randomUUID?.() ??
    `${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`

  return `counseling-${randomPart}`
}

const timeFormatter = new Intl.DateTimeFormat('zh-CN', {
  hour: '2-digit',
  minute: '2-digit',
  hour12: false,
})

export function formatTime(timestamp) {
  if (!timestamp) return ''
  return timeFormatter.format(new Date(timestamp))
}

export function isAbortError(error) {
  return (
    error?.name === 'AbortError' ||
    error?.name === 'CanceledError' ||
    error?.code === 'ERR_CANCELED'
  )
}

export function getFriendlyError(error, endpoint = '') {
  const status = error?.response?.status

  if (status === 401 || status === 403) {
    return '暂时无法连接到咨询空间，请检查访问权限后再试。'
  }

  if (status === 404) {
    const path = endpoint ? `（${endpoint}）` : ''
    return `没有找到对话服务${path}，请确认前后端接口路径一致。`
  }

  if (status === 429) {
    return '消息发送得有些快，请稍等片刻再继续。'
  }

  if (status >= 500) {
    return `咨询空间暂时不可用（${status}），请稍后再试。`
  }

  if (
    error?.code === 'ERR_NETWORK' ||
    /network error|failed to fetch|fetch failed/i.test(error?.message ?? '')
  ) {
    return '暂时无法连接到咨询空间，请确认服务已启动，或 /api 反向代理配置正确。'
  }

  return '对话连接中断了，请稍后重新发送。'
}

export async function copyText(text) {
  if (navigator.clipboard && window.isSecureContext) {
    await navigator.clipboard.writeText(text)
    return
  }

  const textarea = document.createElement('textarea')
  textarea.value = text
  textarea.setAttribute('readonly', '')
  textarea.style.position = 'fixed'
  textarea.style.opacity = '0'
  document.body.appendChild(textarea)
  textarea.select()
  const copied = document.execCommand('copy')
  document.body.removeChild(textarea)

  if (!copied) throw new Error('Copy failed')
}
