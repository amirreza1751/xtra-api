package com.xtra.api.repository;

import com.xtra.api.model.stream.Channel;
import com.xtra.api.model.vod.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MovieRepository extends JpaRepository<Movie, Long>, QuerydslPredicateExecutor<Movie> {
    Page<Movie> findAllByNameContainsOrInfoPlotContainsOrInfoCastContainsOrInfoDirectorContainsOrInfoGenresContainsOrInfoCountryContains(String name, String plot, String cast, String director, String genres, String country, Pageable pageable);
}
