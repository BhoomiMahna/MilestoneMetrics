package com.milestonemetrics.backend.repository;

import com.milestonemetrics.backend.entity.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    Optional<Questionnaire> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}