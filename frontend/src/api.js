const TOKEN_KEY = 'parking.token'
const SKEW_KEY = 'parking.clockSkew'

export const SESSION_EXPIRED_EVENT = 'parking:session-expired'

export class ApiError extends Error {
  constructor(status, message) {
    super(message)
    this.status = status
  }
}

export const getToken = () => localStorage.getItem(TOKEN_KEY)
export const setToken = (token) => localStorage.setItem(TOKEN_KEY, token)
export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

const FALLBACK_MESSAGES = {
  400: 'Please check the details and try again.',
  401: 'Your session has ended. Please sign in again.',
  403: 'Your account is not allowed to do that.',
  404: 'That item no longer exists.',
  409: 'That conflicts with the current data. It may have just changed.',
  500: 'The server could not complete that. Check that all services are running.',
}

async function request(path, { method = 'GET', body } = {}) {
  const isAuthCall = path.startsWith('/auth/')
  const headers = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (!isAuthCall && getToken()) headers.Authorization = `Bearer ${getToken()}`

  let response
  try {
    response = await fetch(path, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch {
    throw new ApiError(0, 'Could not reach the server.')
  }

  const text = await response.text()
  let data = text
  try {
    data = text ? JSON.parse(text) : null
  } catch {
    // Some endpoints answer with plain text
  }

  if (response.ok) return data

  if (response.status === 401 && !isAuthCall) {
    clearToken()
    window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT))
  }
  const serverMessage = response.status < 500 && typeof data?.message === 'string' ? data.message : ''
  throw new ApiError(
    response.status,
    serverMessage || FALLBACK_MESSAGES[response.status] || FALLBACK_MESSAGES[500],
  )
}

// Auth

export async function login(email, password) {
  const data = await request('/auth/users/login', {
    method: 'POST',
    body: { email, password },
  })
  return data.message
}

export const register = ({ username, email, password }) =>
  request('/auth/users/register', {
    method: 'POST',
    body: { username, emailAddress: email, password },
  })

// Lots and spots

export const getLots = () => request('/lots')
export const getSpots = (lotId) => request(`/spots/lot/${lotId}`)
export const createLot = (lot) => request('/lots', { method: 'POST', body: lot })
export const createSpot = (spot) => request('/spots', { method: 'POST', body: spot })

// Vehicles

export const getVehicles = (user) =>
  request(user.isAdmin ? '/api/vehicles' : `/api/vehicles/user/${encodeURIComponent(user.email)}`)
export const addVehicle = (vehicle) => request('/api/vehicles', { method: 'POST', body: vehicle })
export const deleteVehicle = (id) => request(`/api/vehicles/${id}`, { method: 'DELETE' })

// Reservations

export const getReservations = (user) =>
  request(user.isAdmin ? '/api/reservations' : `/api/reservations/user/${encodeURIComponent(user.email)}`)

// reservation-service reserves and frees the spot itself when a reservation is
// created, cancelled or expires.

export async function bookSpot({ spot, vehicleId, amount, method }) {
  const reservation = await request('/api/reservations', {
    method: 'POST',
    body: { vehicleId, spotId: spot.id, lotId: spot.lotId, amount, method },
  })
  rememberClockSkew(reservation.reservedAt)
  return reservation
}

export async function payReservation(reservation) {
  const payment = await request(`/api/payments/reservation/${reservation.id}`)
  return request(`/api/payments/${payment.id}/confirm`, { method: 'POST' })
}

export const markArrived = (reservation) =>
  request(`/spots/${reservation.spotId}/occupy`, { method: 'PATCH' })

export const cancelReservation = (reservation) =>
  request(`/api/reservations/${reservation.id}/cancel`, { method: 'PUT' })

// There is no check-out endpoint yet, and cancelling is the only way to close
// a paid reservation, so it is recorded as CANCELLED after the driver leaves.
// Close the reservation first, so it can never free a spot the next driver has booked.
export async function leaveSpot(reservation) {
  await request(`/api/reservations/${reservation.id}/cancel`, { method: 'PUT' })
  await request(`/spots/${reservation.spotId}/release`, { method: 'PATCH' })
}

// Reservation times are LocalDateTime, so they carry no time zone. Comparing a
// new reservation with the browser clock reveals any zone difference.

function rememberClockSkew(reservedAt) {
  const quarterHour = 15 * 60 * 1000
  const skew = Date.now() - new Date(reservedAt.slice(0, 23)).getTime()
  localStorage.setItem(SKEW_KEY, String(Math.round(skew / quarterHour) * quarterHour))
}

export function serverTime(value) {
  if (!value) return null
  const skew = Number(localStorage.getItem(SKEW_KEY)) || 0
  return new Date(value.slice(0, 23)).getTime() + skew
}
