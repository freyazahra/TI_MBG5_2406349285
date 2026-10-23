<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { simbgService } from '@/services/simbg.service'
import { formatRupiah, formatDate } from '@/utils/formatters'
import type {
  Claim,
  ClaimItem,
  BudgetComponent,
  CreateClaimItemPayload,
  UpdateClaimPayload,
  UpdateClaimStatusPayload,
} from '@/types'

const route = useRoute()
const claimId = route.params.id as string

const loading = ref(true)
const claim = ref<Claim | null>(null)
const items = ref<ClaimItem[]>([])
const components = ref<BudgetComponent[]>([])

// Modals
const isItemModalOpen = ref(false)
const isEditItemMode = ref(false)
const editingItemId = ref<string | null>(null)

const isStatusModalOpen = ref(false)
const isEditClaimModalOpen = ref(false)
const submitting = ref(false)

// Item Form
const itemForm = ref<CreateClaimItemPayload>({
  budgetComponentId: '',
  description: '',
  quantity: 1,
  unitPrice: 0,
})

// Status Form
const statusForm = ref<UpdateClaimStatusPayload>({
  status: 'DIAJUKAN',
  approvedBy: '',
  rejectedReason: '',
})

// Edit Claim Form
const claimForm = ref<UpdateClaimPayload>({
  claimDate: '',
  description: '',
})

async function loadData() {
  loading.value = true
  try {
    const dataClaim = await simbgService.getClaimById(claimId)
    claim.value = dataClaim
    items.value = dataClaim.claimItems || []

    if (dataClaim.anggaranId) {
      const dataComponents = await simbgService.getComponents(dataClaim.anggaranId)
      components.value = dataComponents || []
      if (components.value.length > 0 && !itemForm.value.budgetComponentId) {
        itemForm.value.budgetComponentId = components.value[0]?.id || ''
      }
    }
  } catch (error) {
    console.error('Failed to load claim detail:', error)
  } finally {
    loading.value = false
  }
}

// Item Modal handlers
function openAddItemModal() {
  isEditItemMode.value = false
  editingItemId.value = null
  itemForm.value = {
    budgetComponentId: components.value.length > 0 ? (components.value[0]?.id || '') : '',
    description: '',
    quantity: 1,
    unitPrice: 0,
  }
  isItemModalOpen.value = true
}

function openEditItemModal(item: ClaimItem) {
  isEditItemMode.value = true
  editingItemId.value = item.id
  itemForm.value = {
    budgetComponentId: item.budgetComponentId,
    description: item.description,
    quantity: item.quantity,
    unitPrice: item.unitPrice,
  }
  isItemModalOpen.value = true
}

function closeItemModal() {
  isItemModalOpen.value = false
}

async function handleItemSubmit() {
  submitting.value = true
  try {
    if (isEditItemMode.value && editingItemId.value) {
      await simbgService.updateClaimItem(claimId, editingItemId.value, itemForm.value)
    } else {
      await simbgService.createClaimItem(claimId, itemForm.value)
    }
    closeItemModal()
    await loadData()
  } catch (error) {
    console.error('Failed to save claim item:', error)
  } finally {
    submitting.value = false
  }
}

// Status Modal handlers
function openStatusModal() {
  if (!claim.value) return
  const current = claim.value.status
  let next = 'DIAJUKAN'
  if (current === 'DRAFT') next = 'DIAJUKAN'
  else if (current === 'DIAJUKAN') next = 'DIVERIFIKASI'
  else if (current === 'DIVERIFIKASI') next = 'DISETUJUI'
  else if (current === 'DITOLAK') next = 'DIAJUKAN'

  statusForm.value = {
    status: next,
    approvedBy: '',
    rejectedReason: '',
  }
  isStatusModalOpen.value = true
}

function closeStatusModal() {
  isStatusModalOpen.value = false
}

async function handleStatusSubmit() {
  submitting.value = true
  try {
    await simbgService.updateClaimStatus(claimId, statusForm.value)
    closeStatusModal()
    await loadData()
  } catch (error) {
    console.error('Failed to update status:', error)
  } finally {
    submitting.value = false
  }
}

// Edit Claim handlers
function openEditClaimModal() {
  if (!claim.value) return
  claimForm.value = {
    claimDate: claim.value.claimDate,
    description: claim.value.description || '',
  }
  isEditClaimModalOpen.value = true
}

function closeEditClaimModal() {
  isEditClaimModalOpen.value = false
}

async function handleClaimSubmit() {
  submitting.value = true
  try {
    await simbgService.updateClaim(claimId, claimForm.value)
    closeEditClaimModal()
    await loadData()
  } catch (error) {
    console.error('Failed to update claim:', error)
  } finally {
    submitting.value = false
  }
}

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

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="space-y-6 max-w-7xl mx-auto">
    <!-- Top Details Card -->
    <div v-if="claim" class="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-sm">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-100">
        <div>
          <div class="flex items-center gap-2 mb-1.5">
            <span class="text-xs font-bold uppercase tracking-wider text-slate-400">DETAIL PENGAJUAN - {{ claim.id }}</span>
            <span :class="['px-2.5 py-0.5 rounded-full text-[10px] font-bold border', getStatusBadge(claim.status)]">
              {{ claim.status }}
            </span>
          </div>
          <h2 class="text-2xl font-extrabold text-slate-900">{{ claim.description || 'Klaim Tanpa Deskripsi' }}</h2>
          
          <!-- Rejection Alert -->
          <div v-if="claim.status === 'DITOLAK' && claim.rejectedReason" class="mt-3 p-3 rounded-xl bg-rose-50 border border-rose-200 text-xs text-rose-800 flex items-start gap-2">
            <span class="font-bold shrink-0">Alasan Penolakan:</span>
            <span>{{ claim.rejectedReason }} (Telah ditolak {{ claim.rejectionCount }} kali)</span>
          </div>
        </div>

        <div class="flex items-center flex-wrap gap-2.5">
          <button
            v-if="claim.status === 'DRAFT'"
            @click="openAddItemModal"
            class="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold transition-all cursor-pointer"
          >
            <span>+ Tambah item</span>
          </button>

          <button
            v-if="claim.status === 'DRAFT'"
            @click="openEditClaimModal"
            class="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-bold transition-all cursor-pointer"
          >
            <span>Ubah klaim</span>
          </button>

          <button
            @click="openStatusModal"
            class="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl bg-[#093426] hover:bg-[#124433] text-white text-xs font-bold shadow transition-all cursor-pointer"
          >
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
            </svg>
            <span>Tinjau status</span>
          </button>

          <router-link
            to="/claim"
            class="p-2 rounded-xl text-slate-400 hover:text-slate-700 hover:bg-slate-100 border border-slate-200 transition-colors"
            title="Tutup / Kembali"
          >
            ✕
          </router-link>
        </div>
      </div>

      <!-- Quick Metrics -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-6 pt-6 text-xs">
        <div>
          <p class="text-slate-400 font-medium">Anggaran</p>
          <p class="text-sm font-extrabold text-slate-900 mt-0.5 font-mono">{{ claim.anggaranId }}</p>
        </div>
        <div>
          <p class="text-slate-400 font-medium">Tanggal Pengajuan</p>
          <p class="text-sm font-extrabold text-slate-900 mt-0.5">{{ formatDate(claim.claimDate) }}</p>
        </div>
        <div>
          <p class="text-slate-400 font-medium">Total Klaim</p>
          <p class="text-base font-extrabold text-slate-900 mt-0.5">{{ formatRupiah(claim.totalClaim) }}</p>
        </div>
        <div>
          <p class="text-slate-400 font-medium">Pemberi Persetujuan</p>
          <p class="text-sm font-semibold text-slate-700 mt-0.5">{{ claim.approvedBy || '-' }}</p>
        </div>
      </div>
    </div>

    <!-- Items Table Container -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
      <div class="p-6 border-b border-slate-100 flex items-center justify-between">
        <div>
          <h3 class="font-bold text-base text-slate-900">Rincian item ({{ items.length }})</h3>
          <p class="text-xs text-slate-500 mt-0.5">Daftar item belanja yang diklaim</p>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs">
          <thead class="bg-slate-50/75 border-b border-slate-100 text-[11px] font-bold text-slate-500 uppercase tracking-wider">
            <tr>
              <th class="py-3.5 px-6">DESKRIPSI ITEM</th>
              <th class="py-3.5 px-6">KOMPONEN</th>
              <th class="py-3.5 px-6">KUANTITAS</th>
              <th class="py-3.5 px-6">HARGA SATUAN</th>
              <th class="py-3.5 px-6">SUBTOTAL</th>
              <th class="py-3.5 px-6 text-right" v-if="claim?.status === 'DRAFT'">AKSI</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-if="loading">
              <td colspan="6" class="py-12 text-center text-slate-400 font-medium">Memuat data item...</td>
            </tr>
            <tr v-else-if="items.length === 0">
              <td colspan="6" class="py-12 text-center text-slate-400 font-medium">Belum ada item untuk klaim ini.</td>
            </tr>
            <tr
              v-for="item in items"
              :key="item.id"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <td class="py-4 px-6 font-semibold text-slate-900">{{ item.description }}</td>
              <td class="py-4 px-6 text-slate-600">
                <span class="font-medium text-slate-800">{{ item.componentName || item.budgetComponentId }}</span>
                <span v-if="item.rateBasis" class="text-slate-400 text-[10px]"> ({{ item.rateBasis }})</span>
              </td>
              <td class="py-4 px-6 font-semibold text-slate-700">{{ item.quantity }}</td>
              <td class="py-4 px-6 text-slate-600">{{ formatRupiah(item.unitPrice) }}</td>
              <td class="py-4 px-6 font-bold text-slate-900">{{ formatRupiah(item.amount) }}</td>
              <td class="py-4 px-6 text-right" v-if="claim?.status === 'DRAFT'">
                <button
                  @click="openEditItemModal(item)"
                  class="text-xs font-semibold text-emerald-700 hover:text-emerald-800 hover:underline cursor-pointer"
                >
                  Edit
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Tambah / Ubah Claim Item -->
    <div v-if="isItemModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
      <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-150">
        <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-5">
          <div>
            <p class="text-[10px] font-bold uppercase tracking-wider text-slate-400">INPUT DATA</p>
            <h3 class="text-lg font-extrabold text-slate-900">
              {{ isEditItemMode ? 'Ubah item klaim' : 'Tambah item klaim' }}
            </h3>
          </div>
          <button @click="closeItemModal" class="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100">
            ✕
          </button>
        </div>

        <form @submit.prevent="handleItemSubmit" class="space-y-4 text-xs">
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Komponen anggaran *</label>
            <select v-model="itemForm.budgetComponentId" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 bg-white">
              <option v-for="c in components" :key="c.id" :value="c.id">
                {{ c.componentName }} · Sisa {{ formatRupiah(c.remainingAmount) }} (Maks {{ formatRupiah(c.maxRate) }})
              </option>
            </select>
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Deskripsi item *</label>
            <input v-model="itemForm.description" type="text" required placeholder="Contoh: Pembelian beras" class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block font-semibold text-slate-700 mb-1">Kuantitas *</label>
              <input v-model.number="itemForm.quantity" type="number" min="1" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
            </div>

            <div>
              <label class="block font-semibold text-slate-700 mb-1">Harga satuan *</label>
              <input v-model.number="itemForm.unitPrice" type="number" min="1" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
            </div>
          </div>

          <div class="p-3 bg-slate-50 rounded-xl border border-slate-100 text-slate-600 flex justify-between items-center">
            <span>Estimasi subtotal:</span>
            <span class="font-extrabold text-slate-900 text-sm">{{ formatRupiah(itemForm.quantity * itemForm.unitPrice) }}</span>
          </div>

          <div class="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
            <button type="button" @click="closeItemModal" class="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 font-semibold hover:bg-slate-50 cursor-pointer">
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

    <!-- Modal Perbarui Status Klaim -->
    <div v-if="isStatusModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
      <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-150">
        <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-5">
          <div>
            <p class="text-[10px] font-bold uppercase tracking-wider text-slate-400">TINDAKAN KLAIM</p>
            <h3 class="text-lg font-extrabold text-slate-900">Perbarui status klaim</h3>
          </div>
          <button @click="closeStatusModal" class="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100">
            ✕
          </button>
        </div>

        <form @submit.prevent="handleStatusSubmit" class="space-y-4 text-xs">
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Ubah status menjadi *</label>
            <select v-model="statusForm.status" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 bg-white">
              <option value="DIAJUKAN">Ajukan (DIAJUKAN)</option>
              <option value="DIVERIFIKASI">Verifikasi (DIVERIFIKASI)</option>
              <option value="DISETUJUI">Setujui (DISETUJUI)</option>
              <option value="DITOLAK">Tolak (DITOLAK)</option>
              <option value="DIBATALKAN">Batalkan (DIBATALKAN)</option>
            </select>
          </div>

          <div v-if="statusForm.status === 'DISETUJUI'">
            <label class="block font-semibold text-slate-700 mb-1">Nama pemberi persetujuan *</label>
            <input v-model="statusForm.approvedBy" type="text" required placeholder="Contoh: John Doe" class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
          </div>

          <div v-if="statusForm.status === 'DITOLAK'">
            <label class="block font-semibold text-slate-700 mb-1">Alasan penolakan *</label>
            <textarea v-model="statusForm.rejectedReason" rows="3" required placeholder="Jelaskan alasan penolakan klaim..." class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500"></textarea>
          </div>

          <div class="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
            <button type="button" @click="closeStatusModal" class="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 font-semibold hover:bg-slate-50 cursor-pointer">
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

    <!-- Modal Ubah Data Klaim -->
    <div v-if="isEditClaimModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
      <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-150">
        <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-5">
          <div>
            <p class="text-[10px] font-bold uppercase tracking-wider text-slate-400">PERBARUI DATA</p>
            <h3 class="text-lg font-extrabold text-slate-900">Ubah klaim</h3>
          </div>
          <button @click="closeEditClaimModal" class="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100">
            ✕
          </button>
        </div>

        <form @submit.prevent="handleClaimSubmit" class="space-y-4 text-xs">
          <div>
            <label class="block font-semibold text-slate-700 mb-1">Tanggal klaim *</label>
            <input v-model="claimForm.claimDate" type="date" required class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500" />
          </div>

          <div>
            <label class="block font-semibold text-slate-700 mb-1">Deskripsi</label>
            <textarea v-model="claimForm.description" rows="3" class="w-full px-3 py-2 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500"></textarea>
          </div>

          <div class="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
            <button type="button" @click="closeEditClaimModal" class="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 font-semibold hover:bg-slate-50 cursor-pointer">
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
