import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowRight, ExternalLink } from "lucide-react";

export default function Landing() {
  const [repoUrl, setRepoUrl] = useState("");
  const navigate = useNavigate();
  const [repositories, setRepositories] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

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

  const handleAnalyze = () => {
    if (repoUrl) {
      navigate(`/demo?repo=${encodeURIComponent(repoUrl)}`);
    }
  };

  const getHealthColor = (score: number) => {
    if (score >= 0.8) return "bg-green-500";
    if (score >= 0.6) return "bg-yellow-500";
    return "bg-red-500";
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
      {/* Hero Section */}
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-4xl mx-auto text-center">
          <h1 className="text-4xl md:text-5xl font-bold text-white mb-4">
            AI-Powered Code Review
          </h1>
          <p className="text-lg text-slate-300 mb-6 max-w-2xl mx-auto">
            Analyze your GitHub repositories with advanced AI to identify
            security vulnerabilities, code quality issues, and best practices
            violations in seconds.
          </p>

          {/* Input Section */}
          <div className="max-w-2xl mx-auto mb-8">
            <div className="flex gap-3">
              <input
                type="text"
                placeholder="https://github.com/owner/repo"
                value={repoUrl}
                onChange={(e) => setRepoUrl(e.target.value)}
                className="flex-1 px-4 py-3 rounded-lg bg-slate-700 border border-slate-600 text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
              />
              <button
                onClick={handleAnalyze}
                disabled={!repoUrl}
                className="px-6 py-3 bg-emerald-600 text-white rounded-lg font-semibold hover:bg-emerald-700 disabled:bg-slate-600 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
              >
                Analyze
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

        {/* Repository List */}
        <div className="max-w-6xl mx-auto mt-8">
          <h2 className="text-2xl font-bold text-white text-center mb-6">
            Analysed Repositories
          </h2>
          {loading ? (
            <div className="text-center text-slate-400">
              Loading repositories...
            </div>
          ) : (
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              {repositories.map((repo) => (
                <div
                  key={repo.id}
                  onClick={() =>
                    navigate(`/demo?repo=${encodeURIComponent(repo.url)}`)
                  }
                  className="group p-6 bg-slate-800/50 backdrop-blur rounded-xl border border-slate-700 hover:border-emerald-500 hover:bg-slate-800 transition-colors cursor-pointer relative"
                >
                  <a
                    href={repo.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    onClick={(e) => e.stopPropagation()}
                    className="absolute top-4 right-4 px-3 py-1 bg-slate-700 hover:bg-slate-600 text-slate-300 text-xs rounded flex items-center gap-1 transition-colors"
                  >
                    Code
                    <ExternalLink className="w-3 h-3" />
                  </a>
                  <h3 className="text-lg font-semibold text-emerald-300 mb-2 pr-16 group-hover:text-emerald-200 transition-colors">
                    {repo.projectName || `${repo.owner}/${repo.name}`}
                  </h3>
                  <div className="flex flex-wrap gap-2 mb-4">
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
                    <span className="text-sm font-medium text-slate-300">
                      Health Score:
                    </span>
                    <div className="flex items-center gap-2">
                      <div className="w-24 h-2 bg-slate-700 rounded-full overflow-hidden">
                        <div
                          className={`h-full ${getHealthColor(repo.overallHealthScore)}`}
                          style={{ width: `${repo.overallHealthScore * 100}%` }}
                        />
                      </div>
                      <span className="text-sm font-semibold text-white">
                        {(repo.overallHealthScore * 100).toFixed(0)}%
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Analysis Types */}
        <div className="max-w-4xl mx-auto mt-8">
          <h2 className="text-2xl font-bold text-white text-center mb-6">
            Analysis Categories
          </h2>
          <div className="grid md:grid-cols-5 gap-4">
            {[
              {
                name: "Security",
                className: "bg-red-500",
                desc: "Vulnerabilities & threats",
              },
              {
                name: "Code Quality",
                className: "bg-yellow-500",
                desc: "Complexity & structure",
              },
              {
                name: "Performance",
                className: "bg-green-500",
                desc: "Efficiency & speed",
              },
              {
                name: "Best Practices",
                className: "bg-blue-500",
                desc: "Language conventions",
              },
              {
                name: "Maintainability",
                className: "bg-purple-500",
                desc: "Readability & docs",
              },
            ].map((category, index) => (
              <div
                key={index}
                className="p-4 bg-slate-800/50 backdrop-blur rounded-lg border border-slate-700 hover:bg-slate-800 text-center transition-colors"
              >
                <div
                  className={`w-3 h-3 ${category.className} rounded-full mx-auto mb-2`}
                />
                <p className="text-white font-medium text-sm mb-1">
                  {category.name}
                </p>
                <p className="text-slate-400 text-xs">{category.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
