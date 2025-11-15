package domain.backend.common.notification.repository;

import domain.backend.auth.domain.user.User;
import domain.backend.common.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByTargetUserOrderByCreatedAtDesc(User targetUser, Pageable pageable);
    long countByTargetUserAndIsReadFalse(User targetUser);
}
