<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { simbgService } from '@/services/simbg.service'
import type { Sekolah } from '@/types'

const loading = ref(true)
const schools = ref<Sekolah[]>([])
const searchQuery = ref('')

async function loadData() {
  loading.value = true
  try {
    const data = await simbgService.getSekolah()
    schools.value = data || []
  } catch (error) {
    console.error('Failed to load schools:', error)
  } finally {
    loading.value = false
  }
}

const filteredSchools = computed(() => {
  if (!searchQuery.value.trim()) return schools.value
  const q = searchQuery.value.toLowerCase()
  return schools.value.filter(s =>
    s.nama.toLowerCase().includes(q) ||
    s.id.toLowerCase().includes(q) ||
    (s.address && s.address.toLowerCase().includes(q))
  )
})

function getBadgeColor(level: string) {
  switch (level?.toUpperCase()) {
    case 'SD':
      return 'bg-emerald-50 text-emerald-700 border-emerald-200'
    case 'SMP':
      return 'bg-blue-50 text-blue-700 border-blue-200'
    case 'SMA':
      return 'bg-purple-50 text-purple-700 border-purple-200'
    case 'SMK':
      return 'bg-amber-50 text-amber-700 border-amber-200'
    default:
      return 'bg-slate-50 text-slate-700 border-slate-200'
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="space-y-6 max-w-7xl mx-auto">
    <!-- Header -->
    <div>
      <div class="flex items-center gap-2 mb-1.5">
        <span class="w-2.5 h-0.5 bg-[#e06d53] rounded-full"></span>
        <span class="text-xs font-bold uppercase tracking-wider text-slate-500">DIREKTORI INSTITUSI</span>
      </div>
      <h2 class="text-3xl font-extrabold text-slate-900 tracking-tight">Data sekolah</h2>
      <p class="text-sm text-slate-500 mt-1">Institusi pendidikan yang terdaftar dalam program bantuan.</p>
    </div>

    <!-- Table Card Container -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
      <!-- Search and Subheader -->
      <div class="p-6 border-b border-slate-100 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h3 class="font-bold text-base text-slate-900">Direktori sekolah</h3>
          <p class="text-xs text-slate-500 mt-0.5">{{ schools.length }} institusi terdaftar</p>
        </div>

        <div class="relative w-full sm:w-72">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Cari sekolah..."
            class="w-full pl-9 pr-4 py-2 text-xs rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 transition-all bg-slate-50/50"
          />
          <svg class="w-4 h-4 text-slate-400 absolute left-3 top-2.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
        </div>
      </div>

      <!-- Table -->
      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs">
          <thead class="bg-slate-50/75 border-b border-slate-100 text-[11px] font-bold text-slate-500 uppercase tracking-wider">
            <tr>
              <th class="py-3.5 px-6">INSTITUSI</th>
              <th class="py-3.5 px-6">ID SEKOLAH</th>
              <th class="py-3.5 px-6">JENJANG</th>
              <th class="py-3.5 px-6">ALAMAT</th>
              <th class="py-3.5 px-6 text-right">ANGGARAN AKTIF</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-if="loading">
              <td colspan="5" class="py-12 text-center text-slate-400 font-medium">Memuat data sekolah...</td>
            </tr>
            <tr v-else-if="filteredSchools.length === 0">
              <td colspan="5" class="py-12 text-center text-slate-400 font-medium">Tidak ada data sekolah ditemukan.</td>
            </tr>
            <tr
              v-for="s in filteredSchools"
              :key="s.id"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <td class="py-4 px-6">
                <div class="flex items-center gap-3">
                  <div class="w-8 h-8 rounded-lg bg-emerald-50 text-emerald-700 font-bold flex items-center justify-center shrink-0 border border-emerald-100">
                    S
                  </div>
                  <span class="font-bold text-slate-900">{{ s.nama }}</span>
                </div>
              </td>
              <td class="py-4 px-6 font-mono text-slate-500">{{ s.id }}</td>
              <td class="py-4 px-6">
                <span :class="['px-2.5 py-1 rounded-full text-[11px] font-bold border', getBadgeColor(s.educationLevel)]">
                  {{ s.educationLevel }}
                </span>
              </td>
              <td class="py-4 px-6 text-slate-600 max-w-xs truncate">{{ s.address || '-' }}</td>
              <td class="py-4 px-6 text-right font-medium text-slate-700">
                {{ s.activeAnggaranCount !== undefined ? s.activeAnggaranCount + ' periode' : '-' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
