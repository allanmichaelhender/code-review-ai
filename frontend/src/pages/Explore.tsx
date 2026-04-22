import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

interface Repository {
  id: number;
  owner: string;
  name: string;
  url: string;
  description: string;
  language: string;
  overallHealthScore: number;
}

export default function Explore() {
  const navigate = useNavigate();
  const [repositories, setRepositories] = useState<Repository[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetch("/api/repositories")
      .then((res) => res.json())
      .then((data) => {
        setRepositories(data);
        setLoading(false);
      })
      .catch((err) => {
        setError("Failed to load repositories");
        setLoading(false);
      });
  }, []);

  const getHealthColor = (score: number) => {
    if (score >= 0.8) return "bg-green-500";
    if (score >= 0.6) return "bg-yellow-500";
    return "bg-red-500";
  };

  if (loading) return <div className="p-8">Loading...</div>;
  if (error) return <div className="p-8 text-red-500">{error}</div>;

  return (
    <div className="p-8 max-w-7xl mx-auto">
      <h1 className="text-3xl font-bold mb-8">Pre-Analysed Repositories</h1>
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {repositories.map((repo) => (
          <Card key={repo.id}>
            <CardHeader>
              <CardTitle>
                <a
                  href={repo.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-600 hover:underline"
                >
                  {repo.owner}/{repo.name}
                </a>
              </CardTitle>
              <CardDescription>{repo.description}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                <div className="flex items-center gap-2">
                  <Badge variant="secondary">{repo.language}</Badge>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm font-medium">Health Score:</span>
                  <div className="flex items-center gap-2">
                    <div className="w-24 h-2 bg-gray-200 rounded-full overflow-hidden">
                      <div
                        className={`h-full ${getHealthColor(repo.overallHealthScore)}`}
                        style={{ width: `${repo.overallHealthScore * 100}%` }}
                      />
                    </div>
                    <span className="text-sm font-semibold">
                      {(repo.overallHealthScore * 100).toFixed(0)}%
                    </span>
                  </div>
                </div>
                <Button
                  onClick={() =>
                    navigate(`/demo?repo=${encodeURIComponent(repo.url)}`)
                  }
                  className="w-full"
                >
                  View Analysis
                </Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
