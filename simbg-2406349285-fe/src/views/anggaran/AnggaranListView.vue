<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { simbgService } from '@/services/simbg.service'
import { formatRupiah, formatDate } from '@/utils/formatters'
import type { Anggaran, Sekolah, CreateAnggaranPayload } from '@/types'

const router = useRouter()
const loading = ref(true)
const anggaranList = ref<Anggaran[]>([])
const schools = ref<Sekolah[]>([])
const searchQuery = ref('')

const isModalOpen = ref(false)
const submitting = ref(false)
const form = ref<CreateAnggaranPayload>({
  sekolahId: '',
  status: 'AKTIF',
  startPeriod: '',
  endPeriod: '',
  totalAnggaran: 0,
  description: '',
})

async function loadData() {
  loading.value = true
  try {
    const [dataAnggaran, dataSchools] = await Promise.all([
      simbgService.getAllAnggaran(),
      simbgService.getSekolah(),
    ])
    anggaranList.value = dataAnggaran || []
    schools.value = dataSchools || []
    if (schools.value.length > 0 && !form.value.sekolahId) {
      form.value.sekolahId = schools.value[0]?.id || ''
    }
  } catch (error) {
    console.error('Failed to load data:', error)
  } finally {
    loading.value = false
  }
}

const filteredAnggaran = computed(() => {
  if (!searchQuery.value.trim()) return anggaranList.value
  const q = searchQuery.value.toLowerCase()
  return anggaranList.value.filter(a =>
    a.id.toLowerCase().includes(q) ||
    (a.namaSekolah && a.namaSekolah.toLowerCase().includes(q))
  )
})

function openCreateModal() {
  form.value = {
    sekolahId: schools.value.length > 0 ? (schools.value[0]?.id || '') : '',
    status: 'AKTIF',
    startPeriod: '',
    endPeriod: '',
    totalAnggaran: 0,
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
    const created = await simbgService.createAnggaran(form.value)
    closeModal()
    router.push(`/anggaran/${created.id}`)
  } catch (error) {
    console.error('Failed to create anggaran:', error)
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
          <span class="text-xs font-bold uppercase tracking-wider text-slate-500">PERENCANAAN DANA</span>
        </div>
        <h2 class="text-3xl font-extrabold text-slate-900 tracking-tight">Anggaran bantuan</h2>
        <p class="text-sm text-slate-500 mt-1">Kelola periode, plafon, dan rincian alokasi untuk setiap sekolah.</p>
      </div>

      <button
        @click="openCreateModal"
        class="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#093426] hover:bg-[#124433] text-white text-xs font-bold shadow-md transition-all self-start sm:self-auto cursor-pointer"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
        </svg>
        <span>Buat anggaran</span>
      </button>
    </div>

    <!-- Table Card Container -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
      <!-- Search and Subheader -->
      <div class="p-6 border-b border-slate-100 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h3 class="font-bold text-base text-slate-900">Seluruh anggaran</h3>
          <p class="text-xs text-slate-500 mt-0.5">{{ anggaranList.length }} periode tercatat</p>
        </div>

        <div class="relative w-full sm:w-72">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Cari sekolah atau ID..."
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
              <th class="py-3.5 px-6">PERIODE ANGGARAN</th>
              <th class="py-3.5 px-6">SEKOLAH</th>
              <th class="py-3.5 px-6">TOTAL DANA</th>
              <th class="py-3.5 px-6">TERPAKAI</th>
              <th class="py-3.5 px-6">SISA DANA</th>
              <th class="py-3.5 px-6">STATUS</th>
              <th class="py-3.5 px-6 text-right">AKSI</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-if="loading">
              <td colspan="7" class="py-12 text-center text-slate-400 font-medium">Memuat data anggaran...</td>
            </tr>
            <tr v-else-if="filteredAnggaran.length === 0">
              <td colspan="7" class="py-12 text-center text-slate-400 font-medium">Tidak ada data anggaran.</td>
            </tr>
            <tr
              v-for="a in filteredAnggaran"
              :key="a.id"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <td class="py-4 px-6">
                <div>
                  <p class="font-bold text-slate-900">{{ formatDate(a.startPeriod) }} s/d {{ formatDate(a.endPeriod) }}</p>
                  <p class="font-mono text-[11px] text-slate-400">{{ a.id }}</p>
                </div>
              </td>
              <td class="py-4 px-6 font-semibold text-slate-800">{{ a.namaSekolah || a.sekolahId }}</td>
              <td class="py-4 px-6 font-bold text-slate-900">{{ formatRupiah(a.totalAnggaran) }}</td>
              <td class="py-4 px-6 text-slate-600">{{ formatRupiah(a.totalAllocated) }}</td>
              <td class="py-4 px-6 text-slate-600">{{ formatRupiah(a.remainingAmount) }}</td>
              <td class="py-4 px-6">
                <span class="px-2.5 py-1 rounded-full text-[11px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                  {{ a.status }}
                </span>
              </td>
              <td class="py-4 px-6 text-right">
                <router-link
                  :to="`/anggaran/${a.id}`"
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

    <!-- Modal Buat Anggaran -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
      <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-150">
        <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-5">
          <div>
            <p class="text-[10px] font-bold uppercase tracking-wider text-slate-400">INPUT DATA</p>
            <h3 class="text-lg font-extrabold text-slate-900">Buat anggaran baru</h3>
          </div>
          <button @click="closeModal" class="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100">
            ✕
          </button>
        </div>

        <form @submit.prevent="handleSubmit" class="space-y-4 text-xs">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Sekolah *</label>
              <select v-model="form.sekolahId" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 bg-white">
                <option v-for="s in schools" :key="s.id" :value="s.id">
                  {{ s.nama }} · {{ s.id }}
                </option>
              </select>
            </div>

            <div>
              <label class="block font-semibold text-slate-700 mb-1">Status *</label>
              <select v-model="form.status" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 bg-white">
                <option value="AKTIF">Aktif</option>
                <option value="NONAKTIF">Nonaktif</option>
              </select>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Mulai periode *</label>
              <input v-model="form.startPeriod" type="date" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
            </div>

            <div>
              <label class="block font-semibold text-slate-700 mb-1">Akhir periode *</label>
              <input v-model="form.endPeriod" type="date" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
            </div>
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Total anggaran *</label>
            <input v-model.number="form.totalAnggaran" type="number" min="1" required placeholder="Contoh: 150000000" class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Deskripsi</label>
            <textarea v-model="form.description" rows="3" placeholder="Keterangan tambahan..." class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500"></textarea>
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
