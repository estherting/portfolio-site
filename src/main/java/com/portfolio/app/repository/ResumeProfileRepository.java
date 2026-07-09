package com.portfolio.app.repository;

import com.portfolio.app.model.ResumeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeProfileRepository extends JpaRepository<ResumeProfile, Long> {
}
