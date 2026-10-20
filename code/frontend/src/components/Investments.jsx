import { useEffect, useState } from 'react'
import { authFetch, inr } from '../api'
import NavBar from './NavBar'

const RISK = {
  LOW: 'bg-[#789B4A] text-white',
  MEDIUM: 'bg-[#E8893A] text-white',
  HIGH: 'bg-red-500 text-white',
}

function Investments({ user, onNavigate, onLogout }) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let ok = true
    authFetch('/api/investments/recommendations')
      .then((d) => { if (ok) setData(d) })
      .catch((e) => { if (ok) setError(e.message || 'Could not load investment info.') })
      .finally(() => { if (ok) setLoading(false) })
    return () => { ok = false }
  }, [])

  const p = data?.profile

  return (
    <div className="min-h-screen w-full bg-[#F4EBDD] text-[#171513] p-4 md:p-8">
      <div className="max-w-5xl mx-auto">
        <NavBar user={user} active="investments" onNavigate={onNavigate} onLogout={onLogout} />

        <h1 className="text-3xl font-black mb-1">Investment Information 📚</h1>
        <p className="text-sm opacity-60 mb-4">Educational categories based on your plan — separate from side-hustle suggestions.</p>

        {data?.disclaimer && (
          <div className="mb-6 px-4 py-3 rounded-xl bg-amber-50 border border-amber-200 text-amber-900 text-sm">
            <b>Important: </b>{data.disclaimer}
          </div>
        )}
        {error && <div className="mb-6 px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">{error}</div>}

        {loading ? (
          <p className="text-sm opacity-50">Loading…</p>
        ) : data ? (
          <>
            {p && (
              <div className="bg-[#171513] text-white rounded-[24px] p-6 mb-6 shadow-[6px_6px_0px_#171513] grid grid-cols-2 md:grid-cols-4 gap-4">
                <div><p className="text-xs opacity-60">RISK PROFILE</p><p className="text-xl font-black text-[#E8893A]">{p.riskProfile}</p></div>
                <div><p className="text-xs opacity-60">TIME HORIZON</p><p className="text-xl font-black">{p.timelineYears != null ? `${p.timelineYears} yr` : '—'}</p></div>
                <div><p className="text-xs opacity-60">MONTHLY SURPLUS</p><p className="text-xl font-black">{p.monthlySurplus != null ? inr(p.monthlySurplus) : '—'}</p></div>
                <div><p className="text-xs opacity-60">AGE BAND</p><p className="text-xl font-black">{p.ageBand || '—'}</p></div>
              </div>
            )}

            {data.missingInfo?.length > 0 && (
              <div className="mb-6 px-4 py-3 rounded-xl bg-blue-50 border border-blue-200 text-blue-800 text-sm">
                <p className="font-bold mb-1">To improve this guidance, add:</p>
                <ul className="list-disc ml-5">{data.missingInfo.map((m, i) => <li key={i}>{m}</li>)}</ul>
              </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {data.suggestions.map((s, i) => (
                <div key={i} className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513]">
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <h3 className="text-lg font-black leading-tight">{s.name}</h3>
                      <p className="text-xs font-bold uppercase tracking-wide opacity-60">{s.category} · {s.horizonFit}</p>
                    </div>
                    <span className={`text-[11px] font-black rounded-full px-2.5 py-1 ${RISK[s.riskLevel] || 'bg-[#171513]/10'}`}>{s.riskLevel} RISK</span>
                  </div>
                  <div className="mt-3 bg-[#F4EBDD] rounded-xl p-3">
                    <p className="text-[11px] font-bold uppercase tracking-wide opacity-60 mb-1">Why this may be relevant</p>
                    <p className="text-sm">{s.whyRelevant}</p>
                  </div>
                  <p className="text-sm opacity-80 mt-3"><b>Note:</b> {s.educationalNote}</p>
                  <div className="mt-3 flex flex-wrap gap-1.5">
                    {s.exampleVehicles.map((v) => (
                      <span key={v} className="text-xs font-bold rounded-full px-2.5 py-1 border border-[#171513]/20">{v}</span>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </>
        ) : null}
      </div>
    </div>
  )
}

export default Investments
