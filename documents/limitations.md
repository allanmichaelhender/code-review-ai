# Current Limitations

## MVP Constraints

- **File Analysis Limit:** 1 file per repository (to stay within 50 daily OpenRouter requests)
- **Rate Limits:** OpenRouter free tier has 50 requests/day limit
- **Manual Analysis:** Disabled (analysis only via scheduled jobs)
- **Real-time Updates:** No WebSocket streaming (results only available after completion)
- **Authentication:** No user authentication (public access)
- **Analysis History:** No historical analysis comparison
- **Export:** No PDF or report export functionality

## Known Issues

- IDE warnings about classpath (non-blocking, Docker build works correctly)
- Explore.tsx unused variable (file not used in current flow)
- Some repositories may have empty results if LLM analysis fails

## Future Improvements

- Increase file analysis limit when moving to paid OpenRouter tier
- Add WebSocket support for real-time analysis streaming
- Implement user authentication and authorization
- Add analysis history and comparison features
- Implement PDF report export
- Add more LLM providers (Claude, GPT-4)
- Improve error recovery and user feedback
