import { useState } from 'react'
import { Car, Trash2 } from 'lucide-react'
import * as api from '../api.js'
import { useToast } from '../components/AppShell.jsx'
import { useParking } from '../useParking.js'

export default function Vehicles() {
  const toast = useToast()
  const { vehicles, loading, error, refresh } = useParking()
  const [form, setForm] = useState({ plateNumber: '', model: '' })
  const [busy, setBusy] = useState(false)

  const submit = async (event) => {
    event.preventDefault()
    setBusy(true)
    try {
      await api.addVehicle(form)
      setForm({ plateNumber: '', model: '' })
      toast('Vehicle added.')
      await refresh()
    } catch (err) {
      // A duplicate plate comes back as a plain 500
      toast(err.status === 500 ? 'Could not add that vehicle. The plate may already be registered.' : err.message, 'error')
    } finally {
      setBusy(false)
    }
  }

  const remove = async (vehicle) => {
    if (!window.confirm(`Remove ${vehicle.plateNumber}?`)) return
    try {
      await api.deleteVehicle(vehicle.id)
      toast('Vehicle removed.')
      await refresh()
    } catch (err) {
      toast(err.message, 'error')
    }
  }

  return (
    <div className="two-column">
      <section className="panel page-panel">
        <h1 className="panel-title">My vehicles</h1>
        {error && (
          <p className="banner" role="alert">
            {error}
          </p>
        )}
        {!loading && vehicles.length === 0 && <p className="muted">No vehicles yet. Add one to start booking.</p>}
        <ul className="row-list">
          {vehicles.map((vehicle) => (
            <li key={vehicle.id} className="row-card">
              <Car size={22} aria-hidden="true" />
              <div className="row-main">
                <strong>{vehicle.plateNumber}</strong>
                <span className="muted">{vehicle.model}</span>
              </div>
              <button className="round-button" aria-label={`Remove ${vehicle.plateNumber}`} onClick={() => remove(vehicle)}>
                <Trash2 size={18} />
              </button>
            </li>
          ))}
        </ul>
      </section>

      <section className="panel page-panel">
        <h2 className="panel-title">Add a vehicle</h2>
        <form className="stack" onSubmit={submit}>
          <label className="field">
            <span className="field-label">Plate number</span>
            <input required value={form.plateNumber} onChange={(e) => setForm({ ...form, plateNumber: e.target.value })} />
          </label>
          <label className="field">
            <span className="field-label">Model</span>
            <input required value={form.model} onChange={(e) => setForm({ ...form, model: e.target.value })} />
          </label>
          <button className="button-gold" disabled={busy}>
            Add vehicle
          </button>
        </form>
      </section>
    </div>
  )
}
