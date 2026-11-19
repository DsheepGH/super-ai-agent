import http from './http'

/**
 * 解析单个 SSE 事件块。
 * 支持多行 data，忽略注释行（如 Spring 的心跳 ": ping"）。
 */
function parseEventBlock(block) {
  const dataLines = []
  let event = 'message'

  for (const line of block.split(/\r\n|\r|\n/)) {
    if (!line || line.startsWith(':')) continue

    const colonIndex = line.indexOf(':')
    const field = colonIndex === -1 ? line : line.slice(0, colonIndex)
    let value = colonIndex === -1 ? '' : line.slice(colonIndex + 1)

    if (value.startsWith(' ')) value = value.slice(1)

    if (field === 'data') {
      dataLines.push(value)
    } else if (field === 'event') {
      event = value
    }
  }

  if (dataLines.length === 0) return null

  return {
    event,
    data: dataLines.join('\n'),
  }
}

/**
 * 增量式 SSE 解析器，能处理一个事件被拆到多个网络分片的情况。
 */
export function createSseParser(onMessage) {
  let buffer = ''

  function drain(flush = false) {
    const boundaryPattern = /\r\n\r\n|\n\n|\r\r/
    let match = boundaryPattern.exec(buffer)

    while (match) {
      const rawBlock = buffer.slice(0, match.index)
      buffer = buffer.slice(match.index + match[0].length)

      const parsed = parseEventBlock(rawBlock)
      if (parsed) onMessage(parsed.data, parsed.event)

      match = boundaryPattern.exec(buffer)
    }

    if (flush && buffer.trim()) {
      const parsed = parseEventBlock(buffer)
      if (parsed) onMessage(parsed.data, parsed.event)
      buffer = ''
    }
  }

  return {
    push(chunk) {
      buffer += chunk
      drain()
    },
    flush() {
      drain(true)
    },
  }
}

/**
 * 使用 Axios 的 fetch adapter 读取 text/event-stream 响应体。
 * Axios 负责请求与取消，ReadableStream 负责逐块接收，最终按 SSE 协议解析。
 */
export async function streamSse({ url, signal, onMessage }) {
  const response = await http.get(url, {
    adapter: 'fetch',
    responseType: 'stream',
    signal,
    timeout: 0,
    headers: {
      Accept: 'text/event-stream',
      'Cache-Control': 'no-cache',
    },
  })

  const body = response.data

  if (!body || typeof body.getReader !== 'function') {
    throw new Error('当前浏览器不支持流式响应，请升级浏览器后重试。')
  }

  const reader = body.getReader()
  const decoder = new TextDecoder('utf-8')
  const parser = createSseParser(onMessage)

  try {
    while (true) {
      const { done, value } = await reader.read()

      if (done) break

      parser.push(decoder.decode(value, { stream: true }))
    }

    parser.push(decoder.decode())
    parser.flush()
  } finally {
    reader.releaseLock()
  }
}
