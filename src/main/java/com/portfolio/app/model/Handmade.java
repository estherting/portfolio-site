package com.portfolio.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
// Table name is kept as "hobby" so existing production data is preserved after the rename to Handmade.
@Table(name = "hobby")
public class Handmade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    /** Slug used for the detail page URL (/handmade/{slug}). */
    // Nullable at the DB level so the column can be added to the existing "hobby" table
    // without failing on rows that predate this feature; DataInitializer backfills them
    // and the admin controller always sets one on save.
    @Column(unique = true)
    private String slug;

    /** Short blurb shown on the card and at the top of the detail page. */
    @Column(length = 1000)
    private String description;

    /** Long-form, blog-style write-up shown on the detail page (plain text, line breaks preserved). */
    @Column(length = 20000)
    private String detail;

    /** Cover image shown on the card and the detail page header. */
    private String imageUrl;

    /**
     * Additional images shown in the detail-page gallery.
     * Fetched eagerly because open-in-view is disabled and the detail template reads
     * this list outside the persistence session; an @OrderColumn indexed list (not a bag)
     * so eager fetching is safe.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "position")
    @CollectionTable(name = "handmade_detail_image", joinColumns = @JoinColumn(name = "handmade_id"))
    @Column(name = "image_url", length = 1000)
    private List<String> detailImages = new ArrayList<>();

    private Integer sortOrder = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getDetailImages() { return detailImages; }
    public void setDetailImages(List<String> detailImages) { this.detailImages = detailImages; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
