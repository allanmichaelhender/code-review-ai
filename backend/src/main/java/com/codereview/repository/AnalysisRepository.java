package com.codereview.repository;

import com.codereview.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Optional<Analysis> findByRepositoryIdAndCommitHash(Long repositoryId, String commitHash);
}
