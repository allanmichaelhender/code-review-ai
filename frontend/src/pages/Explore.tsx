import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

interface Repository {
  id: number
  owner: string
  name: string
  url: string
  description: string
  language: string
  overallHealthScore: number
}

export default function Explore() {
  const navigate = useNavigate()
  const [repos, setRepos] = useState<Repository[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchRepos()
  }, [])

  const fetchRepos = async () => {
    try {
      const response = await fetch('/api/repositories')
      if (!response.ok) {
        throw new Error('Failed to fetch repositories')
      }
      const data = await response.json()
      setRepos(data)
    } catch (err) {
      console.error('Error fetching repositories:', err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
      <button
        onClick={() => navigate('/')}
        style={{
          padding: '0.5rem 1rem',
          backgroundColor: '#6c757d',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
          marginBottom: '1rem',
        }}
      >
        Back to Home
      </button>

      <h1>Pre-Analyzed Repositories</h1>
      <p style={{ marginBottom: '2rem' }}>
        Explore repositories that have been analyzed with AI-powered code review
      </p>

      {loading && <p>Loading repositories...</p>}

      {!loading && repos.length === 0 && (
        <p>No pre-analyzed repositories available.</p>
      )}

      {!loading && repos.length > 0 && (
        <div style={{ display: 'grid', gap: '1.5rem' }}>
          {repos.map((repo) => (
            <div
              key={repo.id}
              style={{
                border: '1px solid #ddd',
                borderRadius: '8px',
                padding: '1.5rem',
                backgroundColor: '#f9f9f9',
              }}
            >
              <h2 style={{ marginBottom: '0.5rem' }}>
                {repo.owner}/{repo.name}
              </h2>
              <p style={{ marginBottom: '0.5rem', color: '#666' }}>
                {repo.description || 'No description'}
              </p>
              <p style={{ marginBottom: '0.5rem' }}>
                <strong>Language:</strong> {repo.language || 'N/A'}
              </p>
              <p style={{ marginBottom: '1rem' }}>
                <strong>Health Score:</strong>{' '}
                {repo.overallHealthScore
                  ? `${(repo.overallHealthScore * 100).toFixed(0)}%`
                  : 'N/A'}
              </p>
              <button
                onClick={() => navigate(`/demo?repo=${encodeURIComponent(repo.url)}`)}
                style={{
                  padding: '0.5rem 1rem',
                  backgroundColor: '#007bff',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                View Analysis
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
