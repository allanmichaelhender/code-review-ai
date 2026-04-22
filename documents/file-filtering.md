# File Filtering System

## Purpose

The file filtering system reduces the amount of data sent to the LLM by excluding non-code files, large binaries, and irrelevant data. This:
- Reduces API costs
- Improves analysis speed
- Prevents token limit errors
- Focuses analysis on actual code

## File Type Exclusions

**Model Files:**
- Extensions: .pt, .pth, .h5, .pkl, .onnx, .bin, .safetensors, .gguf, .ckpt

**Video Files:**
- Extensions: .mp4, .avi, .mov, .mkv, .flv, .wmv, .webm

**Audio Files:**
- Extensions: .mp3, .wav, .flac, .aac, .ogg, .m4a

**Large Files:**
- Size limit: > 5MB
- Images: > 1MB

**Archives & Binaries:**
- Extensions: .zip, .tar, .gz, .rar, .7z, .bz2, .exe, .dll, .so, .dylib

**Data Files:**
- Extensions: .csv, .json, .xml, .yaml, .yml, .parquet, .feather, .db, .sqlite, .mdb

**Compiled Files:**
- Extensions: .class, .pyc, .pyo, .jar, .war, .ear

## Directory Exclusions

Common directories excluded:
- node_modules
- .git
- __pycache__
- venv, env
- dist, build
- target, bin, obj
- .next, .nuxt
- out, .cache
- models, data, datasets
- checkpoints, logs
- temp, tmp

## Implementation

Located in `GitService.java`:
```java
private boolean shouldExcludeFile(Path filePath) {
    // Check file extension
    // Check file size
    // Check directory exclusion
    // Check binary content
}
```
