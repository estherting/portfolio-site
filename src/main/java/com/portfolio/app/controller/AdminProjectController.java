package com.portfolio.app.controller;

import com.portfolio.app.model.Project;
import com.portfolio.app.repository.ProjectRepository;
import com.portfolio.app.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/admin/projects")
public class AdminProjectController {

    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;

    public AdminProjectController(ProjectRepository projectRepository, FileStorageService fileStorageService) {
        this.projectRepository = projectRepository;
        this.fileStorageService = fileStorageService;
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
    public String save(@Valid @ModelAttribute("project") Project project, BindingResult result,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "admin/project-form";
        }
        // An uploaded file, when present, takes precedence over the pasted image URL.
        String uploadedUrl = fileStorageService.store(imageFile);
        if (uploadedUrl != null) {
            project.setImageUrl(uploadedUrl);
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
