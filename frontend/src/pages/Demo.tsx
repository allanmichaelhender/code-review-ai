import { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";

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
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<AnalysisResult[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    if (repoUrl) {
      analyzeRepo();
    }
  }, [repoUrl]);

  const analyzeRepo = async () => {
    setLoading(true);
    setError("");
    try {
      // First try to get existing analysis
      const existingResponse = await fetch(
        `/api/analysis/by-repo?repo=${encodeURIComponent(repoUrl)}`,
      );
      if (existingResponse.ok) {
        const data = await existingResponse.json();
        setResults(data.results || []);
        return;
      }

      // If no existing analysis, run new analysis
      const response = await fetch(
        `/api/analyze?repo=${encodeURIComponent(repoUrl)}`,
        {
          method: "POST",
        },
      );
      if (!response.ok) {
        throw new Error("Analysis failed");
      }
      const data = await response.json();
      setResults(data.results || []);
    } catch (err) {
      setError("Failed to analyze repository. Please try again.");
    } finally {
      setLoading(false);
    }
  };

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

      <h1>Analysis Results</h1>
      <p style={{ marginBottom: "1rem" }}>Repository: {repoUrl}</p>

      {loading && <p>Analyzing repository...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      {!loading && !error && results.length === 0 && (
        <p>No issues found in this repository.</p>
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
              {results.map((result, index) => (
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
