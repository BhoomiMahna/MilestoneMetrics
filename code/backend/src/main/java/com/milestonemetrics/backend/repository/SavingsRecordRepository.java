package com.milestonemetrics.backend.repository;

import com.milestonemetrics.backend.entity.PeriodType;
import com.milestonemetrics.backend.entity.SavingsRecord;
import com.milestonemetrics.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsRecordRepository extends JpaRepository<SavingsRecord, Long> {

    List<SavingsRecord> findByUserAndPeriodTypeOrderByPeriodStartDesc(User user, PeriodType periodType);

    List<SavingsRecord> findByUserOrderByPeriodStartDesc(User user);

    Optional<SavingsRecord> findByUserAndPeriodTypeAndPeriodStart(
            User user, PeriodType periodType, LocalDate periodStart);
}
