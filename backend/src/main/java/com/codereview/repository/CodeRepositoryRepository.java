package com.codereview.repository;

import com.codereview.model.CodeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CodeRepositoryRepository extends JpaRepository<CodeRepository, Long> {
    Optional<CodeRepository> findByOwnerAndName(String owner, String name);
    Optional<CodeRepository> findByUrl(String url);
}
