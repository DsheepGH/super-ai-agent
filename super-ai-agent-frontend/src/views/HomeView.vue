<script setup>
import AppIcon from '@/components/AppIcon.vue'
import AppLogo from '@/components/AppLogo.vue'
import { APP_LIST } from '@/config/apps'

const currentYear = new Date().getFullYear()

const careNotes = [
  {
    index: '01',
    title: '先安放情绪',
    copy: '不需要先想清楚该怎么说。焦虑、疲惫、委屈，都可以从一句“我有点难受”开始。',
  },
  {
    index: '02',
    title: '再一起梳理',
    copy: '在安全的对话里看见情绪背后的需要，找到当下更适合你的应对方式。',
  },
  {
    index: '03',
    title: '把改变放小',
    copy: '从一次呼吸、一段记录、一个小行动开始，让照顾自己成为可以持续的事。',
  },
]
</script>

<template>
  <div class="home">
    <div class="grain" aria-hidden="true"></div>
    <div class="ambient ambient--sage" aria-hidden="true"></div>
    <div class="ambient ambient--clay" aria-hidden="true"></div>

    <header class="home-header">
      <RouterLink class="brand" to="/" aria-label="心屿首页">
        <span class="brand-mark"><AppLogo /></span>
        <span class="brand-copy">
          <strong>心屿</strong>
          <small>MIND HAVEN</small>
        </span>
      </RouterLink>

      <div class="header-right">
        <span class="system-status">
          <i aria-hidden="true"></i>
          此刻，你可以慢慢说
        </span>
        <span class="header-divider" aria-hidden="true"></span>
        <span class="header-edition">CALM · SUPPORTIVE · 24/7</span>
      </div>
    </header>

    <main class="home-main">
      <section class="hero">
        <div class="hero-copy">
          <p class="eyebrow">
            <span class="eyebrow-dot" aria-hidden="true"></span>
            A GENTLE SPACE FOR YOUR MIND
          </p>

          <h1 class="hero-title">
            给情绪一处安放，
            <span>给自己一点空间。</span>
          </h1>

          <p class="hero-lead">
            不用准备好所有答案。这里有一段不被打断的倾听，
            <br />
            陪你看见情绪、理解自己，再找到下一步。
          </p>

          <div class="hero-actions">
            <RouterLink class="primary-action" to="/counseling">
              <span>开始一次对话</span>
              <svg viewBox="0 0 18 18" fill="none" aria-hidden="true">
                <path
                  d="M3.5 9h11m-4.1-4.1L14.5 9l-4.1 4.1"
                  stroke="currentColor"
                  stroke-width="1.35"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
            </RouterLink>
            <span class="hero-note">
              <svg viewBox="0 0 16 16" fill="none" aria-hidden="true">
                <path
                  d="M13.2 2.8C7.7 3.5 3.5 6.7 3.5 11.2c4.5 0 7.9-3 9.7-8.4Z"
                  stroke="currentColor"
                  stroke-width="1.15"
                  stroke-linejoin="round"
                />
                <path
                  d="M2.8 13.6c2.3-2.7 5-4.8 8-6.2"
                  stroke="currentColor"
                  stroke-width="1.15"
                  stroke-linecap="round"
                />
              </svg>
              安静对话 · 随时可以停下
            </span>
          </div>
        </div>

        <aside class="hero-visual" aria-label="心屿的陪伴方式">
          <div class="visual-halo" aria-hidden="true"></div>
          <div class="breath-card">
            <div class="breath-orbit breath-orbit--outer" aria-hidden="true"></div>
            <div class="breath-orbit breath-orbit--inner" aria-hidden="true"></div>
            <div class="breath-core">
              <AppIcon name="bloom" />
            </div>

            <div class="breath-label">
              <span>PAUSE</span>
              <p>先深呼吸一次</p>
            </div>

            <div class="breath-steps" aria-label="三步呼吸提示">
              <span>吸气 · 4</span>
              <i aria-hidden="true"></i>
              <span>停留 · 4</span>
              <i aria-hidden="true"></i>
              <span>呼气 · 6</span>
            </div>
          </div>

          <p class="visual-quote">
            “你不需要马上变得很好，
            <br />
            只需要先被好好听见。”
          </p>
        </aside>
      </section>

      <section class="agents" aria-labelledby="agents-title">
        <div class="section-head">
          <div>
            <p class="section-kicker">CHOOSE YOUR SUPPORT</p>
            <h2 id="agents-title">选择此刻更适合你的陪伴</h2>
          </div>
          <span class="section-aside">没有哪一种是“必须”的</span>
        </div>

        <div class="app-grid">
          <RouterLink
            v-for="app in APP_LIST"
            :key="app.id"
            :to="app.path"
            class="app-card"
            :class="`app-card--${app.theme}`"
          >
            <div class="card-top">
              <span class="card-index">{{ app.index }} <i>/</i> SPACE</span>
              <span class="card-arrow" aria-hidden="true">
                <svg viewBox="0 0 20 20" fill="none">
                  <path
                    d="M5.5 14.5 14.5 5.5M7 5.5h7.5V13"
                    stroke="currentColor"
                    stroke-width="1.35"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </span>
            </div>

            <div class="card-main">
              <span class="card-icon"><AppIcon :name="app.icon" /></span>
              <div class="card-copy">
                <p class="card-english">{{ app.english }}</p>
                <h3>{{ app.name }}</h3>
                <p class="card-tagline">{{ app.tagline }}</p>
              </div>
            </div>

            <p class="card-description">{{ app.description }}</p>

            <div class="card-footer">
              <div class="card-tags">
                <span v-for="tag in app.tags" :key="tag">{{ tag }}</span>
              </div>
              <span class="card-enter">
                进入空间
                <svg viewBox="0 0 16 16" fill="none" aria-hidden="true">
                  <path
                    d="M3 8h10m-4-4 4 4-4 4"
                    stroke="currentColor"
                    stroke-width="1.3"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </span>
            </div>

            <div class="card-ghost" aria-hidden="true"><AppIcon :name="app.icon" /></div>
          </RouterLink>
        </div>
      </section>

      <section class="care-path" aria-labelledby="care-title">
        <div class="quality-row">
          <span><i aria-hidden="true"></i> 不评判</span>
          <span><i aria-hidden="true"></i> 不催促</span>
          <span><i aria-hidden="true"></i> 不替代专业诊疗</span>
        </div>

        <div class="care-heading">
          <div>
            <p class="section-kicker">A SMALLER STEP</p>
            <h2 id="care-title">照顾自己，可以从小一点开始</h2>
          </div>
          <p>
            如果你不确定从哪里说起，试着先描述此刻身体的感受，
            <br />
            或最近反复出现的一个念头。
          </p>
        </div>

        <div class="care-notes">
          <article v-for="note in careNotes" :key="note.index" class="care-note">
            <span class="note-index">{{ note.index }}</span>
            <h3>{{ note.title }}</h3>
            <p>{{ note.copy }}</p>
          </article>
        </div>
      </section>

      <section class="safety-note" aria-label="使用提示">
        <span class="safety-icon" aria-hidden="true">
          <svg viewBox="0 0 22 22" fill="none">
            <path
              d="M11 19s6.4-3.4 6.4-8.2V4.7L11 2.5 4.6 4.7v6.1C4.6 15.6 11 19 11 19Z"
              stroke="currentColor"
              stroke-width="1.25"
              stroke-linejoin="round"
            />
            <path
              d="M11 7.3v4.1m0 2.4v.15"
              stroke="currentColor"
              stroke-width="1.35"
              stroke-linecap="round"
            />
          </svg>
        </span>
        <p>
          <strong>关于陪伴的边界</strong>
          AI 对话可以帮助你梳理情绪，但不能替代医生诊断或专业心理治疗。
        </p>
        <p class="safety-crisis">
          如果你正面临伤害自己的风险，请立即联系当地急救、危机干预热线，
          或告诉身边可信赖的人。
        </p>
      </section>
    </main>

    <footer class="home-footer">
      <div class="footer-top">
        <p class="footer-tagline">愿你在这里，先慢下来一点。</p>
        <p class="footer-meta">倾听 · 理解 · 一点点改变</p>
      </div>

      <div class="footer-bottom">
        <p>© {{ currentYear }} 心屿 · 保留所有权利</p>
        <p>AI 陪伴仅供参考 · 必要时请寻求专业帮助</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.home {
  --ink: #35463d;
  --ink-soft: #617268;
  --muted: #89958d;
  --line: rgba(62, 82, 70, 0.13);
  position: relative;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  min-height: 100svh;
  padding: 0 clamp(20px, 5vw, 72px);
  overflow: hidden;
  background:
    radial-gradient(ellipse 46% 42% at 74% 8%, rgba(213, 222, 202, 0.7), transparent 72%),
    radial-gradient(ellipse 40% 32% at 8% 18%, rgba(246, 223, 207, 0.5), transparent 72%),
    linear-gradient(155deg, #f8f4ec 0%, #f3eee4 52%, #eef2e9 100%);
  color: var(--ink);
  isolation: isolate;
}

.grain {
  position: absolute;
  z-index: -1;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 160 160' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='.8' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='.16'/%3E%3C/svg%3E");
  opacity: 0.17;
  pointer-events: none;
}

.ambient {
  position: absolute;
  z-index: -2;
  width: 580px;
  height: 580px;
  border-radius: 50%;
  filter: blur(100px);
  pointer-events: none;
}

.ambient--sage {
  top: 24%;
  right: -300px;
  background: rgba(164, 190, 168, 0.28);
}

.ambient--clay {
  bottom: -210px;
  left: -280px;
  background: rgba(225, 184, 156, 0.22);
}

.home-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: min(1320px, 100%);
  height: 96px;
  margin: 0 auto;
  border-bottom: 1px solid var(--line);
}

.brand {
  display: inline-flex;
  gap: 11px;
  align-items: center;
  border-radius: 10px;
}

.brand-mark {
  width: 36px;
  height: 36px;
  color: var(--ink);
  transition: transform 0.5s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.brand:hover .brand-mark {
  transform: rotate(-8deg);
}

.brand-copy {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.brand-copy strong {
  font-family: var(--font-display);
  font-size: 19px;
  font-weight: 600;
  letter-spacing: 0.13em;
  line-height: 1.25;
}

.brand-copy small {
  color: var(--muted);
  font-size: 7.5px;
  font-weight: 600;
  letter-spacing: 0.22em;
  line-height: 1.4;
}

.header-right {
  display: flex;
  gap: 18px;
  align-items: center;
  color: var(--muted);
  font-size: 10px;
  letter-spacing: 0.07em;
}

.system-status {
  display: inline-flex;
  gap: 8px;
  align-items: center;
  color: #728177;
}

.system-status i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #7e9e83;
  box-shadow: 0 0 0 4px rgba(126, 158, 131, 0.12);
  animation: status-pulse 3s ease-in-out infinite;
}

.header-divider {
  width: 1px;
  height: 12px;
  background: var(--line);
}

.header-edition {
  font-size: 8px;
  font-weight: 600;
  letter-spacing: 0.17em;
}

.home-main {
  width: min(1160px, 100%);
  margin: 0 auto;
  padding: clamp(50px, 8vh, 100px) 0 28px;
}

.hero {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(340px, 0.85fr);
  gap: clamp(42px, 7vw, 100px);
  align-items: center;
}

.hero-copy {
  max-width: 670px;
}

.eyebrow {
  display: inline-flex;
  gap: 10px;
  align-items: center;
  color: var(--ink-soft);
  font-size: 9.5px;
  font-weight: 650;
  letter-spacing: 0.2em;
  animation: rise 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.eyebrow-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #c17e60;
  box-shadow: 0 0 0 4px rgba(193, 126, 96, 0.11);
}

.hero-title {
  margin-top: 22px;
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(36px, 4.65vw, 66px);
  font-weight: 500;
  line-height: 1.2;
  letter-spacing: -0.05em;
  animation: rise 0.85s 0.06s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.hero-title span {
  display: block;
  padding-top: 5px;
  color: #5f7667;
}

.hero-lead {
  margin-top: 25px;
  color: var(--ink-soft);
  font-size: clamp(14px, 1.2vw, 15px);
  line-height: 2;
  letter-spacing: 0.015em;
  animation: rise 0.85s 0.12s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  align-items: center;
  margin-top: 31px;
  animation: rise 0.85s 0.18s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.primary-action {
  display: inline-flex;
  gap: 12px;
  align-items: center;
  justify-content: center;
  height: 45px;
  padding: 0 21px;
  border-radius: 99px;
  background: #4e6657;
  box-shadow: 0 12px 27px rgba(61, 84, 70, 0.19);
  color: #fdfaf4;
  font-size: 12px;
  letter-spacing: 0.035em;
  transition:
    background-color 0.25s ease,
    box-shadow 0.25s ease,
    transform 0.25s ease;
}

.primary-action svg {
  width: 16px;
  height: 16px;
}

.primary-action:hover {
  background: #3f5749;
  box-shadow: 0 15px 31px rgba(61, 84, 70, 0.27);
  transform: translateY(-2px);
}

.hero-note {
  display: inline-flex;
  gap: 7px;
  align-items: center;
  color: #7a897e;
  font-size: 10.5px;
  letter-spacing: 0.025em;
}

.hero-note svg {
  width: 14px;
  height: 14px;
  color: #83a088;
}

.hero-visual {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 375px;
  animation: rise 1s 0.2s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.visual-halo {
  position: absolute;
  top: 19px;
  width: 320px;
  height: 320px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(238, 217, 195, 0.6), rgba(221, 230, 211, 0.3) 43%, transparent 67%);
  filter: blur(13px);
}

.breath-card {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: min(310px, 100%);
  padding: 27px 25px 24px;
  overflow: hidden;
  border: 1px solid rgba(98, 125, 105, 0.17);
  border-radius: 28px;
  background: rgba(255, 252, 245, 0.57);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.82),
    0 26px 65px rgba(76, 103, 83, 0.1);
  backdrop-filter: blur(18px);
}

.breath-card::after {
  position: absolute;
  top: -65px;
  right: -52px;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  background: rgba(230, 199, 177, 0.2);
  content: '';
  pointer-events: none;
}

.breath-orbit {
  position: absolute;
  border: 1px solid rgba(100, 128, 111, 0.16);
  border-radius: 50%;
}

.breath-orbit--outer {
  top: 22px;
  width: 175px;
  height: 175px;
  animation: breathe 7s ease-in-out infinite;
}

.breath-orbit--inner {
  top: 39px;
  width: 141px;
  height: 141px;
  border-color: rgba(203, 154, 124, 0.2);
  animation: breathe 7s 0.45s ease-in-out infinite;
}

.breath-core {
  position: relative;
  display: grid;
  width: 88px;
  height: 88px;
  margin-top: 19px;
  place-items: center;
  border: 1px solid rgba(104, 132, 111, 0.23);
  border-radius: 50%;
  background: linear-gradient(145deg, rgba(217, 231, 212, 0.83), rgba(246, 225, 207, 0.67));
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.85),
    0 11px 25px rgba(99, 128, 107, 0.1);
  color: #57715f;
}

.breath-core :deep(svg) {
  width: 42px;
  height: 42px;
}

.breath-label {
  position: relative;
  margin-top: 44px;
  text-align: center;
}

.breath-label span {
  color: #c28161;
  font-size: 8px;
  font-weight: 650;
  letter-spacing: 0.2em;
}

.breath-label p {
  margin-top: 5px;
  color: var(--ink);
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 500;
  letter-spacing: -0.025em;
}

.breath-steps {
  display: flex;
  gap: 7px;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  color: #788a7e;
  font-size: 9px;
  letter-spacing: 0.025em;
}

.breath-steps i {
  width: 2px;
  height: 2px;
  border-radius: 50%;
  background: #b4c0b3;
}

.visual-quote {
  margin-top: 25px;
  color: #889489;
  font-family: var(--font-display);
  font-size: 12px;
  line-height: 1.85;
  letter-spacing: 0.02em;
  text-align: center;
}

.agents {
  margin-top: clamp(72px, 10vh, 116px);
}

.section-head,
.care-heading {
  display: flex;
  gap: 30px;
  align-items: flex-end;
  justify-content: space-between;
}

.section-kicker {
  color: #8e998f;
  font-size: 8.5px;
  font-weight: 650;
  letter-spacing: 0.19em;
}

.section-head h2,
.care-heading h2 {
  margin-top: 10px;
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(22px, 2.4vw, 30px);
  font-weight: 500;
  letter-spacing: -0.035em;
  line-height: 1.35;
}

.section-aside {
  padding-bottom: 4px;
  color: #849186;
  font-size: 11px;
}

.app-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-top: 23px;
}

.app-card {
  --accent: #62816c;
  --accent-soft: rgba(185, 208, 185, 0.25);
  --accent-line: rgba(105, 140, 113, 0.27);
  --icon-bg: linear-gradient(145deg, rgba(218, 231, 213, 0.8), rgba(251, 236, 221, 0.6));
  position: relative;
  display: flex;
  flex-direction: column;
  min-height: 322px;
  padding: 25px 27px;
  overflow: hidden;
  border: 1px solid rgba(103, 127, 109, 0.16);
  border-radius: 23px;
  background: rgba(255, 252, 246, 0.54);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.8),
    0 18px 45px rgba(80, 105, 86, 0.065);
  animation: rise 0.85s 0.15s cubic-bezier(0.2, 0.8, 0.2, 1) both;
  transition:
    border-color 0.3s ease,
    box-shadow 0.35s ease,
    transform 0.35s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.app-card:nth-child(2) {
  animation-delay: 0.22s;
}

.app-card--assistant {
  --accent: #a06d58;
  --accent-soft: rgba(237, 209, 191, 0.3);
  --accent-line: rgba(184, 137, 113, 0.29);
  --icon-bg: linear-gradient(145deg, rgba(249, 226, 209, 0.9), rgba(233, 234, 211, 0.66));
}

.app-card::before {
  position: absolute;
  z-index: -1;
  top: -80px;
  right: -70px;
  width: 220px;
  height: 220px;
  border-radius: 50%;
  background: var(--accent-soft);
  content: '';
  filter: blur(6px);
}

.app-card:hover {
  border-color: var(--accent-line);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.88),
    0 26px 52px rgba(74, 102, 82, 0.12);
  transform: translateY(-4px);
}

.card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-index {
  color: #8d998f;
  font-size: 8.5px;
  font-weight: 650;
  letter-spacing: 0.17em;
}

.card-index i {
  margin: 0 3px;
  color: #b6bfb4;
  font-style: normal;
}

.card-arrow {
  display: grid;
  width: 32px;
  height: 32px;
  place-items: center;
  border: 1px solid rgba(90, 115, 97, 0.17);
  border-radius: 50%;
  color: #7d8f82;
  transition:
    background-color 0.25s ease,
    border-color 0.25s ease,
    color 0.25s ease,
    transform 0.3s ease;
}

.card-arrow svg {
  width: 14px;
  height: 14px;
}

.app-card:hover .card-arrow {
  border-color: var(--accent-line);
  background: var(--accent-soft);
  color: var(--accent);
  transform: translate(2px, -2px);
}

.card-main {
  display: flex;
  gap: 15px;
  align-items: flex-start;
  margin-top: 27px;
}

.card-icon {
  display: grid;
  flex: 0 0 auto;
  width: 48px;
  height: 48px;
  place-items: center;
  border: 1px solid var(--accent-line);
  border-radius: 15px;
  background: var(--icon-bg);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.65);
  color: var(--accent);
}

.card-icon :deep(svg) {
  width: 27px;
  height: 27px;
}

.card-copy {
  min-width: 0;
}

.card-english {
  color: var(--accent);
  font-size: 8px;
  font-weight: 650;
  letter-spacing: 0.17em;
}

.card-copy h3 {
  margin-top: 5px;
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(21px, 2.1vw, 26px);
  font-weight: 500;
  letter-spacing: -0.035em;
}

.card-tagline {
  margin-top: 7px;
  color: #68786e;
  font-size: 12px;
  line-height: 1.7;
}

.card-description {
  margin-top: 18px;
  padding-left: 63px;
  color: #7c8a80;
  font-size: 11.5px;
  line-height: 1.75;
}

.card-footer {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 22px;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.card-tags span {
  padding: 4px 8px;
  border: 1px solid rgba(96, 121, 102, 0.12);
  border-radius: 99px;
  background: rgba(255, 255, 255, 0.28);
  color: #75847a;
  font-size: 9.5px;
  letter-spacing: 0.015em;
  white-space: nowrap;
}

.card-enter {
  display: inline-flex;
  gap: 5px;
  align-items: center;
  color: #687b6d;
  font-size: 10.5px;
  white-space: nowrap;
  transition: color 0.25s ease;
}

.card-enter svg {
  width: 13px;
  height: 13px;
  transition: transform 0.25s ease;
}

.app-card:hover .card-enter {
  color: var(--accent);
}

.app-card:hover .card-enter svg {
  transform: translateX(3px);
}

.card-ghost {
  position: absolute;
  right: -19px;
  bottom: -29px;
  width: 155px;
  height: 155px;
  color: var(--accent);
  opacity: 0.065;
  pointer-events: none;
  transform: rotate(-10deg);
}

.care-path {
  margin-top: clamp(66px, 10vh, 108px);
  padding: clamp(24px, 3vw, 34px);
  border: 1px solid rgba(102, 126, 108, 0.13);
  border-radius: 22px;
  background: rgba(255, 253, 247, 0.32);
}

.quality-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 22px;
  padding-bottom: 22px;
  border-bottom: 1px solid rgba(101, 128, 107, 0.12);
  color: #7a8c7e;
  font-size: 10px;
  letter-spacing: 0.035em;
}

.quality-row span {
  display: inline-flex;
  gap: 7px;
  align-items: center;
}

.quality-row i {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #c58a6e;
}

.care-heading {
  padding-top: 26px;
}

.care-heading > p {
  max-width: 360px;
  color: #7d8b81;
  font-size: 11px;
  line-height: 1.85;
}

.care-notes {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 22px;
  margin-top: 28px;
}

.care-note {
  padding-top: 16px;
  border-top: 1px solid rgba(104, 129, 110, 0.18);
}

.note-index {
  color: #c18367;
  font-family: var(--font-display);
  font-size: 11px;
  letter-spacing: 0.08em;
}

.care-note h3 {
  margin-top: 11px;
  color: #53685a;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.01em;
}

.care-note p {
  margin-top: 7px;
  color: #7d8b81;
  font-size: 11.5px;
  line-height: 1.85;
}

.safety-note {
  display: flex;
  gap: 13px;
  align-items: flex-start;
  margin-top: 32px;
  padding: 17px 19px;
  border: 1px solid rgba(186, 149, 119, 0.2);
  border-radius: 14px;
  background: rgba(246, 229, 214, 0.29);
  color: #8b796d;
  font-size: 11px;
  line-height: 1.75;
}

.safety-icon {
  width: 18px;
  height: 18px;
  flex: 0 0 auto;
  margin-top: 1px;
  color: #bb8c70;
}

.safety-note strong {
  display: inline-block;
  margin-right: 7px;
  color: #796657;
  font-weight: 650;
}

.safety-crisis {
  margin-left: auto;
  max-width: 44%;
  color: #9b887a;
}

.home-footer {
  width: min(1320px, 100%);
  margin: auto auto 0;
  padding: 31px 1px 25px;
  border-top: 1px solid var(--line);
  color: #8e998f;
  font-size: 10px;
  letter-spacing: 0.035em;
}

.footer-top {
  display: flex;
  gap: 20px;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 22px;
}

.footer-tagline {
  color: #6d7f72;
  font-family: var(--font-display);
  font-size: 12px;
  letter-spacing: 0.02em;
}

.footer-meta {
  color: #8e9a90;
  font-size: 9.5px;
  letter-spacing: 0.055em;
}

.footer-bottom {
  display: flex;
  gap: 18px;
  align-items: center;
  justify-content: space-between;
  padding-top: 17px;
  border-top: 1px solid rgba(101, 128, 107, 0.1);
  color: #9ca69c;
  font-size: 9px;
}

@keyframes rise {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes status-pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
}

@keyframes breathe {
  0%,
  100% {
    opacity: 0.65;
    transform: scale(0.95);
  }
  50% {
    opacity: 1;
    transform: scale(1.04);
  }
}

@media (max-width: 900px) {
  .home {
    padding: 0 25px;
  }

  .home-header {
    height: 79px;
  }

  .header-edition,
  .header-divider {
    display: none;
  }

  .home-main {
    padding-top: 52px;
  }

  .hero {
    grid-template-columns: 1fr;
    gap: 41px;
  }

  .hero-copy {
    max-width: 720px;
  }

  .hero-visual {
    min-height: auto;
  }

  .breath-card {
    width: min(335px, 78%);
  }

  .app-grid {
    grid-template-columns: 1fr;
  }

  .app-card {
    min-height: 293px;
  }

  .care-notes {
    gap: 18px;
  }

  .safety-note {
    flex-wrap: wrap;
  }

  .safety-crisis {
    max-width: 100%;
    margin-left: 31px;
  }
}

@media (max-width: 620px) {
  .home {
    padding: 0 17px;
  }

  .home-header {
    height: 70px;
  }

  .brand-mark {
    width: 30px;
    height: 30px;
  }

  .brand-copy strong {
    font-size: 17px;
  }

  .brand-copy small {
    font-size: 6.5px;
  }

  .system-status {
    font-size: 8.5px;
  }

  .home-main {
    padding-top: 40px;
    padding-bottom: 23px;
  }

  .hero-title {
    margin-top: 17px;
    font-size: clamp(34px, 10.5vw, 48px);
  }

  .hero-lead {
    margin-top: 21px;
    font-size: 13.5px;
    line-height: 1.92;
  }

  .hero-lead br {
    display: none;
  }

  .hero-actions {
    gap: 14px;
    margin-top: 24px;
  }

  .primary-action {
    height: 42px;
    padding: 0 17px;
    font-size: 11px;
  }

  .hero-note {
    font-size: 9.5px;
  }

  .hero-visual {
    margin-top: 1px;
  }

  .breath-card {
    width: 100%;
    max-width: 315px;
  }

  .visual-quote {
    margin-top: 20px;
    font-size: 11px;
  }

  .agents {
    margin-top: 56px;
  }

  .section-head,
  .care-heading {
    display: block;
  }

  .section-aside {
    display: block;
    margin-top: 9px;
  }

  .section-head h2,
  .care-heading h2 {
    font-size: 24px;
  }

  .app-card {
    min-height: 0;
    padding: 21px 20px;
    border-radius: 19px;
  }

  .card-main {
    gap: 12px;
    margin-top: 22px;
  }

  .card-icon {
    width: 43px;
    height: 43px;
    border-radius: 13px;
  }

  .card-icon :deep(svg) {
    width: 24px;
    height: 24px;
  }

  .card-copy h3 {
    font-size: 22px;
  }

  .card-tagline {
    font-size: 11px;
  }

  .card-description {
    margin-top: 16px;
    padding-left: 0;
    font-size: 10.5px;
  }

  .card-footer {
    flex-wrap: wrap;
    gap: 13px;
    padding-top: 19px;
  }

  .care-path {
    margin-top: 54px;
    padding: 21px 18px;
  }

  .quality-row {
    gap: 8px 16px;
    padding-bottom: 18px;
    font-size: 8.5px;
  }

  .care-heading {
    padding-top: 21px;
  }

  .care-heading > p {
    margin-top: 14px;
    font-size: 10.5px;
  }

  .care-heading > p br {
    display: none;
  }

  .care-notes {
    grid-template-columns: 1fr;
    gap: 0;
    margin-top: 19px;
  }

  .care-note {
    padding: 16px 0;
  }

  .care-note:last-child {
    padding-bottom: 0;
  }

  .care-note p {
    font-size: 10.5px;
  }

  .safety-note {
    display: block;
    margin-top: 22px;
    padding: 14px 15px;
    font-size: 10px;
  }

  .safety-icon {
    display: block;
    margin-bottom: 7px;
  }

  .safety-crisis {
    margin: 6px 0 0;
  }

  .home-footer {
    padding-top: 22px;
  }

  .footer-top,
  .footer-bottom {
    display: block;
  }

  .footer-meta {
    margin-top: 7px;
  }

  .footer-bottom p + p {
    margin-top: 6px;
  }
}
</style>
