package com.portfolio.app.repository;

import com.portfolio.app.model.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    List<Hobby> findAllByOrderBySortOrderAscTitleAsc();
}
