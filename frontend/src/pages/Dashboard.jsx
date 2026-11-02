import { useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { useAuth } from '../auth.jsx'
import BookingCard from '../components/BookingCard.jsx'
import CurrentParking from '../components/CurrentParking.jsx'
import SpotGrid from '../components/SpotGrid.jsx'
import { SPOT_STATUS, SPOT_TYPES, money } from '../format.js'
import { useParking } from '../useParking.js'

const LEGEND = [
  { key: 'AVAILABLE', label: 'Free' },
  { key: 'RESERVED', label: 'Reserved' },
  { key: 'OCCUPIED', label: 'Occupied' },
]

export default function Dashboard() {
  const { user } = useAuth()
  const parking = useParking()
  const { lots, spots, vehicles, loading, error, refresh, activeReservations } = parking
  const [params, setParams] = useSearchParams()
  const [selectedId, setSelectedId] = useState(null)

  const lot = lots.find((l) => l.id === params.get('lot')) ?? lots[0]
  const lotSpots = useMemo(() => spots.filter((s) => s.lotId === lot?.id), [spots, lot])
  const selected = lotSpots.find((s) => s.id === selectedId)
  const mySpotIds = useMemo(() => new Set(activeReservations.map((r) => r.spotId)), [activeReservations])

  const counts = { AVAILABLE: 0, RESERVED: 0, OCCUPIED: 0 }
  lotSpots.forEach((s) => {
    counts[s.status] += 1
  })

  const chooseLot = (id) => {
    setParams({ lot: id })
    setSelectedId(null)
  }

  return (
    <div className="dashboard">
      {error && (
        <p className="banner" role="alert">
          {error}
        </p>
      )}

      <section className="panel panel-grid">
        <h1 className="panel-title">{lot?.name ?? (loading ? 'Loading parking...' : 'No parking lots yet')}</h1>
        {lots.length > 0 && (
          <div className="lot-tabs" role="group" aria-label="Parking lot">
            {lots.map((l) => (
              <button
                key={l.id}
                className={`lot-tab${l.id === lot?.id ? ' lot-tab-active' : ''}`}
                aria-pressed={l.id === lot?.id}
                onClick={() => chooseLot(l.id)}
              >
                {l.name}
              </button>
            ))}
          </div>
        )}
        <div className="grid-surface">
          {lotSpots.length > 0 ? (
            <SpotGrid spots={lotSpots} selectedId={selectedId} onSelect={setSelectedId} mySpotIds={mySpotIds} />
          ) : (
            <p className="muted grid-empty">
              {loading ? 'Loading spots...' : 'This lot has no spots yet.'}
            </p>
          )}
        </div>
      </section>

      <section className="panel panel-overview">
        <h2 className="panel-title">Parking Overview</h2>
        <div className="overview-hero">
          <div className="overview-top">
            <span>{lot?.location ?? '-'}</span>
            <span className="live">
              <i aria-hidden="true" /> Live
            </span>
          </div>
          <div className="overview-count">
            <strong>{counts.AVAILABLE}</strong> of {lotSpots.length} spots free
          </div>
          <div className="overview-rate">{lot ? `${money(lot.hourlyRate)} per hour` : ''}</div>
        </div>
        <ul className="legend">
          {LEGEND.map((item) => (
            <li key={item.key}>
              <i className={`dot dot-${item.key.toLowerCase()}`} aria-hidden="true" />
              {item.label}
              <strong>{counts[item.key]}</strong>
            </li>
          ))}
        </ul>
      </section>

      <section className="panel panel-spot">
        <h2 className="panel-title">Spot details</h2>
        {selected ? (
          <div className="stack-tight">
            <div className="info-tile">
              <span>Spot</span>
              <strong>{selected.spotNumber}</strong>
            </div>
            <div className="info-tile">
              <span>Type</span>
              <strong>{SPOT_TYPES[selected.type]}</strong>
            </div>
            <div className="info-tile">
              <span>Status</span>
              <strong>{SPOT_STATUS[selected.status]}</strong>
            </div>
          </div>
        ) : (
          <p className="muted">Select a spot on the grid to see its details.</p>
        )}
      </section>

      <section className="panel panel-current">
        <h2 className="panel-title">Current parked</h2>
        {user.isAdmin ? (
          <div className="empty-note">
            <p>Admin accounts manage parking but don't book spots.</p>
            <Link className="text-link" to="/reservations">
              See all reservations
            </Link>
          </div>
        ) : (
          <CurrentParking parking={parking} />
        )}
      </section>

      <section className="panel panel-book">
        <h2 className="panel-title">Book your parking</h2>
        {user.isAdmin ? (
          <div className="empty-note">
            <p>Add lots and spots from Management.</p>
            <Link className="button-gold" to="/management">
              Open Management
            </Link>
          </div>
        ) : (
          !loading && <BookingCard spot={selected} lot={lot} vehicles={vehicles} onBooked={refresh} />
        )}
      </section>
    </div>
  )
}
