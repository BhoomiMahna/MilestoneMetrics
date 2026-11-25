package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.dto.InvestmentRecommendationResponse;
import com.milestonemetrics.backend.dto.InvestmentRecommendationResponse.ProfileSummary;
import com.milestonemetrics.backend.dto.InvestmentRecommendationResponse.Suggestion;
import com.milestonemetrics.backend.entity.FinancialPlan;
import com.milestonemetrics.backend.entity.User;
import com.milestonemetrics.backend.repository.FinancialPlanRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Transparent, rule-based educational investment guidance built from the data
 * already collected in the questionnaire (income, spending, goal, time horizon,
 * age band). It never guarantees returns, uses no live market data, and does
 * not recommend a specific product based on unsupported assumptions. When key
 * profile fields are missing it says so rather than fabricating them.
 */
@Service
public class InvestmentService {

    private static final String DISCLAIMER =
            "Educational information only — not regulated or personalized financial advice. "
            + "These are general categories to learn about, not product recommendations. "
            + "Investments can lose value; past performance does not guarantee future returns. "
            + "No live market data is used. Consider a licensed advisor before investing.";

    private final UserRepository userRepository;
    private final FinancialPlanRepository planRepository;

    public InvestmentService(UserRepository userRepository, FinancialPlanRepository planRepository) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
    }

    public InvestmentRecommendationResponse getRecommendations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        FinancialPlan plan = planRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Complete the questionnaire first to see investment information."));

        Double income = plan.getMonthlyIncome();
        Double spend = plan.getMonthlySpend();
        Double years = plan.getTimelineYears();
        String ageBand = plan.getAgeBand();
        Double surplus = (income != null && spend != null) ? Math.max(income - spend, 0.0) : null;

        List<String> missing = new ArrayList<>();
        if (income == null) missing.add("Monthly income is not set — add it in the questionnaire for better guidance.");
        if (spend == null) missing.add("Monthly spending is not set — needed to estimate how much you can invest.");
        if (years == null) missing.add("Goal timeline is not set — the time horizon strongly shapes suitable options.");

        String risk = riskProfile(years);
        ProfileSummary profile = new ProfileSummary(income, spend, surplus, years, ageBand, risk);

        List<Suggestion> suggestions = new ArrayList<>();
        String horizonText = years != null ? String.format("~%s-year", trim(years)) : "your";

        // 1) Emergency fund — foundational, always included.
        String efWhy = surplus != null
                ? String.format("Before investing, a buffer of 3–6 months of expenses (around ₹%,.0f) protects your plan from surprises.",
                        spend != null ? spend * 3 : 0.0)
                : "A 3–6 month expense buffer protects your plan from surprises before you invest.";
        suggestions.add(new Suggestion("Emergency fund", "Safety", "LOW", "Any",
                efWhy,
                "Kept accessible (savings account or sweep-in FD) so it is available immediately when needed.",
                List.of("Savings account", "Sweep-in / liquid FD")));

        // 2) Time-horizon-driven categories.
        if (years == null || years < 2) {
            suggestions.add(new Suggestion("Short-term, low-risk saving", "Capital preservation", "LOW",
                    "Short (<2y)",
                    "For a " + horizonText + " goal, keeping money stable matters more than growth, so guaranteed or very-low-risk options fit.",
                    "Returns are modest but predictable, with little chance of a short-term loss.",
                    List.of("Recurring Deposit (RD)", "Short-term Fixed Deposit (FD)", "Liquid funds")));
        }
        if (years != null && years >= 2 && years <= 5) {
            suggestions.add(new Suggestion("Balanced medium-term mix", "Balanced", "MEDIUM",
                    "Medium (2–5y)",
                    "A " + horizonText + " horizon can usually ride out small ups and downs, so a mix of stable and growth options is common.",
                    "Blends guaranteed products with a modest, diversified equity portion; value can fluctuate in the short term.",
                    List.of("FD / RD for the stable portion", "Index-fund SIP for the growth portion")));
        }
        if (years != null && years > 5) {
            suggestions.add(new Suggestion("Long-term growth via SIP", "Growth", "HIGH",
                    "Long (5y+)",
                    "With a " + horizonText + " horizon" + ageLeanText(ageBand) + ", regular SIPs into low-cost, diversified index funds are a widely taught long-term approach.",
                    "Equity values move up and down a lot short-term; the long horizon is what historically smooths this — never guaranteed.",
                    List.of("Index-fund SIP (e.g. Nifty 50 index fund)", "ELSS (equity + tax saving, 3y lock-in)")));
            suggestions.add(new Suggestion("Long-term stable (PPF)", "Capital preservation", "LOW",
                    "Long (5y+)",
                    "For the safer slice of a long-term goal, government-backed schemes offer stability and tax benefits.",
                    "Long lock-in and rates set periodically by the government; very low risk to capital.",
                    List.of("Public Provident Fund (PPF)")));
        }

        return new InvestmentRecommendationResponse(profile, missing, suggestions, DISCLAIMER);
    }

    private static String riskProfile(Double years) {
        if (years == null) return "Unknown";
        if (years < 2) return "Conservative";
        if (years <= 5) return "Moderate";
        return "Growth";
    }

    private static String ageLeanText(String ageBand) {
        if (ageBand == null) return "";
        if (ageBand.startsWith("Under 18") || ageBand.contains("18")) {
            return " and many years ahead";
        }
        return "";
    }

    private static String trim(double v) {
        if (v == Math.floor(v)) return String.valueOf((long) v);
        return String.valueOf(Math.round(v * 10.0) / 10.0);
    }
}
