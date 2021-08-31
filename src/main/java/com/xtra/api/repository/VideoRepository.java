package com.xtra.api.repository;

import com.xtra.api.model.vod.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByToken(String token);
}
