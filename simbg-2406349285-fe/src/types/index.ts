export interface BaseResponse<T> {
  status: number
  message: string
  timestamp: string
  data: T
}

export interface DashboardData {
  jumlahSekolah: number
  totalAnggaran: number
  jumlahInvoice: number
  totalNominalInvoice: number
}

export interface Sekolah {
  id: string
  nama: string
  educationLevel: string
  address: string
  createdAt?: string
  updatedAt?: string
  activeAnggaranCount?: number
}

export interface Anggaran {
  id: string
  startPeriod: string
  endPeriod: string
  totalAnggaran: number
  status: string
  description?: string
  createdAt?: string
  updatedAt?: string
  sekolahId: string
  namaSekolah?: string
  totalAllocated: number
  remainingAmount: number
}

export interface CreateAnggaranPayload {
  sekolahId: string
  startPeriod: string
  endPeriod: string
  totalAnggaran: number
  status: string
  description?: string
}

export interface BudgetComponent {
  id: string
  componentName: string
  allocatedAmount: number
  maxRate: number
  rateBasis: string
  description?: string
  createdAt?: string
  updatedAt?: string
  anggaranId: string
  usedAmount: number
  remainingAmount: number
}

export interface CreateBudgetComponentPayload {
  componentName: string
  allocatedAmount: number
  maxRate: number
  rateBasis: string
  description?: string
}

export interface ClaimItem {
  id: string
  description: string
  quantity: number
  unitPrice: number
  amount: number
  claimId: string
  budgetComponentId: string
  componentName?: string
  rateBasis?: string
  maxRate?: number
}

export interface CreateClaimItemPayload {
  budgetComponentId: string
  description: string
  quantity: number
  unitPrice: number
}

export interface Claim {
  id: string
  claimDate: string
  totalClaim: number
  status: string
  submittedAt?: string
  approvedAt?: string
  approvedBy?: string
  rejectedReason?: string
  rejectionCount?: number
  description?: string
  createdAt?: string
  updatedAt?: string
  anggaranId: string
  namaSekolah?: string
  itemCount?: number
  claimItems?: ClaimItem[]
}

export interface CreateClaimPayload {
  anggaranId: string
  claimDate: string
  description?: string
}

export interface UpdateClaimPayload {
  claimDate: string
  description?: string
}

export interface UpdateClaimStatusPayload {
  status: string
  approvedBy?: string
  rejectedReason?: string
}

export interface PaymentInvoice {
  invoiceNumber: string
  invoiceDate: string
  dueDate: string
  totalAmount: number
  status: string
  createdAt?: string
  updatedAt?: string
  claimId: string
  claimDate?: string
  claimItems?: ClaimItem[]
}

export interface CreateInvoicePayload {
  claimId: string
  invoiceDate: string
  dueDate: string
}

export interface ClaimStatItem {
  status: string
  jumlah: number
  totalNominal: number
}

export interface InvoiceStatItem {
  status: string
  jumlah: number
  totalNominal: number
}

export interface StatistikData {
  claimStatistics: ClaimStatItem[]
  invoiceStatistics: InvoiceStatItem[]
}
