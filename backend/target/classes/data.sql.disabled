-- Seed data for pre-cached repositories

-- Repositories
INSERT INTO repositories (id, owner, name, url, description, language, stars, overall_health_score, created_at, updated_at) VALUES
(1, 'allanmichaelhender', 'guniea-pig-v2', 'https://github.com/allanmichaelhender/guniea-pig-v2', 'Guinea Pig Portfolio - Python + React + TS', 'TypeScript', 0, 0.85, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'allanmichaelhender', 'Vantage-Point-ML', 'https://github.com/allanmichaelhender/Vantage-Point-ML', 'Vantage Point - Python + React + TS', 'TypeScript', 0, 0.78, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'allanmichaelhender', 'hybrid_AI_coach', 'https://github.com/allanmichaelhender/hybrid_AI_coach', 'Hybrid Hour - Python + React + TS', 'TypeScript', 0, 0.82, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'allanmichaelhender', 'allanmichaelhender.github.io', 'https://github.com/allanmichaelhender/allanmichaelhender.github.io', 'Portfolio Website - React + TS', 'TypeScript', 0, 0.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Analyses
INSERT INTO analyses (id, repository_id, commit_hash, analyzed_at, analysis_provider, total_files_analyzed, total_issues_found, critical_count, high_count, medium_count, low_count, info_count, created_at) VALUES
(1, 1, 'abc123def456', CURRENT_TIMESTAMP, 'deepseek', 25, 8, 0, 2, 3, 2, 1, CURRENT_TIMESTAMP),
(2, 2, 'def456ghi789', CURRENT_TIMESTAMP, 'deepseek', 30, 12, 1, 3, 4, 3, 1, CURRENT_TIMESTAMP),
(3, 3, 'ghi789jkl012', CURRENT_TIMESTAMP, 'deepseek', 20, 6, 0, 1, 2, 2, 1, CURRENT_TIMESTAMP),
(4, 4, 'jkl012mno345', CURRENT_TIMESTAMP, 'deepseek', 15, 4, 0, 1, 1, 1, 1, CURRENT_TIMESTAMP);

-- Analysis Results
INSERT INTO analysis_results (analysis_id, type, severity, file_path, line_number, message, suggestion, created_at) VALUES
-- guniea-pig-v2 results
(1, 'security', 'high', 'src/config/api.ts', 15, 'Hardcoded API key detected', 'Use environment variables or a secrets manager', CURRENT_TIMESTAMP),
(1, 'security', 'medium', 'src/utils/auth.ts', 23, 'Weak password validation', 'Implement stronger password requirements', CURRENT_TIMESTAMP),
(1, 'quality', 'medium', 'src/components/Header.tsx', 45, 'Function too long (45 lines)', 'Consider breaking into smaller functions', CURRENT_TIMESTAMP),
(1, 'quality', 'low', 'src/services/api.ts', 12, 'Code duplication detected', 'Extract common logic to a utility function', CURRENT_TIMESTAMP),
(1, 'quality', 'info', 'src/App.tsx', 8, 'Consider adding error boundary', 'Add React Error Boundary for better error handling', CURRENT_TIMESTAMP),
(1, 'security', 'high', 'src/middleware/auth.ts', 30, 'Missing rate limiting', 'Implement rate limiting to prevent abuse', CURRENT_TIMESTAMP),
(1, 'quality', 'medium', 'src/hooks/useAuth.ts', 18, 'Complex cyclomatic complexity', 'Simplify logic or extract to separate functions', CURRENT_TIMESTAMP),
(1, 'quality', 'low', 'src/types/index.ts', 5, 'Unused type definition', 'Remove or document the type', CURRENT_TIMESTAMP),

-- Vantage-Point-ML results
(2, 'security', 'critical', 'src/models/ml.py', 42, 'SQL injection vulnerability', 'Use parameterized queries', CURRENT_TIMESTAMP),
(2, 'security', 'high', 'src/api/endpoints.ts', 28, 'CORS misconfiguration', 'Restrict CORS to specific origins', CURRENT_TIMESTAMP),
(2, 'security', 'high', 'src/utils/validation.ts', 15, 'Insufficient input validation', 'Add stricter validation rules', CURRENT_TIMESTAMP),
(2, 'quality', 'medium', 'src/components/Chart.tsx', 60, 'Large component file', 'Split into smaller components', CURRENT_TIMESTAMP),
(2, 'quality', 'medium', 'src/hooks/useData.ts', 35, 'Missing error handling', 'Add try-catch blocks for async operations', CURRENT_TIMESTAMP),
(2, 'quality', 'low', 'src/styles/theme.css', 120, 'Unused CSS classes', 'Remove unused styles', CURRENT_TIMESTAMP),
(2, 'quality', 'low', 'src/config/constants.ts', 8, 'Magic number detected', 'Use named constants', CURRENT_TIMESTAMP),
(2, 'quality', 'info', 'src/App.tsx', 15, 'Consider lazy loading', 'Implement code splitting for better performance', CURRENT_TIMESTAMP),
(2, 'security', 'medium', 'src/middleware/security.ts', 22, 'Missing CSRF protection', 'Add CSRF tokens for state-changing operations', CURRENT_TIMESTAMP),
(2, 'quality', 'medium', 'src/services/data.ts', 50, 'High cognitive complexity', 'Refactor to improve readability', CURRENT_TIMESTAMP),
(2, 'quality', 'low', 'src/utils/helpers.ts', 18, 'Redundant null check', 'Remove unnecessary checks', CURRENT_TIMESTAMP),
(2, 'quality', 'info', 'src/index.tsx', 5, 'Missing analytics', 'Consider adding analytics tracking', CURRENT_TIMESTAMP),

-- hybrid_AI_coach results
(3, 'security', 'high', 'src/ai/client.ts', 20, 'API key exposed in client code', 'Move API calls to backend', CURRENT_TIMESTAMP),
(3, 'quality', 'medium', 'src/components/Chat.tsx', 55, 'Deeply nested components', 'Flatten component hierarchy', CURRENT_TIMESTAMP),
(3, 'quality', 'medium', 'src/hooks/useAI.ts', 40, 'Missing cleanup in useEffect', 'Add cleanup function to prevent memory leaks', CURRENT_TIMESTAMP),
(3, 'quality', 'low', 'src/types/chat.ts', 12, 'Type could be more specific', 'Use more precise TypeScript types', CURRENT_TIMESTAMP),
(3, 'quality', 'info', 'src/App.tsx', 10, 'Consider adding loading states', 'Improve UX with loading indicators', CURRENT_TIMESTAMP),
(3, 'quality', 'low', 'src/utils/format.ts', 25, 'Inefficient string concatenation', 'Use template literals', CURRENT_TIMESTAMP),

-- allanmichaelhender.github.io results
(4, 'security', 'high', 'src/components/Contact.tsx', 18, 'Email exposed in frontend', 'Use contact form with backend processing', CURRENT_TIMESTAMP),
(4, 'quality', 'medium', 'src/pages/About.tsx', 30, 'Missing alt text on images', 'Add descriptive alt text for accessibility', CURRENT_TIMESTAMP),
(4, 'quality', 'low', 'src/styles/global.css', 200, 'Large CSS file', 'Consider CSS modules or styled-components', CURRENT_TIMESTAMP),
(4, 'quality', 'info', 'src/App.tsx', 12, 'Missing meta tags', 'Add SEO meta tags for better indexing', CURRENT_TIMESTAMP);
