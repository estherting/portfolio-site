package com.portfolio.app.exception;

import com.portfolio.app.dto.PollResultsResponse;

public class AlreadyVotedException extends RuntimeException {

    private final PollResultsResponse results;

    public AlreadyVotedException(PollResultsResponse results) {
        super("This IP address has already voted on this poll.");
        this.results = results;
    }

    public PollResultsResponse getResults() {
        return results;
    }
}
