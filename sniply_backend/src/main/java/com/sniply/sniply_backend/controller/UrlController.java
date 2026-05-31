package com.sniply.sniply_backend.controller;

import com.sniply.sniply_backend.dto.AnalyticsResponse;
import com.sniply.sniply_backend.dto.ShortenRequest;
import com.sniply.sniply_backend.dto.ShortenResponse;
import com.sniply.sniply_backend.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponse> shorten(@RequestBody ShortenRequest request) {
        ShortenResponse response = urlService.createShortUrl(
            request.getOriginalUrl(),
            request.getCustomAlias(),
            request.getExpiresAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        String originalUrl = urlService.getOriginalUrl(shortCode);

        String ipAddress = getClientIp(request);
        String deviceType = detectDeviceType(request.getHeader("User-Agent"));

        urlService.recordClick(shortCode, ipAddress, deviceType, null);

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, originalUrl)
            .build();
    }

    @GetMapping("/api/analytics/{shortCode}")
    public ResponseEntity<AnalyticsResponse> analytics(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlService.getAnalytics(shortCode));
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String detectDeviceType(String userAgent) {
        if (userAgent == null) return "unknown";
        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) return "mobile";
        if (ua.contains("tablet") || ua.contains("ipad")) return "tablet";
        return "desktop";
    }
}
