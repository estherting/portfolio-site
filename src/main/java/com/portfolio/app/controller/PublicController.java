package com.portfolio.app.controller;

import com.portfolio.app.model.Recipe;
import com.portfolio.app.model.WeeklyPoll;
import com.portfolio.app.repository.*;
import com.portfolio.app.service.PollService;
import com.portfolio.app.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

@Controller
public class PublicController {

    private final RecipeRepository recipeRepository;
    private final HandmadeRepository handmadeRepository;
    private final ResumeProfileRepository resumeProfileRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final PollService pollService;

    public PublicController(RecipeRepository recipeRepository, HandmadeRepository handmadeRepository,
                             ResumeProfileRepository resumeProfileRepository, ExperienceRepository experienceRepository,
                             EducationRepository educationRepository, SkillRepository skillRepository,
                             ProjectRepository projectRepository, PollService pollService) {
        this.recipeRepository = recipeRepository;
        this.handmadeRepository = handmadeRepository;
        this.resumeProfileRepository = resumeProfileRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.pollService = pollService;
    }

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        Optional<WeeklyPoll> activePoll = pollService.getActivePoll();
        if (activePoll.isPresent()) {
            WeeklyPoll poll = activePoll.get();
            String ip = IpUtil.getClientIp(request);
            boolean hasVoted = pollService.hasVoted(poll.getId(), ip);
            model.addAttribute("poll", poll);
            model.addAttribute("hasVoted", hasVoted);
            model.addAttribute("results", pollService.getResults(poll));
        }
        return "index";
    }

    @GetMapping("/resume")
    public String resume(Model model) {
        resumeProfileRepository.findById(1L).ifPresent(p -> model.addAttribute("profile", p));
        model.addAttribute("experiences", experienceRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("educations", educationRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("skills", skillRepository.findAllByOrderBySortOrderAsc());
        return "resume";
    }

    @GetMapping("/handmade")
    public String handmade(Model model) {
        model.addAttribute("handmadeItems", handmadeRepository.findAllByOrderBySortOrderAscTitleAsc());
        return "handmade";
    }

    // Preserve old bookmarks and search-engine links from before the Hobbies → Handmade rename.
    @GetMapping("/hobbies")
    public String hobbiesRedirect() {
        return "redirect:/handmade";
    }

    @GetMapping("/projects")
    public String projects(Model model) {
        model.addAttribute("projects", projectRepository.findAllByOrderBySortOrderAscTitleAsc());
        return "projects";
    }

    @GetMapping("/recipes")
    public String recipeList(Model model) {
        List<Recipe> recipes = recipeRepository.findAllByOrderBySortOrderAscTitleAsc();
        model.addAttribute("recipes", recipes);
        // The "What's in my fridge?" menu offers the union of every recipe's top-five
        // ingredients (case-insensitive, alphabetised) so a selection always matches a card.
        TreeSet<String> fridge = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Recipe recipe : recipes) {
            fridge.addAll(recipe.getTopIngredients());
        }
        model.addAttribute("fridgeIngredients", fridge);
        return "recipes";
    }

    @GetMapping("/recipes/{slug}")
    public String recipe(@PathVariable String slug, Model model) {
        Recipe recipe = recipeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        model.addAttribute("recipe", recipe);
        return "recipe";
    }
}
