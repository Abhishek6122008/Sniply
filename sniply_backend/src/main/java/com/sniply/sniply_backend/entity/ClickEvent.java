package com.sniply.sniply_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "click_events", indexes = {
    @Index(name = "idx_click_short_code", columnList = "short_code")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String shortCode;

    @Column(nullable = false)
    private LocalDateTime clickedAt;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 30)
    private String deviceType;

    @Column(length = 100)
    private String country;

    @PrePersist
    void prePersist() {
        clickedAt = LocalDateTime.now();
    }
}
