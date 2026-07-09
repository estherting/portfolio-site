package com.portfolio.app.controller;

import com.portfolio.app.dto.PollResultsResponse;
import com.portfolio.app.model.WeeklyPoll;
import com.portfolio.app.repository.PollResponseRepository;
import com.portfolio.app.repository.WeeklyPollRepository;
import com.portfolio.app.service.PollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/polls")
public class AdminPollController {

    private final WeeklyPollRepository pollRepository;
    private final PollResponseRepository responseRepository;
    private final PollService pollService;

    public AdminPollController(WeeklyPollRepository pollRepository, PollResponseRepository responseRepository,
                                PollService pollService) {
        this.pollRepository = pollRepository;
        this.responseRepository = responseRepository;
        this.pollService = pollService;
    }

    @GetMapping
    public String list(Model model) {
        List<WeeklyPoll> polls = pollRepository.findAllByOrderByCreatedAtDesc();
        Map<Long, PollResultsResponse> resultsByPoll = new HashMap<>();
        for (WeeklyPoll poll : polls) {
            resultsByPoll.put(poll.getId(), pollService.getResults(poll));
        }
        model.addAttribute("polls", polls);
        model.addAttribute("resultsByPoll", resultsByPoll);
        return "admin/poll-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("poll", new WeeklyPoll());
        return "admin/poll-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        WeeklyPoll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found"));
        model.addAttribute("poll", poll);
        return "admin/poll-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("poll") WeeklyPoll poll, BindingResult result,
                        @RequestParam(value = "makeActive", required = false) Boolean makeActive) {
        if (result.hasErrors()) {
            return "admin/poll-form";
        }
        if (Boolean.TRUE.equals(makeActive)) {
            deactivateAll();
            poll.setActive(true);
        } else {
            poll.setActive(false);
        }
        pollRepository.save(poll);
        return "redirect:/admin/polls";
    }

    @PostMapping("/{id}/activate")
    public String activate(@PathVariable Long id) {
        deactivateAll();
        WeeklyPoll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found"));
        poll.setActive(true);
        pollRepository.save(poll);
        return "redirect:/admin/polls";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        responseRepository.deleteByPollId(id);
        pollRepository.deleteById(id);
        return "redirect:/admin/polls";
    }

    private void deactivateAll() {
        List<WeeklyPoll> all = pollRepository.findAll();
        boolean anyChanged = false;
        for (WeeklyPoll p : all) {
            if (p.isActive()) {
                p.setActive(false);
                anyChanged = true;
            }
        }
        if (anyChanged) {
            pollRepository.saveAll(all);
        }
    }
}
