// //import Auth from './components/Auth'
// import Questionnaire from './components/Questionnaire'
// function App() {
//   //return <Auth />
//   return <Questionnaire />
// }

// export default App  
import { useState } from 'react'
import Auth from './components/Auth'
import Questionnaire from './components/Questionnaire'

function App() {
  // This is the master switch for your demo
  const [loggedIn, setLoggedIn] = useState(false)

  return (
    <>
      {loggedIn ? (
        <Questionnaire />
      ) : (
        <Auth onLogin={() => setLoggedIn(true)} />
      )}
    </>
  )
}

export default App