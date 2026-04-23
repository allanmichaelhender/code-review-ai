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

- Two-column layout (5/7 left column, 1/3 right column) on desktop
- Mobile responsive: columns stack vertically on mobile
- Vertically and horizontally centered content
- Uses 95% of viewport width on desktop
- Animated gradient background (purple to indigo to slate)
- Pulsing radial gradient overlays
- Gradient animated title ("\*Repo Reviewer" with emerald-cyan-purple gradient)
- Glassmorphism effects with backdrop blur
- Left column (5/7):
  - Title and description (hidden after analysis submission)
  - Input section with Analyse button inside input box
  - File/Repo mode toggle slider (Repo disabled in demo mode)
  - Analysis results expand to fill space after submission
  - Analysis categories displayed without title at bottom
- Right column (1/3):
  - "Demo Repositories" header
  - Repository cards with health scores
  - 2-column grid on mobile, 1 column on desktop
- Single File mode:
  - GitHub file URL input with Analyse button inside
  - Analysis result display with severity colors and icons
- Repository mode (disabled in demo):
  - Repository URL input with disabled button
- Analysis categories: Security, Quality, Performance, Best Practices, Maintainability

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
