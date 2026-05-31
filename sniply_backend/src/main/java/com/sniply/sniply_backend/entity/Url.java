package com.sniply.sniply_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls", indexes = {
    @Index(name = "idx_url_short_code", columnList = "short_code")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 50)
    private String shortCode;

    @Column(length = 50)
    private String customAlias;

    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long clickCount;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        if (clickCount == null) clickCount = 0L;
    }
}
