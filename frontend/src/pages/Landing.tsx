import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Shield, Zap, GitBranch, BarChart3, ArrowRight } from "lucide-react";

export default function Landing() {
  const [repoUrl, setRepoUrl] = useState("");
  const navigate = useNavigate();

  const handleAnalyze = () => {
    if (repoUrl) {
      navigate(`/demo?repo=${encodeURIComponent(repoUrl)}`);
    }
  };

  const features = [
    {
      icon: Shield,
      title: "Security Analysis",
      description:
        "Detect vulnerabilities, hardcoded secrets, and security issues with AI-powered analysis",
    },
    {
      icon: Zap,
      title: "Fast Analysis",
      description:
        "Get comprehensive code review results in seconds using DeepSeek AI",
    },
    {
      icon: GitBranch,
      title: "Git Integration",
      description:
        "Seamlessly analyze any GitHub repository with smart file filtering",
    },
    {
      icon: BarChart3,
      title: "Health Scores",
      description:
        "Track code quality with detailed health metrics and category breakdowns",
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
      {/* Hero Section */}
      <div className="container mx-auto px-4 py-20">
        <div className="max-w-4xl mx-auto text-center">
          <h1 className="text-5xl md:text-6xl font-bold text-white mb-6">
            AI-Powered Code Review
          </h1>
          <p className="text-xl text-slate-300 mb-8 max-w-2xl mx-auto">
            Analyze your GitHub repositories with advanced AI to identify
            security vulnerabilities, code quality issues, and best practices
            violations in seconds.
          </p>

          {/* Input Section */}
          <div className="max-w-2xl mx-auto mb-12">
            <div className="flex gap-3">
              <input
                type="text"
                placeholder="https://github.com/owner/repo"
                value={repoUrl}
                onChange={(e) => setRepoUrl(e.target.value)}
                className="flex-1 px-4 py-3 rounded-lg bg-slate-700 border border-slate-600 text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              <button
                onClick={handleAnalyze}
                disabled={!repoUrl}
                className="px-6 py-3 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 disabled:bg-slate-600 disabled:cursor-not-allowed transition-colors flex items-center gap-2"
              >
                Analyze
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* CTA Buttons */}
          <div className="flex gap-4 justify-center mb-16">
            <button
              onClick={() => navigate("/explore")}
              className="px-6 py-3 bg-slate-700 text-white rounded-lg font-semibold hover:bg-slate-600 transition-colors"
            >
              View Demo Repositories
            </button>
          </div>
        </div>

        {/* Features Grid */}
        <div className="max-w-6xl mx-auto mt-20">
          <h2 className="text-3xl font-bold text-white text-center mb-12">
            Powerful Features
          </h2>
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {features.map((feature, index) => (
              <div
                key={index}
                className="p-6 bg-slate-800/50 backdrop-blur rounded-xl border border-slate-700 hover:border-slate-600 transition-colors"
              >
                <div className="w-12 h-12 bg-blue-600/20 rounded-lg flex items-center justify-center mb-4">
                  <feature.icon className="w-6 h-6 text-blue-400" />
                </div>
                <h3 className="text-lg font-semibold text-white mb-2">
                  {feature.title}
                </h3>
                <p className="text-slate-400 text-sm">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Analysis Categories */}
        <div className="max-w-4xl mx-auto mt-20">
          <h2 className="text-3xl font-bold text-white text-center mb-12">
            Comprehensive Analysis
          </h2>
          <div className="grid md:grid-cols-5 gap-4">
            {[
              { name: "Security", color: "red" },
              { name: "Code Quality", color: "yellow" },
              { name: "Performance", color: "green" },
              { name: "Best Practices", color: "blue" },
              { name: "Maintainability", color: "purple" },
            ].map((category, index) => (
              <div
                key={index}
                className="p-4 bg-slate-800/50 backdrop-blur rounded-lg border border-slate-700 text-center"
              >
                <div
                  className={`w-3 h-3 bg-${category.color}-500 rounded-full mx-auto mb-2`}
                />
                <p className="text-white font-medium text-sm">
                  {category.name}
                </p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
