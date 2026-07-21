package com.portfolio.app.controller;

import com.portfolio.app.model.Handmade;
import com.portfolio.app.repository.HandmadeRepository;
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
@RequestMapping("/admin/handmade")
public class AdminHandmadeController {

    private final HandmadeRepository handmadeRepository;
    private final FileStorageService fileStorageService;

    public AdminHandmadeController(HandmadeRepository handmadeRepository, FileStorageService fileStorageService) {
        this.handmadeRepository = handmadeRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("handmadeItems", handmadeRepository.findAllByOrderBySortOrderAscTitleAsc());
        return "admin/handmade-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("handmade", new Handmade());
        return "admin/handmade-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Handmade handmade = handmadeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Handmade item not found"));
        model.addAttribute("handmade", handmade);
        return "admin/handmade-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("handmade") Handmade handmade, BindingResult result,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "admin/handmade-form";
        }
        // An uploaded file, when present, takes precedence over the pasted image URL.
        String uploadedUrl = fileStorageService.store(imageFile);
        if (uploadedUrl != null) {
            handmade.setImageUrl(uploadedUrl);
        }
        handmadeRepository.save(handmade);
        return "redirect:/admin/handmade";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        handmadeRepository.deleteById(id);
        return "redirect:/admin/handmade";
    }
}
