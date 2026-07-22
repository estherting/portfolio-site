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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                       @RequestParam(value = "detailImageFiles", required = false) MultipartFile[] detailImageFiles,
                       @RequestParam(value = "removeDetailImages", required = false) List<String> removeDetailImages) {
        if (result.hasErrors()) {
            return "admin/handmade-form";
        }

        if (handmade.getSlug() == null || handmade.getSlug().isBlank()) {
            handmade.setSlug(slugify(handmade.getTitle()));
        } else {
            handmade.setSlug(slugify(handmade.getSlug()));
        }
        // Ensure slug uniqueness (append a suffix if a different item already owns it).
        handmadeRepository.findBySlug(handmade.getSlug()).ifPresent(existing -> {
            if (handmade.getId() == null || !existing.getId().equals(handmade.getId())) {
                handmade.setSlug(handmade.getSlug() + "-" + System.currentTimeMillis() % 10000);
            }
        });

        // An uploaded cover file, when present, takes precedence over the pasted image URL.
        String uploadedUrl = fileStorageService.store(imageFile);
        if (uploadedUrl != null) {
            handmade.setImageUrl(uploadedUrl);
        }

        // Detail-image gallery: start from what's already saved (the bound form object doesn't
        // carry the collection), drop any the user unchecked, then append newly uploaded files.
        List<String> images = new ArrayList<>();
        if (handmade.getId() != null) {
            handmadeRepository.findById(handmade.getId())
                    .ifPresent(existing -> images.addAll(existing.getDetailImages()));
        }
        if (removeDetailImages != null) {
            images.removeAll(removeDetailImages);
        }
        if (detailImageFiles != null) {
            for (MultipartFile file : detailImageFiles) {
                String url = fileStorageService.store(file);
                if (url != null) {
                    images.add(url);
                }
            }
        }
        handmade.setDetailImages(images);

        handmadeRepository.save(handmade);
        return "redirect:/admin/handmade";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        handmadeRepository.deleteById(id);
        return "redirect:/admin/handmade";
    }

    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w\\s-]").matcher(normalized).replaceAll("");
        slug = slug.trim().toLowerCase().replaceAll("[\\s_-]+", "-");
        return slug.replaceAll("^-|-$", "");
    }
}
