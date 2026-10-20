import { useEffect, useState } from 'react'
import { authFetch, inr } from '../api'
import NavBar from './NavBar'

const TABS = ['overview', 'users', 'coupons', 'opportunities']
const input = 'w-full bg-[#F4EBDD] border-2 border-transparent focus:border-[#171513] rounded-xl px-3 py-2 outline-none text-sm'

function AdminDashboard({ user, onNavigate, onLogout }) {
  const [tab, setTab] = useState('overview')
  const [error, setError] = useState('')

  return (
    <div className="min-h-screen w-full bg-[#F4EBDD] text-[#171513] p-4 md:p-8">
      <div className="max-w-6xl mx-auto">
        <NavBar user={user} active="admin" onNavigate={onNavigate} onLogout={onLogout} />
        <h1 className="text-3xl font-black mb-4">Admin Dashboard 🛠️</h1>

        <div className="flex gap-2 mb-6 flex-wrap">
          {TABS.map((t) => (
            <button key={t} onClick={() => setTab(t)}
              className={`px-4 py-2 rounded-full border-2 font-bold text-sm capitalize transition ${
                tab === t ? 'bg-[#171513] text-white border-[#171513]' : 'bg-white border-[#171513]/20 hover:border-[#171513]'}`}>
              {t}
            </button>
          ))}
        </div>

        {error && <div className="mb-6 px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">{error}</div>}

        {tab === 'overview' && <Overview setError={setError} />}
        {tab === 'users' && <Users setError={setError} />}
        {tab === 'coupons' && <Coupons setError={setError} />}
        {tab === 'opportunities' && <Opportunities setError={setError} />}
      </div>
    </div>
  )
}

function Overview({ setError }) {
  const [a, setA] = useState(null)
  useEffect(() => {
    authFetch('/api/admin/analytics').then(setA).catch((e) => setError(e.message))
    // eslint-disable-next-line
  }, [])
  if (!a) return <p className="text-sm opacity-50">Loading analytics…</p>
  const tiles = [
    ['Users', a.userCount], ['Admins', a.adminCount], ['Plans', a.planCount],
    ['Savings records', a.savingsRecordCount], ['Goals achieved', a.goalsAchieved],
    ['Badges awarded', a.achievementsAwarded], ['Coupons', a.couponCount],
    ['Active coupons', a.activeCouponCount], ['Redemptions', a.couponRedemptions],
  ]
  return (
    <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
      {tiles.map(([l, v]) => (
        <div key={l} className="bg-white border-2 border-[#171513] rounded-2xl p-5 shadow-[4px_4px_0px_#171513]">
          <p className="text-[11px] font-bold uppercase tracking-wide opacity-60">{l}</p>
          <p className="text-3xl font-black mt-1">{v}</p>
        </div>
      ))}
    </div>
  )
}

function Users({ setError }) {
  const [users, setUsers] = useState([])
  const load = () => authFetch('/api/admin/users').then(setUsers).catch((e) => setError(e.message))
  useEffect(() => { load(); /* eslint-disable-next-line */ }, [])
  async function setRole(id, role) {
    try { await authFetch(`/api/admin/users/${id}/role`, { method: 'PUT', body: JSON.stringify({ role }) }); load() }
    catch (e) { setError(e.message) }
  }
  return (
    <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513] overflow-x-auto">
      <table className="w-full text-sm">
        <thead><tr className="text-left border-b-2 border-[#171513]/10">
          <th className="py-2 pr-4 text-xs uppercase opacity-60">Name</th>
          <th className="py-2 pr-4 text-xs uppercase opacity-60">Email</th>
          <th className="py-2 pr-4 text-xs uppercase opacity-60">Role</th>
          <th className="py-2 pr-4 text-xs uppercase opacity-60">Joined</th>
          <th className="py-2 pr-4 text-xs uppercase opacity-60">Action</th>
        </tr></thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id} className="border-b border-[#171513]/5">
              <td className="py-3 pr-4 font-bold">{u.name}</td>
              <td className="py-3 pr-4">{u.email}</td>
              <td className="py-3 pr-4"><span className={`font-black ${u.role === 'ADMIN' ? 'text-[#E8893A]' : ''}`}>{u.role}</span></td>
              <td className="py-3 pr-4 opacity-60">{new Date(u.createdAt).toLocaleDateString()}</td>
              <td className="py-3 pr-4">
                {u.role === 'ADMIN'
                  ? <button onClick={() => setRole(u.id, 'USER')} className="text-xs font-bold underline">make user</button>
                  : <button onClick={() => setRole(u.id, 'ADMIN')} className="text-xs font-bold underline">make admin</button>}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function Coupons({ setError }) {
  const [coupons, setCoupons] = useState([])
  const [form, setForm] = useState({ title: '', description: '', rewardLabel: '', category: '', pointsRequired: 0, expiryDate: '', demoCode: '' })
  const load = () => authFetch('/api/admin/coupons').then(setCoupons).catch((e) => setError(e.message))
  useEffect(() => { load(); /* eslint-disable-next-line */ }, [])
  async function create(e) {
    e.preventDefault()
    try {
      await authFetch('/api/admin/coupons', { method: 'POST', body: JSON.stringify({
        ...form, pointsRequired: Number(form.pointsRequired), expiryDate: form.expiryDate || null,
      }) })
      setForm({ title: '', description: '', rewardLabel: '', category: '', pointsRequired: 0, expiryDate: '', demoCode: '' })
      load()
    } catch (err) { setError(err.message) }
  }
  async function toggle(c) {
    try { await authFetch(`/api/admin/coupons/${c.id}/active?active=${!c.active}`, { method: 'PUT' }); load() }
    catch (err) { setError(err.message) }
  }
  return (
    <div className="space-y-6">
      <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513]">
        <h3 className="font-black mb-3">Create demo coupon</h3>
        <form onSubmit={create} className="grid md:grid-cols-3 gap-3">
          <input className={input} required placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
          <input className={input} required placeholder="Reward label (e.g. ₹100 off)" value={form.rewardLabel} onChange={(e) => setForm({ ...form, rewardLabel: e.target.value })} />
          <input className={input} placeholder="Category" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} />
          <input className={`${input} md:col-span-2`} required placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <input className={input} type="number" placeholder="Points required" value={form.pointsRequired} onChange={(e) => setForm({ ...form, pointsRequired: e.target.value })} />
          <input className={input} type="date" value={form.expiryDate} onChange={(e) => setForm({ ...form, expiryDate: e.target.value })} />
          <input className={input} placeholder="Demo code (optional)" value={form.demoCode} onChange={(e) => setForm({ ...form, demoCode: e.target.value })} />
          <button className="bg-[#171513] text-white py-2 rounded-xl font-bold hover:bg-[#E8893A] hover:text-[#171513] transition">Create</button>
        </form>
      </div>
      <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513] overflow-x-auto">
        <table className="w-full text-sm">
          <thead><tr className="text-left border-b-2 border-[#171513]/10">
            <th className="py-2 pr-4 text-xs uppercase opacity-60">Title</th>
            <th className="py-2 pr-4 text-xs uppercase opacity-60">Reward</th>
            <th className="py-2 pr-4 text-xs uppercase opacity-60">Points</th>
            <th className="py-2 pr-4 text-xs uppercase opacity-60">Active</th>
            <th className="py-2 pr-4 text-xs uppercase opacity-60">Action</th>
          </tr></thead>
          <tbody>
            {coupons.map((c) => (
              <tr key={c.id} className="border-b border-[#171513]/5">
                <td className="py-3 pr-4 font-bold">{c.title}</td>
                <td className="py-3 pr-4">{c.rewardLabel}</td>
                <td className="py-3 pr-4">{c.pointsRequired}</td>
                <td className="py-3 pr-4">{c.active ? '✅' : '—'}</td>
                <td className="py-3 pr-4"><button onClick={() => toggle(c)} className="text-xs font-bold underline">{c.active ? 'deactivate' : 'activate'}</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

function Opportunities({ setError }) {
  const [list, setList] = useState([])
  const [form, setForm] = useState({ name: '', description: '', requiredSkills: '', interestTags: '', minMonthlyEarning: '', maxMonthlyEarning: '', minHoursPerWeek: '', maxHoursPerWeek: '', difficulty: 'BEGINNER', online: true, platformName: '', platformUrl: '' })
  const load = () => authFetch('/api/admin/opportunities').then(setList).catch((e) => setError(e.message))
  useEffect(() => { load(); /* eslint-disable-next-line */ }, [])
  async function create(e) {
    e.preventDefault()
    try {
      await authFetch('/api/admin/opportunities', { method: 'POST', body: JSON.stringify({
        ...form,
        minMonthlyEarning: num(form.minMonthlyEarning), maxMonthlyEarning: num(form.maxMonthlyEarning),
        minHoursPerWeek: num(form.minHoursPerWeek), maxHoursPerWeek: num(form.maxHoursPerWeek),
      }) })
      setForm({ ...form, name: '', description: '', requiredSkills: '', interestTags: '', minMonthlyEarning: '', maxMonthlyEarning: '', minHoursPerWeek: '', maxHoursPerWeek: '', platformName: '', platformUrl: '' })
      load()
    } catch (err) { setError(err.message) }
  }
  async function del(id) {
    try { await authFetch(`/api/admin/opportunities/${id}`, { method: 'DELETE' }); load() } catch (err) { setError(err.message) }
  }
  return (
    <div className="space-y-6">
      <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513]">
        <h3 className="font-black mb-3">Add side-hustle opportunity</h3>
        <form onSubmit={create} className="grid md:grid-cols-3 gap-3">
          <input className={input} required placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          <input className={input} placeholder="Required skills (comma)" value={form.requiredSkills} onChange={(e) => setForm({ ...form, requiredSkills: e.target.value })} />
          <input className={input} placeholder="Interest tags (comma)" value={form.interestTags} onChange={(e) => setForm({ ...form, interestTags: e.target.value })} />
          <input className={`${input} md:col-span-3`} required placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <input className={input} type="number" placeholder="Min ₹/mo" value={form.minMonthlyEarning} onChange={(e) => setForm({ ...form, minMonthlyEarning: e.target.value })} />
          <input className={input} type="number" placeholder="Max ₹/mo" value={form.maxMonthlyEarning} onChange={(e) => setForm({ ...form, maxMonthlyEarning: e.target.value })} />
          <select className={input} value={form.difficulty} onChange={(e) => setForm({ ...form, difficulty: e.target.value })}>
            <option>BEGINNER</option><option>INTERMEDIATE</option><option>ADVANCED</option>
          </select>
          <input className={input} type="number" placeholder="Min hrs/wk" value={form.minHoursPerWeek} onChange={(e) => setForm({ ...form, minHoursPerWeek: e.target.value })} />
          <input className={input} type="number" placeholder="Max hrs/wk" value={form.maxHoursPerWeek} onChange={(e) => setForm({ ...form, maxHoursPerWeek: e.target.value })} />
          <label className="flex items-center gap-2 text-sm font-bold"><input type="checkbox" checked={form.online} onChange={(e) => setForm({ ...form, online: e.target.checked })} /> Online</label>
          <input className={input} placeholder="Platform name" value={form.platformName} onChange={(e) => setForm({ ...form, platformName: e.target.value })} />
          <input className={input} placeholder="Platform URL" value={form.platformUrl} onChange={(e) => setForm({ ...form, platformUrl: e.target.value })} />
          <button className="bg-[#171513] text-white py-2 rounded-xl font-bold hover:bg-[#E8893A] hover:text-[#171513] transition">Add</button>
        </form>
      </div>
      <div className="bg-white border-2 border-[#171513] rounded-[24px] p-6 shadow-[6px_6px_0px_#171513] overflow-x-auto">
        <table className="w-full text-sm">
          <thead><tr className="text-left border-b-2 border-[#171513]/10">
            <th className="py-2 pr-4 text-xs uppercase opacity-60">Name</th>
            <th className="py-2 pr-4 text-xs uppercase opacity-60">Skills</th>
            <th className="py-2 pr-4 text-xs uppercase opacity-60">₹/mo</th>
            <th className="py-2 pr-4 text-xs uppercase opacity-60">Action</th>
          </tr></thead>
          <tbody>
            {list.map((o) => (
              <tr key={o.id} className="border-b border-[#171513]/5">
                <td className="py-3 pr-4 font-bold">{o.name}</td>
                <td className="py-3 pr-4 opacity-70">{o.requiredSkills}</td>
                <td className="py-3 pr-4">{inr(o.minMonthlyEarning)}–{inr(o.maxMonthlyEarning)}</td>
                <td className="py-3 pr-4"><button onClick={() => del(o.id)} className="text-xs font-bold text-red-600 underline">delete</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

const num = (v) => (v === '' || v == null ? null : Number(v))

export default AdminDashboard
