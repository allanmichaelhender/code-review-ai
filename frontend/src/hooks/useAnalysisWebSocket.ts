import { useState, useEffect, useCallback, useRef } from 'react'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

interface AnalysisResult {
  type: string
  severity: string
  filePath: string
  lineNumber: number
  message: string
  suggestion: string
}

interface WebSocketMessage {
  type: 'status' | 'progress' | 'result' | 'complete' | 'error'
  message?: string
  current?: number
  total?: number
  result?: AnalysisResult
  analysisId?: number
  totalIssues?: number
  healthScore?: number
}

export function useAnalysisWebSocket() {
  const [isConnected, setIsConnected] = useState(false)
  const [status, setStatus] = useState('')
  const [progress, setProgress] = useState({ current: 0, total: 0 })
  const [results, setResults] = useState<AnalysisResult[]>([])
  const [isComplete, setIsComplete] = useState(false)
  const [error, setError] = useState('')
  const [analysisId, setAnalysisId] = useState<number | null>(null)
  const [totalIssues, setTotalIssues] = useState(0)
  const [healthScore, setHealthScore] = useState(0)
  
  const stompClientRef = useRef<Client | null>(null)
  const sessionIdRef = useRef<string>('')

  const connect = useCallback(() => {
    const sessionId = 'session-' + Date.now()
    sessionIdRef.current = sessionId

    const socket = new SockJS('http://localhost:8080/ws-analysis')
    const stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })

    stompClient.onConnect = () => {
      setIsConnected(true)
      console.log('WebSocket connected')

      stompClient.subscribe(`/topic/analysis/${sessionId}`, (message) => {
        const data: WebSocketMessage = JSON.parse(message.body)
        
        switch (data.type) {
          case 'status':
            setStatus(data.message || '')
            break
          case 'progress':
            setStatus(data.message || '')
            if (data.current !== undefined && data.total !== undefined) {
              setProgress({ current: data.current, total: data.total })
            }
            break
          case 'result':
            if (data.result) {
              setResults(prev => [...prev, data.result!])
            }
            break
          case 'complete':
            setIsComplete(true)
            setStatus('Analysis complete')
            if (data.analysisId) setAnalysisId(data.analysisId)
            if (data.totalIssues) setTotalIssues(data.totalIssues)
            if (data.healthScore) setHealthScore(data.healthScore)
            break
          case 'error':
            setError(data.message || 'An error occurred')
            setIsComplete(true)
            break
        }
      })
    }

    stompClient.onStompError = (frame) => {
      console.error('STOMP error:', frame)
      setError('Connection error')
    }

    stompClient.activate()
    stompClientRef.current = stompClient
  }, [])

  const disconnect = useCallback(() => {
    if (stompClientRef.current) {
      stompClientRef.current.deactivate()
      stompClientRef.current = null
      setIsConnected(false)
    }
  }, [])

  const analyzeRepository = useCallback((repoUrl: string, provider: string = 'gemini') => {
    if (!stompClientRef.current || !isConnected) {
      console.error('WebSocket not connected')
      return
    }

    stompClientRef.current.publish({
      destination: '/app/analyze',
      body: JSON.stringify({
        repo: repoUrl,
        provider: provider,
        sessionId: sessionIdRef.current
      })
    })
  }, [isConnected])

  const reset = useCallback(() => {
    setResults([])
    setStatus('')
    setProgress({ current: 0, total: 0 })
    setIsComplete(false)
    setError('')
    setAnalysisId(null)
    setTotalIssues(0)
    setHealthScore(0)
  }, [])

  useEffect(() => {
    return () => {
      disconnect()
    }
  }, [disconnect])

  return {
    isConnected,
    status,
    progress,
    results,
    isComplete,
    error,
    analysisId,
    totalIssues,
    healthScore,
    connect,
    disconnect,
    analyzeRepository,
    reset
  }
}
