package com.sniply.sniply_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortenRequest {
    private String originalUrl;
    private String customAlias;
    private LocalDateTime expiresAt;
}
