package com.milestonemetrics.backend.repository;

import com.milestonemetrics.backend.entity.SideHustleBookmark;
import com.milestonemetrics.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SideHustleBookmarkRepository extends JpaRepository<SideHustleBookmark, Long> {

    List<SideHustleBookmark> findByUser(User user);

    Optional<SideHustleBookmark> findByUserAndOpportunityId(User user, Long opportunityId);

    boolean existsByUserAndOpportunityId(User user, Long opportunityId);
}
