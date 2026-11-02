import { useState } from 'react'
import { Link } from 'react-router-dom'
import * as api from '../api.js'
import { clock, money } from '../format.js'
import { reservationStage, useNow } from '../useParking.js'
import { useToast } from './AppShell.jsx'

// Shared by the dashboard card and the reservations list
export function ReservationActions({ reservation, stage, onChange }) {
  const toast = useToast()
  const [busy, setBusy] = useState(false)

  const run = async (action, success) => {
    setBusy(true)
    try {
      await action(reservation)
      toast(success)
    } catch (error) {
      toast(error.message, 'error')
    } finally {
      setBusy(false)
      onChange()
    }
  }

  return (
    <div className="actions">
      {stage === 'pay' && (
        <button className="button-dark" disabled={busy} onClick={() => run(api.payReservation, 'Payment confirmed. Your spot is held.')}>
          Pay {money(reservation.totalFee)}
        </button>
      )}
      {stage === 'arrive' && (
        <button className="button-dark" disabled={busy} onClick={() => run(api.markArrived, 'Welcome. You are checked in.')}>
          I've arrived
        </button>
      )}
      {stage === 'parked' && (
        <button className="button-dark" disabled={busy} onClick={() => run(api.leaveSpot, 'Thanks for parking. The spot is free again.')}>
          Leave spot
        </button>
      )}
      {stage !== 'parked' && (
        <button className="button-quiet" disabled={busy} onClick={() => run(api.cancelReservation, 'Reservation cancelled.')}>
          Cancel
        </button>
      )}
    </div>
  )
}

const TIMER_LABEL = { pay: 'Pay within', arrive: 'Paid, spot held', parked: 'Duration' }

export default function CurrentParking({ parking }) {
  const { activeReservations, spotById, lotById, vehicleById, refresh } = parking
  const now = useNow()
  const reservation = activeReservations[0]

  if (!reservation) {
    return (
      <div className="empty-note">
        <p>No active reservation.</p>
        <p className="muted">Pick a green spot on the grid to book it.</p>
      </div>
    )
  }

  const spot = spotById.get(reservation.spotId)
  const lot = lotById.get(reservation.lotId)
  const stage = reservationStage(reservation, spot)
  const timer =
    stage === 'pay'
      ? clock(api.serverTime(reservation.expiresAt) - now)
      : stage === 'parked'
        ? clock(now - api.serverTime(reservation.reservedAt))
        : null

  return (
    <>
      <dl className="current-facts">
        <div>
          <dt>Price per hour</dt>
          <dd>{lot ? money(lot.hourlyRate) : '-'}</dd>
        </div>
        <div>
          <dt>Car number</dt>
          <dd>{vehicleById.get(reservation.vehicleId)?.plateNumber ?? '-'}</dd>
        </div>
        <div>
          <dt>Parking slot</dt>
          <dd>{spot?.spotNumber ?? '-'}</dd>
        </div>
      </dl>
      <div className="current-footer">
        <div>
          <span className="field-label">{TIMER_LABEL[stage]}</span>
          {timer && (
            <div className="timer" role="timer">
              {timer}
            </div>
          )}
        </div>
        <ReservationActions reservation={reservation} stage={stage} onChange={refresh} />
      </div>
      {activeReservations.length > 1 && (
        <Link className="text-link" to="/reservations">
          +{activeReservations.length - 1} more active
        </Link>
      )}
    </>
  )
}
