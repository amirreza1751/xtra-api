package com.xtra.api.mapper.line;

import com.xtra.api.model.vod.Movie;
import com.xtra.api.projection.line.movie.MoviePlayListView;
import com.xtra.api.projection.line.movie.MoviePlayView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Value;

import java.util.stream.Collectors;

import static com.xtra.api.service.system.UserAuthService.getCurrentLine;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Mapper(componentModel = "spring")
public abstract class LineMovieMapper {
    @Value("${server.external.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @Mapping(source = "info", target = ".")
    @Mapping(target = "categories", ignore = true)
    public abstract MoviePlayListView convertToPlayListView(Movie movie);

    @Mapping(source = "info", target = ".")
    public abstract MoviePlayView convertToPlayView(Movie movie);

    @AfterMapping
    public void assignCategories(final Movie movie, @MappingTarget MoviePlayListView moviePlayView) {
        moviePlayView.setCategories(emptyIfNull(movie.getCategories())
                .stream().map(categoryStream -> categoryStream.getCategory().getId()).collect(Collectors.toSet()));

        moviePlayView.setId(movie.getId());
    }

    @AfterMapping
    public void assignLink(final Movie movie, @MappingTarget MoviePlayView moviePlayView) {
        moviePlayView.setDuration(movie.getInfo() != null ? movie.getInfo().getRuntime() : 0);
        var line = getCurrentLine();
        var video = movie.getVideo();
        moviePlayView.setLink("http://" + serverAddress + ":" + serverPort + "/api/play/video/" + line.getLineToken() + "/" + video.getToken());

        moviePlayView.setId(movie.getId());
    }
}
