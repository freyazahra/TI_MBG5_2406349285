<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { simbgService } from '@/services/simbg.service'
import { formatRupiah, formatDate } from '@/utils/formatters'
import type { StatistikData } from '@/types'

const loading = ref(true)
const statistik = ref<StatistikData>({
  claimStatistics: [],
  invoiceStatistics: [],
})

async function loadData() {
  loading.value = true
  try {
    const data = await simbgService.getStatistik()
    if (data) {
      statistik.value = data
    }
  } catch (error) {
    console.error('Failed to load statistik:', error)
  } finally {
    loading.value = false
  }
}

function getStatusColor(status: string) {
  switch (status?.toUpperCase()) {
    case 'DISETUJUI':
      return 'bg-emerald-600'
    case 'DIAJUKAN':
      return 'bg-amber-500'
    case 'DITOLAK':
      return 'bg-[#e06d53]'
    case 'DIBATALKAN':
    default:
      return 'bg-slate-300'
  }
}

function calculateMaxInvoice(): number {
  if (!statistik.value.invoiceStatistics || statistik.value.invoiceStatistics.length === 0) return 1
  return Math.max(...statistik.value.invoiceStatistics.map(i => i.totalNominal), 1)
}

function calculateMaxClaim(): number {
  if (!statistik.value.claimStatistics || statistik.value.claimStatistics.length === 0) return 1
  return Math.max(...statistik.value.claimStatistics.map(c => c.totalNominal), 1)
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="space-y-8 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <div class="flex items-center gap-2 mb-1.5">
          <span class="w-2.5 h-0.5 bg-[#e06d53] rounded-full"></span>
          <span class="text-xs font-bold uppercase tracking-wider text-slate-500">STATISTIK KLAIM & INVOICE</span>
        </div>
        <h2 class="text-3xl font-extrabold text-slate-900 tracking-tight">Statistik serapan anggaran</h2>
        <p class="text-sm text-slate-500 mt-1">Laporan tren pembayaran dan status pengajuan klaim program.</p>
      </div>

      <router-link
        to="/invoice"
        class="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-bold transition-all"
      >
        <span>← Kembali ke Invoice</span>
      </router-link>
    </div>

    <!-- Section 1: Tren Pembayaran (Invoice per bulan) -->
    <div class="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-sm">
      <div class="flex items-center justify-between pb-6 border-b border-slate-100">
        <div>
          <p class="text-[11px] font-bold uppercase tracking-wider text-slate-400">TREN PEMBAYARAN</p>
          <h3 class="text-lg font-extrabold text-slate-900 mt-0.5">Invoice per bulan</h3>
        </div>
        <span class="text-xs font-bold text-slate-400">{{ statistik.invoiceStatistics.length }} periode</span>
      </div>

      <div v-if="loading" class="text-center py-12 text-slate-400 text-xs">
        Memuat data statistik...
      </div>

      <div v-else-if="statistik.invoiceStatistics.length === 0" class="text-center py-12 text-slate-400 text-xs">
        Belum ada data invoice bulanan.
      </div>

      <!-- Chart Bars -->
      <div v-else class="pt-8 pb-4 flex items-end gap-12 justify-start overflow-x-auto min-h-[260px] px-6">
        <div
          v-for="inv in statistik.invoiceStatistics"
          :key="inv.status"
          class="flex flex-col items-center gap-3 shrink-0"
        >
          <!-- Value Label -->
          <span class="text-[11px] font-bold text-slate-600">
            {{ formatRupiah(inv.totalNominal) }}
          </span>

          <!-- Bar -->
          <div class="w-16 bg-slate-100 rounded-t-xl h-44 flex items-end overflow-hidden p-1">
            <div
              class="w-full bg-[#124433] rounded-t-lg transition-all duration-500"
              :style="{ height: `${Math.max(12, Math.round((inv.totalNominal / calculateMaxInvoice()) * 100))}%` }"
            ></div>
          </div>

          <!-- Month Label -->
          <div class="text-center">
            <p class="text-xs font-bold text-slate-800">{{ formatDate(inv.status) }}</p>
            <p class="text-[10px] text-slate-400 mt-0.5">{{ inv.jumlah }} invoice</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Section 2: Ringkasan Pengajuan (Status Klaim) -->
    <div class="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-sm">
      <div class="pb-6 border-b border-slate-100">
        <p class="text-[11px] font-bold uppercase tracking-wider text-slate-400">RINGKASAN PENGAJUAN</p>
        <h3 class="text-lg font-extrabold text-slate-900 mt-0.5">Status klaim</h3>
      </div>

      <div v-if="loading" class="text-center py-12 text-slate-400 text-xs">
        Memuat data klaim...
      </div>

      <div v-else class="space-y-6 pt-6">
        <div
          v-for="c in statistik.claimStatistics"
          :key="c.status"
          class="grid grid-cols-1 md:grid-cols-12 gap-4 items-center text-xs"
        >
          <!-- Status Name -->
          <div class="md:col-span-2 flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full shrink-0" :class="getStatusColor(c.status)"></span>
            <span class="font-bold text-slate-800">{{ c.status }}</span>
          </div>

          <!-- Progress Bar -->
          <div class="md:col-span-8">
            <div class="w-full h-3 bg-slate-100 rounded-full overflow-hidden">
              <div
                class="h-full rounded-full transition-all duration-500"
                :class="getStatusColor(c.status)"
                :style="{ width: `${c.totalNominal > 0 ? Math.max(5, Math.round((c.totalNominal / calculateMaxClaim()) * 100)) : 0}%` }"
              ></div>
            </div>
          </div>

          <!-- Count & Nominal -->
          <div class="md:col-span-2 flex items-center justify-between md:justify-end gap-6 text-right">
            <span class="font-bold text-slate-900 text-sm w-8">{{ c.jumlah }}</span>
            <span class="font-semibold text-slate-600 min-w-[90px]">{{ formatRupiah(c.totalNominal) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
