package com.portfolio.app.repository;

import com.portfolio.app.model.Handmade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HandmadeRepository extends JpaRepository<Handmade, Long> {
    List<Handmade> findAllByOrderBySortOrderAscTitleAsc();
}
