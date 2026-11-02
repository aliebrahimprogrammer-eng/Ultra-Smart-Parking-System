import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import * as api from '../api.js'
import { PAYMENT_METHODS, money } from '../format.js'
import { useToast } from './AppShell.jsx'

export default function BookingCard({ spot, lot, vehicles, onBooked }) {
  const toast = useToast()
  const [vehicleId, setVehicleId] = useState('')
  const [hours, setHours] = useState(1)
  const [method, setMethod] = useState(PAYMENT_METHODS[0].value)
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    if (!vehicles.some((v) => v.id === vehicleId)) setVehicleId(vehicles[0]?.id ?? '')
  }, [vehicles, vehicleId])

  const amount = lot ? lot.hourlyRate * hours : 0
  const spotIsFree = spot?.status === 'AVAILABLE'

  const submit = async (event) => {
    event.preventDefault()
    setBusy(true)
    try {
      await api.bookSpot({ spot, vehicleId, amount, method })
      toast(`Spot ${spot.spotNumber} is yours for 15 minutes. Pay to keep it.`)
    } catch (error) {
      toast(error.status === 409 ? `Someone just took ${spot.spotNumber}. Please pick another spot.` : error.message, 'error')
    } finally {
      setBusy(false)
      onBooked()
    }
  }

  if (vehicles.length === 0) {
    return (
      <div className="empty-note">
        <p>Add a vehicle before you book.</p>
        <Link className="button-gold" to="/vehicles">
          Add a vehicle
        </Link>
      </div>
    )
  }

  let hint = 'Select a green spot on the grid'
  if (spot) hint = spotIsFree ? `${spot.spotNumber} - free` : `${spot.spotNumber} is not free`

  return (
    <form className="stack" onSubmit={submit}>
      <div className="field">
        <span className="field-label">Spot</span>
        <output className={`field-value${spotIsFree ? '' : ' muted'}`}>{hint}</output>
      </div>
      <label className="field">
        <span className="field-label">Vehicle</span>
        <select value={vehicleId} onChange={(e) => setVehicleId(e.target.value)}>
          {vehicles.map((v) => (
            <option key={v.id} value={v.id}>
              {v.plateNumber} - {v.model}
            </option>
          ))}
        </select>
      </label>
      <div className="field-row">
        <label className="field">
          <span className="field-label">Hours</span>
          <select value={hours} onChange={(e) => setHours(Number(e.target.value))}>
            {[1, 2, 3, 4, 6, 8, 12].map((h) => (
              <option key={h} value={h}>
                {h} {h === 1 ? 'hour' : 'hours'}
              </option>
            ))}
          </select>
        </label>
        <label className="field">
          <span className="field-label">Payment</span>
          <select value={method} onChange={(e) => setMethod(e.target.value)}>
            {PAYMENT_METHODS.map((m) => (
              <option key={m.value} value={m.value}>
                {m.label}
              </option>
            ))}
          </select>
        </label>
      </div>
      <button className="button-gold" disabled={!spotIsFree || busy || !vehicleId}>
        {busy ? 'Booking...' : spotIsFree ? `Book for ${money(amount)}` : 'Booking'}
      </button>
    </form>
  )
}
