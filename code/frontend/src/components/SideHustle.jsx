import { useEffect, useMemo, useState } from 'react'
import { authFetch, inr } from '../api'

const DIFFICULTIES = ['ALL', 'BEGINNER', 'INTERMEDIATE', 'ADVANCED']

function splitCsv(text) {
  return (text || '')
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
}

function titleCase(s) {
  if (!s) return ''
  return s.charAt(0) + s.slice(1).toLowerCase()
}

function Card({ rec, onToggleBookmark }) {
  const matched = new Set((rec.matchedSkills || []).map((s) => s.toLowerCase()))
  return (
    <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513] flex flex-col">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h3 className="text-xl font-black leading-tight">{rec.name}</h3>
          <p className="text-xs font-bold uppercase tracking-wide opacity-60 mt-1">
            {rec.online ? 'Online' : 'Offline'} · {titleCase(rec.difficulty)}
          </p>
        </div>
        <div className="flex flex-col items-end gap-2">
          <div className="bg-[#789B4A] text-white text-sm font-black rounded-full px-3 py-1 whitespace-nowrap">
            {rec.matchScore}% match
          </div>
          <button
            onClick={() => onToggleBookmark(rec)}
            title={rec.bookmarked ? 'Remove bookmark' : 'Save opportunity'}
            className={`text-xs font-bold rounded-full px-3 py-1 border-2 border-[#171513] transition ${
              rec.bookmarked ? 'bg-[#171513] text-white' : 'bg-white hover:bg-[#F4EBDD]'
            }`}
          >
            {rec.bookmarked ? '★ Saved' : '☆ Save'}
          </button>
        </div>
      </div>

      <p className="text-sm opacity-80 mt-3">{rec.description}</p>

      {/* Why recommended */}
      <div className="mt-4 bg-[#F4EBDD] rounded-xl p-3">
        <p className="text-[11px] font-bold uppercase tracking-wide opacity-60 mb-1">Why this is recommended</p>
        <p className="text-sm">{rec.whyRecommended}</p>
      </div>

      {/* Required skills */}
      <div className="mt-4">
        <p className="text-[11px] font-bold uppercase tracking-wide opacity-60 mb-2">Required skills</p>
        <div className="flex flex-wrap gap-1.5">
          {(rec.requiredSkills || []).map((s) => (
            <span
              key={s}
              className={`text-xs font-bold rounded-full px-2.5 py-1 border ${
                matched.has(s.toLowerCase())
                  ? 'bg-[#789B4A] text-white border-[#789B4A]'
                  : 'bg-white border-[#171513]/20'
              }`}
            >
              {s}
            </span>
          ))}
        </div>
      </div>

      {/* Facts */}
      <div className="grid grid-cols-2 gap-3 mt-4 text-sm">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wide opacity-60">Earning potential</p>
          <p className="font-bold">{inr(rec.minMonthlyEarning)}–{inr(rec.maxMonthlyEarning)}/mo</p>
        </div>
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wide opacity-60">Time commitment</p>
          <p className="font-bold">{rec.minHoursPerWeek}–{rec.maxHoursPerWeek} hrs/week</p>
        </div>
      </div>

      <a
        href={rec.platformUrl}
        target="_blank"
        rel="noopener noreferrer"
        className="mt-5 w-full text-center bg-[#171513] text-white py-3 rounded-xl font-bold hover:bg-[#E8893A] hover:text-[#171513] transition"
      >
        View on {rec.platformName} →
      </a>
    </div>
  )
}

function SideHustle({ user, onBack, onLogout }) {
  const [profile, setProfile] = useState(null)
  const [recs, setRecs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [savingProfile, setSavingProfile] = useState(false)

  // editable profile inputs
  const [skillsInput, setSkillsInput] = useState('')
  const [interestsInput, setInterestsInput] = useState('')
  const [hours, setHours] = useState('')
  const [desired, setDesired] = useState('')

  // filters / sorting
  const [fSkill, setFSkill] = useState('')
  const [fHours, setFHours] = useState('')
  const [fEarn, setFEarn] = useState('')
  const [fDiff, setFDiff] = useState('ALL')
  const [fMode, setFMode] = useState('ALL') // ALL | online | offline
  const [fBookmark, setFBookmark] = useState(false)
  const [sortBy, setSortBy] = useState('match') // match | earning | time

  function applyProfile(p) {
    setProfile(p)
    setSkillsInput((p.skills || []).join(', '))
    setInterestsInput((p.interests || []).join(', '))
    setHours(p.availableHoursPerWeek ?? '')
    setDesired(p.desiredMonthlyIncome ?? '')
  }

  async function loadAll() {
    setLoading(true)
    setError('')
    try {
      const [p, r] = await Promise.all([
        authFetch('/api/sidehustle/profile'),
        authFetch('/api/sidehustle/recommendations'),
      ])
      applyProfile(p)
      setRecs(r)
    } catch (e) {
      setError(e.message || 'Could not load recommendations.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadAll()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  async function saveProfile() {
    setSavingProfile(true)
    setError('')
    try {
      const p = await authFetch('/api/sidehustle/profile', {
        method: 'PUT',
        body: JSON.stringify({
          skills: splitCsv(skillsInput),
          interests: splitCsv(interestsInput),
          availableHoursPerWeek: hours === '' ? null : Number(hours),
          desiredMonthlyIncome: desired === '' ? null : Number(desired),
        }),
      })
      applyProfile(p)
      const r = await authFetch('/api/sidehustle/recommendations')
      setRecs(r)
    } catch (e) {
      setError(e.message || 'Could not update your profile.')
    } finally {
      setSavingProfile(false)
    }
  }

  async function toggleBookmark(rec) {
    try {
      await authFetch(`/api/sidehustle/bookmarks/${rec.id}`, {
        method: rec.bookmarked ? 'DELETE' : 'POST',
      })
      setRecs((rs) => rs.map((x) => (x.id === rec.id ? { ...x, bookmarked: !x.bookmarked } : x)))
    } catch (e) {
      setError(e.message || 'Could not update bookmark.')
    }
  }

  const filtered = useMemo(() => {
    let list = [...recs]
    if (fSkill.trim()) {
      const q = fSkill.trim().toLowerCase()
      list = list.filter(
        (r) =>
          (r.requiredSkills || []).some((s) => s.toLowerCase().includes(q)) ||
          r.name.toLowerCase().includes(q),
      )
    }
    if (fHours !== '') {
      const h = Number(fHours)
      list = list.filter((r) => (r.minHoursPerWeek ?? 0) <= h)
    }
    if (fEarn !== '') {
      const e = Number(fEarn)
      list = list.filter((r) => (r.maxMonthlyEarning ?? 0) >= e)
    }
    if (fDiff !== 'ALL') list = list.filter((r) => r.difficulty === fDiff)
    if (fMode !== 'ALL') list = list.filter((r) => (fMode === 'online' ? r.online : !r.online))
    if (fBookmark) list = list.filter((r) => r.bookmarked)

    if (sortBy === 'earning') list.sort((a, b) => (b.maxMonthlyEarning || 0) - (a.maxMonthlyEarning || 0))
    else if (sortBy === 'time') list.sort((a, b) => (a.minHoursPerWeek || 0) - (b.minHoursPerWeek || 0))
    else list.sort((a, b) => (b.matchScore || 0) - (a.matchScore || 0))
    return list
  }, [recs, fSkill, fHours, fEarn, fDiff, fMode, fBookmark, sortBy])

  const inputClass =
    'w-full bg-[#F4EBDD] border-2 border-transparent focus:border-[#171513] rounded-xl px-4 py-3 outline-none'
  const selectClass =
    'bg-white border-2 border-[#171513]/20 focus:border-[#171513] rounded-xl px-3 py-2 outline-none text-sm font-bold'

  return (
    <div className="min-h-screen w-full bg-[#F4EBDD] text-[#171513] p-4 md:p-8">
      <div className="max-w-6xl mx-auto">

        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <p className="text-xs font-bold uppercase tracking-wide opacity-60">Side-Hustle Recommendations</p>
            <h1 className="text-3xl font-black">Earn more, {user?.name ? user.name.split(' ')[0] : 'friend'} </h1>
          </div>
          <div className="flex gap-2">
            <button onClick={onBack} className="px-4 py-2 rounded-xl border-2 border-[#171513] font-bold text-sm hover:bg-[#171513] hover:text-white transition">
              ← Dashboard
            </button>
            <button onClick={onLogout} className="px-4 py-2 rounded-xl border-2 border-[#171513] font-bold text-sm hover:bg-[#E8893A] transition">
              Log out
            </button>
          </div>
        </div>

        {error && (
          <div className="mb-6 px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">{error}</div>
        )}

        {/* Profile editor */}
        <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513] mb-6">
          <h3 className="font-black text-lg mb-1">Your side-hustle profile</h3>
          <p className="text-sm opacity-60 mb-5">
            Tell us your skills, interests and free time — we'll match opportunities and refresh your recommendations.
          </p>
          <div className="grid md:grid-cols-2 gap-4">
            <div>
              <label className="text-xs font-bold uppercase tracking-wide opacity-60 block mb-2">Skills (comma separated)</label>
              <input value={skillsInput} onChange={(e) => setSkillsInput(e.target.value)} placeholder="Python, Web Development, Writing" className={inputClass} />
            </div>
            <div>
              <label className="text-xs font-bold uppercase tracking-wide opacity-60 block mb-2">Interests (comma separated)</label>
              <input value={interestsInput} onChange={(e) => setInterestsInput(e.target.value)} placeholder="Coding, Teaching, Design" className={inputClass} />
            </div>
            <div>
              <label className="text-xs font-bold uppercase tracking-wide opacity-60 block mb-2">Available hours / week</label>
              <input type="number" value={hours} onChange={(e) => setHours(e.target.value)} placeholder="e.g. 10" className={inputClass} />
            </div>
            <div>
              <label className="text-xs font-bold uppercase tracking-wide opacity-60 block mb-2">Extra income wanted (₹/month)</label>
              <input type="number" value={desired} onChange={(e) => setDesired(e.target.value)} placeholder="e.g. 10000" className={inputClass} />
            </div>
          </div>
          <button
            onClick={saveProfile}
            disabled={savingProfile}
            className="mt-5 bg-[#171513] text-white px-6 py-3 rounded-xl font-bold hover:bg-[#E8893A] hover:text-[#171513] transition disabled:opacity-40"
          >
            {savingProfile ? 'refreshing…' : 'Save & refresh recommendations'}
          </button>
        </div>

        {/* Filters */}
        <div className="flex flex-wrap items-end gap-3 mb-6">
          <div>
            <label className="text-[11px] font-bold uppercase tracking-wide opacity-60 block mb-1">Skill</label>
            <input value={fSkill} onChange={(e) => setFSkill(e.target.value)} placeholder="search skill" className={`${selectClass} w-40`} />
          </div>
          <div>
            <label className="text-[11px] font-bold uppercase tracking-wide opacity-60 block mb-1">Time available (hrs)</label>
            <input type="number" value={fHours} onChange={(e) => setFHours(e.target.value)} placeholder="max" className={`${selectClass} w-28`} />
          </div>
          <div>
            <label className="text-[11px] font-bold uppercase tracking-wide opacity-60 block mb-1">Min earning (₹/mo)</label>
            <input type="number" value={fEarn} onChange={(e) => setFEarn(e.target.value)} placeholder="e.g. 10000" className={`${selectClass} w-32`} />
          </div>
          <div>
            <label className="text-[11px] font-bold uppercase tracking-wide opacity-60 block mb-1">Difficulty</label>
            <select value={fDiff} onChange={(e) => setFDiff(e.target.value)} className={selectClass}>
              {DIFFICULTIES.map((d) => <option key={d} value={d}>{d === 'ALL' ? 'All' : titleCase(d)}</option>)}
            </select>
          </div>
          <div>
            <label className="text-[11px] font-bold uppercase tracking-wide opacity-60 block mb-1">Mode</label>
            <select value={fMode} onChange={(e) => setFMode(e.target.value)} className={selectClass}>
              <option value="ALL">All</option>
              <option value="online">Online</option>
              <option value="offline">Offline</option>
            </select>
          </div>
          <div>
            <label className="text-[11px] font-bold uppercase tracking-wide opacity-60 block mb-1">Sort by</label>
            <select value={sortBy} onChange={(e) => setSortBy(e.target.value)} className={selectClass}>
              <option value="match">Best match</option>
              <option value="earning">Earning potential</option>
              <option value="time">Least time</option>
            </select>
          </div>
          <label className="flex items-center gap-2 text-sm font-bold cursor-pointer pb-2">
            <input type="checkbox" checked={fBookmark} onChange={(e) => setFBookmark(e.target.checked)} />
            Saved only
          </label>
        </div>

        {/* Recommendations */}
        {loading ? (
          <p className="text-sm opacity-50">Finding opportunities for you…</p>
        ) : filtered.length === 0 ? (
          <p className="text-sm opacity-50">No opportunities match these filters. Try relaxing them or updating your profile.</p>
        ) : (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filtered.map((rec) => (
              <Card key={rec.id} rec={rec} onToggleBookmark={toggleBookmark} />
            ))}
          </div>
        )}

      </div>
    </div>
  )
}

export default SideHustle
