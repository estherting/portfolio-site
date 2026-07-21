package com.portfolio.app.controller;

import com.portfolio.app.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    private final RecipeRepository recipeRepository;
    private final HandmadeRepository handmadeRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final WeeklyPollRepository weeklyPollRepository;

    public AdminDashboardController(RecipeRepository recipeRepository, HandmadeRepository handmadeRepository,
                                     ExperienceRepository experienceRepository, EducationRepository educationRepository,
                                     SkillRepository skillRepository, ProjectRepository projectRepository,
                                     WeeklyPollRepository weeklyPollRepository) {
        this.recipeRepository = recipeRepository;
        this.handmadeRepository = handmadeRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.weeklyPollRepository = weeklyPollRepository;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("recipeCount", recipeRepository.count());
        model.addAttribute("handmadeCount", handmadeRepository.count());
        model.addAttribute("projectCount", projectRepository.count());
        model.addAttribute("experienceCount", experienceRepository.count());
        model.addAttribute("educationCount", educationRepository.count());
        model.addAttribute("skillCount", skillRepository.count());
        model.addAttribute("pollCount", weeklyPollRepository.count());
        return "admin/dashboard";
    }
}
