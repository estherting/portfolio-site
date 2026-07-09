package com.portfolio.app.controller;

import com.portfolio.app.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    private final BlogPostRepository blogPostRepository;
    private final HobbyRepository hobbyRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final WeeklyPollRepository weeklyPollRepository;

    public AdminDashboardController(BlogPostRepository blogPostRepository, HobbyRepository hobbyRepository,
                                     ExperienceRepository experienceRepository, EducationRepository educationRepository,
                                     SkillRepository skillRepository, ProjectRepository projectRepository,
                                     WeeklyPollRepository weeklyPollRepository) {
        this.blogPostRepository = blogPostRepository;
        this.hobbyRepository = hobbyRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.weeklyPollRepository = weeklyPollRepository;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("postCount", blogPostRepository.count());
        model.addAttribute("hobbyCount", hobbyRepository.count());
        model.addAttribute("projectCount", projectRepository.count());
        model.addAttribute("experienceCount", experienceRepository.count());
        model.addAttribute("educationCount", educationRepository.count());
        model.addAttribute("skillCount", skillRepository.count());
        model.addAttribute("pollCount", weeklyPollRepository.count());
        return "admin/dashboard";
    }
}
