package com.portfolio.app.repository;

import com.portfolio.app.model.Handmade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HandmadeRepository extends JpaRepository<Handmade, Long> {
    Optional<Handmade> findBySlug(String slug);
    List<Handmade> findAllByOrderBySortOrderAscTitleAsc();
}
