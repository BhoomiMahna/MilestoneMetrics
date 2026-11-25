package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.entity.Notification;
import com.milestonemetrics.backend.entity.NotificationType;
import com.milestonemetrics.backend.entity.User;
import com.milestonemetrics.backend.repository.NotificationRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Unit tests (no Spring/DB) for notification dedup and ownership rules. */
class NotificationServiceTest {

    private NotificationRepository repo;
    private UserRepository userRepo;
    private NotificationService service;
    private final User user = new User("Jo", "jo@test.com", "x");

    @BeforeEach
    void setup() {
        repo = mock(NotificationRepository.class);
        userRepo = mock(UserRepository.class);
        service = new NotificationService(repo, userRepo);
        when(userRepo.findByEmail("jo@test.com")).thenReturn(Optional.of(user));
    }

    @Test
    void createIfAbsentSkipsDuplicateEvent() {
        when(repo.existsByUserAndDedupeKey(user, "ACH:FIRST_SAVINGS")).thenReturn(true);
        Notification n = service.createIfAbsent(user, NotificationType.ACHIEVEMENT, "t", "m", "ACH:FIRST_SAVINGS");
        assertNull(n);
        verify(repo, never()).save(any());
    }

    @Test
    void createIfAbsentCreatesWhenKeyAbsent() {
        when(repo.existsByUserAndDedupeKey(user, "ACH:FIRST_SAVINGS")).thenReturn(false);
        when(repo.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        Notification n = service.createIfAbsent(user, NotificationType.ACHIEVEMENT, "t", "m", "ACH:FIRST_SAVINGS");
        assertNotNull(n);
        verify(repo, times(1)).save(any(Notification.class));
    }

    @Test
    void markReadRejectsOtherUsersNotification() {
        // findByIdAndUser returns empty => the notification is not this user's
        when(repo.findByIdAndUser(99L, user)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.markRead("jo@test.com", 99L));
        assertEquals(404, ex.getStatusCode().value());
    }
}
