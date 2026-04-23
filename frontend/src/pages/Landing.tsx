import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  ExternalLink,
  FileText,
  AlertCircle,
  AlertTriangle,
  Info,
} from "lucide-react";

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
  info: Info,
};

export default function Landing() {
  const [repoUrl, setRepoUrl] = useState("");
  const [fileUrl, setFileUrl] = useState("");
  const [inputMode, setInputMode] = useState<"repo" | "file">("file");
  const navigate = useNavigate();
  const [repositories, setRepositories] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [analyzing, setAnalyzing] = useState(false);
  const [analysisResult, setAnalysisResult] = useState<any[] | null>(null);
  const [hasSubmitted, setHasSubmitted] = useState(false);

  useEffect(() => {
    fetch("/api/repositories")
      .then((res) => res.json())
      .then((data) => {
        setRepositories(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Failed to load repositories", err);
        setLoading(false);
      });
  }, []);

  const handleAnalyzeRepo = () => {
    if (repoUrl) {
      navigate(`/demo?repo=${encodeURIComponent(repoUrl)}`);
    }
  };

  const handleAnalyzeFile = async () => {
    if (!fileUrl.trim()) return;

    setAnalyzing(true);
    setAnalysisResult(null);
    setHasSubmitted(true);

    try {
      const response = await fetch("/api/analyse-file", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          fileUrl,
          provider: "gemini",
        }),
      });

      const data = await response.json();
      if (response.ok) {
        // Parse the JSON result
        try {
          const parsed = JSON.parse(data.result);
          setAnalysisResult(Array.isArray(parsed) ? parsed : [parsed]);
        } catch {
          // If parsing fails, treat as error
          setAnalysisResult([{ severity: "error", message: data.result }]);
        }
      } else {
        setAnalysisResult([{ severity: "error", message: data.error }]);
      }
    } catch (err) {
      setAnalysisResult([
        {
          severity: "error",
          message:
            err instanceof Error ? err.message : "Failed to analyze file",
        },
      ]);
    } finally {
      setAnalyzing(false);
    }
  };

  return (
    <div className="h-screen bg-slate-900 relative overflow-hidden">
      {/* Main content */}
      <div className="relative z-10 h-screen flex items-center justify-center px-2 py-4 gap-4 max-w-[95vw]">
        {/* Left column - 5/7ths */}
        <div className="flex-[5/7] flex flex-col">
          {/* Title and Description */}
          {!hasSubmitted && (
            <>
              <h1 className="text-4xl md:text-5xl font-bold mb-2 text-white">
                REPO REVIEWER
              </h1>
              <p className="text-sm text-slate-300 mb-4 line-clamp-2">
                Analyse your GitHub files/repositories to identify security
                vulnerabilities, code quality issues, and best practices
                violations in seconds.
              </p>
            </>
          )}

          {/* Input Section */}
          <div className="flex-1 flex flex-col">
            <div className="flex gap-3 mb-4">
              {/* Input box with button inside */}
              <div className="flex-1 relative">
                {inputMode === "repo" && (
                  <>
                    <input
                      type="text"
                      placeholder="https://github.com/owner/repo"
                      value={repoUrl}
                      onChange={(e) => setRepoUrl(e.target.value)}
                      className="w-full px-4 py-3 pr-24 rounded-lg bg-slate-800/50 backdrop-blur border border-slate-700 text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
                    />
                    <button
                      onClick={handleAnalyzeRepo}
                      disabled={true}
                      className="absolute right-2 top-1/2 -translate-y-1/2 px-4 py-2 bg-slate-700 text-slate-400 rounded-lg font-semibold cursor-not-allowed transition-colors flex items-center gap-2"
                    >
                      <FileText className="w-4 h-4" />
                    </button>
                  </>
                )}
                {inputMode === "file" && (
                  <>
                    <input
                      type="text"
                      placeholder="https://github.com/owner/repo/blob/main/file.ts"
                      value={fileUrl}
                      onChange={(e) => setFileUrl(e.target.value)}
                      className="w-full px-4 py-3 pr-24 rounded-lg bg-slate-800/50 backdrop-blur border border-slate-700 text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
                    />
                    <button
                      onClick={handleAnalyzeFile}
                      disabled={!fileUrl.trim() || analyzing}
                      className="absolute right-2 top-1/2 -translate-y-1/2 px-4 py-2 bg-slate-800 text-emerald-400 border border-emerald-500 rounded-lg font-semibold hover:bg-emerald-600 hover:text-white hover:border-emerald-600 disabled:bg-slate-700 disabled:text-slate-400 disabled:border-slate-600 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
                    >
                      {analyzing ? "Analysing..." : "Analyse"}
                      <FileText className="w-4 h-4" />
                    </button>
                  </>
                )}
              </div>

              {/* Mode Toggle - Slider */}
              <div className="bg-slate-800/50 backdrop-blur p-1 rounded-lg inline-flex border border-slate-700">
                <button
                  onClick={() => setInputMode("file")}
                  className={`px-4 py-2 rounded-md font-medium transition-all ${
                    inputMode === "file"
                      ? "bg-emerald-600 text-white shadow-lg shadow-emerald-500/30"
                      : "text-slate-300 hover:text-white"
                  }`}
                >
                  File
                </button>
                <button
                  onClick={() => setInputMode("repo")}
                  className={`px-4 py-2 rounded-md font-medium transition-all ${
                    inputMode === "repo"
                      ? "bg-emerald-600 text-white shadow-lg shadow-emerald-500/30"
                      : "text-slate-300 hover:text-white"
                  }`}
                >
                  Repo
                </button>
              </div>
            </div>

            {/* Analysis Results */}
            {analysisResult && (
              <div className="flex-1 space-y-3 overflow-y-auto">
                <h3 className="text-white font-semibold">
                  Found {analysisResult.length}{" "}
                  {analysisResult.length === 1 ? "issue" : "issues"}
                </h3>
                {analysisResult.map((result: any, index: number) => {
                  const SeverityIcon =
                    severityIcons[
                      result.severity as keyof typeof severityIcons
                    ] || Info;
                  return (
                    <div
                      key={index}
                      className="bg-slate-800 border border-slate-700 rounded-lg p-4 hover:border-slate-600 transition-colors"
                    >
                      <div className="flex items-start gap-3">
                        <div
                          className={`w-8 h-8 ${severityColors[result.severity as keyof typeof severityColors]} rounded-lg flex items-center justify-center flex-shrink-0`}
                        >
                          <SeverityIcon className="w-4 h-4 text-white" />
                        </div>
                        <div className="flex-1">
                          <div className="flex items-start justify-between mb-2">
                            <div>
                              <h3 className="text-white font-semibold text-sm">
                                {result.message}
                              </h3>
                            </div>
                            <span
                              className={`px-2 py-0.5 rounded-full text-xs font-semibold ${
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
                              {result.severity?.toUpperCase() || "INFO"}
                            </span>
                          </div>
                          {result.suggestion && (
                            <div className="bg-blue-500/10 border border-blue-500/30 rounded-lg p-2">
                              <p className="text-blue-300 text-xs font-medium mb-1">
                                Suggestion:
                              </p>
                              <p className="text-slate-300 text-xs">
                                {result.suggestion}
                              </p>
                            </div>
                          )}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}

            {/* Analysis Categories - No title */}
            <div className="grid grid-cols-5 gap-3">
              {[
                {
                  name: "Security",
                  desc: "Vulnerabilities & threats",
                  color: "bg-red-500",
                },
                {
                  name: "Quality",
                  desc: "Complexity & structure",
                  color: "bg-yellow-500",
                },
                {
                  name: "Performance",
                  desc: "Efficiency & speed",
                  color: "bg-green-500",
                },
                {
                  name: "Best Practices",
                  desc: "Language conventions",
                  color: "bg-blue-500",
                },
                {
                  name: "Maintainability",
                  desc: "Readability & docs",
                  color: "bg-purple-500",
                },
              ].map((category, index) => (
                <div
                  key={index}
                  className="p-3 bg-slate-800/50 backdrop-blur rounded-lg border border-slate-700 text-center"
                >
                  <div
                    className={`w-3 h-3 ${category.color} rounded-full mx-auto mb-1`}
                  />
                  <p className="text-white font-medium text-xs mb-1">
                    {category.name}
                  </p>
                  <p className="text-slate-400 text-xs">{category.desc}</p>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right column - 2/7ths - Demo Repositories */}
        <div className="flex-[2/7] flex flex-col">
          <h2 className="text-xl font-bold text-white mb-4">
            Demo Repositories
          </h2>
          {loading ? (
            <div className="text-center text-slate-400">
              Loading repositories...
            </div>
          ) : (
            <div className="space-y-4 overflow-y-auto flex-1">
              {repositories.map((repo) => (
                <div
                  key={repo.id}
                  onClick={() =>
                    navigate(`/demo?repo=${encodeURIComponent(repo.url)}`)
                  }
                  className="group p-4 bg-slate-800/50 backdrop-blur rounded-xl border border-slate-700 hover:border-emerald-500 hover:bg-slate-800 transition-colors cursor-pointer relative"
                >
                  <a
                    href={repo.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    onClick={(e) => e.stopPropagation()}
                    className="absolute top-3 right-3 px-2 py-1 bg-slate-700 hover:bg-slate-600 text-slate-300 text-xs rounded flex items-center gap-1 transition-colors"
                  >
                    Code
                    <ExternalLink className="w-3 h-3" />
                  </a>
                  <h3 className="text-base font-semibold text-emerald-300 mb-2 pr-12 group-hover:text-emerald-200 transition-colors">
                    {repo.projectName || `${repo.owner}/${repo.name}`}
                  </h3>
                  <div className="flex flex-wrap gap-2 mb-3">
                    {repo.techStack ? (
                      repo.techStack
                        .split(",")
                        .map((tech: string, idx: number) => (
                          <span
                            key={idx}
                            className="px-2 py-1 bg-slate-700 hover:bg-emerald-600 text-slate-300 hover:text-white text-xs rounded transition-colors cursor-default"
                          >
                            {tech.trim()}
                          </span>
                        ))
                    ) : (
                      <span className="px-2 py-1 bg-slate-700 hover:bg-emerald-600 text-slate-300 hover:text-white text-xs rounded transition-colors cursor-default">
                        {repo.language}
                      </span>
                    )}
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-medium text-slate-300">
                      Health Score:
                    </span>
                    <div className="flex items-center gap-2">
                      <div className="w-20 h-2 bg-slate-700 rounded-full overflow-hidden">
                        <div
                          className={`h-full ${
                            repo.overallHealthScore >= 0.8
                              ? "bg-green-500"
                              : repo.overallHealthScore >= 0.6
                                ? "bg-yellow-500"
                                : "bg-red-500"
                          }`}
                          style={{ width: `${repo.overallHealthScore * 100}%` }}
                        />
                      </div>
                      <span
                        className={`text-xs font-semibold ${
                          repo.overallHealthScore >= 0.8
                            ? "text-green-400"
                            : repo.overallHealthScore >= 0.6
                              ? "text-yellow-400"
                              : "text-red-400"
                        }`}
                      >
                        {(repo.overallHealthScore * 100).toFixed(0)}%
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
