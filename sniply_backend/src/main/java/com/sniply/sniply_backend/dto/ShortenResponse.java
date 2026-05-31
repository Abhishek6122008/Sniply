package com.sniply.sniply_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShortenResponse {
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
