-- Repositories table
CREATE TABLE repositories (
    id SERIAL PRIMARY KEY,
    owner VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL UNIQUE,
    description TEXT,
    language VARCHAR(50),
    stars INTEGER DEFAULT 0,
    last_analyzed_commit VARCHAR(40),
    last_analyzed_at TIMESTAMP,
    overall_health_score DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Analyses table
CREATE TABLE analyses (
    id SERIAL PRIMARY KEY,
    repository_id INTEGER REFERENCES repositories(id) ON DELETE CASCADE,
    commit_hash VARCHAR(40) NOT NULL,
    analyzed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    analysis_provider VARCHAR(50),
    total_files_analyzed INTEGER DEFAULT 0,
    total_issues_found INTEGER DEFAULT 0,
    critical_count INTEGER DEFAULT 0,
    high_count INTEGER DEFAULT 0,
    medium_count INTEGER DEFAULT 0,
    low_count INTEGER DEFAULT 0,
    info_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(repository_id, commit_hash)
);

-- Analysis results table
CREATE TABLE analysis_results (
    id SERIAL PRIMARY KEY,
    analysis_id INTEGER REFERENCES analyses(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    line_number INTEGER,
    end_line_number INTEGER,
    message TEXT NOT NULL,
    suggestion TEXT,
    code_snippet TEXT,
    status VARCHAR(20) DEFAULT 'open',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_repositories_owner_name ON repositories(owner, name);
CREATE INDEX idx_analyses_repo_commit ON analyses(repository_id, commit_hash);
CREATE INDEX idx_analysis_results_type_severity ON analysis_results(type, severity);
CREATE INDEX idx_analysis_results_file ON analysis_results(file_path);
