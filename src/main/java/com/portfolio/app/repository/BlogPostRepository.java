package com.portfolio.app.repository;

import com.portfolio.app.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Optional<BlogPost> findBySlug(String slug);
    List<BlogPost> findByPublishedTrueOrderByCreatedAtDesc();
    List<BlogPost> findAllByOrderByCreatedAtDesc();
    boolean existsBySlug(String slug);
}
