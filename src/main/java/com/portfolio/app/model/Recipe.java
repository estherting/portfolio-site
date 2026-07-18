package com.portfolio.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String slug;

    /** Photo shown on the card and recipe page. */
    private String imageUrl;

    @Column(length = 500)
    private String description;

    /**
     * The "top five" ingredients — shown on the card and used by the
     * "What's in my fridge?" filter. Kept as a small ordered list so both the
     * card display and the ingredient menu can be built from the same data.
     */
    // Fetched eagerly: open-in-view is disabled, and both the controller and the
    // templates read these outside the persistence session. They're indexed lists
    // (@OrderColumn), not bags, so eager fetching won't trigger MultipleBagFetchException.
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "position")
    @CollectionTable(name = "recipe_top_ingredient", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient")
    private List<String> topIngredients = new ArrayList<>();

    /** Full ingredient list shown on the recipe's own page, one entry per line. */
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "position")
    @CollectionTable(name = "recipe_ingredient", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient", length = 500)
    private List<String> ingredients = new ArrayList<>();

    /** Ordered preparation steps shown on the recipe's own page. */
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "position")
    @CollectionTable(name = "recipe_step", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "step", length = 1000)
    private List<String> steps = new ArrayList<>();

    private Integer sortOrder = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTopIngredients() { return topIngredients; }
    public void setTopIngredients(List<String> topIngredients) { this.topIngredients = topIngredients; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
