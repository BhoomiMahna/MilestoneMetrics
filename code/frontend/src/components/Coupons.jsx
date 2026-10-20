import { useEffect, useState } from 'react'
import { authFetch } from '../api'
import NavBar from './NavBar'

const STATUS_STYLE = {
  AVAILABLE: 'bg-[#789B4A] text-white',
  REDEEMED: 'bg-[#171513] text-white',
  LOCKED: 'bg-[#171513]/10 text-[#171513]',
  EXPIRED: 'bg-red-100 text-red-700',
}

function Coupons({ user, onNavigate, onLogout }) {
  const [points, setPoints] = useState(0)
  const [coupons, setCoupons] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [busyId, setBusyId] = useState(null)
  const [flash, setFlash] = useState('')

  async function load() {
    setLoading(true)
    setError('')
    try {
      const d = await authFetch('/api/coupons')
      setPoints(d.points || 0)
      setCoupons(d.coupons || [])
    } catch (e) {
      setError(e.message || 'Could not load offers.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [])

  async function redeem(id) {
    setBusyId(id)
    setError('')
    setFlash('')
    try {
      const r = await authFetch(`/api/coupons/${id}/redeem`, { method: 'POST' })
      setFlash(`${r.title} redeemed — demo code: ${r.codeIssued}`)
      await load()
    } catch (e) {
      setError(e.message || 'Could not redeem.')
    } finally {
      setBusyId(null)
    }
  }

  return (
    <div className="min-h-screen w-full bg-[#F4EBDD] text-[#171513] p-4 md:p-8">
      <div className="max-w-5xl mx-auto">
        <NavBar user={user} active="coupons" onNavigate={onNavigate} onLogout={onLogout} />

        <div className="flex items-end justify-between mb-2">
          <h1 className="text-3xl font-black">Coupons &amp; Offers 🎁</h1>
          <div className="bg-white border-2 border-[#171513] rounded-xl px-4 py-2 font-black shadow-[3px_3px_0px_#171513]">
            {points} pts
          </div>
        </div>
        <div className="mb-6 px-4 py-2 rounded-xl bg-amber-50 border border-amber-200 text-amber-800 text-sm">
          Demonstration offers only — codes are not valid with any real merchant and no external coupon provider is used.
        </div>

        {error && <div className="mb-6 px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">{error}</div>}
        {flash && <div className="mb-6 px-4 py-3 rounded-xl bg-green-50 border border-green-200 text-green-700 text-sm">{flash}</div>}

        {loading ? (
          <p className="text-sm opacity-50">Loading offers…</p>
        ) : coupons.length === 0 ? (
          <p className="text-sm opacity-50">No offers available.</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {coupons.map((c) => (
              <div key={c.id} className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513] flex flex-col">
                <div className="flex items-start justify-between gap-2">
                  <h3 className="text-lg font-black leading-tight">{c.title}</h3>
                  <span className={`text-[11px] font-black rounded-full px-2.5 py-1 whitespace-nowrap ${STATUS_STYLE[c.status] || ''}`}>
                    {c.status}
                  </span>
                </div>
                <p className="text-sm opacity-80 mt-2">{c.description}</p>
                <p className="mt-3 text-xl font-black text-[#E8893A]">{c.rewardLabel}</p>
                <div className="mt-2 text-sm grid grid-cols-2 gap-2">
                  <div><span className="opacity-60">Needs</span> <b>{c.pointsRequired} pts</b></div>
                  {c.expiryDate && <div><span className="opacity-60">Expires</span> <b>{c.expiryDate}</b></div>}
                </div>

                {c.status === 'REDEEMED' && c.codeIssued && (
                  <div className="mt-4 bg-[#F4EBDD] rounded-xl p-3 text-sm">
                    <p className="text-[11px] font-bold uppercase tracking-wide opacity-60">Your demo code</p>
                    <p className="font-black">{c.codeIssued}</p>
                  </div>
                )}

                <div className="mt-auto pt-4">
                  {c.status === 'AVAILABLE' && (
                    <button onClick={() => redeem(c.id)} disabled={busyId === c.id}
                      className="w-full bg-[#171513] text-white py-3 rounded-xl font-bold hover:bg-[#E8893A] hover:text-[#171513] transition disabled:opacity-40">
                      {busyId === c.id ? 'redeeming…' : 'Redeem'}
                    </button>
                  )}
                  {c.status === 'LOCKED' && (
                    <p className="text-center text-sm font-bold opacity-60">Earn {c.pointsRequired - points} more pts to unlock</p>
                  )}
                  {c.status === 'EXPIRED' && <p className="text-center text-sm font-bold text-red-600">Expired</p>}
                  {c.status === 'REDEEMED' && <p className="text-center text-sm font-bold text-[#789B4A]">Redeemed ✓</p>}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default Coupons
