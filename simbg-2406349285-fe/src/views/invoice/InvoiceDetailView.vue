<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { simbgService } from '@/services/simbg.service'
import { formatRupiah, formatDate } from '@/utils/formatters'
import type { PaymentInvoice } from '@/types'

const route = useRoute()
const invoiceNumber = route.params.invoiceNumber as string

const loading = ref(true)
const invoice = ref<PaymentInvoice | null>(null)

async function loadData() {
  loading.value = true
  try {
    const data = await simbgService.getInvoiceByNumber(invoiceNumber)
    invoice.value = data
  } catch (error) {
    console.error('Failed to load invoice:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="space-y-6 max-w-7xl mx-auto">
    <!-- Top Details Card -->
    <div v-if="invoice" class="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-sm">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-100">
        <div>
          <div class="flex items-center gap-2 mb-1.5">
            <span class="text-xs font-bold uppercase tracking-wider text-slate-400">DETAIL INVOICE</span>
            <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-amber-50 text-amber-800 border border-amber-200">
              {{ invoice.status.replace('_', ' ') }}
            </span>
          </div>
          <h2 class="text-2xl font-extrabold text-slate-900 font-mono">{{ invoice.invoiceNumber }}</h2>
          <p class="text-xs text-slate-500 mt-1">Diterbitkan atas klaim dasar {{ invoice.claimId }}</p>
        </div>

        <div>
          <router-link
            to="/invoice"
            class="p-2 rounded-xl text-slate-400 hover:text-slate-700 hover:bg-slate-100 border border-slate-200 transition-colors inline-block"
            title="Kembali"
          >
            ✕
          </router-link>
        </div>
      </div>

      <!-- Quick Metrics -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-6 pt-6 text-xs">
        <div>
          <p class="text-slate-400 font-medium">Tanggal Invoice</p>
          <p class="text-sm font-extrabold text-slate-900 mt-0.5">{{ formatDate(invoice.invoiceDate) }}</p>
        </div>
        <div>
          <p class="text-slate-400 font-medium">Jatuh Tempo</p>
          <p class="text-sm font-extrabold text-slate-900 mt-0.5">{{ formatDate(invoice.dueDate) }}</p>
        </div>
        <div>
          <p class="text-slate-400 font-medium">Dasar Klaim</p>
          <router-link :to="`/claim/${invoice.claimId}`" class="text-sm font-extrabold text-emerald-700 hover:underline mt-0.5 block font-mono">
            {{ invoice.claimId }} ↗
          </router-link>
        </div>
        <div>
          <p class="text-slate-400 font-medium">Total Nominal Tagihan</p>
          <p class="text-lg font-black text-slate-900 mt-0.5">{{ formatRupiah(invoice.totalAmount) }}</p>
        </div>
      </div>
    </div>

    <!-- Claim Items Table Container -->
    <div v-if="invoice" class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
      <div class="p-6 border-b border-slate-100">
        <h3 class="font-bold text-base text-slate-900">Rincian item belanja</h3>
        <p class="text-xs text-slate-500 mt-0.5">{{ invoice.claimItems ? invoice.claimItems.length : 0 }} item terlampir</p>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs">
          <thead class="bg-slate-50/75 border-b border-slate-100 text-[11px] font-bold text-slate-500 uppercase tracking-wider">
            <tr>
              <th class="py-3.5 px-6">ID ITEM</th>
              <th class="py-3.5 px-6">DESKRIPSI ITEM</th>
              <th class="py-3.5 px-6">KOMPONEN</th>
              <th class="py-3.5 px-6">KUANTITAS</th>
              <th class="py-3.5 px-6">HARGA SATUAN</th>
              <th class="py-3.5 px-6 text-right">SUBTOTAL</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-if="!invoice.claimItems || invoice.claimItems.length === 0">
              <td colspan="6" class="py-12 text-center text-slate-400 font-medium">Tidak ada rincian item.</td>
            </tr>
            <tr
              v-for="item in invoice.claimItems"
              :key="item.id"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <td class="py-4 px-6 font-mono font-bold text-slate-700">{{ item.id }}</td>
              <td class="py-4 px-6 font-semibold text-slate-900">{{ item.description }}</td>
              <td class="py-4 px-6 text-slate-600">
                <span>{{ item.componentName || item.budgetComponentId }}</span>
              </td>
              <td class="py-4 px-6 font-semibold text-slate-700">{{ item.quantity }}</td>
              <td class="py-4 px-6 text-slate-600">{{ formatRupiah(item.unitPrice) }}</td>
              <td class="py-4 px-6 text-right font-bold text-slate-900">{{ formatRupiah(item.amount) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
