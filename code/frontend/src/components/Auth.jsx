import { useState } from 'react'

function Auth() {
  const [activeTab, setActiveTab] = useState('login')

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
              HELLO THERE 👋
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
                onClick={() => setActiveTab('login')}
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
                onClick={() => setActiveTab('signup')}
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


            {/* LOGIN */}
            {activeTab === 'login' && (
              <form className="space-y-5">

                <div>
                  <label className="text-xs font-bold uppercase tracking-wide">
                    email
                  </label>

                  <input
                    type="email"
                    placeholder="you@example.com"
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
                    className="mt-2 w-full bg-[#F4EBDD] border-2 border-transparent
                    focus:border-[#171513]
                    rounded-xl px-4 py-3 outline-none transition"
                  />
                </div>


                <button
                  type="submit"
                  className="w-full bg-[#171513] text-white py-4
                  rounded-xl font-bold text-base
                  hover:bg-[#E8893A] hover:text-[#171513]
                  transition"
                >
                  continue →
                </button>

              </form>
            )}


            {/* SIGN UP */}
            {activeTab === 'signup' && (
              <form className="space-y-5">

                <div>
                  <label className="text-xs font-bold uppercase tracking-wide">
                    your name
                  </label>

                  <input
                    type="text"
                    placeholder="what should we call you?"
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
                    className="mt-2 w-full bg-[#F4EBDD] border-2 border-transparent
                    focus:border-[#171513]
                    rounded-xl px-4 py-3 outline-none transition"
                  />
                </div>


                <button
                  type="submit"
                  className="w-full bg-[#171513] text-white py-4
                  rounded-xl font-bold text-base
                  hover:bg-[#789B4A] hover:text-[#171513]
                  transition"
                >
                  continue →
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