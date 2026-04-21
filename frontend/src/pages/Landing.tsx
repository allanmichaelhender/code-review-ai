import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

export default function Landing() {
  const [repoUrl, setRepoUrl] = useState('')
  const navigate = useNavigate()

  const handleAnalyze = () => {
    if (repoUrl) {
      navigate(`/demo?repo=${encodeURIComponent(repoUrl)}`)
    }
  }

  return (
    <div style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto' }}>
      <h1>AI Code Review Platform</h1>
      <p style={{ marginTop: '1rem', marginBottom: '2rem' }}>
        Analyze your GitHub repositories with AI-powered code review
      </p>
      
      <div style={{ marginBottom: '2rem' }}>
        <input
          type="text"
          placeholder="Enter GitHub repository URL (e.g., https://github.com/owner/repo)"
          value={repoUrl}
          onChange={(e) => setRepoUrl(e.target.value)}
          style={{
            width: '100%',
            padding: '0.75rem',
            fontSize: '1rem',
            border: '1px solid #ccc',
            borderRadius: '4px',
            marginBottom: '1rem',
          }}
        />
        <button
          onClick={handleAnalyze}
          disabled={!repoUrl}
          style={{
            padding: '0.75rem 2rem',
            fontSize: '1rem',
            backgroundColor: repoUrl ? '#007bff' : '#ccc',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: repoUrl ? 'pointer' : 'not-allowed',
          }}
        >
          Analyze Repository
        </button>
      </div>

      <div>
        <h2>Explore Pre-Analyzed Repositories</h2>
        <button
          onClick={() => navigate('/explore')}
          style={{
            marginTop: '1rem',
            padding: '0.5rem 1rem',
            backgroundColor: '#6c757d',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
        >
          View Demo Repositories
        </button>
      </div>
    </div>
  )
}
