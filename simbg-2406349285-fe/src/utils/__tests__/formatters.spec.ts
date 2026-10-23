import { describe, it, expect } from 'vitest'
import { formatRupiah, formatDate } from '../formatters'

describe('Formatters Utility', () => {
  it('formats rupiah correctly', () => {
    expect(formatRupiah(1000000)).toContain('1.000.000')
    expect(formatRupiah(0)).toContain('0')
    expect(formatRupiah(null)).toBe('Rp0')
  })

  it('formats date correctly', () => {
    const result = formatDate('2024-06-01')
    expect(result).toContain('2024')
    expect(formatDate(null)).toBe('-')
  })
})
