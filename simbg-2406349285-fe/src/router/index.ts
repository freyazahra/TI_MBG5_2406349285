import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SekolahListView from '@/views/sekolah/SekolahListView.vue'
import AnggaranListView from '@/views/anggaran/AnggaranListView.vue'
import AnggaranDetailView from '@/views/anggaran/AnggaranDetailView.vue'
import ClaimListView from '@/views/claim/ClaimListView.vue'
import ClaimDetailView from '@/views/claim/ClaimDetailView.vue'
import InvoiceListView from '@/views/invoice/InvoiceListView.vue'
import InvoiceDetailView from '@/views/invoice/InvoiceDetailView.vue'
import InvoiceStatistikView from '@/views/invoice/InvoiceStatistikView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/home',
      name: 'dashboard',
      component: HomeView,
    },
    {
      path: '/sekolah',
      name: 'sekolah-list',
      component: SekolahListView,
    },
    {
      path: '/anggaran',
      name: 'anggaran-list',
      component: AnggaranListView,
    },
    {
      path: '/anggaran/:id',
      name: 'anggaran-detail',
      component: AnggaranDetailView,
    },
    {
      path: '/claim',
      name: 'claim-list',
      component: ClaimListView,
    },
    {
      path: '/claim/:id',
      name: 'claim-detail',
      component: ClaimDetailView,
    },
    {
      path: '/invoice',
      name: 'invoice-list',
      component: InvoiceListView,
    },
    {
      path: '/invoice/statistik',
      name: 'invoice-statistik',
      component: InvoiceStatistikView,
    },
    {
      path: '/invoice/:invoiceNumber',
      name: 'invoice-detail',
      component: InvoiceDetailView,
    },
  ],
})

export default router
