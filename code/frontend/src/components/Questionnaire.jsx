import { useState } from 'react'
import { authFetch, inr } from '../api'

function Chip({ active, onClick, children }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`px-4 py-2 rounded-full border-2 text-sm font-bold transition ${
        active
          ? 'bg-[#171513] text-white border-[#171513]'
          : 'bg-white text-[#171513] border-[#171513]/20 hover:border-[#171513]'
      }`}
    >
      {children}
    </button>
  )
}

function Field({ label, children }) {
  return (
    <div className="mb-8">
      <label className="text-xs font-bold uppercase tracking-wide opacity-60 block mb-2">{label}</label>
      {children}
    </div>
  )
}

function Questionnaire({ onDone, existingPlan }) {
  const [data, setData] = useState({
    income: existingPlan?.monthlyIncome ?? '',
    monthlySpend: existingPlan?.monthlySpend ?? '',
    investments: existingPlan?.currentInvestments ?? '',
    age: existingPlan?.ageBand ?? '',
    goalAmount: existingPlan?.goalAmount ?? '',
    timelineYears: existingPlan?.timelineYears ?? '',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const set = (key, value) => setData((d) => ({ ...d, [key]: value }))

  const allFilled = data.income && data.monthlySpend && data.investments && data.age && data.goalAmount && data.timelineYears

  const inputClass = "w-full bg-[#F4EBDD] border-2 border-transparent focus:border-[#171513] rounded-xl px-4 py-3 outline-none text-lg"

  const years = parseFloat(data.timelineYears) || 0
  const months = Math.max(Math.round(years * 12), 1)
  const monthly = data.goalAmount ? Math.ceil(Number(data.goalAmount) / months) : 0

  async function buildPlan() {
    setError('')
    setLoading(true)
    try {
      const plan = await authFetch('/api/plan', {
        method: 'POST',
        body: JSON.stringify({
          monthlyIncome: Number(data.income),
          monthlySpend: Number(data.monthlySpend),
          currentInvestments: Number(data.investments),
          ageBand: data.age,
          goalAmount: Number(data.goalAmount),
          timelineYears: Number(data.timelineYears),
        }),
      })
      onDone(plan)
    } catch (e) {
      setError(e.message || 'Could not save your plan. Is the backend running?')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="h-screen w-screen bg-[#F4EBDD] text-[#171513] p-4 flex flex-col">
      <div className="w-full h-full flex flex-col">
        <div className="flex-1 bg-white border-2 border-[#171513] rounded-[24px] p-8 shadow-[6px_6px_0px_#171513] overflow-y-auto">
          <div className="max-w-2xl mx-auto w-full">

            <h2 className="text-3xl font-black mb-2">Let's figure out your money situation.</h2>
            <p className="text-sm opacity-70 mb-8">Takes a couple minutes. Be as accurate as you can. No right or wrong answers here!</p>

            {error && (
              <div className="mb-6 px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                {error}
              </div>
            )}

            <Field label="Monthly salary">
              <input type="number" placeholder="₹" value={data.income} onChange={(e) => set('income', e.target.value)} className={inputClass} />
            </Field>

            <Field label="Monthly spending (tax, loan, occasional expenses — all combined)">
              <input type="number" placeholder="₹" value={data.monthlySpend} onChange={(e) => set('monthlySpend', e.target.value)} className={inputClass} />
            </Field>

            <Field label="Current investments">
              <input type="number" placeholder="₹" value={data.investments} onChange={(e) => set('investments', e.target.value)} className={inputClass} />
            </Field>

            <Field label="Age">
              <div className="flex flex-wrap gap-2">
                {['Under 18', '18–25', '26–35', '35+'].map((opt) => (
                  <Chip key={opt} active={data.age === opt} onClick={() => set('age', opt)}>{opt}</Chip>
                ))}
              </div>
            </Field>

            <Field label="Goal amount">
              <input type="number" placeholder="₹" value={data.goalAmount} onChange={(e) => set('goalAmount', e.target.value)} className={inputClass} />
            </Field>

            <Field label="Time to reach it (in years, decimals okay — e.g. 0.5 for 6 months)">
              <input type="number" step="0.1" placeholder="e.g. 1.5" value={data.timelineYears} onChange={(e) => set('timelineYears', e.target.value)} className={inputClass} />
            </Field>

            {allFilled && (
              <div className="bg-[#171513] text-white rounded-2xl p-6 mb-4">
                <p className="text-xs opacity-60">YOUR GOAL</p>
                <p className="text-2xl font-bold mb-4">{inr(data.goalAmount)}</p>
                <p className="text-xs opacity-60">TIMELINE</p>
                <p className="text-2xl font-bold mb-4">{years} year{years === 1 ? '' : 's'}</p>
                <p className="text-xs opacity-60">THAT'S ABOUT</p>
                <p className="text-3xl font-black text-[#E8893A]">{inr(monthly)}/month</p>
              </div>
            )}

            <button
              onClick={buildPlan}
              disabled={!allFilled || loading}
              className="w-full bg-[#171513] text-white py-4 rounded-xl font-bold hover:bg-[#E8893A] hover:text-[#171513] transition disabled:opacity-30 mt-2"
            >
              {loading ? 'saving your plan...' : (existingPlan ? 'Update my plan →' : 'Build my plan →')}
            </button>

          </div>
        </div>
      </div>
    </div>
  )
}

export default Questionnaire
