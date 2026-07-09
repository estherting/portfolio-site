package com.portfolio.app.controller;

import com.portfolio.app.model.Hobby;
import com.portfolio.app.repository.HobbyRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/admin/hobbies")
public class AdminHobbyController {

    private final HobbyRepository hobbyRepository;

    public AdminHobbyController(HobbyRepository hobbyRepository) {
        this.hobbyRepository = hobbyRepository;
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
    public String save(@Valid @ModelAttribute("hobby") Hobby hobby, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/hobby-form";
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
