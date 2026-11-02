import { useAuth } from '../auth.jsx'
import { ReservationActions } from '../components/CurrentParking.jsx'
import * as api from '../api.js'
import { money } from '../format.js'
import { reservationStage, useParking } from '../useParking.js'

const dateFormat = new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' })

export default function Reservations() {
  const { user } = useAuth()
  const { reservations, spotById, lotById, vehicleById, loading, error, refresh } = useParking()

  return (
    <section className="panel page-panel">
      <h1 className="panel-title">{user.isAdmin ? 'All reservations' : 'My reservations'}</h1>
      {error && (
        <p className="banner" role="alert">
          {error}
        </p>
      )}
      {!loading && reservations.length === 0 && <p className="muted">No reservations yet.</p>}
      <ul className="row-list">
        {reservations.map((reservation) => {
          const spot = spotById.get(reservation.spotId)
          const stage = user.isAdmin ? null : reservationStage(reservation, spot)
          return (
            <li key={reservation.id} className="row-card">
              <div className="row-main">
                <strong>
                  {spot?.spotNumber ?? 'Spot'} at {lotById.get(reservation.lotId)?.name ?? 'unknown lot'}
                </strong>
                <span className="muted">
                  {vehicleById.get(reservation.vehicleId)?.plateNumber ?? 'Unknown vehicle'}
                  {' · '}
                  {money(reservation.totalFee)}
                  {' · '}
                  {reservation.reservedAt ? dateFormat.format(api.serverTime(reservation.reservedAt)) : '-'}
                  {user.isAdmin && ` · ${reservation.userId}`}
                </span>
              </div>
              <span className={`status status-${reservation.status.toLowerCase()}`}>{reservation.status}</span>
              {stage && <ReservationActions reservation={reservation} stage={stage} onChange={refresh} />}
            </li>
          )
        })}
      </ul>
    </section>
  )
}
