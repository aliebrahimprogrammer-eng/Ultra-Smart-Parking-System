export const money = (value) => `BD ${Number(value).toFixed(3)}`

export const sortSpots = (spots) =>
  [...spots].sort((a, b) => a.spotNumber.localeCompare(b.spotNumber, undefined, { numeric: true }))

export function clock(ms) {
  const total = Math.max(0, Math.floor(ms / 1000))
  const pad = (n) => String(n).padStart(2, '0')
  const hours = Math.floor(total / 3600)
  const minutes = Math.floor((total % 3600) / 60)
  return `${hours ? `${pad(hours)}:` : ''}${pad(minutes)}:${pad(total % 60)}`
}

export const PAYMENT_METHODS = [
  { value: 'CREDIT_CARD', label: 'Credit card' },
  { value: 'DEBIT_CARD', label: 'Debit card' },
  { value: 'APPLE_PAY', label: 'Apple Pay' },
  { value: 'GOOGLE_PAY', label: 'Google Pay' },
  { value: 'CASH', label: 'Cash' },
]

export const SPOT_TYPES = { REGULAR: 'Regular', DISABLED: 'Accessible', ELECTRIC: 'EV charging' }
export const SPOT_STATUS = { AVAILABLE: 'Free', RESERVED: 'Reserved', OCCUPIED: 'Occupied' }
