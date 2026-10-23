<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { simbgService } from '@/services/simbg.service'
import { formatRupiah, formatDate } from '@/utils/formatters'
import type { PaymentInvoice, Claim, CreateInvoicePayload } from '@/types'

const router = useRouter()
const loading = ref(true)
const invoices = ref<PaymentInvoice[]>([])
const claims = ref<Claim[]>([])
const searchQuery = ref('')

const isModalOpen = ref(false)
const submitting = ref(false)
const form = ref<CreateInvoicePayload>({
  claimId: '',
  invoiceDate: '',
  dueDate: '',
})

async function loadData() {
  loading.value = true
  try {
    const [dataInvoices, dataClaims] = await Promise.all([
      simbgService.getAllInvoices(),
      simbgService.getAllClaims(),
    ])
    invoices.value = dataInvoices || []
    claims.value = dataClaims || []
  } catch (error) {
    console.error('Failed to load invoices:', error)
  } finally {
    loading.value = false
  }
}

// Eligible claims for invoice creation:
// status == 'DISETUJUI', totalClaim > 0, and not already invoiced
const eligibleClaims = computed(() => {
  const invoicedClaimIds = new Set(invoices.value.map(i => i.claimId))
  return claims.value.filter(c =>
    c.status === 'DISETUJUI' &&
    c.totalClaim > 0 &&
    !invoicedClaimIds.has(c.id)
  )
})

const totalAmount = computed(() =>
  invoices.value.reduce((sum, inv) => sum + (inv.totalAmount || 0), 0)
)

const unpaidCount = computed(() =>
  invoices.value.filter(inv => inv.status === 'BELUM_DIBAYAR').length
)

const filteredInvoices = computed(() => {
  if (!searchQuery.value.trim()) return invoices.value
  const q = searchQuery.value.toLowerCase()
  return invoices.value.filter(i =>
    i.invoiceNumber.toLowerCase().includes(q) ||
    i.claimId.toLowerCase().includes(q) ||
    i.status.toLowerCase().includes(q)
  )
})

function openCreateModal() {
  const today = new Date().toISOString().split('T')[0] || ''
  const next2Weeks = new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().split('T')[0] || ''

  form.value = {
    claimId: eligibleClaims.value.length > 0 ? (eligibleClaims.value[0]?.id || '') : '',
    invoiceDate: today,
    dueDate: next2Weeks,
  }
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
}

async function handleSubmit() {
  submitting.value = true
  try {
    const created = await simbgService.createInvoice(form.value)
    closeModal()
    router.push(`/invoice/${created.invoiceNumber}`)
  } catch (error) {
    console.error('Failed to create invoice:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="space-y-6 max-w-7xl mx-auto">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
      <div>
        <div class="flex items-center gap-2 mb-1.5">
          <span class="w-2.5 h-0.5 bg-[#e06d53] rounded-full"></span>
          <span class="text-xs font-bold uppercase tracking-wider text-slate-500">ADMINISTRASI PEMBAYARAN</span>
        </div>
        <h2 class="text-3xl font-extrabold text-slate-900 tracking-tight">Invoice pembayaran</h2>
        <p class="text-sm text-slate-500 mt-1">Pantau tagihan dan administrasi pencairan bantuan sekolah.</p>
      </div>

      <div class="flex items-center gap-3">
        <router-link
          to="/invoice/statistik"
          class="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-bold transition-all"
        >
          <svg class="w-4 h-4 text-slate-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
          </svg>
          <span>Statistik Claim Invoice</span>
        </router-link>

        <button
          @click="openCreateModal"
          class="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#093426] hover:bg-[#124433] text-white text-xs font-bold shadow-md transition-all cursor-pointer"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
          </svg>
          <span>Terbitkan invoice</span>
        </button>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex flex-col justify-between">
        <div>
          <p class="text-xs font-medium text-slate-500">Total invoice</p>
          <h3 class="text-3xl font-extrabold text-slate-900 mt-2">{{ invoices.length }}</h3>
        </div>
        <p class="text-xs text-slate-400 mt-4 pt-3 border-t border-slate-100">Seluruh invoice terdaftar</p>
      </div>

      <div class="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex flex-col justify-between">
        <div>
          <p class="text-xs font-medium text-slate-500">Total nilai tagihan</p>
          <h3 class="text-2xl font-black text-slate-900 mt-2">{{ formatRupiah(totalAmount) }}</h3>
        </div>
        <p class="text-xs text-slate-400 mt-4 pt-3 border-t border-slate-100">Nilai keseluruhan invoice</p>
      </div>

      <div class="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex flex-col justify-between">
        <div>
          <p class="text-xs font-medium text-slate-500">Belum dibayar</p>
          <h3 class="text-3xl font-extrabold text-[#e06d53] mt-2">{{ unpaidCount }}</h3>
        </div>
        <p class="text-xs text-slate-400 mt-4 pt-3 border-t border-slate-100">Menunggu penyelesaian pembayaran</p>
      </div>
    </div>

    <!-- Table Card Container -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
      <!-- Search and Subheader -->
      <div class="p-6 border-b border-slate-100 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h3 class="font-bold text-base text-slate-900">Riwayat invoice</h3>
          <p class="text-xs text-slate-500 mt-0.5">{{ invoices.length }} tagihan</p>
        </div>

        <div class="relative w-full sm:w-72">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Cari nomor invoice..."
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
              <th class="py-3.5 px-6">NOMOR INVOICE</th>
              <th class="py-3.5 px-6">TANGGAL TERBIT</th>
              <th class="py-3.5 px-6">JATUH TEMPO</th>
              <th class="py-3.5 px-6">ID KLAIM</th>
              <th class="py-3.5 px-6">NILAI TAGIHAN</th>
              <th class="py-3.5 px-6">STATUS</th>
              <th class="py-3.5 px-6 text-right">AKSI</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-if="loading">
              <td colspan="7" class="py-12 text-center text-slate-400 font-medium">Memuat data invoice...</td>
            </tr>
            <tr v-else-if="filteredInvoices.length === 0">
              <td colspan="7" class="py-12 text-center text-slate-400 font-medium">Tidak ada data invoice.</td>
            </tr>
            <tr
              v-for="inv in filteredInvoices"
              :key="inv.invoiceNumber"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <td class="py-4 px-6 font-mono font-bold text-slate-900">{{ inv.invoiceNumber }}</td>
              <td class="py-4 px-6 text-slate-600">{{ formatDate(inv.invoiceDate) }}</td>
              <td class="py-4 px-6 text-slate-600">{{ formatDate(inv.dueDate) }}</td>
              <td class="py-4 px-6 font-mono text-slate-500">{{ inv.claimId }}</td>
              <td class="py-4 px-6 font-bold text-slate-900">{{ formatRupiah(inv.totalAmount) }}</td>
              <td class="py-4 px-6">
                <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-amber-50 text-amber-800 border border-amber-200">
                  {{ inv.status.replace('_', ' ') }}
                </span>
              </td>
              <td class="py-4 px-6 text-right">
                <router-link
                  :to="`/invoice/${inv.invoiceNumber}`"
                  class="inline-flex items-center justify-center w-8 h-8 rounded-lg hover:bg-slate-100 text-slate-500 hover:text-slate-900 transition-colors"
                >
                  ›
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Terbitkan Invoice -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
      <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-150">
        <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-5">
          <div>
            <p class="text-[10px] font-bold uppercase tracking-wider text-slate-400">INPUT DATA</p>
            <h3 class="text-lg font-extrabold text-slate-900">Terbitkan invoice</h3>
          </div>
          <button @click="closeModal" class="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100">
            ✕
          </button>
        </div>

        <form @submit.prevent="handleSubmit" class="space-y-4 text-xs">
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Klaim yang akan ditagihkan *</label>
            <select v-model="form.claimId" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 bg-white">
              <option v-for="c in eligibleClaims" :key="c.id" :value="c.id">
                {{ c.id }} · {{ formatRupiah(c.totalClaim) }} · {{ c.status }}
              </option>
            </select>
            <p class="text-[11px] text-slate-400 mt-1">Hanya klaim bernilai di atas Rp0 yang belum memiliki invoice.</p>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Tanggal invoice *</label>
              <input v-model="form.invoiceDate" type="date" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
            </div>

            <div>
              <label class="block font-semibold text-slate-700 mb-1">Jatuh tempo *</label>
              <input v-model="form.dueDate" type="date" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
            </div>
          </div>

          <div class="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
            <button type="button" @click="closeModal" class="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 font-semibold hover:bg-slate-50 cursor-pointer">
              Batal
            </button>
            <button type="submit" :disabled="submitting || eligibleClaims.length === 0" class="px-5 py-2 rounded-xl bg-[#093426] hover:bg-[#124433] text-white font-bold flex items-center gap-1.5 cursor-pointer shadow">
              <span v-if="submitting">Menyimpan...</span>
              <span v-else>✓ Simpan perubahan</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
