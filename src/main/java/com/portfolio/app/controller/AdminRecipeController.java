package com.portfolio.app.controller;

import com.portfolio.app.model.Recipe;
import com.portfolio.app.repository.RecipeRepository;
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
@RequestMapping("/admin/recipes")
public class AdminRecipeController {

    private final RecipeRepository recipeRepository;
    private final FileStorageService fileStorageService;

    public AdminRecipeController(RecipeRepository recipeRepository, FileStorageService fileStorageService) {
        this.recipeRepository = recipeRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("recipes", recipeRepository.findAllByOrderBySortOrderAscTitleAsc());
        return "admin/recipe-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        model.addAttribute("topIngredientsText", "");
        model.addAttribute("ingredientsText", "");
        model.addAttribute("stepsText", "");
        return "admin/recipe-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        model.addAttribute("recipe", recipe);
        model.addAttribute("topIngredientsText", String.join("\n", recipe.getTopIngredients()));
        model.addAttribute("ingredientsText", String.join("\n", recipe.getIngredients()));
        model.addAttribute("stepsText", String.join("\n", recipe.getSteps()));
        return "admin/recipe-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("recipe") Recipe recipe, BindingResult result,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                       @RequestParam(value = "topIngredientsText", required = false) String topIngredientsText,
                       @RequestParam(value = "ingredientsText", required = false) String ingredientsText,
                       @RequestParam(value = "stepsText", required = false) String stepsText,
                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("topIngredientsText", topIngredientsText);
            model.addAttribute("ingredientsText", ingredientsText);
            model.addAttribute("stepsText", stepsText);
            return "admin/recipe-form";
        }

        // The card and the "What's in my fridge?" filter both read from the top-five list;
        // keep at most five so a card never shows more than the promised five ingredients.
        List<String> top = parseLines(topIngredientsText);
        if (top.size() > 5) {
            top = new ArrayList<>(top.subList(0, 5));
        }
        recipe.setTopIngredients(top);
        recipe.setIngredients(parseLines(ingredientsText));
        recipe.setSteps(parseLines(stepsText));

        if (recipe.getSlug() == null || recipe.getSlug().isBlank()) {
            recipe.setSlug(slugify(recipe.getTitle()));
        } else {
            recipe.setSlug(slugify(recipe.getSlug()));
        }
        // Ensure slug uniqueness (append a suffix if a different recipe already owns it).
        recipeRepository.findBySlug(recipe.getSlug()).ifPresent(existing -> {
            if (recipe.getId() == null || !existing.getId().equals(recipe.getId())) {
                recipe.setSlug(recipe.getSlug() + "-" + System.currentTimeMillis() % 10000);
            }
        });

        // An uploaded file, when present, takes precedence over the pasted image URL.
        String uploadedUrl = fileStorageService.store(imageFile);
        if (uploadedUrl != null) {
            recipe.setImageUrl(uploadedUrl);
        }

        recipeRepository.save(recipe);
        return "redirect:/admin/recipes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        recipeRepository.deleteById(id);
        return "redirect:/admin/recipes";
    }

    /** Splits a textarea value into a trimmed, blank-free list — one entry per line. */
    private List<String> parseLines(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null) {
            return lines;
        }
        for (String line : text.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                lines.add(trimmed);
            }
        }
        return lines;
    }

    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w\\s-]").matcher(normalized).replaceAll("");
        slug = slug.trim().toLowerCase().replaceAll("[\\s_-]+", "-");
        return slug.replaceAll("^-|-$", "");
    }
}
