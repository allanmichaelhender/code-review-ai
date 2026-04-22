import { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { useAnalysisWebSocket } from "../hooks/useAnalysisWebSocket";

interface AnalysisResult {
  type: string;
  severity: string;
  filePath: string;
  lineNumber: number;
  message: string;
  suggestion: string;
}

export default function Demo() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const repoUrl = searchParams.get("repo") || "";
  const [useStreaming, setUseStreaming] = useState(true);
  const [fallbackResults, setFallbackResults] = useState<AnalysisResult[]>([]);
  const [fallbackLoading, setFallbackLoading] = useState(false);
  const [fallbackError, setFallbackError] = useState("");

  const {
    isConnected,
    status,
    progress,
    results: streamingResults,
    isComplete,
    error: streamingError,
    analysisId,
    totalIssues,
    healthScore,
    connect,
    disconnect,
    analyzeRepository: analyzeWithWebSocket,
    reset: resetStreaming,
  } = useAnalysisWebSocket();

  useEffect(() => {
    if (repoUrl && useStreaming) {
      connect();
      return () => {
        disconnect();
      };
    }
  }, [repoUrl, useStreaming, connect, disconnect]);

  useEffect(() => {
    if (repoUrl && useStreaming && isConnected) {
      resetStreaming();
      analyzeWithWebSocket(repoUrl, "gemini");
    }
  }, [
    repoUrl,
    useStreaming,
    isConnected,
    resetStreaming,
    analyzeWithWebSocket,
  ]);

  const analyzeRepoFallback = async () => {
    setFallbackLoading(true);
    setFallbackError("");
    try {
      const existingResponse = await fetch(
        `/api/analysis/by-repo?repo=${encodeURIComponent(repoUrl)}`,
      );
      if (existingResponse.ok) {
        const data = await existingResponse.json();
        setFallbackResults(data.results || []);
        return;
      }

      const response = await fetch(
        `/api/analyze?repo=${encodeURIComponent(repoUrl)}`,
        { method: "POST" },
      );
      if (!response.ok) {
        throw new Error("Analysis failed");
      }
      const data = await response.json();
      setFallbackResults(data.results || []);
    } catch (err) {
      setFallbackError("Failed to analyze repository. Please try again.");
    } finally {
      setFallbackLoading(false);
    }
  };

  useEffect(() => {
    if (repoUrl && !useStreaming) {
      analyzeRepoFallback();
    }
  }, [repoUrl, useStreaming]);

  const loading = useStreaming
    ? !isComplete && !streamingError
    : fallbackLoading;
  const error = useStreaming ? streamingError : fallbackError;
  const results = useStreaming ? streamingResults : fallbackResults;

  return (
    <div style={{ padding: "2rem", maxWidth: "1200px", margin: "0 auto" }}>
      <button
        onClick={() => navigate("/")}
        style={{
          padding: "0.5rem 1rem",
          backgroundColor: "#6c757d",
          color: "white",
          border: "none",
          borderRadius: "4px",
          cursor: "pointer",
          marginBottom: "1rem",
        }}
      >
        Back to Home
      </button>

      <div style={{ marginBottom: "1rem" }}>
        <label style={{ marginRight: "0.5rem" }}>
          <input
            type="checkbox"
            checked={useStreaming}
            onChange={(e) => {
              setUseStreaming(e.target.checked);
              if (e.target.checked) {
                setFallbackResults([]);
              } else {
                resetStreaming();
              }
            }}
            style={{ marginRight: "0.25rem" }}
          />
          Enable real-time streaming
        </label>
      </div>

      <h1>Analysis Results</h1>
      <p style={{ marginBottom: "1rem" }}>Repository: {repoUrl}</p>

      {useStreaming && status && (
        <div
          style={{
            marginBottom: "1rem",
            padding: "1rem",
            backgroundColor: "#f0f0f0",
            borderRadius: "4px",
          }}
        >
          <p style={{ marginBottom: "0.5rem", fontWeight: "bold" }}>
            Status: {status}
          </p>
          {progress.total > 0 && (
            <p>
              Progress: {progress.current} / {progress.total} files
            </p>
          )}
        </div>
      )}

      {loading && <p>Analyzing repository...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      {!loading && !error && results.length === 0 && (
        <p>No issues found in this repository.</p>
      )}

      {useStreaming && isComplete && healthScore > 0 && (
        <div
          style={{
            marginBottom: "1rem",
            padding: "1rem",
            backgroundColor: "#e8f5e9",
            borderRadius: "4px",
          }}
        >
          <p>
            <strong>Analysis Complete!</strong>
          </p>
          <p>Total Issues: {totalIssues}</p>
          <p>Health Score: {(healthScore * 100).toFixed(0)}%</p>
        </div>
      )}

      {!loading && results.length > 0 && (
        <div>
          <h2>Found {results.length} issues</h2>
          <table
            style={{
              width: "100%",
              borderCollapse: "collapse",
              marginTop: "1rem",
            }}
          >
            <thead>
              <tr style={{ borderBottom: "2px solid #ddd" }}>
                <th style={{ padding: "0.5rem", textAlign: "left" }}>
                  Severity
                </th>
                <th style={{ padding: "0.5rem", textAlign: "left" }}>Type</th>
                <th style={{ padding: "0.5rem", textAlign: "left" }}>File</th>
                <th style={{ padding: "0.5rem", textAlign: "left" }}>Line</th>
                <th style={{ padding: "0.5rem", textAlign: "left" }}>
                  Message
                </th>
              </tr>
            </thead>
            <tbody>
              {results.map((result: AnalysisResult, index: number) => (
                <tr key={index} style={{ borderBottom: "1px solid #ddd" }}>
                  <td style={{ padding: "0.5rem" }}>{result.severity}</td>
                  <td style={{ padding: "0.5rem" }}>{result.type}</td>
                  <td style={{ padding: "0.5rem" }}>{result.filePath}</td>
                  <td style={{ padding: "0.5rem" }}>{result.lineNumber}</td>
                  <td style={{ padding: "0.5rem" }}>{result.message}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
