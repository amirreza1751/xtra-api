package com.xtra.api.projection.admin.analytics;

import com.xtra.api.projection.admin.movie.MovieView;
import com.xtra.api.projection.admin.series.SeriesView;
import lombok.Data;

import java.util.List;

@Data
public class LineAnalytics {
    private String expireDate;
    private List<MovieView> recentMovies;
    private List<SeriesView> recentSeries;
}
