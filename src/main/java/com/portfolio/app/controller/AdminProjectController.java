package com.portfolio.app.controller;

import com.portfolio.app.model.Project;
import com.portfolio.app.repository.ProjectRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/admin/projects")
public class AdminProjectController {

    private final ProjectRepository projectRepository;

    public AdminProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("projects", projectRepository.findAllByOrderBySortOrderAscTitleAsc());
        return "admin/project-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("project", new Project());
        return "admin/project-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        model.addAttribute("project", project);
        return "admin/project-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("project") Project project, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/project-form";
        }
        projectRepository.save(project);
        return "redirect:/admin/projects";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        projectRepository.deleteById(id);
        return "redirect:/admin/projects";
    }
}
