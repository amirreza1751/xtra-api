package com.xtra.api.projection.admin.analytics;

import com.xtra.api.model.vod.Movie;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class ResellerAnalytics {
    private long subResellersCount;
    private List<Movie> recentMovies;
    private long onlineUsersCount;
    private long linesCount;
}
