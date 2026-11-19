<script setup>
defineProps({
  name: {
    type: String,
    default: 'spark',
  },
})
</script>

<template>
  <svg
    v-if="name === 'bloom'"
    class="app-icon"
    viewBox="0 0 32 32"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <path d="M16 26.2V13.4" stroke="currentColor" stroke-width="1.45" stroke-linecap="round" />
    <path
      d="M16 20.2c-5.15 0-8.2-3.05-8.2-7.25 5.15 0 8.2 3.05 8.2 7.25Z"
      stroke="currentColor"
      stroke-width="1.4"
      stroke-linejoin="round"
    />
    <path
      d="M16 17.35c0-4.8 3.05-7.85 7.25-7.85 0 4.8-3.05 7.85-7.25 7.85Z"
      stroke="currentColor"
      stroke-width="1.4"
      stroke-linejoin="round"
    />
    <path d="M10.4 27h11.2" stroke="currentColor" stroke-width="1.45" stroke-linecap="round" />
    <circle cx="24.2" cy="6.6" r="2.3" fill="currentColor" fill-opacity="0.8" />
  </svg>

  <svg
    v-else-if="name === 'compass'"
    class="app-icon"
    viewBox="0 0 32 32"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <circle cx="16" cy="16" r="11.25" stroke="currentColor" stroke-width="1.4" />
    <path
      d="m20.75 11.25-2.1 6.05-6.05 2.1 2.1-6.05 6.05-2.1Z"
      stroke="currentColor"
      stroke-width="1.35"
      stroke-linejoin="round"
    />
    <circle cx="16" cy="16" r="1.45" fill="currentColor" />
    <path d="M16 3.3v2.5m0 20.4v2.5M3.3 16h2.5m20.4 0h2.5" stroke="currentColor" stroke-width="1.25" stroke-linecap="round" opacity="0.6" />
  </svg>

  <svg
    v-else
    class="app-icon"
    viewBox="0 0 32 32"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <path
      d="M16 3.5c.86 6.17 4.33 9.64 10.5 10.5-6.17.86-9.64 4.33-10.5 10.5C15.14 18.33 11.67 14.86 5.5 14 11.67 13.14 15.14 9.67 16 3.5Z"
      stroke="currentColor"
      stroke-width="1.45"
      stroke-linejoin="round"
    />
    <path d="m25 21 .42 1.5 1.5.42-1.5.42L25 25l-.42-1.66-1.5-.42 1.5-.42L25 21Z" fill="currentColor" />
    <circle cx="7.2" cy="24.8" r="1.25" fill="currentColor" fill-opacity="0.55" />
  </svg>
</template>

<style scoped>
.app-icon {
  display: block;
  width: 100%;
  height: 100%;
}
</style>
