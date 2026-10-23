import { apiRequest } from './http'
import type {
  DashboardData,
  Sekolah,
  Anggaran,
  CreateAnggaranPayload,
  BudgetComponent,
  CreateBudgetComponentPayload,
  Claim,
  CreateClaimPayload,
  UpdateClaimPayload,
  UpdateClaimStatusPayload,
  ClaimItem,
  CreateClaimItemPayload,
  PaymentInvoice,
  CreateInvoicePayload,
  StatistikData,
} from '@/types'

export const simbgService = {
  // 1. Dashboard
  getDashboard(): Promise<DashboardData> {
    return apiRequest<DashboardData>('/home')
  },

  // 2. Sekolah
  getSekolah(): Promise<Sekolah[]> {
    return apiRequest<Sekolah[]>('/sekolah')
  },

  // 3. Anggaran
  getAllAnggaran(): Promise<Anggaran[]> {
    return apiRequest<Anggaran[]>('/anggaran')
  },
  getAnggaranById(id: string): Promise<Anggaran> {
    return apiRequest<Anggaran>(`/anggaran/${id}`)
  },
  createAnggaran(payload: CreateAnggaranPayload): Promise<Anggaran> {
    return apiRequest<Anggaran>('/anggaran', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },

  // 4. Budget Component
  getComponents(anggaranId: string): Promise<BudgetComponent[]> {
    return apiRequest<BudgetComponent[]>(`/anggaran/${anggaranId}/komponen`)
  },
  getComponentById(anggaranId: string, id: string): Promise<BudgetComponent> {
    return apiRequest<BudgetComponent>(`/anggaran/${anggaranId}/komponen/${id}`)
  },
  createComponent(anggaranId: string, payload: CreateBudgetComponentPayload): Promise<BudgetComponent> {
    return apiRequest<BudgetComponent>(`/anggaran/${anggaranId}/komponen`, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  updateComponent(anggaranId: string, id: string, payload: CreateBudgetComponentPayload): Promise<BudgetComponent> {
    return apiRequest<BudgetComponent>(`/anggaran/${anggaranId}/komponen/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },

  // 5. Claim
  getAllClaims(): Promise<Claim[]> {
    return apiRequest<Claim[]>('/claim')
  },
  getClaimById(id: string): Promise<Claim> {
    return apiRequest<Claim>(`/claim/${id}`)
  },
  createClaim(payload: CreateClaimPayload): Promise<Claim> {
    return apiRequest<Claim>('/claim', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  updateClaim(id: string, payload: UpdateClaimPayload): Promise<Claim> {
    return apiRequest<Claim>(`/claim/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  updateClaimStatus(id: string, payload: UpdateClaimStatusPayload): Promise<Claim> {
    return apiRequest<Claim>(`/claim/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify(payload),
    })
  },

  // 6. Claim Item
  getClaimItems(claimId: string): Promise<ClaimItem[]> {
    return apiRequest<ClaimItem[]>(`/claim/${claimId}/item`)
  },
  getClaimItemById(claimId: string, id: string): Promise<ClaimItem> {
    return apiRequest<ClaimItem>(`/claim/${claimId}/item/${id}`)
  },
  createClaimItem(claimId: string, payload: CreateClaimItemPayload): Promise<ClaimItem> {
    return apiRequest<ClaimItem>(`/claim/${claimId}/item`, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  updateClaimItem(claimId: string, id: string, payload: CreateClaimItemPayload): Promise<ClaimItem> {
    return apiRequest<ClaimItem>(`/claim/${claimId}/item/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },

  // 7. Invoice
  getAllInvoices(): Promise<PaymentInvoice[]> {
    return apiRequest<PaymentInvoice[]>('/invoice')
  },
  getInvoiceByNumber(invoiceNumber: string): Promise<PaymentInvoice> {
    return apiRequest<PaymentInvoice>(`/invoice/${invoiceNumber}`)
  },
  createInvoice(payload: CreateInvoicePayload): Promise<PaymentInvoice> {
    return apiRequest<PaymentInvoice>('/invoice', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  getStatistik(): Promise<StatistikData> {
    return apiRequest<StatistikData>('/invoice/statistik')
  },
}
