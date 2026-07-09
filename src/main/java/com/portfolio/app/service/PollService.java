package com.portfolio.app.service;

import com.portfolio.app.dto.PollResultsResponse;
import com.portfolio.app.exception.AlreadyVotedException;
import com.portfolio.app.model.PollResponse;
import com.portfolio.app.model.WeeklyPoll;
import com.portfolio.app.repository.PollResponseRepository;
import com.portfolio.app.repository.WeeklyPollRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PollService {

    private final WeeklyPollRepository pollRepository;
    private final PollResponseRepository responseRepository;

    public PollService(WeeklyPollRepository pollRepository, PollResponseRepository responseRepository) {
        this.pollRepository = pollRepository;
        this.responseRepository = responseRepository;
    }

    public Optional<WeeklyPoll> getActivePoll() {
        return pollRepository.findByActiveTrue();
    }

    public boolean hasVoted(Long pollId, String ipAddress) {
        return responseRepository.existsByPollIdAndIpAddress(pollId, ipAddress);
    }

    public PollResultsResponse getResults(WeeklyPoll poll) {
        long countA = responseRepository.countByPollIdAndSelectedOption(poll.getId(), "A");
        long countB = responseRepository.countByPollIdAndSelectedOption(poll.getId(), "B");
        long total = countA + countB;
        double percentA = total == 0 ? 0.0 : Math.round(countA * 1000.0 / total) / 10.0;
        double percentB = total == 0 ? 0.0 : Math.round(countB * 1000.0 / total) / 10.0;
        return new PollResultsResponse(poll.getOptionAText(), poll.getOptionBText(),
                countA, countB, percentA, percentB, total);
    }

    /**
     * Records a vote. Throws AlreadyVotedException (carrying current results) if this
     * IP has already voted on this poll, either because we already know that, or
     * because a concurrent request slipped past the check and hit the DB's unique
     * constraint on (poll_id, ip_address).
     */
    @Transactional
    public PollResultsResponse submitVote(Long pollId, String option, String ipAddress) {
        WeeklyPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        if (!poll.isActive()) {
            throw new IllegalStateException("This poll is no longer active");
        }
        if (!"A".equals(option) && !"B".equals(option)) {
            throw new IllegalArgumentException("Invalid option");
        }
        if (responseRepository.existsByPollIdAndIpAddress(pollId, ipAddress)) {
            throw new AlreadyVotedException(getResults(poll));
        }

        PollResponse response = new PollResponse();
        response.setPollId(pollId);
        response.setIpAddress(ipAddress);
        response.setSelectedOption(option);

        try {
            responseRepository.save(response);
        } catch (DataIntegrityViolationException e) {
            // Two near-simultaneous requests from the same IP raced past the exists() check;
            // the DB's unique constraint caught it. Treat as "already voted".
            throw new AlreadyVotedException(getResults(poll));
        }

        return getResults(poll);
    }
}
