package com.xtra.api.repository;

import com.xtra.api.model.vod.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    Page<Series> findAllByNameContainsOrInfoPlotContainsOrInfoCastContainsOrInfoDirectorContainsOrInfoGenresContainsOrInfoCountryContains(String name, String plot, String cast, String director, String genres, String country, Pageable pageable);

    List<Series> findTop10ByOrderByCreatedDateDesc();
}
