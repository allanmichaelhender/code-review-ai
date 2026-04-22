package com.codereview.repository;

import com.codereview.model.Analysis;
import com.codereview.model.CodeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Optional<Analysis> findTopByRepositoryOrderByAnalyzedAtDesc(CodeRepository repository);
    Optional<Analysis> findByRepositoryIdAndCommitHash(Long repositoryId, String commitHash);
    java.util.List<Analysis> findByRepositoryId(Long repositoryId);
}
