package com.portfolio.app.controller;

import com.portfolio.app.model.BlogPost;
import com.portfolio.app.repository.BlogPostRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/admin/blog")
public class AdminBlogController {

    private final BlogPostRepository blogPostRepository;

    public AdminBlogController(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", blogPostRepository.findAllByOrderByCreatedAtDesc());
        return "admin/blog-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("post", new BlogPost());
        return "admin/blog-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        model.addAttribute("post", post);
        return "admin/blog-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("post") BlogPost post, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/blog-form";
        }
        if (post.getSlug() == null || post.getSlug().isBlank()) {
            post.setSlug(slugify(post.getTitle()));
        } else {
            post.setSlug(slugify(post.getSlug()));
        }
        // Ensure slug uniqueness (append id-based suffix if a different post already owns it)
        blogPostRepository.findBySlug(post.getSlug()).ifPresent(existing -> {
            if (post.getId() == null || !existing.getId().equals(post.getId())) {
                post.setSlug(post.getSlug() + "-" + System.currentTimeMillis() % 10000);
            }
        });
        blogPostRepository.save(post);
        return "redirect:/admin/blog";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        blogPostRepository.deleteById(id);
        return "redirect:/admin/blog";
    }

    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w\\s-]").matcher(normalized).replaceAll("");
        slug = slug.trim().toLowerCase().replaceAll("[\\s_-]+", "-");
        return slug.replaceAll("^-|-$", "");
    }
}
