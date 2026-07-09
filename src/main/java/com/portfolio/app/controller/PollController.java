package com.portfolio.app.controller;

import com.portfolio.app.dto.PollResultsResponse;
import com.portfolio.app.exception.AlreadyVotedException;
import com.portfolio.app.service.PollService;
import com.portfolio.app.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/poll")
public class PollController {

    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @PostMapping("/vote")
    public ResponseEntity<PollResultsResponse> vote(@RequestParam Long pollId,
                                                      @RequestParam String option,
                                                      HttpServletRequest request) {
        String ip = IpUtil.getClientIp(request);
        try {
            PollResultsResponse results = pollService.submitVote(pollId, option, ip);
            return ResponseEntity.ok(results);
        } catch (AlreadyVotedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getResults());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
