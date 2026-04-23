# Frontend Architecture

## Component Structure

```
src/
├── components/ui/       # shadcn/ui components
│   ├── button.tsx
│   ├── card.tsx
│   ├── badge.tsx
│   └── ...
├── lib/
│   └── utils.ts         # Utility functions
├── pages/
│   ├── Landing.tsx      # Home page with repository list
│   └── Demo.tsx         # Analysis results display
├── App.tsx             # Main app component with routing
└── index.css           # TailwindCSS imports
```

## Page Descriptions

**Landing.tsx:**

- Single-screen layout (no scrolling on desktop)
- Animated gradient background (purple to indigo to slate)
- Pulsing radial gradient overlays
- Gradient animated title ("\*Repo Reviewer" with emerald-cyan-purple gradient)
- Glassmorphism effects with backdrop blur
- Mode toggle slider (Single File / Repository - Not available in demo)
- Single File mode:
  - GitHub file URL input
  - "Analyse" button with Gemini provider
  - Analysis result display
- Repository mode (disabled in demo):
  - Repository URL input
  - Disabled "Not available in demo" button
- Analysis categories badges with gradient text
- Removed: Repository list and categories section

**Demo.tsx:**

- Displays analysis results for a specific repository
- Shows loading state during analysis
- Displays issues grouped by category
- Color-coded severity indicators
- Read-only (no manual analysis trigger)

## Styling System

**Accent Color:** Emerald

- Primary buttons: emerald-600 → emerald-700 (hover)
- Input focus ring: emerald-500
- Repository titles: emerald-300 → emerald-200 (hover)
- Tech stack badges: emerald-600 (hover)
- Repository cards: border-emerald-500 (hover)

**Analysis Category Colors:**

- Security: red-500
- Code Quality: yellow-500
- Performance: green-500
- Best Practices: blue-500
- Maintainability: purple-500

## Routing

```typescript
// App.tsx
<Routes>
  <Route path="/" element={<Landing />} />
  <Route path="/demo" element={<Demo />} />
</Routes>
```

Navigation to Demo:

```typescript
navigate(`/demo?repo=${encodeURIComponent(repoUrl)}`);
```

## API Integration

**Base URL:** `/api` (proxied to backend)

**Example calls:**

```typescript
// Fetch repositories
fetch("/api/repositories").then((res) => res.json());

// Fetch analysis
fetch(`/api/analysis/by-repo?repo=${encodeURIComponent(repoUrl)}`).then((res) =>
  res.json(),
);
```

## Proxy Configuration

**vite.config.ts:**

```typescript
proxy: {
  "/api": {
    target: "http://backend:8080",  // Docker service name
    changeOrigin: true,
    timeout: 300000,  // 5 minutes
    proxyTimeout: 300000,
  },
}
```
