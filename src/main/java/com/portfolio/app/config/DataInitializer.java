package com.portfolio.app.config;

import com.portfolio.app.model.*;
import com.portfolio.app.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final HandmadeRepository handmadeRepository;
    private final ResumeProfileRepository resumeProfileRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final WeeklyPollRepository weeklyPollRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${portfolio.admin.username}")
    private String adminUsername;

    @Value("${portfolio.admin.password}")
    private String adminPassword;

    public DataInitializer(UserRepository userRepository, RecipeRepository recipeRepository,
                            HandmadeRepository handmadeRepository, ResumeProfileRepository resumeProfileRepository,
                            ExperienceRepository experienceRepository, EducationRepository educationRepository,
                            SkillRepository skillRepository, WeeklyPollRepository weeklyPollRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.handmadeRepository = handmadeRepository;
        this.resumeProfileRepository = resumeProfileRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.skillRepository = skillRepository;
        this.weeklyPollRepository = weeklyPollRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedResumeProfile();
        seedSampleContent();
        seedSamplePoll();
        backfillHandmadeSlugs();
    }

    /**
     * Handmade items created before the detail-page feature have no slug. Give each one a
     * slug derived from its title so its detail page (/handmade/{slug}) is reachable.
     */
    private void backfillHandmadeSlugs() {
        for (Handmade item : handmadeRepository.findAll()) {
            if (item.getSlug() == null || item.getSlug().isBlank()) {
                String slug = slugify(item.getTitle());
                if (slug.isBlank()) {
                    slug = "item";
                }
                // Avoid colliding with a slug another item already holds.
                String candidate = slug;
                int suffix = 2;
                while (handmadeRepository.findBySlug(candidate)
                        .filter(other -> !other.getId().equals(item.getId())).isPresent()) {
                    candidate = slug + "-" + suffix++;
                }
                item.setSlug(candidate);
                handmadeRepository.save(item);
            }
        }
    }

    private String slugify(String input) {
        if (input == null) {
            return "";
        }
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[^\\w\\s-]", "");
        slug = slug.trim().toLowerCase().replaceAll("[\\s_-]+", "-");
        return slug.replaceAll("^-|-$", "");
    }

    private void seedAdminUser() {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User user = new User();
            user.setUsername(adminUsername);
            user.setPasswordHash(passwordEncoder.encode(adminPassword));
            user.setRole("ADMIN");
            userRepository.save(user);
            System.out.println("======================================================");
            System.out.println(" Created admin account: " + adminUsername);
            System.out.println(" Log in at /admin/login and change the password ASAP");
            System.out.println("======================================================");
        }
    }

    private void seedResumeProfile() {
        if (resumeProfileRepository.count() == 0) {
            resumeProfileRepository.save(new ResumeProfile());
        }
        if (experienceRepository.count() == 0) {
            Experience e = new Experience();
            e.setRole("Your Job Title");
            e.setCompany("Company Name");
            e.setStartDate("2023");
            e.setEndDate("Present");
            e.setDescription("Describe your responsibilities and achievements here.");
            e.setSortOrder(0);
            experienceRepository.save(e);
        }
        if (educationRepository.count() == 0) {
            Education ed = new Education();
            ed.setSchool("Your University");
            ed.setDegree("Your Degree");
            ed.setStartDate("2019");
            ed.setEndDate("2023");
            ed.setDescription("Relevant coursework, honors, or activities.");
            ed.setSortOrder(0);
            educationRepository.save(ed);
        }
        if (skillRepository.count() == 0) {
            String[][] skills = {
                {"Java", "Languages"}, {"Spring Boot", "Frameworks"}, {"SQL", "Tools"}
            };
            int order = 0;
            for (String[] s : skills) {
                Skill skill = new Skill();
                skill.setName(s[0]);
                skill.setCategory(s[1]);
                skill.setSortOrder(order++);
                skillRepository.save(skill);
            }
        }
    }

    private void seedSampleContent() {
        if (handmadeRepository.count() == 0) {
            Handmade h = new Handmade();
            h.setTitle("Pressed Flower Art");
            h.setSlug("pressed-flower-art");
            h.setDescription("Handmade keepsakes from garden flowers, collected and pressed by hand.");
            h.setDetail("Every spring I collect flowers from the garden and press them between the pages "
                    + "of old books. After a few weeks they're paper-thin and keep their colour for years.\n\n"
                    + "Write about your process, the materials you used, and the story behind the piece here. "
                    + "Add as many photos as you like from the editor.");
            h.setSortOrder(0);
            handmadeRepository.save(h);
        }
        if (recipeRepository.count() == 0) {
            seedRecipe("Garden Herb Frittata", "garden-herb-frittata",
                    "A soft, golden frittata that turns whatever greens are in the fridge into brunch.",
                    List.of("Eggs", "Spinach", "Onion", "Parmesan", "Olive oil"),
                    List.of("6 eggs", "2 cups spinach, roughly chopped", "1 small onion, diced",
                            "1/2 cup grated parmesan", "2 tbsp olive oil", "Salt and pepper to taste"),
                    List.of("Heat the olive oil in an oven-safe skillet and soften the onion over medium heat.",
                            "Add the spinach and cook just until wilted.",
                            "Whisk the eggs with salt, pepper, and half the parmesan, then pour into the skillet.",
                            "Scatter the remaining parmesan on top and cook until the edges set.",
                            "Finish under the broiler for 3–4 minutes until puffed and golden. Slice into wedges."),
                    0);
            seedRecipe("Rustic Tomato Basil Soup", "rustic-tomato-basil-soup",
                    "A cozy, slow-simmered tomato soup brightened with a handful of fresh basil.",
                    List.of("Tomato", "Onion", "Garlic", "Basil", "Olive oil"),
                    List.of("2 lbs ripe tomatoes, quartered", "1 onion, sliced", "3 cloves garlic, smashed",
                            "1 large handful fresh basil", "3 tbsp olive oil", "2 cups vegetable stock",
                            "Salt and pepper to taste"),
                    List.of("Warm the olive oil and sweat the onion and garlic until translucent.",
                            "Add the tomatoes and stock, then simmer for 25 minutes.",
                            "Stir in most of the basil and blend until smooth.",
                            "Season to taste and serve topped with the remaining torn basil."),
                    1);
            seedRecipe("Lemon Garlic Roast Chicken", "lemon-garlic-roast-chicken",
                    "A weeknight roast chicken with bright lemon and plenty of garlic.",
                    List.of("Chicken", "Lemon", "Garlic", "Rosemary", "Olive oil"),
                    List.of("1 whole chicken (about 3.5 lbs)", "1 lemon, halved", "1 head of garlic, halved",
                            "3 sprigs rosemary", "3 tbsp olive oil", "Salt and pepper to taste"),
                    List.of("Heat the oven to 425°F (220°C).",
                            "Rub the chicken all over with olive oil, salt, and pepper.",
                            "Tuck the lemon, garlic, and rosemary into the cavity.",
                            "Roast for about 70 minutes, until the juices run clear.",
                            "Rest for 10 minutes before carving."),
                    2);
        }
    }

    private void seedRecipe(String title, String slug, String description,
                            java.util.List<String> topIngredients, java.util.List<String> ingredients,
                            java.util.List<String> steps, int sortOrder) {
        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setSlug(slug);
        recipe.setDescription(description);
        recipe.setTopIngredients(new java.util.ArrayList<>(topIngredients));
        recipe.setIngredients(new java.util.ArrayList<>(ingredients));
        recipe.setSteps(new java.util.ArrayList<>(steps));
        recipe.setSortOrder(sortOrder);
        recipeRepository.save(recipe);
    }

    private void seedSamplePoll() {
        if (weeklyPollRepository.count() == 0) {
            WeeklyPoll poll = new WeeklyPoll();
            poll.setQuestion("Which season do you love most?");
            poll.setOptionAText("Spring");
            poll.setOptionBText("Autumn");
            poll.setActive(true);
            weeklyPollRepository.save(poll);
        }
    }
}
