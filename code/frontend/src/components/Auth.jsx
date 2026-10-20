import { useState } from 'react'

const API = 'http://localhost:8080'

function Auth({ onAuth, notice }) {
  const [activeTab, setActiveTab] = useState('login')

  // Login state
  const [loginEmail, setLoginEmail] = useState('')
  const [loginPassword, setLoginPassword] = useState('')

  // Signup state
  const [signupName, setSignupName] = useState('')
  const [signupEmail, setSignupEmail] = useState('')
  const [signupPassword, setSignupPassword] = useState('')

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  async function handleLogin(e) {
    e.preventDefault()
    setError('')
    setSuccess('')
    setLoading(true)
    try {
      const res = await fetch(`${API}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: loginEmail, password: loginPassword }),
      })
      const data = await res.json()
      if (!res.ok) {
        setError(data.message || 'Login failed. Check your credentials.')
      } else {
        localStorage.setItem('token', data.token)
        onAuth(data)
      }
    } catch {
      setError('Could not reach the server. Is the backend running on port 8080?')
    } finally {
      setLoading(false)
    }
  }

  async function handleSignup(e) {
    e.preventDefault()
    setError('')
    setSuccess('')
    setLoading(true)
    try {
      const res = await fetch(`${API}/api/auth/signup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: signupName, email: signupEmail, password: signupPassword }),
      })
      const data = await res.json()
      if (!res.ok) {
        setError(data.message || 'Signup failed. Try again.')
      } else {
        setSuccess('Account created! Please log in.')
        setSignupName('')
        setSignupEmail('')
        setSignupPassword('')
        setActiveTab('login')
      }
    } catch {
      setError('Could not reach the server. Is the backend running on port 8080?')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-[#F4EBDD] text-[#171513] p-6 flex items-center justify-center">

      {/* Main layout */}
      <div className="w-full max-w-6xl grid md:grid-cols-2 gap-8 items-center">

        {/* LEFT — personality / visual */}
        <div className="hidden md:block">

          <p className="text-sm font-medium mb-4 tracking-wide">
            PERSONAL FINANCE, WITHOUT THE HEADACHE.
          </p>

          <h1 className="text-6xl font-black leading-[0.9] tracking-tight max-w-lg">
            SPEND SMART.
            <br />
            HUSTLE HARD.
            <br />
            <span className="text-[#E8893A]">STAY ON TRACK.</span>
          </h1>

          <p className="mt-6 max-w-md text-base leading-relaxed opacity-70">
            Set a goal. Build a plan. Keep going.
            We'll help you figure out the rest.
          </p>

          {/* little visual */}
          <div className="mt-10 relative w-[360px] h-[210px]">

            {/* beige card */}
            <div className="absolute left-4 top-4 w-64 h-40
              bg-[#E8893A]
              border-2 border-[#171513]
              rounded-[24px]
              rotate-[-5deg]">
            </div>

            {/* green card */}
            <div className="absolute left-20 top-8 w-64 h-40
              bg-[#789B4A]
              border-2 border-[#171513]
              rounded-[24px]
              rotate-[5deg]">
            </div>

            {/* black card */}
            <div className="absolute left-12 top-16 w-64 h-40
              bg-[#171513]
              text-white
              border-2 border-[#171513]
              rounded-[24px]
              p-6">

              <p className="text-xs opacity-60">
                YOUR NEXT GOAL
              </p>

              <p className="text-3xl font-bold mt-3">
                ₹50,000
              </p>

              <div className="mt-5 h-2 bg-white/20 rounded-full">
                <div className="h-2 w-2/3 bg-[#E8893A] rounded-full"></div>
              </div>

              <p className="text-xs mt-2 opacity-60">
                you're getting there.
              </p>

            </div>
          </div>

        </div>


        {/* RIGHT — AUTH */}
        <div className="w-full max-w-md mx-auto">

          <div className="mb-8">

            <p className="text-sm font-medium opacity-60 mb-2">
              HELLO THERE 
            </p>

            <h2 className="text-4xl font-black tracking-tight">
              Let's get started.
            </h2>

            <p className="mt-2 text-sm opacity-60">
              Your goals are easier when you have a plan.
            </p>

          </div>


          {/* Auth box */}
          <div className="bg-white border-2 border-[#171513] rounded-[24px] p-7 shadow-[6px_6px_0px_#171513]">

            {/* tabs */}
            <div className="flex gap-7 border-b-2 border-[#171513]/10 mb-7">

              <button
                onClick={() => { setActiveTab('login'); setError(''); setSuccess('') }}
                className={`pb-3 font-bold text-lg relative ${
                  activeTab === 'login'
                    ? 'text-[#E8893A]'
                    : 'text-[#171513]/50'
                }`}
              >
                log in

                {activeTab === 'login' && (
                  <span className="absolute left-0 bottom-[-2px] w-full h-[3px] bg-[#E8893A]" />
                )}
              </button>


              <button
                onClick={() => { setActiveTab('signup'); setError(''); setSuccess('') }}
                className={`pb-3 font-bold text-lg relative ${
                  activeTab === 'signup'
                    ? 'text-[#E8893A]'
                    : 'text-[#171513]/50'
                }`}
              >
                sign up

                {activeTab === 'signup' && (
                  <span className="absolute left-0 bottom-[-2px] w-full h-[3px] bg-[#E8893A]" />
                )}
              </button>

            </div>

            {/* Session / error / success banners */}
            {notice && !error && !success && (
              <div className="mb-4 px-4 py-3 rounded-xl bg-amber-50 border border-amber-200 text-amber-800 text-sm">
                {notice}
              </div>
            )}
            {error && (
              <div className="mb-4 px-4 py-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                {error}
              </div>
            )}
            {success && (
              <div className="mb-4 px-4 py-3 rounded-xl bg-green-50 border border-green-200 text-green-700 text-sm">
                {success}
              </div>
            )}


            {/* LOGIN */}
            {activeTab === 'login' && (
              <form className="space-y-5" onSubmit={handleLogin}>

                <div>
                  <label className="text-xs font-bold uppercase tracking-wide">
                    email
                  </label>

                  <input
                    type="email"
                    placeholder="you@example.com"
                    value={loginEmail}
                    onChange={e => setLoginEmail(e.target.value)}
                    required
                    className="mt-2 w-full bg-[#F4EBDD] border-2 border-transparent
                    focus:border-[#171513]
                    rounded-xl px-4 py-3 outline-none transition"
                  />
                </div>


                <div>
                  <label className="text-xs font-bold uppercase tracking-wide">
                    password
                  </label>

                  <input
                    type="password"
                    placeholder="••••••••"
                    value={loginPassword}
                    onChange={e => setLoginPassword(e.target.value)}
                    required
                    className="mt-2 w-full bg-[#F4EBDD] border-2 border-transparent
                    focus:border-[#171513]
                    rounded-xl px-4 py-3 outline-none transition"
                  />
                </div>


                <button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-[#171513] text-white py-4
                  rounded-xl font-bold text-base
                  hover:bg-[#E8893A] hover:text-[#171513]
                  transition disabled:opacity-50"
                >
                  {loading ? 'checking...' : 'continue →'}
                </button>

              </form>
            )}


            {/* SIGN UP */}
            {activeTab === 'signup' && (
              <form className="space-y-5" onSubmit={handleSignup}>

                <div>
                  <label className="text-xs font-bold uppercase tracking-wide">
                    your name
                  </label>

                  <input
                    type="text"
                    placeholder="what should we call you?"
                    value={signupName}
                    onChange={e => setSignupName(e.target.value)}
                    required
                    className="mt-2 w-full bg-[#F4EBDD] border-2 border-transparent
                    focus:border-[#171513]
                    rounded-xl px-4 py-3 outline-none transition"
                  />
                </div>


                <div>
                  <label className="text-xs font-bold uppercase tracking-wide">
                    email
                  </label>

                  <input
                    type="email"
                    placeholder="you@example.com"
                    value={signupEmail}
                    onChange={e => setSignupEmail(e.target.value)}
                    required
                    className="mt-2 w-full bg-[#F4EBDD] border-2 border-transparent
                    focus:border-[#171513]
                    rounded-xl px-4 py-3 outline-none transition"
                  />
                </div>


                <div>
                  <label className="text-xs font-bold uppercase tracking-wide">
                    password
                  </label>

                  <input
                    type="password"
                    placeholder="create a password"
                    value={signupPassword}
                    onChange={e => setSignupPassword(e.target.value)}
                    required
                    minLength={6}
                    className="mt-2 w-full bg-[#F4EBDD] border-2 border-transparent
                    focus:border-[#171513]
                    rounded-xl px-4 py-3 outline-none transition"
                  />
                </div>


                <button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-[#171513] text-white py-4
                  rounded-xl font-bold text-base
                  hover:bg-[#789B4A] hover:text-[#171513]
                  transition disabled:opacity-50"
                >
                  {loading ? 'creating account...' : 'continue →'}
                </button>

              </form>
            )}

          </div>

        </div>

      </div>

    </div>
  )
}

export default Auth
