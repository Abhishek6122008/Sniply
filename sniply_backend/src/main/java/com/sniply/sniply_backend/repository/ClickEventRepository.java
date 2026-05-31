package com.sniply.sniply_backend.repository;

import com.sniply.sniply_backend.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    List<ClickEvent> findByShortCode(String shortCode);

    long countByShortCode(String shortCode);
}
