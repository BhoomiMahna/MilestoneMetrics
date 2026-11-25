import { useEffect, useState } from 'react'
import Auth from './components/Auth'
import Questionnaire from './components/Questionnaire'
import FinancialProgress from './components/FinancialProgress'
import SideHustle from './components/SideHustle'
import Rewards from './components/Rewards'
import Coupons from './components/Coupons'
import Notifications from './components/Notifications'
import Investments from './components/Investments'
import AdminDashboard from './components/AdminDashboard'
import { authFetch, setAuthErrorHandler } from './api'

function App() {
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('user'))
    } catch {
      return null
    }
  })
  const [plan, setPlan] = useState(null)
  const [view, setView] = useState('loading') // loading | questionnaire | dashboard | sidehustle
  const [notice, setNotice] = useState('')

  // When any API call reports the session is invalid/expired (401/403), drop
  // back to the login screen with an explanation instead of failing silently.
  useEffect(() => {
    setAuthErrorHandler(() => {
      setUser(null)
      setPlan(null)
      setView('loading')
      setNotice('Your session expired. Please log in again.')
    })
  }, [])

  // After auth (or on reload with a saved session), decide where to land:
  // a saved plan -> dashboard, otherwise -> questionnaire.
  useEffect(() => {
    if (!user) return
    let active = true
    setView('loading')
    ;(async () => {
      try {
        const p = await authFetch('/api/plan')
        if (!active) return
        if (p) {
          setPlan(p)
          setView('dashboard')
        } else {
          setView('questionnaire')
        }
      } catch {
        if (active) setView('questionnaire')
      }
    })()
    return () => {
      active = false
    }
  }, [user])

  function handleAuth(data) {
    localStorage.setItem('user', JSON.stringify(data))
    setNotice('')
    setUser(data)
  }

  function handleLogout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
    setPlan(null)
    setView('loading')
  }

  function handlePlanDone(savedPlan) {
    setPlan(savedPlan)
    setView('dashboard')
  }

  if (!user) {
    return <Auth onAuth={handleAuth} notice={notice} />
  }

  if (view === 'loading') {
    return (
      <div className="min-h-screen w-full bg-[#F4EBDD] text-[#171513] flex items-center justify-center">
        <p className="text-lg font-bold opacity-60">Loading your plan…</p>
      </div>
    )
  }

  if (view === 'questionnaire') {
    return <Questionnaire onDone={handlePlanDone} existingPlan={plan} />
  }

  const nav = (v) => setView(v)
  const common = { user, onNavigate: nav, onLogout: handleLogout }

  if (view === 'sidehustle') {
    return <SideHustle user={user} onBack={() => setView('dashboard')} onNavigate={nav} onLogout={handleLogout} />
  }
  if (view === 'rewards') return <Rewards {...common} />
  if (view === 'coupons') return <Coupons {...common} />
  if (view === 'notifications') return <Notifications {...common} />
  if (view === 'investments') return <Investments {...common} />
  if (view === 'admin') return <AdminDashboard {...common} />

  return (
    <FinancialProgress
      plan={plan}
      user={user}
      onEditPlan={() => setView('questionnaire')}
      onOpenSideHustle={() => setView('sidehustle')}
      onNavigate={nav}
      onLogout={handleLogout}
    />
  )
}

export default App
