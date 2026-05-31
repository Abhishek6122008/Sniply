package com.sniply.sniply_backend.service;

import com.sniply.sniply_backend.dto.AnalyticsResponse;
import com.sniply.sniply_backend.dto.ShortenResponse;
import com.sniply.sniply_backend.entity.ClickEvent;
import com.sniply.sniply_backend.entity.Url;
import com.sniply.sniply_backend.repository.ClickEventRepository;
import com.sniply.sniply_backend.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final ClickEventRepository clickEventRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;
    private final Random random = new Random();

    @Transactional
    public ShortenResponse createShortUrl(String originalUrl, String customAlias, LocalDateTime expiresAt) {
        String shortCode;

        if (customAlias != null && !customAlias.isBlank()) {
            if (urlRepository.existsByShortCode(customAlias)) {
                throw new IllegalArgumentException("Custom alias already in use: " + customAlias);
            }
            shortCode = customAlias;
        } else {
            do {
                shortCode = generateCode();
            } while (urlRepository.existsByShortCode(shortCode));
        }

        Url url = Url.builder()
            .originalUrl(originalUrl)
            .shortCode(shortCode)
            .customAlias(customAlias)
            .expiresAt(expiresAt)
            .build();

        url = urlRepository.save(url);

        return ShortenResponse.builder()
            .shortCode(shortCode)
            .shortUrl(baseUrl + "/" + shortCode)
            .originalUrl(originalUrl)
            .expiresAt(expiresAt)
            .createdAt(url.getCreatedAt())
            .build();
    }

    @Cacheable(value = "urls", key = "#shortCode")
    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new IllegalArgumentException("Short URL not found: " + shortCode));

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Short URL has expired: " + shortCode);
        }

        return url.getOriginalUrl();
    }

    @Transactional
    public void recordClick(String shortCode, String ipAddress, String deviceType, String country) {
        ClickEvent event = ClickEvent.builder()
            .shortCode(shortCode)
            .ipAddress(ipAddress)
            .deviceType(deviceType)
            .country(country)
            .build();
        clickEventRepository.save(event);
        urlRepository.incrementClickCount(shortCode);
    }

    public AnalyticsResponse getAnalytics(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new IllegalArgumentException("Short URL not found: " + shortCode));

        List<ClickEvent> events = clickEventRepository.findByShortCode(shortCode);

        List<AnalyticsResponse.ClickDetail> clicks = events.stream()
            .map(e -> AnalyticsResponse.ClickDetail.builder()
                .clickedAt(e.getClickedAt())
                .ipAddress(e.getIpAddress())
                .deviceType(e.getDeviceType())
                .country(e.getCountry())
                .build())
            .collect(Collectors.toList());

        return AnalyticsResponse.builder()
            .shortCode(shortCode)
            .originalUrl(url.getOriginalUrl())
            .clickCount(url.getClickCount())
            .createdAt(url.getCreatedAt())
            .expiresAt(url.getExpiresAt())
            .clicks(clicks)
            .build();
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
