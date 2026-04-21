package com.codereview.repository;

import com.codereview.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RepositoryRepository extends JpaRepository<Repository, Long> {
    Optional<Repository> findByOwnerAndName(String owner, String name);
    Optional<Repository> findByUrl(String url);
}
