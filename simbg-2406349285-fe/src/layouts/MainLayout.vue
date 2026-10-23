<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const breadcrumbText = computed(() => {
  if (route.path === '/' || route.path === '/home') return 'Ringkasan'
  if (route.path.startsWith('/sekolah')) return 'Sekolah'
  if (route.path.startsWith('/anggaran')) return 'Anggaran'
  if (route.path.startsWith('/claim')) return 'Klaim'
  if (route.path.startsWith('/invoice')) return 'Invoice'
  return 'Beranda'
})

function reloadPage() {
  window.location.reload()
}
</script>

<template>
  <div class="flex min-h-screen bg-[#f8fafc] text-slate-800">
    <!-- Sidebar -->
    <aside class="w-64 bg-[#093426] text-white flex flex-col justify-between shrink-0 shadow-xl border-r border-[#0d4734]">
      <div>
        <!-- Brand Header -->
        <router-link to="/home" class="p-6 flex items-center gap-3 border-b border-[#124433]" title="Ke Beranda">
          <div class="w-10 h-10 rounded-xl bg-[#e06d53] flex items-center justify-center text-white shadow-md">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
            </svg>
          </div>
          <div>
            <h1 class="font-extrabold text-lg leading-tight tracking-tight">SIMBG</h1>
            <p class="text-xs text-emerald-300 font-medium">Manajemen Bantuan</p>
          </div>
        </router-link>

        <!-- Navigation Menu -->
        <div class="px-4 py-6">
          <p class="text-[11px] font-bold text-emerald-400 uppercase tracking-wider mb-3 px-3">WORKSPACE</p>
          <nav class="space-y-1">
            <router-link
              to="/"
              class="flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-semibold transition-all group hover:bg-[#124433] text-emerald-100"
              active-class="bg-[#17523e] text-white shadow-sm ring-1 ring-emerald-500/30"
              exact
            >
              <div class="flex items-center gap-3">
                <svg class="w-5 h-5 opacity-80 group-hover:opacity-100" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path>
                </svg>
                <span>Ringkasan</span>
              </div>
              <span class="text-xs opacity-60">›</span>
            </router-link>

            <router-link
              to="/sekolah"
              class="flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-semibold transition-all group hover:bg-[#124433] text-emerald-100"
              active-class="bg-[#17523e] text-white shadow-sm ring-1 ring-emerald-500/30"
            >
              <div class="flex items-center gap-3">
                <svg class="w-5 h-5 opacity-80 group-hover:opacity-100" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l9-5-9-5-9 5 9 5z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z"></path>
                </svg>
                <span>Sekolah</span>
              </div>
              <span class="text-xs opacity-60">›</span>
            </router-link>

            <router-link
              to="/anggaran"
              class="flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-semibold transition-all group hover:bg-[#124433] text-emerald-100"
              active-class="bg-[#17523e] text-white shadow-sm ring-1 ring-emerald-500/30"
            >
              <div class="flex items-center gap-3">
                <svg class="w-5 h-5 opacity-80 group-hover:opacity-100" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
                </svg>
                <span>Anggaran</span>
              </div>
              <span class="text-xs opacity-60">›</span>
            </router-link>

            <router-link
              to="/claim"
              class="flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-semibold transition-all group hover:bg-[#124433] text-emerald-100"
              active-class="bg-[#17523e] text-white shadow-sm ring-1 ring-emerald-500/30"
            >
              <div class="flex items-center gap-3">
                <svg class="w-5 h-5 opacity-80 group-hover:opacity-100" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"></path>
                </svg>
                <span>Klaim</span>
              </div>
              <span class="text-xs opacity-60">›</span>
            </router-link>

            <router-link
              to="/invoice"
              class="flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-semibold transition-all group hover:bg-[#124433] text-emerald-100"
              active-class="bg-[#17523e] text-white shadow-sm ring-1 ring-emerald-500/30"
            >
              <div class="flex items-center gap-3">
                <svg class="w-5 h-5 opacity-80 group-hover:opacity-100" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
                <span>Invoice</span>
              </div>
              <span class="text-xs opacity-60">›</span>
            </router-link>
          </nav>
        </div>
      </div>

      <!-- Bottom User & Status -->
      <div class="p-4 space-y-4">
        <!-- Active Period Card -->
        <div class="p-3.5 rounded-2xl bg-[#0d4432] border border-[#145a43] relative overflow-hidden">
          <div class="flex items-center gap-2 mb-1">
            <span class="w-5 h-5 rounded-lg bg-emerald-500/20 flex items-center justify-center text-emerald-300">
              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
              </svg>
            </span>
            <span class="text-[11px] font-medium text-emerald-200">Periode aktif</span>
          </div>
          <div class="text-xl font-extrabold text-white">2026</div>
          <p class="text-[11px] text-emerald-300/80 mt-0.5">Pengelolaan bantuan sekolah</p>
        </div>

        <!-- Administrator Info -->
        <div class="pt-3 border-t border-[#124433] flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="w-9 h-9 rounded-full bg-[#f28e79] text-white flex items-center justify-center font-bold text-xs shadow">
              AD
            </div>
            <div>
              <p class="text-xs font-bold text-white leading-tight">Administrator</p>
              <p class="text-[10px] text-emerald-300 leading-tight">Pengelola sistem</p>
            </div>
          </div>
          <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 ring-4 ring-emerald-500/20"></span>
        </div>
      </div>
    </aside>

    <!-- Main Content Area -->
    <div class="flex-1 flex flex-col min-w-0 overflow-y-auto">
      <!-- Topbar Header -->
      <header class="h-16 bg-white border-b border-slate-200/80 px-8 flex items-center justify-between shrink-0">
        <!-- Breadcrumbs -->
        <div class="flex items-center gap-2 text-xs font-medium text-slate-500">
          <span class="text-slate-400 font-semibold">SIMBG</span>
          <span>›</span>
          <span class="text-slate-800 font-bold capitalize">{{ breadcrumbText }}</span>
        </div>

        <!-- Right action icons -->
        <div class="flex items-center gap-4">
          <div class="flex items-center gap-2 px-3 py-1.5 rounded-full bg-emerald-50 border border-emerald-100 text-xs font-semibold text-emerald-700">
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            <span>Sistem operasional</span>
          </div>

          <button
            @click="reloadPage"
            class="p-2 rounded-xl text-slate-500 hover:text-slate-800 hover:bg-slate-100 transition-colors"
            title="Muat ulang halaman"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
          </button>

          <div class="w-8 h-8 rounded-full bg-[#f28e79] text-white flex items-center justify-center font-bold text-xs shadow-sm">
            AD
          </div>
        </div>
      </header>

      <!-- Main Slot Content -->
      <main class="flex-1 p-8">
        <slot />
      </main>
    </div>
  </div>
</template>
