import { useEffect, useMemo, useState } from 'react'
import { authFetch, inr } from '../api'
import NavBar from './NavBar'

const TABS = [
  { key: 'WEEK', label: 'Weekly' },
  { key: 'MONTH', label: 'Monthly' },
  { key: 'YEAR', label: 'Yearly' },
]

// ---- date helpers (all local time, no UTC drift) ----
function fmtISO(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function periodRange(period, value) {
  if (period === 'WEEK') {
    const d = new Date(`${value}T00:00:00`)
    const offset = (d.getDay() + 6) % 7 // Monday = 0
    const start = new Date(d)
    start.setDate(d.getDate() - offset)
    const end = new Date(start)
    end.setDate(start.getDate() + 6)
    const opts = { day: 'numeric', month: 'short' }
    return {
      start,
      end,
      label: `${start.toLocaleDateString('en-GB', opts)} – ${end.toLocaleDateString('en-GB', opts)}`,
    }
  }
  if (period === 'MONTH') {
    const [y, m] = value.split('-').map(Number)
    const start = new Date(y, m - 1, 1)
    const end = new Date(y, m, 0)
    return {
      start,
      end,
      label: start.toLocaleDateString('en-GB', { month: 'short', year: 'numeric' }),
    }
  }
  // YEAR
  const y = Number(value)
  return { start: new Date(y, 0, 1), end: new Date(y, 11, 31), label: String(y) }
}

function StatCard({ label, value, accent }) {
  return (
    <div className="bg-white border-2 border-[#171513] rounded-2xl p-5 shadow-[4px_4px_0px_#171513]">
      <p className="text-[11px] font-bold uppercase tracking-wide opacity-60">{label}</p>
      <p className={`text-2xl font-black mt-1 ${accent || ''}`}>{value}</p>
    </div>
  )
}

function ProgressChart({ records }) {
  const shown = [...records].slice(0, 8).reverse() // oldest -> newest
  if (shown.length === 0) {
    return <p className="text-sm opacity-50">No savings logged yet — add your first entry below.</p>
  }
  const max = Math.max(...shown.map((r) => Math.max(r.actualAmount || 0, r.targetAmount || 0)), 1)
  return (
    <div className="flex items-end gap-4 h-48 overflow-x-auto pb-2">
      {shown.map((r) => {
        const met = (r.actualAmount || 0) >= (r.targetAmount || 0)
        return (
          <div key={r.id} className="flex flex-col items-center gap-2 min-w-[54px]">
            <div className="flex items-end gap-1 h-40">
              <div
                className="w-4 bg-[#171513]/15 rounded-t"
                style={{ height: `${((r.targetAmount || 0) / max) * 100}%` }}
                title={`Target ${inr(r.targetAmount)}`}
              />
              <div
                className={`w-4 rounded-t ${met ? 'bg-[#789B4A]' : 'bg-[#E8893A]'}`}
                style={{ height: `${((r.actualAmount || 0) / max) * 100}%` }}
                title={`Actual ${inr(r.actualAmount)}`}
              />
            </div>
            <span className="text-[10px] font-medium opacity-60 text-center leading-tight">{r.periodLabel}</span>
          </div>
        )
      })}
    </div>
  )
}

function FinancialProgress({ plan, user, onEditPlan, onOpenSideHustle, onLogout, onNavigate }) {
  const [period, setPeriod] = useState('WEEK')
  const [progress, setProgress] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  // period selector inputs
  const today = new Date()
  const [weekDate, setWeekDate] = useState(fmtISO(today))
  const [monthValue, setMonthValue] = useState(fmtISO(today).slice(0, 7))
  const [yearValue, setYearValue] = useState(String(today.getFullYear()))
  const [actual, setActual] = useState('')
  const [saving, setSaving] = useState(false)

  const selectorValue = period === 'WEEK' ? weekDate : period === 'MONTH' ? monthValue : yearValue
  const range = useMemo(() => periodRange(period, selectorValue), [period, selectorValue])

  async function loadProgress(p = period) {
    setLoading(true)
    setError('')
    try {
      const data = await authFetch(`/api/progress?period=${p}`)
      setProgress(data)
    } catch (e) {
      setError(e.message || 'Could not load progress.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadProgress(period)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [period])

  // Prefill the amount if the selected period was already logged.
  useEffect(() => {
    if (!progress) return
    const startISO = fmtISO(range.start)
    const existing = progress.records.find((r) => r.periodStart === startISO)
    setActual(existing ? String(existing.actualAmount) : '')
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [progress, period, selectorValue])

  const targetPerPeriod = progress?.targetPerPeriod || 0
  const actualNum = Number(actual) || 0
  const remaining = Math.max(targetPerPeriod - actualNum, 0)
  const percent = targetPerPeriod > 0 ? Math.min(Math.round((actualNum / targetPerPeriod) * 100), 999) : 0

  async function saveSavings() {
    setSaving(true)
    setError('')
    try {
      await authFetch('/api/savings', {
        method: 'POST',
        body: JSON.stringify({
          periodType: period,
          periodStart: fmtISO(range.start),
          periodEnd: fmtISO(range.end),
          periodLabel: range.label,
          actualAmount: actualNum,
        }),
      })
      await loadProgress(period)
    } catch (e) {
      setError(e.message || 'Could not save your savings.')
    } finally {
      setSaving(false)
    }
  }

  const goalPercent = progress?.goalPercent || 0

  return (
    <div className="min-h-screen w-full bg-[#F4EBDD] text-[#171513] p-4 md:p-8">
      <div className="max-w-5xl mx-auto">

        {onNavigate && <NavBar user={user} active="dashboard" onNavigate={onNavigate} onLogout={onLogout} />}

        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <p className="text-xs font-bold uppercase tracking-wide opacity-60">Financial Progress</p>
            <h1 className="text-3xl font-black">Hey{user?.name ? `, ${user.name.split(' ')[0]}` : ''} </h1>
          </div>
          <div className="flex gap-2">
            <button onClick={onEditPlan} className="px-4 py-2 rounded-xl border-2 border-[#171513] font-bold text-sm hover:bg-[#171513] hover:text-white transition">
              Edit plan
            </button>
          </div>
        </div>

        {/* Goal summary */}
        <div className="bg-[#171513] text-white rounded-[24px] p-6 mb-6 shadow-[6px_6px_0px_#171513]">
          <div className="flex flex-wrap justify-between gap-4">
            <div>
              <p className="text-xs opacity-60">GOAL</p>
              <p className="text-2xl font-black">{inr(plan?.goalAmount)}</p>
            </div>
            <div>
              <p className="text-xs opacity-60">SAVED SO FAR ({period.toLowerCase()} view)</p>
              <p className="text-2xl font-black text-[#E8893A]">{inr(progress?.totalActual)}</p>
            </div>
            <div>
              <p className="text-xs opacity-60">REMAINING TO GOAL</p>
              <p className="text-2xl font-black">{inr(progress?.remainingToGoal)}</p>
            </div>
            <div>
              <p className="text-xs opacity-60">GOAL ACHIEVED</p>
              <p className="text-2xl font-black text-[#789B4A]">{goalPercent}%</p>
            </div>
          </div>
          <div className="mt-4 h-3 bg-white/20 rounded-full overflow-hidden">
            <div className="h-3 bg-[#789B4A] rounded-full transition-all" style={{ width: `${Math.min(goalPercent, 100)}%` }} />
          </div>
        </div>

        {/* Tabs */}
        <div className="flex gap-2 mb-6">
          {TABS.map((t) => (
            <button
              key={t.key}
              onClick={() => setPeriod(t.key)}
              className={`px-5 py-2 rounded-full border-2 font-bold text-sm transition ${
                period === t.key
                  ? 'bg-[#171513] text-white border-[#171513]'
                  : 'bg-white text-[#171513] border-[#171513]/20 hover:border-[#171513]'
              }`}
            >
              {t.label}
            </button>
          ))}
        </div>

        {error && (
          <div className="mb-6 px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">{error}</div>
        )}

        {/* Per-period stat cards */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          <StatCard label="Target savings" value={inr(targetPerPeriod)} />
          <StatCard label="Actual savings" value={inr(actualNum)} accent="text-[#E8893A]" />
          <StatCard label="Remaining" value={inr(remaining)} />
          <StatCard label="Progress" value={`${percent}%`} accent={percent >= 100 ? 'text-[#789B4A]' : ''} />
        </div>

        <div className="grid md:grid-cols-2 gap-6">
          {/* Enter / update savings */}
          <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513]">
            <h3 className="font-black text-lg mb-1">Log your savings</h3>
            <p className="text-sm opacity-60 mb-5">Pick a {period.toLowerCase()} and enter what you actually saved.</p>

            <label className="text-xs font-bold uppercase tracking-wide opacity-60 block mb-2">Select {period.toLowerCase()}</label>
            {period === 'WEEK' && (
              <input type="date" value={weekDate} onChange={(e) => setWeekDate(e.target.value)}
                className="w-full bg-[#F4EBDD] border-2 border-transparent focus:border-[#171513] rounded-xl px-4 py-3 outline-none mb-2" />
            )}
            {period === 'MONTH' && (
              <input type="month" value={monthValue} onChange={(e) => setMonthValue(e.target.value)}
                className="w-full bg-[#F4EBDD] border-2 border-transparent focus:border-[#171513] rounded-xl px-4 py-3 outline-none mb-2" />
            )}
            {period === 'YEAR' && (
              <input type="number" value={yearValue} onChange={(e) => setYearValue(e.target.value)}
                className="w-full bg-[#F4EBDD] border-2 border-transparent focus:border-[#171513] rounded-xl px-4 py-3 outline-none mb-2" />
            )}
            <p className="text-sm font-bold mb-5">{range.label} · planned {inr(targetPerPeriod)}</p>

            <label className="text-xs font-bold uppercase tracking-wide opacity-60 block mb-2">Actual savings</label>
            <input type="number" placeholder="₹" value={actual} onChange={(e) => setActual(e.target.value)}
              className="w-full bg-[#F4EBDD] border-2 border-transparent focus:border-[#171513] rounded-xl px-4 py-3 outline-none text-lg mb-5" />

            <button onClick={saveSavings} disabled={saving || actual === ''}
              className="w-full bg-[#171513] text-white py-4 rounded-xl font-bold hover:bg-[#E8893A] hover:text-[#171513] transition disabled:opacity-30">
              {saving ? 'saving...' : 'Save savings →'}
            </button>
          </div>

          {/* Chart */}
          <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513]">
            <h3 className="font-black text-lg mb-1">Progress chart</h3>
            <p className="text-sm opacity-60 mb-5">
              <span className="inline-block w-3 h-3 bg-[#171513]/15 rounded-sm align-middle mr-1" /> target
              <span className="inline-block w-3 h-3 bg-[#E8893A] rounded-sm align-middle ml-3 mr-1" /> actual (below target)
              <span className="inline-block w-3 h-3 bg-[#789B4A] rounded-sm align-middle ml-3 mr-1" /> target met
            </p>
            {loading ? <p className="text-sm opacity-50">Loading…</p> : <ProgressChart records={progress?.records || []} />}
          </div>
        </div>

        {/* History */}
        <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513] mt-6">
          <h3 className="font-black text-lg mb-4">Savings history · {period.toLowerCase()}</h3>
          {loading ? (
            <p className="text-sm opacity-50">Loading…</p>
          ) : (progress?.records?.length || 0) === 0 ? (
            <p className="text-sm opacity-50">Nothing logged yet for this view.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left border-b-2 border-[#171513]/10">
                    <th className="py-2 pr-4 font-bold uppercase text-xs opacity-60">Period</th>
                    <th className="py-2 pr-4 font-bold uppercase text-xs opacity-60">Planned</th>
                    <th className="py-2 pr-4 font-bold uppercase text-xs opacity-60">Actual</th>
                    <th className="py-2 pr-4 font-bold uppercase text-xs opacity-60">Remaining</th>
                    <th className="py-2 pr-4 font-bold uppercase text-xs opacity-60">Progress</th>
                  </tr>
                </thead>
                <tbody>
                  {progress.records.map((r) => (
                    <tr key={r.id} className="border-b border-[#171513]/5">
                      <td className="py-3 pr-4 font-bold">{r.periodLabel}</td>
                      <td className="py-3 pr-4">{inr(r.targetAmount)}</td>
                      <td className="py-3 pr-4">{inr(r.actualAmount)}</td>
                      <td className="py-3 pr-4">{inr(r.remaining)}</td>
                      <td className="py-3 pr-4">
                        <span className={`font-black ${r.percent >= 100 ? 'text-[#789B4A]' : 'text-[#E8893A]'}`}>{r.percent}%</span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

      </div>
    </div>
  )
}

export default FinancialProgress
