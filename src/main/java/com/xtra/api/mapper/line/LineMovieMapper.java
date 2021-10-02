package com.xtra.api.mapper.line;

import com.xtra.api.model.vod.Movie;
import com.xtra.api.projection.admin.movie.MovieVideoInfo;
import com.xtra.api.projection.line.movie.MoviePlayListView;
import com.xtra.api.projection.line.movie.MoviePlayView;
import com.xtra.api.service.line.LineLineServiceImpl;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.stream.Collectors;

import static com.xtra.api.service.system.UserAuthService.getCurrentLine;

@Mapper(componentModel = "spring")
public abstract class LineMovieMapper {
    @Value("${server.external.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @Mapping(source = "info.posterPath", target = "posterPath")
    @Mapping(source = "info.backdropPath", target = "backdropPath")
    @Mapping(source = "info.rating", target = "rating")
    @Mapping(source = "info.country", target = "country")
    @Mapping(source = "info.releaseDate", target = "releaseDate")
    public abstract MoviePlayListView convertToPlayListView(Movie movie);

    @Mapping(source = "info.posterPath", target = "posterPath")
    @Mapping(source = "info.backdropPath", target = "backdropPath")
    @Mapping(source = "info.rating", target = "rating")
    @Mapping(source = "info.country", target = "country")
    @Mapping(source = "info.releaseDate", target = "releaseDate")
    @Mapping(source = "info.plot", target = "plot")
    @Mapping(source = "info.cast", target = "cast")
    @Mapping(source = "info.director", target = "director")
    @Mapping(source = "info.genres", target = "genres")
    @Mapping(source = "info.runtime", target = "runtime")
    @Mapping(source = "info.youtubeTrailer", target = "youtubeTrailer")
    public abstract MoviePlayView convertToPlayView(Movie movie);

    @AfterMapping
    public void assignLink(final Movie movie, @MappingTarget MoviePlayView moviePlayView) {
        moviePlayView.setDuration(movie.getInfo() != null ? movie.getInfo().getRuntime() : 0);
        if (!movie.getVideos().isEmpty()) {
            //set video info
            moviePlayView.setLinks(movie.getVideos().stream().map(video -> {
                if (video.getVideoInfo() != null) {
                    var line = getCurrentLine();
                    return "http://" + serverAddress + ":" + serverPort + "/api/play/video/" + line.getLineToken() + "/" + video.getToken();
                } else return "";
            }).collect(Collectors.toList()));
        }
    }
}
