package com.portfolio.app.config;

import com.portfolio.app.model.*;
import com.portfolio.app.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BlogPostRepository blogPostRepository;
    private final HobbyRepository hobbyRepository;
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

    public DataInitializer(UserRepository userRepository, BlogPostRepository blogPostRepository,
                            HobbyRepository hobbyRepository, ResumeProfileRepository resumeProfileRepository,
                            ExperienceRepository experienceRepository, EducationRepository educationRepository,
                            SkillRepository skillRepository, WeeklyPollRepository weeklyPollRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.blogPostRepository = blogPostRepository;
        this.hobbyRepository = hobbyRepository;
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
        if (hobbyRepository.count() == 0) {
            Hobby h = new Hobby();
            h.setTitle("Pressed Flower Art");
            h.setDescription("A quiet hobby of collecting and pressing garden flowers into keepsakes.");
            h.setSortOrder(0);
            hobbyRepository.save(h);
        }
        if (blogPostRepository.count() == 0) {
            BlogPost post = new BlogPost();
            post.setTitle("Welcome to My Corner of the Internet");
            post.setSlug("welcome");
            post.setSummary("A short note introducing this little journal.");
            post.setContent("<p>Hello, and welcome! This is the first entry in my new blog. "
                    + "Edit or delete this post from the admin dashboard, and start writing your own.</p>");
            post.setPublished(true);
            blogPostRepository.save(post);
        }
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
