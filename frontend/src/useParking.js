import { useCallback, useEffect, useMemo, useState } from 'react'
import * as api from './api.js'
import { useAuth } from './auth.jsx'

const POLL_MS = 5000

// What the driver can do next with a reservation, or null once it is finished
export function reservationStage(reservation, spot) {
  if (reservation.status === 'PENDING') return 'pay'
  if (reservation.status !== 'CONFIRMED') return null
  return spot?.status === 'OCCUPIED' ? 'parked' : 'arrive'
}

export function useParking() {
  const { user } = useAuth()
  const [lots, setLots] = useState([])
  const [spots, setSpots] = useState([])
  const [reservations, setReservations] = useState([])
  const [vehicles, setVehicles] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const refresh = useCallback(async () => {
    // One service being down should not blank the parts the others can still show
    const [lotsResult, reservationsResult, vehiclesResult] = await Promise.allSettled([
      api.getLots(),
      api.getReservations(user),
      api.getVehicles(user),
    ])

    let spotsFailed = false
    if (lotsResult.status === 'fulfilled') {
      setLots(lotsResult.value)
      try {
        const lists = await Promise.all(lotsResult.value.map((lot) => api.getSpots(lot.id)))
        setSpots(lists.flat())
      } catch {
        spotsFailed = true
      }
    }
    if (reservationsResult.status === 'fulfilled') setReservations(reservationsResult.value)
    if (vehiclesResult.status === 'fulfilled') setVehicles(vehiclesResult.value)

    if (lotsResult.status === 'rejected' || spotsFailed) {
      setError('Could not load parking data. Is parking-service running?')
    } else if (reservationsResult.status === 'rejected' || vehiclesResult.status === 'rejected') {
      setError('Could not load your reservations or vehicles. Is reservation-service running?')
    } else {
      setError('')
    }
    setLoading(false)
  }, [user])

  useEffect(() => {
    refresh()
    const timer = setInterval(() => {
      if (!document.hidden) refresh()
    }, POLL_MS)
    return () => clearInterval(timer)
  }, [refresh])

  const spotById = useMemo(() => new Map(spots.map((s) => [s.id, s])), [spots])
  const lotById = useMemo(() => new Map(lots.map((l) => [l.id, l])), [lots])
  const vehicleById = useMemo(() => new Map(vehicles.map((v) => [v.id, v])), [vehicles])

  const sortedReservations = useMemo(
    () => [...reservations].sort((a, b) => (b.reservedAt || '').localeCompare(a.reservedAt || '')),
    [reservations],
  )

  const activeReservations = useMemo(
    () =>
      user.isAdmin
        ? []
        : sortedReservations.filter((r) => reservationStage(r, spotById.get(r.spotId))),
    [sortedReservations, spotById, user.isAdmin],
  )

  return {
    lots,
    spots,
    vehicles,
    loading,
    error,
    refresh,
    spotById,
    lotById,
    vehicleById,
    reservations: sortedReservations,
    activeReservations,
  }
}

export function useNow() {
  const [now, setNow] = useState(() => Date.now())
  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 1000)
    return () => clearInterval(timer)
  }, [])
  return now
}
