import { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  AlertCircle,
  RefreshCw,
  AlertTriangle,
  Info,
  CheckCircle,
} from "lucide-react";

interface AnalysisResult {
  category: string;
  type: string;
  severity: string;
  filePath: string;
  lineNumber: number;
  message: string;
  suggestion: string;
  explanation?: string;
  confidenceScore?: number;
  impactScore?: number;
  effortScore?: number;
  cweId?: string;
  owaspCategory?: string;
}

const severityColors = {
  critical: "bg-red-500",
  high: "bg-orange-500",
  medium: "bg-yellow-500",
  low: "bg-blue-500",
  info: "bg-slate-500",
};

const severityIcons = {
  critical: AlertCircle,
  high: AlertTriangle,
  medium: AlertTriangle,
  low: Info,
  info: CheckCircle,
};

export default function Demo() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const repoUrl = searchParams.get("repo") || "";
  const [results, setResults] = useState<AnalysisResult[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const analyzeRepo = async () => {
    setLoading(true);
    setError("");
    try {
      const existingResponse = await fetch(
        `/api/analysis/by-repo?repo=${encodeURIComponent(repoUrl)}`,
      );
      if (existingResponse.ok) {
        const data = await existingResponse.json();
        setResults(data.results || []);
        setLoading(false);
        return;
      }

      // If no existing analysis, trigger a new one
      const response = await fetch(
        `/api/analyze?repo=${encodeURIComponent(repoUrl)}&provider=openrouter`,
        { method: "POST" },
      );
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || "Analysis failed");
      }
      const data = await response.json();
      setResults(data.results || []);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to analyze repository. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (repoUrl) {
      analyzeRepo();
    }
  }, [repoUrl]);

  if (!repoUrl) {
    return (
      <div className="min-h-screen bg-slate-900 flex items-center justify-center">
        <div className="text-center">
          <p className="text-slate-400 mb-4">No repository URL provided</p>
          <button
            onClick={() => navigate("/")}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            Go to Home
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-900">
      {/* Header */}
      <div className="bg-slate-800 border-b border-slate-700">
        <div className="container mx-auto px-4 py-4">
          <button
            onClick={() => navigate("/")}
            className="flex items-center gap-2 text-slate-300 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to Home
          </button>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        {/* Repository Info */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-white mb-2">
            Analysis Results
          </h1>
          <p className="text-slate-400 break-all">{repoUrl}</p>
        </div>

        {/* Loading State */}
        {loading && (
          <div className="flex flex-col items-center justify-center py-20">
            <RefreshCw className="w-12 h-12 text-blue-500 animate-spin mb-4" />
            <p className="text-slate-300 text-lg">Analyzing repository...</p>
            <p className="text-slate-500 text-sm mt-2">
              This may take a moment
            </p>
          </div>
        )}

        {/* Error State */}
        {error && !loading && (
          <div className="bg-red-500/10 border border-red-500/50 rounded-lg p-6 mb-6">
            <div className="flex items-start gap-3">
              <AlertCircle className="w-6 h-6 text-red-500 flex-shrink-0 mt-0.5" />
              <div className="flex-1">
                <h3 className="text-red-400 font-semibold mb-1">
                  Analysis Failed
                </h3>
                <p className="text-slate-300 mb-4">{error}</p>
                <button
                  onClick={analyzeRepo}
                  className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors flex items-center gap-2"
                >
                  <RefreshCw className="w-4 h-4" />
                  Retry Analysis
                </button>
              </div>
            </div>
          </div>
        )}

        {/* No Results State */}
        {!loading && !error && results.length === 0 && (
          <div className="bg-green-500/10 border border-green-500/50 rounded-lg p-8 text-center">
            <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
            <h3 className="text-green-400 font-semibold text-xl mb-2">
              No Issues Found
            </h3>
            <p className="text-slate-300">
              Great job! No issues were detected in this repository.
            </p>
          </div>
        )}

        {/* Results */}
        {!loading && !error && results.length > 0 && (
          <div>
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-white">
                Found {results.length}{" "}
                {results.length === 1 ? "issue" : "issues"}
              </h2>
              <button
                onClick={analyzeRepo}
                className="px-4 py-2 bg-slate-700 text-white rounded-lg hover:bg-slate-600 transition-colors flex items-center gap-2"
              >
                <RefreshCw className="w-4 h-4" />
                Re-analyze
              </button>
            </div>

            <div className="space-y-4">
              {results.map((result: AnalysisResult, index: number) => {
                const SeverityIcon =
                  severityIcons[
                    result.severity as keyof typeof severityIcons
                  ] || Info;
                return (
                  <div
                    key={index}
                    className="bg-slate-800 border border-slate-700 rounded-lg p-6 hover:border-slate-600 transition-colors"
                  >
                    <div className="flex items-start gap-4">
                      <div
                        className={`w-10 h-10 ${severityColors[result.severity as keyof typeof severityColors]} rounded-lg flex items-center justify-center flex-shrink-0`}
                      >
                        <SeverityIcon className="w-5 h-5 text-white" />
                      </div>
                      <div className="flex-1">
                        <div className="flex items-start justify-between mb-2">
                          <div>
                            <h3 className="text-white font-semibold text-lg">
                              {result.message}
                            </h3>
                            <p className="text-slate-400 text-sm mt-1">
                              {result.category} • {result.type}
                            </p>
                          </div>
                          <span
                            className={`px-3 py-1 rounded-full text-xs font-semibold ${
                              result.severity === "critical"
                                ? "bg-red-500/20 text-red-400"
                                : result.severity === "high"
                                  ? "bg-orange-500/20 text-orange-400"
                                  : result.severity === "medium"
                                    ? "bg-yellow-500/20 text-yellow-400"
                                    : result.severity === "low"
                                      ? "bg-blue-500/20 text-blue-400"
                                      : "bg-slate-500/20 text-slate-400"
                            }`}
                          >
                            {result.severity.toUpperCase()}
                          </span>
                        </div>

                        <div className="bg-slate-900 rounded-lg p-3 mb-3">
                          <p className="text-slate-300 text-sm font-mono">
                            {result.filePath}:{result.lineNumber}
                          </p>
                        </div>

                        {result.explanation && (
                          <p className="text-slate-400 text-sm mb-3">
                            {result.explanation}
                          </p>
                        )}

                        <div className="bg-blue-500/10 border border-blue-500/30 rounded-lg p-3">
                          <p className="text-blue-300 text-sm font-medium mb-1">
                            Suggestion:
                          </p>
                          <p className="text-slate-300 text-sm">
                            {result.suggestion}
                          </p>
                        </div>

                        {(result.confidenceScore ||
                          result.impactScore ||
                          result.effortScore) && (
                          <div className="flex gap-4 mt-3 text-xs text-slate-500">
                            {result.confidenceScore && (
                              <span>
                                Confidence:{" "}
                                {Math.round(result.confidenceScore * 100)}%
                              </span>
                            )}
                            {result.impactScore && (
                              <span>
                                Impact: {Math.round(result.impactScore * 100)}%
                              </span>
                            )}
                            {result.effortScore && (
                              <span>
                                Effort: {Math.round(result.effortScore * 100)}%
                              </span>
                            )}
                          </div>
                        )}

                        {result.cweId && (
                          <p className="text-slate-500 text-xs mt-2">
                            CWE: {result.cweId}
                            {result.owaspCategory &&
                              ` • OWASP: ${result.owaspCategory}`}
                          </p>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
