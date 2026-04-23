-- Repositories table
CREATE TABLE repositories (
    id BIGSERIAL PRIMARY KEY,
    owner VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    project_name VARCHAR(255),
    tech_stack TEXT,
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
    id BIGSERIAL PRIMARY KEY,
    repository_id BIGINT REFERENCES repositories(id) ON DELETE CASCADE,
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
    security_count INTEGER DEFAULT 0,
    code_quality_count INTEGER DEFAULT 0,
    performance_count INTEGER DEFAULT 0,
    best_practices_count INTEGER DEFAULT 0,
    maintainability_count INTEGER DEFAULT 0,
    overall_health_score DECIMAL(3,2),
    security_health_score DECIMAL(3,2),
    code_quality_health_score DECIMAL(3,2),
    performance_health_score DECIMAL(3,2),
    analysis_duration_ms BIGINT,
    tokens_used BIGINT,
    model_version VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(repository_id, commit_hash)
);

-- Analysis results table
CREATE TABLE analysis_results (
    id BIGSERIAL PRIMARY KEY,
    analysis_id BIGINT REFERENCES analyses(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    line_number INTEGER,
    end_line_number INTEGER,
    message TEXT NOT NULL,
    suggestion TEXT,
    code_snippet TEXT,
    explanation TEXT,
    confidence_score DECIMAL(3,2),
    impact_score DECIMAL(3,2),
    effort_score DECIMAL(3,2),
    cwe_id VARCHAR(20),
    owasp_category VARCHAR(100),
    status VARCHAR(20) DEFAULT 'open',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_repositories_owner_name ON repositories(owner, name);
CREATE INDEX idx_analyses_repo_commit ON analyses(repository_id, commit_hash);
CREATE INDEX idx_analysis_results_category_type_severity ON analysis_results(category, type, severity);
CREATE INDEX idx_analysis_results_file ON analysis_results(file_path);
CREATE INDEX idx_analysis_results_analysis_id ON analysis_results(analysis_id);
