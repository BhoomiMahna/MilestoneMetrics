import { useEffect, useState } from 'react'
import { authFetch } from '../api'

const LINKS = [
  ['dashboard', 'Dashboard'],
  ['rewards', 'Rewards'],
  ['coupons', 'Coupons'],
  ['investments', 'Invest'],
  ['sidehustle', 'Side-hustles'],
]

function NavBar({ user, active, onNavigate, onLogout }) {
  const [unread, setUnread] = useState(0)

  useEffect(() => {
    let ok = true
    authFetch('/api/notifications/unread-count')
      .then((d) => { if (ok) setUnread(d?.count || 0) })
      .catch(() => {})
    return () => { ok = false }
  }, [active])

  const btn = (key, label) => (
    <button
      key={key}
      onClick={() => onNavigate(key)}
      className={`px-3 py-2 rounded-xl border-2 border-[#171513] font-bold text-sm transition ${
        active === key ? 'bg-[#171513] text-white' : 'bg-white hover:bg-[#F4EBDD]'
      }`}
    >
      {label}
    </button>
  )

  return (
    <div className="flex flex-wrap items-center gap-2 mb-6">
      <span className="font-black text-lg mr-2">MilestoneMetrics</span>
      {LINKS.map(([k, l]) => btn(k, l))}
      <button
        onClick={() => onNavigate('notifications')}
        className={`relative px-3 py-2 rounded-xl border-2 border-[#171513] font-bold text-sm transition ${
          active === 'notifications' ? 'bg-[#171513] text-white' : 'bg-white hover:bg-[#F4EBDD]'
        }`}
      >
        🔔
        {unread > 0 && (
          <span className="absolute -top-2 -right-2 bg-[#E8893A] text-white text-[10px] font-black rounded-full px-1.5 py-0.5">
            {unread}
          </span>
        )}
      </button>
      {user?.role === 'ADMIN' && btn('admin', 'Admin')}
      <div className="ml-auto">
        <button
          onClick={onLogout}
          className="px-3 py-2 rounded-xl border-2 border-[#171513] font-bold text-sm hover:bg-[#E8893A] transition"
        >
          Log out
        </button>
      </div>
    </div>
  )
}

export default NavBar
