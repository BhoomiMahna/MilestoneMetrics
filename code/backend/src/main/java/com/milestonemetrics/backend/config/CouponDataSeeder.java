package com.milestonemetrics.backend.config;

import com.milestonemetrics.backend.entity.Coupon;
import com.milestonemetrics.backend.repository.CouponRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds a small curated catalog of DEMONSTRATION offers on first startup (only
 * when the table is empty). These are prototype rewards unlocked by earning
 * points — they are not real commercial coupons and work with no external
 * merchant or API.
 */
@Configuration
public class CouponDataSeeder {

    @Bean
    @Order(1)
    CommandLineRunner seedCoupons(CouponRepository repository) {
        return args -> {
            if (repository.count() > 0) return;
            List<Coupon> seed = new ArrayList<>();

            seed.add(build("Budgeting e-book", "A free beginner's guide to budgeting for young earners.",
                    "Free download", "Learning", 0, "MM-EBOOK-FREE", LocalDate.now().plusMonths(12)));
            seed.add(build("₹100 bookstore voucher", "Demo voucher toward personal-finance books.",
                    "₹100 off", "Shopping", 100, "MM-BOOKS-100", LocalDate.now().plusMonths(6)));
            seed.add(build("1 month premium (demo)", "Unlock demo premium dashboard themes for a month.",
                    "1 month free", "Platform", 200, "MM-PREMIUM-1M", LocalDate.now().plusMonths(6)));
            seed.add(build("₹250 course discount", "Demo discount on an online skill course.",
                    "₹250 off", "Learning", 300, "MM-COURSE-250", LocalDate.now().plusMonths(6)));
            seed.add(build("Goal Crusher badge frame", "A cosmetic profile frame for reaching your goal.",
                    "Cosmetic reward", "Platform", 500, "MM-FRAME-GOAL", null));
            seed.add(build("₹500 shopping voucher", "Our top demo reward for consistent savers.",
                    "₹500 off", "Shopping", 800, "MM-SHOP-500", LocalDate.now().plusMonths(3)));

            repository.saveAll(seed);
        };
    }

    private static Coupon build(String title, String desc, String reward, String category,
                                int pointsRequired, String code, LocalDate expiry) {
        Coupon c = new Coupon();
        c.setTitle(title);
        c.setDescription(desc);
        c.setRewardLabel(reward);
        c.setCategory(category);
        c.setPointsRequired(pointsRequired);
        c.setDemoCode(code);
        c.setExpiryDate(expiry);
        c.setActive(true);
        c.setDemo(true);
        return c;
    }
}
