package com.codereview.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GitService {

    private static final Set<String> EXCLUDED_EXTENSIONS = new HashSet<>(Set.of(
            // Model files
            ".pt", ".pth", ".h5", ".pkl", ".onnx", ".bin", ".safetensors", ".gguf", ".ckpt",
            // Video files
            ".mp4", ".avi", ".mov", ".mkv", ".flv", ".wmv", ".webm",
            // Audio files
            ".mp3", ".wav", ".flac", ".aac", ".ogg", ".m4a",
            // Large images (will check size separately)
            ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".tiff", ".webp",
            // Archives
            ".zip", ".tar", ".gz", ".rar", ".7z", ".bz2",
            // Binaries
            ".exe", ".dll", ".so", ".dylib", ".app", ".deb", ".rpm",
            // Data files
            ".csv", ".json", ".xml", ".yaml", ".yml", ".parquet", ".feather",
            // Database files
            ".db", ".sqlite", ".mdb",
            // Compiled files
            ".class", ".pyc", ".pyo", ".jar", ".war", ".ear",
            // Other heavy files
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"
    ));

    private static final Set<String> EXCLUDED_DIRECTORIES = new HashSet<>(Set.of(
            "node_modules",
            ".git",
            "__pycache__",
            "venv",
            "env",
            ".venv",
            "dist",
            "build",
            "target",
            "bin",
            "obj",
            ".next",
            ".nuxt",
            "out",
            ".cache",
            "models",
            "data",
            "datasets",
            "checkpoints",
            "logs",
            "temp",
            "tmp"
    ));

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_IMAGE_SIZE = 1 * 1024 * 1024; // 1MB for images

    /**
     * Clone a repository and return paths of code files for analysis
     */
    public List<Path> cloneRepository(String repoUrl, String targetDir) throws GitAPIException, IOException {
        File targetDirFile = new File(targetDir);
        
        // Clone the repository
        try (Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(targetDirFile)
                .setDepth(1) // Shallow clone to save space
                .call()) {
            
            // Get all files that should be analyzed
            return collectCodeFiles(targetDirFile.toPath());
        } finally {
            // Cleanup: delete the cloned repository after analysis
            deleteDirectory(targetDirFile);
        }
    }

    /**
     * Collect all code files from the repository, filtering out heavy files
     */
    private List<Path> collectCodeFiles(Path rootPath) throws IOException {
        List<Path> codeFiles = new ArrayList<>();
        
        Files.walk(rootPath)
                .filter(path -> !Files.isDirectory(path))
                .filter(this::shouldIncludeFile)
                .forEach(codeFiles::add);
        
        return codeFiles;
    }

    /**
     * Determine if a file should be included in analysis
     */
    private boolean shouldIncludeFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        String extension = getFileExtension(fileName);
        
        // Check if extension is excluded
        if (EXCLUDED_EXTENSIONS.contains(extension)) {
            return false;
        }
        
        // Check if file is in excluded directory
        Path relativePath = path.subpath(0, path.getNameCount());
        for (Path part : relativePath) {
            if (EXCLUDED_DIRECTORIES.contains(part.toString())) {
                return false;
            }
        }
        
        // Check file size
        try {
            long fileSize = Files.size(path);
            
            // Exclude large files
            if (fileSize > MAX_FILE_SIZE) {
                return false;
            }
            
            // Exclude large images specifically
            if (isImageFile(extension) && fileSize > MAX_IMAGE_SIZE) {
                return false;
            }
            
            // Exclude binary files
            if (isBinaryFile(path)) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        
        return true;
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : "";
    }

    /**
     * Check if file is an image
     */
    private boolean isImageFile(String extension) {
        return extension.equals(".png") || extension.equals(".jpg") || 
               extension.equals(".jpeg") || extension.equals(".gif") ||
               extension.equals(".bmp") || extension.equals(".webp");
    }

    /**
     * Check if file is binary by reading first bytes
     */
    private boolean isBinaryFile(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            if (bytes.length == 0) return false;
            
            // Check for null bytes (indicator of binary)
            for (byte b : bytes) {
                if (b == 0) return true;
            }
            
            // Check if high percentage of non-printable characters
            int nonPrintable = 0;
            int checkLimit = Math.min(bytes.length, 512);
            for (int i = 0; i < checkLimit; i++) {
                byte b = bytes[i];
                if (b < 32 && b != 9 && b != 10 && b != 13) {
                    nonPrintable++;
                }
            }
            
            return (nonPrintable * 100 / checkLimit) > 30;
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * Delete directory recursively
     */
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    /**
     * Get the latest commit hash for a repository
     */
    public String getLatestCommitHash(String repoUrl, String targetDir) throws GitAPIException, IOException {
        File targetDirFile = new File(targetDir);
        
        try (Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(targetDirFile)
                .setDepth(1)
                .call()) {
            
            Ref head = git.getRepository().findRef("HEAD");
            return head.getObjectId().getName();
        } finally {
            deleteDirectory(targetDirFile);
        }
    }
}
