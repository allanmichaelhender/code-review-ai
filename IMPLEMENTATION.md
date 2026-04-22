# API Call Implementation Summary

## Overview
Simple Python Flask backend for testing OpenRouter API calls with various AI models.

## Architecture

### Backend Framework
- **Flask** - Lightweight WSGI web application framework
- Runs on port 5000 in debug mode

### API Integration
- **OpenRouter API** - Unified interface for multiple AI models
- Endpoint: `https://openrouter.ai/api/v1/chat/completions`
- Authentication: Bearer token via `OPEN_ROUTER_API_KEY` environment variable

## Implementation Details

### Environment Configuration
```python
OPEN_ROUTER_API_KEY = os.getenv("OPEN_ROUTER_API_KEY")
OPEN_ROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions"
```

### Request Headers
```python
headers = {
    "Authorization": f"Bearer {OPEN_ROUTER_API_KEY}",
    "Content-Type": "application/json",
    "HTTP-Referer": "http://localhost:5000",
    "X-Title": "DeepSeek Test App",
}
```

### Request Payload
```python
payload = {
    "model": model,  # e.g., "nvidia/nemotron-3-super-120b-a12b:free"
    "messages": [{"role": "user", "content": prompt}],
    "temperature": 0.7,
}
```

### Endpoints

#### POST /test
Tests the OpenRouter API with a given prompt.

**Request Body:**
```json
{
  "prompt": "Your question here",
  "model": "optional-model-name"
}
```

**Response:** Returns the full OpenRouter API response including:
- `choices` - Model responses with content, reasoning, and metadata
- `model` - Actual model used
- `usage` - Token counts and cost information
- `provider` - Model provider (e.g., Nvidia, Liquid, Z.AI)

#### GET /health
Health check endpoint.

**Response:**
```json
{
  "status": "healthy",
  "api_key_configured": true
}
```

### Error Handling
- Captures HTTP errors and returns detailed error messages
- Returns response body for debugging API issues
- Handles missing API key gracefully

## Current Model
- Default: `nvidia/nemotron-3-super-120b-a12b:free`
- Can be overridden per request via the `model` parameter

## Usage Example
```bash
curl -X POST http://localhost:5000/test \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Hello"}'
```

## Dependencies
- flask==3.0.0
- requests==2.31.0
- python-dotenv==1.0.0
