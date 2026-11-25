import { useEffect, useState } from 'react'
import { authFetch } from '../api'
import NavBar from './NavBar'

const ICON = {
  SAVINGS_REMINDER: '⏰', BEHIND_TARGET: '⚠️', ACHIEVEMENT: '🏅', GOAL_COMPLETED: '🎯',
  COUPON_UNLOCKED: '🎁', COUPON_REDEEMED: '✅', BILL_REMINDER: '🧾', MILESTONE: '📈',
}

function Notifications({ user, onNavigate, onLogout }) {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [form, setForm] = useState({ title: '', note: '', dueDate: '', amount: '' })
  const [saving, setSaving] = useState(false)

  async function load() {
    setLoading(true)
    setError('')
    try {
      setItems(await authFetch('/api/notifications'))
    } catch (e) {
      setError(e.message || 'Could not load notifications.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [])

  async function markRead(id) {
    try { await authFetch(`/api/notifications/${id}/read`, { method: 'POST' }); await load() } catch (e) { setError(e.message) }
  }
  async function markAll() {
    try { await authFetch('/api/notifications/read-all', { method: 'POST' }); await load() } catch (e) { setError(e.message) }
  }
  async function dismiss(id) {
    try { await authFetch(`/api/notifications/${id}`, { method: 'DELETE' }); await load() } catch (e) { setError(e.message) }
  }

  async function addReminder(e) {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      await authFetch('/api/notifications/bill-reminder', {
        method: 'POST',
        body: JSON.stringify({
          title: form.title,
          note: form.note || null,
          dueDate: form.dueDate,
          amount: form.amount === '' ? null : Number(form.amount),
        }),
      })
      setForm({ title: '', note: '', dueDate: '', amount: '' })
      await load()
    } catch (e) {
      setError(e.message || 'Could not add reminder.')
    } finally {
      setSaving(false)
    }
  }

  const input = 'w-full bg-[#F4EBDD] border-2 border-transparent focus:border-[#171513] rounded-xl px-3 py-2 outline-none text-sm'

  return (
    <div className="min-h-screen w-full bg-[#F4EBDD] text-[#171513] p-4 md:p-8">
      <div className="max-w-4xl mx-auto">
        <NavBar user={user} active="notifications" onNavigate={onNavigate} onLogout={onLogout} />

        <div className="flex items-center justify-between mb-6">
          <h1 className="text-3xl font-black">Notifications 🔔</h1>
          <button onClick={markAll} className="px-4 py-2 rounded-xl border-2 border-[#171513] font-bold text-sm hover:bg-[#171513] hover:text-white transition">
            Mark all read
          </button>
        </div>

        {error && <div className="mb-6 px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">{error}</div>}

        {/* Bill reminder creator */}
        <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513] mb-6">
          <h3 className="font-black text-lg mb-3">Add a bill / payment reminder</h3>
          <form onSubmit={addReminder} className="grid md:grid-cols-4 gap-3 items-end">
            <div className="md:col-span-2">
              <label className="text-[11px] font-bold uppercase tracking-wide opacity-60">Title</label>
              <input className={input} required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} placeholder="Electricity bill" />
            </div>
            <div>
              <label className="text-[11px] font-bold uppercase tracking-wide opacity-60">Due date</label>
              <input type="date" className={input} required value={form.dueDate} onChange={(e) => setForm({ ...form, dueDate: e.target.value })} />
            </div>
            <div>
              <label className="text-[11px] font-bold uppercase tracking-wide opacity-60">Amount (₹, optional)</label>
              <input type="number" className={input} value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} placeholder="1200" />
            </div>
            <div className="md:col-span-3">
              <label className="text-[11px] font-bold uppercase tracking-wide opacity-60">Note (optional)</label>
              <input className={input} value={form.note} onChange={(e) => setForm({ ...form, note: e.target.value })} placeholder="Pay before late fee" />
            </div>
            <button disabled={saving} className="bg-[#171513] text-white py-2 rounded-xl font-bold hover:bg-[#E8893A] hover:text-[#171513] transition disabled:opacity-40">
              {saving ? 'adding…' : 'Add reminder'}
            </button>
          </form>
        </div>

        {loading ? (
          <p className="text-sm opacity-50">Loading…</p>
        ) : items.length === 0 ? (
          <p className="text-sm opacity-50">No notifications yet. Log savings or earn a badge to see activity here.</p>
        ) : (
          <div className="space-y-3">
            {items.map((n) => (
              <div key={n.id} className={`border-2 border-[#171513] rounded-2xl p-4 flex items-start gap-3 ${n.read ? 'bg-white/60' : 'bg-white shadow-[3px_3px_0px_#171513]'}`}>
                <span className="text-2xl">{ICON[n.type] || '🔔'}</span>
                <div className="flex-1">
                  <div className="flex items-center gap-2">
                    {!n.read && <span className="w-2 h-2 rounded-full bg-[#E8893A]" />}
                    <p className="font-black">{n.title}</p>
                  </div>
                  <p className="text-sm opacity-80">{n.message}</p>
                  <p className="text-[11px] opacity-50 mt-1">
                    {n.dueDate ? `Due ${n.dueDate} · ` : ''}{new Date(n.createdAt).toLocaleString()}
                  </p>
                </div>
                <div className="flex flex-col gap-1">
                  {!n.read && <button onClick={() => markRead(n.id)} className="text-xs font-bold underline">read</button>}
                  <button onClick={() => dismiss(n.id)} className="text-xs font-bold opacity-60 hover:opacity-100">dismiss</button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default Notifications
