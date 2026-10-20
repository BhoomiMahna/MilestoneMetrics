package com.milestonemetrics.backend.repository;

import com.milestonemetrics.backend.entity.FinancialPlan;
import com.milestonemetrics.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinancialPlanRepository extends JpaRepository<FinancialPlan, Long> {
    Optional<FinancialPlan> findByUser(User user);
}
