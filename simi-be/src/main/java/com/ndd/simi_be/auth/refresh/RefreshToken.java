package com.ndd.simi_be.auth.refresh;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {
    @Column(nullable = false, unique = true, length = 500)
    private String refreshToken;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Builder.Default
    private boolean revoked = false;
}
