<script setup>
import { RouterView, useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <RouterView v-slot="{ Component }">
    <Transition name="page" mode="out-in">
      <component :is="Component" :key="route.name" />
    </Transition>
  </RouterView>
</template>

<style>
.page-enter-active,
.page-leave-active {
  transition:
    opacity 0.28s ease,
    transform 0.28s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(7px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

@media (prefers-reduced-motion: reduce) {
  .page-enter-active,
  .page-leave-active {
    transition: none;
  }
}
</style>
