import { useState } from 'react'
import * as api from '../api.js'
import { useToast } from '../components/AppShell.jsx'
import { SPOT_TYPES, money } from '../format.js'
import { useParking } from '../useParking.js'

const EMPTY_LOT = { name: '', location: '', hourlyRate: '', totalSpots: '' }

export default function Management() {
  const toast = useToast()
  const { lots, spots, error, refresh } = useParking()
  const [lotForm, setLotForm] = useState(EMPTY_LOT)
  const [spotForm, setSpotForm] = useState({ lotId: '', spotNumber: '', type: 'REGULAR' })
  const [busy, setBusy] = useState(false)

  const targetLotId = spotForm.lotId || lots[0]?.id || ''

  const addLot = async (event) => {
    event.preventDefault()
    setBusy(true)
    try {
      const totalSpots = Number(lotForm.totalSpots)
      const lot = await api.createLot({ ...lotForm, hourlyRate: Number(lotForm.hourlyRate), totalSpots })
      // A new lot starts empty, so number its spots the same way DataSeeder does
      await Promise.all(
        Array.from({ length: totalSpots }, (_, i) =>
          api.createSpot({ lotId: lot.id, spotNumber: `P${i + 1}`, type: 'REGULAR' }),
        ),
      )
      setLotForm(EMPTY_LOT)
      toast(`${lot.name} created with ${totalSpots} spots.`)
    } catch (err) {
      toast(err.status === 409 ? 'A lot with that name already exists.' : err.message, 'error')
    } finally {
      setBusy(false)
      refresh()
    }
  }

  const addSpot = async (event) => {
    event.preventDefault()
    setBusy(true)
    try {
      await api.createSpot({ ...spotForm, lotId: targetLotId })
      setSpotForm({ ...spotForm, spotNumber: '' })
      toast('Spot added.')
    } catch (err) {
      toast(err.status === 409 ? 'That spot number already exists in this lot.' : err.message, 'error')
    } finally {
      setBusy(false)
      refresh()
    }
  }

  return (
    <div className="two-column">
      <section className="panel page-panel">
        <h1 className="panel-title">Parking lots</h1>
        {error && (
          <p className="banner" role="alert">
            {error}
          </p>
        )}
        <ul className="row-list">
          {lots.map((lot) => {
            const lotSpots = spots.filter((s) => s.lotId === lot.id)
            const free = lotSpots.filter((s) => s.status === 'AVAILABLE').length
            return (
              <li key={lot.id} className="row-card">
                <div className="row-main">
                  <strong>{lot.name}</strong>
                  <span className="muted">
                    {lot.location} · {money(lot.hourlyRate)} per hour
                  </span>
                </div>
                <span className="status">
                  {free} of {lotSpots.length} free
                </span>
              </li>
            )
          })}
        </ul>
      </section>

      <div className="stack">
        <section className="panel page-panel">
          <h2 className="panel-title">Add a lot</h2>
          <form className="stack" onSubmit={addLot}>
            <label className="field">
              <span className="field-label">Name</span>
              <input required value={lotForm.name} onChange={(e) => setLotForm({ ...lotForm, name: e.target.value })} />
            </label>
            <label className="field">
              <span className="field-label">Location</span>
              <input required value={lotForm.location} onChange={(e) => setLotForm({ ...lotForm, location: e.target.value })} />
            </label>
            <div className="field-row">
              <label className="field">
                <span className="field-label">Rate per hour (BD)</span>
                <input required type="number" min="0" step="0.001" value={lotForm.hourlyRate} onChange={(e) => setLotForm({ ...lotForm, hourlyRate: e.target.value })} />
              </label>
              <label className="field">
                <span className="field-label">Number of spots</span>
                <input required type="number" min="1" max="200" value={lotForm.totalSpots} onChange={(e) => setLotForm({ ...lotForm, totalSpots: e.target.value })} />
              </label>
            </div>
            <button className="button-gold" disabled={busy}>
              Create lot
            </button>
          </form>
        </section>

        <section className="panel page-panel">
          <h2 className="panel-title">Add a spot</h2>
          <form className="stack" onSubmit={addSpot}>
            <label className="field">
              <span className="field-label">Lot</span>
              <select value={targetLotId} onChange={(e) => setSpotForm({ ...spotForm, lotId: e.target.value })}>
                {lots.map((lot) => (
                  <option key={lot.id} value={lot.id}>
                    {lot.name}
                  </option>
                ))}
              </select>
            </label>
            <div className="field-row">
              <label className="field">
                <span className="field-label">Spot number</span>
                <input required value={spotForm.spotNumber} onChange={(e) => setSpotForm({ ...spotForm, spotNumber: e.target.value })} />
              </label>
              <label className="field">
                <span className="field-label">Type</span>
                <select value={spotForm.type} onChange={(e) => setSpotForm({ ...spotForm, type: e.target.value })}>
                  {Object.entries(SPOT_TYPES).map(([value, label]) => (
                    <option key={value} value={value}>
                      {label}
                    </option>
                  ))}
                </select>
              </label>
            </div>
            <button className="button-gold" disabled={busy || !targetLotId}>
              Add spot
            </button>
          </form>
        </section>
      </div>
    </div>
  )
}
