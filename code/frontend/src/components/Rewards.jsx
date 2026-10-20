import { useEffect, useState } from 'react'
import { authFetch } from '../api'
import NavBar from './NavBar'

function Rewards({ user, onNavigate, onLogout }) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [celebrate, setCelebrate] = useState([])

  async function load() {
    setLoading(true)
    setError('')
    try {
      const d = await authFetch('/api/gamification/summary')
      setData(d)
      // Celebrate badges unlocked since the user last visited this page.
      const unlocked = (d.achievements || []).filter((a) => a.unlocked).map((a) => a.code)
      let seen = []
      try { seen = JSON.parse(localStorage.getItem('seenBadges') || '[]') } catch { seen = [] }
      const fresh = (d.achievements || []).filter((a) => a.unlocked && !seen.includes(a.code))
      if (fresh.length) setCelebrate(fresh)
      localStorage.setItem('seenBadges', JSON.stringify(unlocked))
    } catch (e) {
      setError(e.message || 'Could not load rewards.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [])

  const next = data?.nextAchievement

  return (
    <div className="min-h-screen w-full bg-[#F4EBDD] text-[#171513] p-4 md:p-8">
      <div className="max-w-5xl mx-auto">
        <NavBar user={user} active="rewards" onNavigate={onNavigate} onLogout={onLogout} />

        <h1 className="text-3xl font-black mb-1">Rewards &amp; Streaks 🏆</h1>
        <p className="text-sm opacity-60 mb-6">Points and badges are earned only from your real savings progress.</p>

        {error && <div className="mb-6 px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">{error}</div>}

        {celebrate.length > 0 && (
          <div className="mb-6 px-5 py-4 rounded-2xl bg-[#789B4A] text-white border-2 border-[#171513] shadow-[4px_4px_0px_#171513]">
            <p className="font-black text-lg">🎉 New milestone{celebrate.length > 1 ? 's' : ''} unlocked!</p>
            <p className="text-sm mt-1">{celebrate.map((c) => c.title).join(', ')} — keep it up!</p>
            <button onClick={() => setCelebrate([])} className="mt-2 text-xs underline">dismiss</button>
          </div>
        )}

        {loading ? (
          <p className="text-sm opacity-50">Loading…</p>
        ) : data ? (
          <>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
              <Stat label="Total points" value={data.totalPoints} accent="text-[#E8893A]" />
              <Stat label="Current weekly streak" value={`${data.currentWeeklyStreak} 🔥`} />
              <Stat label="Longest weekly streak" value={data.longestWeeklyStreak} />
              <Stat label="Weeks target met" value={data.qualifyingWeeks} accent="text-[#789B4A]" />
            </div>

            {next && (
              <div className="bg-[#171513] text-white rounded-[24px] p-6 mb-6 shadow-[6px_6px_0px_#171513]">
                <p className="text-xs opacity-60">NEXT ACHIEVEMENT</p>
                <p className="text-xl font-black">{next.title}</p>
                <p className="text-sm opacity-70 mb-3">{next.description}</p>
                <div className="h-3 bg-white/20 rounded-full overflow-hidden">
                  <div className="h-3 bg-[#E8893A] rounded-full transition-all" style={{ width: `${Math.min(next.progressPercent, 100)}%` }} />
                </div>
                <p className="text-xs mt-2 opacity-70">
                  {Math.round(next.progressCurrent)} / {Math.round(next.progressTarget)} ({next.progressPercent}%)
                </p>
              </div>
            )}

            <h3 className="font-black text-lg mb-3">Badges</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {data.achievements.map((a) => (
                <div
                  key={a.code}
                  className={`border-2 border-[#171513] rounded-2xl p-5 ${
                    a.unlocked ? 'bg-white shadow-[4px_4px_0px_#171513]' : 'bg-[#171513]/5 opacity-70'
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <span className="text-2xl">{a.unlocked ? '🏅' : '🔒'}</span>
                    <span className={`text-xs font-black rounded-full px-2 py-1 ${a.unlocked ? 'bg-[#789B4A] text-white' : 'bg-[#171513]/10'}`}>
                      +{a.points} pts
                    </span>
                  </div>
                  <p className="font-black mt-2">{a.title}</p>
                  <p className="text-sm opacity-70">{a.description}</p>
                  <p className="text-[11px] mt-2 font-bold uppercase tracking-wide opacity-50">
                    {a.unlocked ? 'Unlocked' : 'Locked'}
                  </p>
                </div>
              ))}
            </div>
          </>
        ) : null}
      </div>
    </div>
  )
}

function Stat({ label, value, accent }) {
  return (
    <div className="bg-white border-2 border-[#171513] rounded-2xl p-5 shadow-[4px_4px_0px_#171513]">
      <p className="text-[11px] font-bold uppercase tracking-wide opacity-60">{label}</p>
      <p className={`text-2xl font-black mt-1 ${accent || ''}`}>{value}</p>
    </div>
  )
}

export default Rewards
