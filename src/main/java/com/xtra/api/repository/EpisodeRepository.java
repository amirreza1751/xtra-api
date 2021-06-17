package com.xtra.api.repository;

import com.xtra.api.model.vod.Episode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    Page<Episode> findAllByEpisodeNameContains(String name, Pageable pageable);
}
