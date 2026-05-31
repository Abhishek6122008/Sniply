package com.sniply.sniply_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AnalyticsResponse {
    private String shortCode;
    private String originalUrl;
    private long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private List<ClickDetail> clicks;

    @Data
    @Builder
    public static class ClickDetail {
        private LocalDateTime clickedAt;
        private String ipAddress;
        private String deviceType;
        private String country;
    }
}
