import { useState } from 'react'
import { ParkingSquare } from 'lucide-react'
import { Link } from 'react-router-dom'
import { useAuth } from '../auth.jsx'

// Decorative preview of the grid: 1 = free, 0 = taken
const PREVIEW = [1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1]

export default function AuthPage({ mode }) {
  const isLogin = mode === 'login'
  const { login, register } = useAuth()
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const update = (key) => (event) => setForm({ ...form, [key]: event.target.value })

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    setBusy(true)
    try {
      if (isLogin) await login(form.email, form.password)
      else await register(form)
    } catch (err) {
      setError(err.status === 409 ? 'An account with that email already exists.' : err.message)
      setBusy(false)
    }
  }

  return (
    <div className="backdrop backdrop-center">
      <div className="auth-card">
        <section className="auth-form">
          <div className="brand">
            <span className="round-mark" aria-hidden="true">
              <ParkingSquare size={22} />
            </span>
            Smart Parking
          </div>
          <h1>{isLogin ? 'Welcome back' : 'Create your account'}</h1>
          <p className="muted">{isLogin ? 'Sign in to find and reserve a spot.' : 'Reserve a parking spot in seconds.'}</p>

          <form className="stack" onSubmit={submit}>
            {!isLogin && (
              <label className="field">
                <span className="field-label">Username</span>
                <input required autoComplete="username" value={form.username} onChange={update('username')} />
              </label>
            )}
            <label className="field">
              <span className="field-label">Email</span>
              <input required type="email" autoComplete="email" value={form.email} onChange={update('email')} />
            </label>
            <label className="field">
              <span className="field-label">Password</span>
              <input
                required
                type="password"
                autoComplete={isLogin ? 'current-password' : 'new-password'}
                value={form.password}
                onChange={update('password')}
              />
            </label>
            {error && (
              <p className="form-error" role="alert">
                {error}
              </p>
            )}
            <button className="button-gold" disabled={busy}>
              {busy ? 'One moment...' : isLogin ? 'Sign in' : 'Create account'}
            </button>
          </form>

          <p className="auth-switch">
            {isLogin ? 'New here?' : 'Already have an account?'}{' '}
            <Link className="text-link" to={isLogin ? '/register' : '/login'} onClick={() => setError('')}>
              {isLogin ? 'Create an account' : 'Sign in'}
            </Link>
          </p>
        </section>

        <aside className="auth-visual" aria-hidden="true">
          <div className="preview-grid">
            {PREVIEW.map((free, index) => (
              <span key={index} className={free ? 'preview-free' : 'preview-taken'} />
            ))}
          </div>
          <h2>Know it's free before you drive there.</h2>
          <p>Live availability, and only one driver can ever reserve a spot.</p>
        </aside>
      </div>
    </div>
  )
}
