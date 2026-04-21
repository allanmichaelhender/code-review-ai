import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Landing from './pages/Landing'
import Demo from './pages/Demo'
import Explore from './pages/Explore'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/demo" element={<Demo />} />
        <Route path="/explore" element={<Explore />} />
      </Routes>
    </Router>
  )
}

export default App
