import { toast } from 'vue3-toastify'
import type { BaseResponse } from '@/types'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export class ApiRequestError extends Error {
  constructor(
    message: string,
    public readonly status?: number,
  ) {
    super(message)
    this.name = 'ApiRequestError'
  }
}

function failRequest(message: string, status?: number): never {
  toast.error(message)
  throw new ApiRequestError(message, status)
}

export async function apiRequest<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  if (options.body !== undefined && options.body !== null && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  let response: Response
  try {
    response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers })
  } catch (err) {
    return failRequest('Gagal terhubung ke server backend')
  }

  let payload: BaseResponse<T>
  try {
    payload = (await response.json()) as BaseResponse<T>
  } catch {
    return failRequest('Format respons server tidak valid')
  }

  if (!response.ok) {
    return failRequest(payload.message || 'Terjadi kesalahan pada permintaan', response.status)
  }

  if (options.method && options.method !== 'GET') {
    toast.success(payload.message || 'Operasi berhasil')
  }

  return payload.data
}
