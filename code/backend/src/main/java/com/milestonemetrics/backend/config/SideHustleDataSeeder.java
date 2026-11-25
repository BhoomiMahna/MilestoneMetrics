package com.milestonemetrics.backend.config;

import com.milestonemetrics.backend.entity.Difficulty;
import com.milestonemetrics.backend.entity.SideHustleOpportunity;
import com.milestonemetrics.backend.repository.SideHustleOpportunityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the side-hustle opportunity catalog with a realistic set of options on
 * startup, but only when the table is empty. This stands in for the external
 * "Side-Hustle Platforms (API / Scraping)" integration so the recommendation
 * engine has real, database-backed data to rank.
 */
@Configuration
public class SideHustleDataSeeder {

    @Bean
    @Order(1)
    CommandLineRunner seedSideHustleOpportunities(SideHustleOpportunityRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            List<SideHustleOpportunity> seed = new ArrayList<>();

            seed.add(build("Freelance Python Developer",
                    "Build scripts, automations and small backend services for clients on a per-project basis.",
                    "Python, Backend, APIs", "Coding, Technology",
                    15000, 60000, 8, 20, Difficulty.INTERMEDIATE, true,
                    "Upwork", "https://www.upwork.com"));

            seed.add(build("Web Development Freelancer",
                    "Design and build websites and web apps for small businesses and startups.",
                    "Web Development, JavaScript, HTML, CSS, React", "Coding, Design, Technology",
                    12000, 55000, 8, 25, Difficulty.INTERMEDIATE, true,
                    "Fiverr", "https://www.fiverr.com"));

            seed.add(build("Coding Tutor",
                    "Teach programming basics to school and college students in 1:1 or small group sessions.",
                    "Python, Web Development, Teaching, Communication", "Teaching, Coding",
                    8000, 30000, 4, 12, Difficulty.BEGINNER, true,
                    "Preply", "https://preply.com"));

            seed.add(build("Technical Content Creator",
                    "Write tutorials, blog posts and documentation about programming and tools.",
                    "Writing, Python, Web Development, Communication", "Writing, Coding, Technology",
                    6000, 35000, 5, 15, Difficulty.BEGINNER, true,
                    "Medium Partner Program", "https://medium.com"));

            seed.add(build("Graphic Designer",
                    "Create logos, social media creatives and marketing assets for brands.",
                    "Design, Photoshop, Illustrator, Creativity", "Design, Art",
                    8000, 40000, 6, 18, Difficulty.INTERMEDIATE, true,
                    "99designs", "https://99designs.com"));

            seed.add(build("Social Media Manager",
                    "Plan, schedule and grow social accounts for small businesses and creators.",
                    "Marketing, Communication, Writing, Creativity", "Marketing, Social Media",
                    7000, 35000, 5, 15, Difficulty.BEGINNER, true,
                    "LinkedIn", "https://www.linkedin.com"));

            seed.add(build("Data Entry & Virtual Assistant",
                    "Handle data entry, scheduling and admin tasks remotely for busy professionals.",
                    "Excel, Communication, Organization", "Admin",
                    5000, 18000, 5, 20, Difficulty.BEGINNER, true,
                    "Freelancer", "https://www.freelancer.com"));

            seed.add(build("Video Editor",
                    "Edit short-form and long-form videos for YouTubers and brands.",
                    "Video Editing, Premiere Pro, Creativity", "Video, Design, Art",
                    9000, 45000, 6, 20, Difficulty.INTERMEDIATE, true,
                    "YouTube Creators", "https://www.youtube.com"));

            seed.add(build("Online Subject Tutor",
                    "Tutor school students in maths, science or languages over video calls.",
                    "Teaching, Communication", "Teaching",
                    7000, 28000, 4, 12, Difficulty.BEGINNER, true,
                    "Vedantu", "https://www.vedantu.com"));

            seed.add(build("Data Analyst (Freelance)",
                    "Clean, analyze and visualize data for small businesses using spreadsheets and Python.",
                    "Python, Excel, Data Analysis, SQL", "Data, Coding, Technology",
                    15000, 55000, 8, 20, Difficulty.ADVANCED, true,
                    "Toptal", "https://www.toptal.com"));

            seed.add(build("Handmade Crafts Seller",
                    "Make and sell handmade products like candles, jewellery or art locally and online.",
                    "Creativity, Craft", "Art, Craft",
                    4000, 20000, 6, 20, Difficulty.BEGINNER, false,
                    "Etsy", "https://www.etsy.com"));

            seed.add(build("Food Delivery Partner",
                    "Deliver food and parcels in your city on a flexible schedule.",
                    "Driving, Time Management", "Fitness, Outdoors",
                    8000, 25000, 10, 30, Difficulty.BEGINNER, false,
                    "Zomato", "https://www.zomato.com"));

            repository.saveAll(seed);
        };
    }

    private static SideHustleOpportunity build(String name, String description,
                                               String requiredSkills, String interestTags,
                                               double minEarn, double maxEarn,
                                               int minHours, int maxHours,
                                               Difficulty difficulty, boolean online,
                                               String platformName, String platformUrl) {
        SideHustleOpportunity o = new SideHustleOpportunity();
        o.setName(name);
        o.setDescription(description);
        o.setRequiredSkills(requiredSkills);
        o.setInterestTags(interestTags);
        o.setMinMonthlyEarning(minEarn);
        o.setMaxMonthlyEarning(maxEarn);
        o.setMinHoursPerWeek(minHours);
        o.setMaxHoursPerWeek(maxHours);
        o.setDifficulty(difficulty);
        o.setOnline(online);
        o.setPlatformName(platformName);
        o.setPlatformUrl(platformUrl);
        return o;
    }
}
