package com.portfolio.app.controller;

import com.portfolio.app.model.*;
import com.portfolio.app.repository.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/admin/resume")
public class AdminResumeController {

    private final ResumeProfileRepository resumeProfileRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;

    public AdminResumeController(ResumeProfileRepository resumeProfileRepository, ExperienceRepository experienceRepository,
                                  EducationRepository educationRepository, SkillRepository skillRepository) {
        this.resumeProfileRepository = resumeProfileRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.skillRepository = skillRepository;
    }

    @GetMapping
    public String overview(Model model) {
        model.addAttribute("profile", resumeProfileRepository.findById(1L).orElseGet(ResumeProfile::new));
        model.addAttribute("experiences", experienceRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("educations", educationRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("skills", skillRepository.findAllByOrderBySortOrderAsc());
        return "admin/resume-edit";
    }

    // ---- Profile ----
    @PostMapping("/profile/save")
    public String saveProfile(@ModelAttribute ResumeProfile profile) {
        profile.setId(1L);
        resumeProfileRepository.save(profile);
        return "redirect:/admin/resume";
    }

    // ---- Experience ----
    @GetMapping("/experience/new")
    public String newExperience(Model model) {
        model.addAttribute("experience", new Experience());
        return "admin/experience-form";
    }

    @GetMapping("/experience/{id}/edit")
    public String editExperience(@PathVariable Long id, Model model) {
        model.addAttribute("experience", experienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return "admin/experience-form";
    }

    @PostMapping("/experience/save")
    public String saveExperience(@Valid @ModelAttribute("experience") Experience experience, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/experience-form";
        }
        experienceRepository.save(experience);
        return "redirect:/admin/resume";
    }

    @PostMapping("/experience/{id}/delete")
    public String deleteExperience(@PathVariable Long id) {
        experienceRepository.deleteById(id);
        return "redirect:/admin/resume";
    }

    // ---- Education ----
    @GetMapping("/education/new")
    public String newEducation(Model model) {
        model.addAttribute("education", new Education());
        return "admin/education-form";
    }

    @GetMapping("/education/{id}/edit")
    public String editEducation(@PathVariable Long id, Model model) {
        model.addAttribute("education", educationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return "admin/education-form";
    }

    @PostMapping("/education/save")
    public String saveEducation(@Valid @ModelAttribute("education") Education education, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/education-form";
        }
        educationRepository.save(education);
        return "redirect:/admin/resume";
    }

    @PostMapping("/education/{id}/delete")
    public String deleteEducation(@PathVariable Long id) {
        educationRepository.deleteById(id);
        return "redirect:/admin/resume";
    }

    // ---- Skill ----
    @GetMapping("/skill/new")
    public String newSkill(Model model) {
        model.addAttribute("skill", new Skill());
        return "admin/skill-form";
    }

    @GetMapping("/skill/{id}/edit")
    public String editSkill(@PathVariable Long id, Model model) {
        model.addAttribute("skill", skillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return "admin/skill-form";
    }

    @PostMapping("/skill/save")
    public String saveSkill(@Valid @ModelAttribute("skill") Skill skill, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/skill-form";
        }
        skillRepository.save(skill);
        return "redirect:/admin/resume";
    }

    @PostMapping("/skill/{id}/delete")
    public String deleteSkill(@PathVariable Long id) {
        skillRepository.deleteById(id);
        return "redirect:/admin/resume";
    }
}
