package com.portfolio.app.repository;

import com.portfolio.app.model.PollResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollResponseRepository extends JpaRepository<PollResponse, Long> {
    boolean existsByPollIdAndIpAddress(Long pollId, String ipAddress);
    long countByPollIdAndSelectedOption(Long pollId, String selectedOption);
    void deleteByPollId(Long pollId);
}
