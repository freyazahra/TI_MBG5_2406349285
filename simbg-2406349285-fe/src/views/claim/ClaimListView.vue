<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { simbgService } from '@/services/simbg.service'
import { formatRupiah, formatDate } from '@/utils/formatters'
import type { Claim, Anggaran, CreateClaimPayload } from '@/types'

const router = useRouter()
const loading = ref(true)
const claims = ref<Claim[]>([])
const anggaranList = ref<Anggaran[]>([])
const searchQuery = ref('')

const isModalOpen = ref(false)
const submitting = ref(false)
const form = ref<CreateClaimPayload>({
  anggaranId: '',
  claimDate: '',
  description: '',
})

async function loadData() {
  loading.value = true
  try {
    const [dataClaims, dataAnggaran] = await Promise.all([
      simbgService.getAllClaims(),
      simbgService.getAllAnggaran(),
    ])
    claims.value = dataClaims || []
    anggaranList.value = dataAnggaran || []
    if (anggaranList.value.length > 0 && !form.value.anggaranId) {
      form.value.anggaranId = anggaranList.value[0]?.id || ''
    }
  } catch (error) {
    console.error('Failed to load claims:', error)
  } finally {
    loading.value = false
  }
}

const statsSummary = computed(() => {
  const total = claims.value.length
  const disetujui = claims.value.filter(c => c.status === 'DISETUJUI').length
  const review = claims.value.filter(c => c.status === 'DIAJUKAN' || c.status === 'DIVERIFIKASI').length
  return `${total} klaim tercatat · ${disetujui} disetujui · ${review} menunggu tinjauan`
})

const filteredClaims = computed(() => {
  if (!searchQuery.value.trim()) return claims.value
  const q = searchQuery.value.toLowerCase()
  return claims.value.filter(c =>
    c.id.toLowerCase().includes(q) ||
    c.status.toLowerCase().includes(q) ||
    (c.anggaranId && c.anggaranId.toLowerCase().includes(q))
  )
})

function getStatusBadge(status: string) {
  switch (status?.toUpperCase()) {
    case 'DISETUJUI':
      return 'bg-emerald-50 text-emerald-700 border-emerald-200'
    case 'DIAJUKAN':
      return 'bg-amber-50 text-amber-700 border-amber-200'
    case 'DIVERIFIKASI':
      return 'bg-blue-50 text-blue-700 border-blue-200'
    case 'DITOLAK':
      return 'bg-rose-50 text-rose-700 border-rose-200'
    case 'DIBATALKAN':
      return 'bg-slate-100 text-slate-600 border-slate-300'
    case 'DRAFT':
    default:
      return 'bg-amber-50 text-amber-800 border-amber-200'
  }
}

function openCreateModal() {
  form.value = {
    anggaranId: anggaranList.value.length > 0 ? (anggaranList.value[0]?.id || '') : '',
    claimDate: new Date().toISOString().split('T')[0] || '',
    description: '',
  }
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
}

async function handleSubmit() {
  submitting.value = true
  try {
    const created = await simbgService.createClaim(form.value)
    closeModal()
    router.push(`/claim/${created.id}`)
  } catch (error) {
    console.error('Failed to create claim:', error)
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
          <span class="text-xs font-bold uppercase tracking-wider text-slate-500">VERIFIKASI PENGAJUAN</span>
        </div>
        <h2 class="text-3xl font-extrabold text-slate-900 tracking-tight">Manajemen klaim</h2>
        <p class="text-sm text-slate-500 mt-1">Tinjau pengajuan, kelola bukti item, dan perbarui status klaim.</p>
        
        <!-- Summary pill -->
        <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-slate-100/80 border border-slate-200 text-xs font-semibold text-slate-600 mt-3">
          <svg class="w-3.5 h-3.5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
          </svg>
          <span>{{ statsSummary }}</span>
        </div>
      </div>

      <button
        @click="openCreateModal"
        class="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#093426] hover:bg-[#124433] text-white text-xs font-bold shadow-md transition-all self-start sm:self-auto cursor-pointer"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
        </svg>
        <span>Buat klaim</span>
      </button>
    </div>

    <!-- Table Card Container -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
      <!-- Search and Subheader -->
      <div class="p-6 border-b border-slate-100 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h3 class="font-bold text-base text-slate-900">Daftar klaim</h3>
          <p class="text-xs text-slate-500 mt-0.5">Pengajuan dan rincian klaim sekolah</p>
        </div>

        <div class="relative w-full sm:w-72">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Cari ID atau status..."
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
              <th class="py-3.5 px-6">ID KLAIM</th>
              <th class="py-3.5 px-6">TANGGAL PENGAJUAN</th>
              <th class="py-3.5 px-6">ANGGARAN</th>
              <th class="py-3.5 px-6">ITEM</th>
              <th class="py-3.5 px-6">NILAI KLAIM</th>
              <th class="py-3.5 px-6">STATUS</th>
              <th class="py-3.5 px-6 text-right">AKSI</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-if="loading">
              <td colspan="7" class="py-12 text-center text-slate-400 font-medium">Memuat data klaim...</td>
            </tr>
            <tr v-else-if="filteredClaims.length === 0">
              <td colspan="7" class="py-12 text-center text-slate-400 font-medium">Tidak ada data klaim.</td>
            </tr>
            <tr
              v-for="c in filteredClaims"
              :key="c.id"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <td class="py-4 px-6 font-mono font-bold text-slate-900">{{ c.id }}</td>
              <td class="py-4 px-6 text-slate-600">{{ formatDate(c.claimDate) }}</td>
              <td class="py-4 px-6 font-mono text-slate-500">{{ c.anggaranId }}</td>
              <td class="py-4 px-6 text-slate-600">{{ c.itemCount || (c.claimItems ? c.claimItems.length : 0) }} item</td>
              <td class="py-4 px-6 font-bold text-slate-900">{{ formatRupiah(c.totalClaim) }}</td>
              <td class="py-4 px-6">
                <span :class="['px-2.5 py-1 rounded-full text-[11px] font-bold border', getStatusBadge(c.status)]">
                  {{ c.status }}
                </span>
              </td>
              <td class="py-4 px-6 text-right">
                <router-link
                  :to="`/claim/${c.id}`"
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

    <!-- Modal Buat Pengajuan Klaim -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
      <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-150">
        <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-5">
          <div>
            <p class="text-[10px] font-bold uppercase tracking-wider text-slate-400">INPUT DATA</p>
            <h3 class="text-lg font-extrabold text-slate-900">Buat pengajuan klaim</h3>
          </div>
          <button @click="closeModal" class="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100">
            ✕
          </button>
        </div>

        <form @submit.prevent="handleSubmit" class="space-y-4 text-xs">
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Anggaran *</label>
            <select v-model="form.anggaranId" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 bg-white">
              <option v-for="a in anggaranList" :key="a.id" :value="a.id">
                {{ a.namaSekolah || a.sekolahId }} - {{ a.id }}
              </option>
            </select>
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Tanggal klaim *</label>
            <input v-model="form.claimDate" type="date" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Deskripsi</label>
            <textarea v-model="form.description" rows="3" placeholder="Deskripsi pengajuan..." class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500"></textarea>
          </div>

          <div class="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
            <button type="button" @click="closeModal" class="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 font-semibold hover:bg-slate-50 cursor-pointer">
              Batal
            </button>
            <button type="submit" :disabled="submitting" class="px-5 py-2 rounded-xl bg-[#093426] hover:bg-[#124433] text-white font-bold flex items-center gap-1.5 cursor-pointer shadow">
              <span v-if="submitting">Menyimpan...</span>
              <span v-else>✓ Simpan perubahan</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
