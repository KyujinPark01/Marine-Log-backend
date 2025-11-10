package domain.backend.common.notification.domain;

import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_user_id")
    private User sentUser;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String type;

    private LocalDateTime readAt;

    @Column(nullable = false)
    private boolean isRead = false;

    @Builder
    public Notification(User targetUser, User sentUser, String message, String type) {
        this.targetUser = targetUser;
        this.sentUser = sentUser;
        this.message = message;
        this.type = type;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}