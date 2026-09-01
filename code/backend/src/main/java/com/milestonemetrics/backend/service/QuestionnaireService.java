package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.dto.QuestionnaireRequest;
import com.milestonemetrics.backend.dto.QuestionnaireResponse;
import com.milestonemetrics.backend.entity.Questionnaire;
import com.milestonemetrics.backend.entity.User;
import com.milestonemetrics.backend.repository.QuestionnaireRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final UserRepository userRepository;

    public QuestionnaireService(
            QuestionnaireRepository questionnaireRepository,
            UserRepository userRepository
    ) {
        this.questionnaireRepository = questionnaireRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public QuestionnaireResponse create(
            QuestionnaireRequest request,
            String email
    ) {

        User user = getUserByEmail(email);

        if (questionnaireRepository.existsByUserId(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Questionnaire already exists for this user"
            );
        }

        Questionnaire questionnaire = new Questionnaire();

        questionnaire.setUser(user);
        questionnaire.setMonthlyIncome(request.getMonthlyIncome());
        questionnaire.setMonthlySpend(request.getMonthlySpend());
        questionnaire.setCurrentInvestments(request.getCurrentInvestments());
        questionnaire.setAgeGroup(request.getAgeGroup().trim());
        questionnaire.setGoalAmount(request.getGoalAmount());
        questionnaire.setTimelineYears(request.getTimelineYears());

        Questionnaire saved = questionnaireRepository.save(questionnaire);

        return convertToResponse(saved);
    }

    @Transactional(readOnly = true)
    public QuestionnaireResponse getMyQuestionnaire(String email) {

        User user = getUserByEmail(email);

        Questionnaire questionnaire = questionnaireRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Questionnaire not found"
                ));

        return convertToResponse(questionnaire);
    }

    @Transactional
    public QuestionnaireResponse update(
            QuestionnaireRequest request,
            String email
    ) {

        User user = getUserByEmail(email);

        Questionnaire questionnaire = questionnaireRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Questionnaire not found"
                ));

        questionnaire.setMonthlyIncome(request.getMonthlyIncome());
        questionnaire.setMonthlySpend(request.getMonthlySpend());
        questionnaire.setCurrentInvestments(request.getCurrentInvestments());
        questionnaire.setAgeGroup(request.getAgeGroup().trim());
        questionnaire.setGoalAmount(request.getGoalAmount());
        questionnaire.setTimelineYears(request.getTimelineYears());

        Questionnaire updated = questionnaireRepository.save(questionnaire);

        return convertToResponse(updated);
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));
    }

    private QuestionnaireResponse convertToResponse(
            Questionnaire questionnaire
    ) {

        int totalMonths = questionnaire.getTimelineYears() * 12;

        BigDecimal monthlyTarget =
                questionnaire.getGoalAmount()
                        .divide(
                                BigDecimal.valueOf(totalMonths),
                                2,
                                RoundingMode.CEILING
                        );

        BigDecimal monthlySurplus =
                questionnaire.getMonthlyIncome()
                        .subtract(questionnaire.getMonthlySpend());

        boolean feasible =
                monthlySurplus.compareTo(monthlyTarget) >= 0;

        QuestionnaireResponse response = new QuestionnaireResponse();

        response.setId(questionnaire.getId());

        response.setMonthlyIncome(
                questionnaire.getMonthlyIncome()
        );

        response.setMonthlySpend(
                questionnaire.getMonthlySpend()
        );

        response.setCurrentInvestments(
                questionnaire.getCurrentInvestments()
        );

        response.setAgeGroup(
                questionnaire.getAgeGroup()
        );

        response.setGoalAmount(
                questionnaire.getGoalAmount()
        );

        response.setTimelineYears(
                questionnaire.getTimelineYears()
        );

        response.setTotalMonths(totalMonths);
        response.setMonthlyTarget(monthlyTarget);
        response.setMonthlySurplus(monthlySurplus);
        response.setFeasible(feasible);

        response.setCreatedAt(
                questionnaire.getCreatedAt()
        );

        response.setUpdatedAt(
                questionnaire.getUpdatedAt()
        );

        return response;
    }
}