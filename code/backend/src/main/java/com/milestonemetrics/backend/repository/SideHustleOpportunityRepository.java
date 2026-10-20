package com.milestonemetrics.backend.repository;

import com.milestonemetrics.backend.entity.SideHustleOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SideHustleOpportunityRepository extends JpaRepository<SideHustleOpportunity, Long> {
}
