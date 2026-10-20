package com.milestonemetrics.backend.repository;

import com.milestonemetrics.backend.entity.AchievementType;
import com.milestonemetrics.backend.entity.User;
import com.milestonemetrics.backend.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    List<UserAchievement> findByUser(User user);

    boolean existsByUserAndType(User user, AchievementType type);

    long countByType(AchievementType type);
}
