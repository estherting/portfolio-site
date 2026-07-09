package com.portfolio.app.controller;

import com.portfolio.app.model.BlogPost;
import com.portfolio.app.model.WeeklyPoll;
import com.portfolio.app.repository.*;
import com.portfolio.app.service.PollService;
import com.portfolio.app.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Controller
public class PublicController {

    private final BlogPostRepository blogPostRepository;
    private final HobbyRepository hobbyRepository;
    private final ResumeProfileRepository resumeProfileRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final PollService pollService;

    public PublicController(BlogPostRepository blogPostRepository, HobbyRepository hobbyRepository,
                             ResumeProfileRepository resumeProfileRepository, ExperienceRepository experienceRepository,
                             EducationRepository educationRepository, SkillRepository skillRepository,
                             PollService pollService) {
        this.blogPostRepository = blogPostRepository;
        this.hobbyRepository = hobbyRepository;
        this.resumeProfileRepository = resumeProfileRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.skillRepository = skillRepository;
        this.pollService = pollService;
    }

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        Optional<WeeklyPoll> activePoll = pollService.getActivePoll();
        if (activePoll.isPresent()) {
            WeeklyPoll poll = activePoll.get();
            String ip = IpUtil.getClientIp(request);
            boolean hasVoted = pollService.hasVoted(poll.getId(), ip);
            model.addAttribute("poll", poll);
            model.addAttribute("hasVoted", hasVoted);
            model.addAttribute("results", pollService.getResults(poll));
        }
        return "index";
    }

    @GetMapping("/resume")
    public String resume(Model model) {
        resumeProfileRepository.findById(1L).ifPresent(p -> model.addAttribute("profile", p));
        model.addAttribute("experiences", experienceRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("educations", educationRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("skills", skillRepository.findAllByOrderBySortOrderAsc());
        return "resume";
    }

    @GetMapping("/hobbies")
    public String hobbies(Model model) {
        model.addAttribute("hobbies", hobbyRepository.findAllByOrderBySortOrderAscTitleAsc());
        return "hobbies";
    }

    @GetMapping("/blog")
    public String blogList(Model model) {
        model.addAttribute("posts", blogPostRepository.findByPublishedTrueOrderByCreatedAtDesc());
        return "blog-list";
    }

    @GetMapping("/blog/{slug}")
    public String blogPost(@PathVariable String slug, Model model) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .filter(BlogPost::isPublished)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        model.addAttribute("post", post);
        return "blog-post";
    }
}
