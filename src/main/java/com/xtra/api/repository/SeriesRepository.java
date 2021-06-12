package com.xtra.api.repository;

import com.xtra.api.model.vod.Series;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesRepository extends JpaRepository<Series, Long> {
}
