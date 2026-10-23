<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { simbgService } from '@/services/simbg.service'
import { formatRupiah } from '@/utils/formatters'
import type { Anggaran, BudgetComponent, CreateBudgetComponentPayload } from '@/types'

const route = useRoute()
const router = useRouter()
const anggaranId = route.params.id as string

const loading = ref(true)
const anggaran = ref<Anggaran | null>(null)
const components = ref<BudgetComponent[]>([])

const isModalOpen = ref(false)
const isEditMode = ref(false)
const editingComponentId = ref<string | null>(null)
const submitting = ref(false)

const form = ref<CreateBudgetComponentPayload>({
  componentName: '',
  allocatedAmount: 0,
  maxRate: 0,
  rateBasis: 'PER_PORSI',
  description: '',
})

async function loadData() {
  loading.value = true
  try {
    const [dataAnggaran, dataComponents] = await Promise.all([
      simbgService.getAnggaranById(anggaranId),
      simbgService.getComponents(anggaranId),
    ])
    anggaran.value = dataAnggaran
    components.value = dataComponents || []
  } catch (error) {
    console.error('Failed to load detail:', error)
  } finally {
    loading.value = false
  }
}

function openAddModal() {
  isEditMode.value = false
  editingComponentId.value = null
  form.value = {
    componentName: '',
    allocatedAmount: 0,
    maxRate: 0,
    rateBasis: 'PER_PORSI',
    description: '',
  }
  isModalOpen.value = true
}

function openEditModal(c: BudgetComponent) {
  isEditMode.value = true
  editingComponentId.value = c.id
  form.value = {
    componentName: c.componentName,
    allocatedAmount: c.allocatedAmount,
    maxRate: c.maxRate,
    rateBasis: c.rateBasis,
    description: c.description || '',
  }
  isModalOpen.value = true
}

function closeModal() {
  isModalOpen.value = false
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (isEditMode.value && editingComponentId.value) {
      await simbgService.updateComponent(anggaranId, editingComponentId.value, form.value)
    } else {
      await simbgService.createComponent(anggaranId, form.value)
    }
    closeModal()
    await loadData()
  } catch (error) {
    console.error('Failed to save component:', error)
  } finally {
    submitting.value = false
  }
}

function calculateProgress(used: number, total: number): number {
  if (!total || total <= 0) return 0
  const pct = (used / total) * 100
  return Math.min(100, Math.max(0, Math.round(pct)))
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="space-y-6 max-w-7xl mx-auto">
    <!-- Top Details Card -->
    <div v-if="anggaran" class="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-sm">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-100">
        <div>
          <div class="flex items-center gap-2 mb-1">
            <span class="text-xs font-bold uppercase tracking-wider text-slate-400">RINCIAN ANGGARAN - {{ anggaran.id }}</span>
            <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
              {{ anggaran.status }}
            </span>
          </div>
          <h2 class="text-2xl font-extrabold text-slate-900">{{ anggaran.namaSekolah || anggaran.sekolahId }}</h2>
          <p class="text-xs text-slate-500 mt-1">{{ anggaran.description || 'Tidak ada deskripsi' }}</p>
        </div>

        <div class="flex items-center gap-3">
          <button
            @click="openAddModal"
            class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-[#093426] hover:bg-[#124433] text-white text-xs font-bold shadow transition-all cursor-pointer"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
            </svg>
            <span>Tambah komponen</span>
          </button>

          <router-link
            to="/anggaran"
            class="p-2 rounded-xl text-slate-400 hover:text-slate-700 hover:bg-slate-100 border border-slate-200 transition-colors"
            title="Tutup / Kembali"
          >
            ✕
          </router-link>
        </div>
      </div>

      <!-- Quick Metrics -->
      <div class="grid grid-cols-3 gap-6 pt-6 text-xs">
        <div>
          <p class="text-slate-400 font-medium">Total Anggaran</p>
          <p class="text-base font-extrabold text-slate-900 mt-0.5">{{ formatRupiah(anggaran.totalAnggaran) }}</p>
        </div>
        <div>
          <p class="text-slate-400 font-medium">Total Teralokasi</p>
          <p class="text-base font-extrabold text-emerald-700 mt-0.5">{{ formatRupiah(anggaran.totalAllocated) }}</p>
        </div>
        <div>
          <p class="text-slate-400 font-medium">Sisa Alokasi</p>
          <p class="text-base font-extrabold text-slate-700 mt-0.5">{{ formatRupiah(anggaran.remainingAmount) }}</p>
        </div>
      </div>
    </div>

    <!-- Components Grid -->
    <div>
      <div class="flex items-center justify-between mb-4">
        <h3 class="font-extrabold text-slate-900 text-sm">Komponen Anggaran ({{ components.length }})</h3>
      </div>

      <div v-if="loading" class="text-center py-12 text-slate-400 font-medium text-xs">
        Memuat komponen...
      </div>

      <div v-else-if="components.length === 0" class="text-center py-12 bg-white rounded-2xl border border-slate-200/80 text-slate-400 font-medium text-xs">
        Belum ada komponen anggaran ditambahkan.
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="c in components"
          :key="c.id"
          class="bg-white rounded-2xl border border-slate-200/80 p-5 shadow-sm hover:shadow-md transition-all flex flex-col justify-between"
        >
          <div>
            <div class="flex items-start justify-between">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold text-xs border border-emerald-100">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
                  </svg>
                </div>
                <div>
                  <h4 class="font-bold text-slate-900 text-sm leading-tight">{{ c.componentName }}</h4>
                  <p class="text-[11px] text-slate-400 font-mono mt-0.5">{{ c.id }}</p>
                </div>
              </div>

              <button
                @click="openEditModal(c)"
                class="text-xs font-semibold text-emerald-700 hover:text-emerald-800 hover:underline cursor-pointer"
              >
                Edit
              </button>
            </div>

            <div class="mt-4">
              <p class="text-xs font-semibold text-slate-700">{{ formatRupiah(c.allocatedAmount) }} dialokasikan</p>
              
              <!-- Progress Bar -->
              <div class="w-full h-1.5 bg-slate-100 rounded-full mt-2 overflow-hidden">
                <div
                  class="h-full bg-emerald-600 rounded-full transition-all duration-300"
                  :style="{ width: `${calculateProgress(c.usedAmount, c.allocatedAmount)}%` }"
                ></div>
              </div>

              <div class="flex items-center justify-between mt-2 text-[11px] text-slate-500">
                <span>{{ formatRupiah(c.remainingAmount) }} tersisa</span>
                <span class="font-bold text-slate-700">· {{ c.rateBasis }}</span>
              </div>
            </div>
          </div>

          <div class="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-[11px] text-slate-400">
            <span>Tarif maks: {{ formatRupiah(c.maxRate) }}</span>
            <span v-if="c.description" class="truncate max-w-[140px]">{{ c.description }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Tambah / Ubah Komponen -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
      <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-150">
        <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-5">
          <div>
            <p class="text-[10px] font-bold uppercase tracking-wider text-slate-400">
              {{ isEditMode ? 'PERBARUI DATA' : 'INPUT DATA' }}
            </p>
            <h3 class="text-lg font-extrabold text-slate-900">
              {{ isEditMode ? 'Ubah komponen' : 'Tambah komponen' }}
            </h3>
          </div>
          <button @click="closeModal" class="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100">
            ✕
          </button>
        </div>

        <form @submit.prevent="handleSubmit" class="space-y-4 text-xs">
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Nama komponen *</label>
            <input v-model="form.componentName" type="text" required placeholder="Contoh: Beras, Minyak" class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Alokasi dana *</label>
              <input v-model.number="form.allocatedAmount" type="number" min="1" required placeholder="Nominal Rp" class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
            </div>

            <div>
              <label class="block font-semibold text-slate-700 mb-1">Tarif maksimum *</label>
              <input v-model.number="form.maxRate" type="number" min="1" required placeholder="Tarif per satuan" class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
            </div>
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Basis tarif *</label>
            <select v-model="form.rateBasis" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 bg-white">
              <option value="PER_PORSI">PER_PORSI</option>
              <option value="PER_HARI">PER_HARI</option>
              <option value="TOTAL">TOTAL</option>
            </select>
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Deskripsi</label>
            <textarea v-model="form.description" rows="3" placeholder="Keterangan komponen..." class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500"></textarea>
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
