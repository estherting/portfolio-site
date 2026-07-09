package com.portfolio.app.repository;

import com.portfolio.app.model.WeeklyPoll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WeeklyPollRepository extends JpaRepository<WeeklyPoll, Long> {
    Optional<WeeklyPoll> findByActiveTrue();
    List<WeeklyPoll> findAllByOrderByCreatedAtDesc();
}
