package com.portfolio.app.controller;

import com.portfolio.app.model.Hobby;
import com.portfolio.app.repository.HobbyRepository;
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
@RequestMapping("/admin/hobbies")
public class AdminHobbyController {

    private final HobbyRepository hobbyRepository;
    private final FileStorageService fileStorageService;

    public AdminHobbyController(HobbyRepository hobbyRepository, FileStorageService fileStorageService) {
        this.hobbyRepository = hobbyRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("hobbies", hobbyRepository.findAllByOrderBySortOrderAscTitleAsc());
        return "admin/hobby-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("hobby", new Hobby());
        return "admin/hobby-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Hobby hobby = hobbyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hobby not found"));
        model.addAttribute("hobby", hobby);
        return "admin/hobby-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("hobby") Hobby hobby, BindingResult result,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "admin/hobby-form";
        }
        // An uploaded file, when present, takes precedence over the pasted image URL.
        String uploadedUrl = fileStorageService.store(imageFile);
        if (uploadedUrl != null) {
            hobby.setImageUrl(uploadedUrl);
        }
        hobbyRepository.save(hobby);
        return "redirect:/admin/hobbies";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        hobbyRepository.deleteById(id);
        return "redirect:/admin/hobbies";
    }
}
