<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { simbgService } from '@/services/simbg.service'
import { formatRupiah } from '@/utils/formatters'
import type { DashboardData } from '@/types'

const loading = ref(true)
const dashboard = ref<DashboardData>({
  jumlahSekolah: 0,
  totalAnggaran: 0,
  jumlahAnggaran: 0,
  jumlahClaim: 0,
  totalNominalClaim: 0,
  jumlahInvoice: 0,
  totalNominalInvoice: 0,
})

const currentDateStr = new Intl.DateTimeFormat('id-ID', {
  weekday: 'long',
  day: 'numeric',
  month: 'long',
  year: 'numeric',
}).format(new Date())

async function loadData() {
  loading.value = true
  try {
    const data = await simbgService.getDashboard()
    if (data) {
      dashboard.value = data
    }
  } catch (error) {
    console.error('Failed to load dashboard:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="space-y-8 max-w-7xl mx-auto">
    <!-- Page Header -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <div class="flex items-center gap-2 mb-1.5">
          <span class="w-2.5 h-0.5 bg-[#e06d53] rounded-full"></span>
          <span class="text-xs font-bold uppercase tracking-wider text-slate-500">MONITORING PROGRAM</span>
        </div>
        <h2 class="text-3xl font-extrabold text-slate-900 tracking-tight">Ringkasan program</h2>
        <p class="text-sm text-slate-500 mt-1">Ikhtisar pengelolaan bantuan dan pencairan dana sekolah.</p>
      </div>

      <div class="flex items-center gap-2 text-xs font-semibold text-slate-500 bg-white px-3.5 py-2 rounded-xl border border-slate-200 shadow-sm self-start md:self-auto">
        <svg class="w-4 h-4 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
        </svg>
        <span>{{ currentDateStr }}</span>
      </div>
    </div>

    <!-- Summary Cards Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
      <!-- Card 1: Total Anggaran -->
      <div class="p-6 rounded-2xl bg-[#093426] text-white shadow-lg relative overflow-hidden flex flex-col justify-between">
        <div class="flex items-start justify-between">
          <div>
            <p class="text-xs font-medium text-emerald-300">Total anggaran</p>
            <h3 class="text-2xl font-black mt-2 tracking-tight">
              {{ formatRupiah(dashboard.totalAnggaran) }}
            </h3>
          </div>
          <div class="w-10 h-10 rounded-xl bg-emerald-500/20 border border-emerald-400/30 flex items-center justify-center text-emerald-300">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
            </svg>
          </div>
        </div>

        <div class="mt-8 pt-4 border-t border-[#145a43] flex items-center justify-between text-xs text-emerald-200">
          <span>{{ dashboard.jumlahAnggaran }} periode anggaran</span>
          <router-link to="/anggaran" class="font-semibold text-emerald-300">Lihat ↗</router-link>
        </div>
      </div>

      <!-- Card 2: Sekolah Terdaftar -->
      <div class="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
        <div class="flex items-start justify-between">
          <div>
            <p class="text-xs font-medium text-slate-500">Sekolah terdaftar</p>
            <h3 class="text-3xl font-extrabold text-slate-900 mt-2">
              {{ dashboard.jumlahSekolah }}
            </h3>
          </div>
          <div class="w-10 h-10 rounded-xl bg-emerald-50 border border-emerald-100 flex items-center justify-center text-emerald-600">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
            </svg>
          </div>
        </div>

        <div class="mt-8 pt-4 border-t border-slate-100 flex items-center justify-between text-xs">
          <span class="text-slate-500">Institusi penerima program</span>
          <router-link to="/sekolah" class="font-bold text-emerald-700 hover:text-emerald-800 flex items-center gap-1">
            Lihat <span>↗</span>
          </router-link>
        </div>
      </div>

      <!-- Card: Total Claim -->
      <div class="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
        <div>
          <p class="text-xs font-medium text-slate-500">Total claim</p>
          <h3 class="text-3xl font-extrabold text-slate-900 mt-2">{{ dashboard.jumlahClaim }}</h3>
        </div>
        <div class="mt-8 pt-4 border-t border-slate-100 flex items-center justify-between text-xs">
          <span class="text-slate-500">Nilai {{ formatRupiah(dashboard.totalNominalClaim) }}</span>
          <router-link to="/claim" class="font-bold text-emerald-700 hover:text-emerald-800 flex items-center gap-1">
            Detail <span>↗</span>
          </router-link>
        </div>
      </div>

      <!-- Card 3: Invoice Diterbitkan -->
      <div class="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
        <div class="flex items-start justify-between">
          <div>
            <p class="text-xs font-medium text-slate-500">Invoice diterbitkan</p>
            <h3 class="text-3xl font-extrabold text-slate-900 mt-2">
              {{ dashboard.jumlahInvoice }}
            </h3>
          </div>
          <div class="w-10 h-10 rounded-xl bg-orange-50 border border-orange-100 flex items-center justify-center text-[#e06d53]">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
            </svg>
          </div>
        </div>

        <div class="mt-8 pt-4 border-t border-slate-100 flex items-center justify-between text-xs">
          <span class="text-slate-500">Nilai {{ formatRupiah(dashboard.totalNominalInvoice) }}</span>
          <router-link to="/invoice" class="font-bold text-emerald-700 hover:text-emerald-800 flex items-center gap-1">
            Detail <span>↗</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>
