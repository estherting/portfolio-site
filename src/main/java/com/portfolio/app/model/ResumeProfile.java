package com.portfolio.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "resume_profile")
public class ResumeProfile {

    @Id
    private Long id = 1L; // singleton row

    private String fullName = "Your Name";
    private String tagline = "Add a short tagline about yourself";

    @Column(length = 2000)
    private String summary = "Write a short professional summary here.";

    private String email = "you@example.com";
    private String location = "City, Country";
    private String linkedinUrl = "";
    private String websiteUrl = "";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
}
