export const APPS = {
  counseling: {
    id: 'counseling',
    index: '01',
    path: '/counseling',
    name: '心屿心理咨询',
    shortName: '心屿',
    english: 'MIND HAVEN',
    icon: 'bloom',
    theme: 'counseling',
    tagline: '给情绪一处安放，也给自己一点空间。',
    description: '以温和、尊重的方式，陪你梳理情绪、压力与关系中的困扰。',
    endpoint: '/ai/counseling_app/chat/sse',
    requiresChatId: true,
    onlineLabel: '随时倾听',
    tags: ['情绪梳理', '压力调适', '自我探索'],
    greeting:
      '你好，我是心屿。你可以从最近的一件小事说起，也可以先告诉我：此刻的你，感觉怎么样？',
    prompts: [
      '最近总是焦虑，脑子停不下来',
      '晚上睡不好，白天也没有精神',
      '我想更了解自己的情绪',
    ],
  },
  assistant: {
    id: 'assistant',
    index: '02',
    path: '/assistant',
    name: '心理自助助手',
    shortName: '自助助手',
    english: 'SELF-CARE GUIDE',
    icon: 'compass',
    theme: 'assistant',
    tagline: '把照顾自己的方法，变成今天能做的一小步。',
    description: '围绕睡眠、压力与日常情绪，陪你整理信息、生成练习与行动方案。',
    endpoint: '/ai/super_manus/chat',
    requiresChatId: false,
    onlineLabel: '正在陪伴',
    tags: ['自助练习', '情绪记录', '行动计划'],
    greeting:
      '你好，我是你的心理自助助手。我们可以一起把困扰拆小，找到今天就能开始的那一步。',
    prompts: [
      '帮我制定一份睡前放松练习',
      '给我一个五分钟的减压计划',
      '设计一份一周情绪记录表',
    ],
  },
}

export const APP_LIST = Object.values(APPS)
