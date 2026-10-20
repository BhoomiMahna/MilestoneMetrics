package com.milestonemetrics.backend.repository;

import com.milestonemetrics.backend.entity.Notification;
import com.milestonemetrics.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    long countByUserAndReadFalse(User user);

    boolean existsByUserAndDedupeKey(User user, String dedupeKey);

    Optional<Notification> findByIdAndUser(Long id, User user);
}
