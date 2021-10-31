package com.xtra.api.mapper.admin;

import com.xtra.api.model.category.CategoryVod;
import com.xtra.api.model.category.CategoryVodId;
import com.xtra.api.model.collection.CollectionVod;
import com.xtra.api.model.collection.CollectionVodId;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.vod.*;
import com.xtra.api.projection.admin.movie.*;
import com.xtra.api.projection.admin.video.ServerEncodeStatus;
import com.xtra.api.projection.admin.video.ServerIdEncodeStatus;
import com.xtra.api.repository.*;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Mapper(componentModel = "spring")
public abstract class MovieMapper extends VideoMapper {

    @Autowired
    private CollectionVodRepository collectionVodRepository;
    @Autowired
    private CollectionRepository collectionRepository;
    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${server.external.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @Mapping(target = "video.sourceLocation", source = "sourceLocation")
    @Mapping(target = "video.targetResolutions", source = "targetResolutions")
    @Mapping(target = "categories", ignore = true)
    public abstract Movie convertToEntity(MovieInsertView movieView);

    @Mapping(target = "video.sourceLocation", source = "sourceLocation")
    @Mapping(target = "video.targetResolutions", source = "targetResolutions")
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "video", ignore = true)
    public abstract Movie updateEntityFromInsertView(MovieInsertView movieView, @MappingTarget final Movie movie);

    @AfterMapping
    void addRelationshipsToEntity(final MovieInsertView insertView, @MappingTarget final Movie movie) {
        var video = movie.getVideo();
        if (video != null) {
            video.getSourceAudios().clear();
            video.getSourceAudios().addAll(insertView.getSourceAudios().stream().map(this::toAudio).collect(Collectors.toList()));

            video.getSourceSubtitles().clear();
            video.getSourceSubtitles().addAll(insertView.getSourceSubtitles().stream().map(this::toSubtitle).collect(Collectors.toList()));

            var serverId = insertView.getSourceServer();
            var server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server", serverId.toString()));
            video.setSourceServer(server);

            VideoServer videoServer = new VideoServer(new VideoServerId(video.getId(), serverId));
            videoServer.setServer(server);
            videoServer.setVideo(video);
            video.getVideoServers().clear();
            video.getVideoServers().add(videoServer);
        }

        var collectionIds = insertView.getCollections();
        if (collectionIds != null) {
            Set<CollectionVod> collectionVods = new HashSet<>();
            for (var id : collectionIds) {
                var collectionVod = new CollectionVod(new CollectionVodId(id, movie.getId()));
                var orderCount = collectionVodRepository.countAllByIdCollectionId(id);
                collectionVod.setOrder(orderCount);
                var col = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Collection", id.toString()));
                collectionVod.setId(new CollectionVodId(id, movie.getId()));
                collectionVod.setCollection(col);
                collectionVod.setVod(movie);
                collectionVods.add(collectionVod);
            }
            movie.getCollectionAssigns().retainAll(collectionVods);
            movie.getCollectionAssigns().addAll(collectionVods);
        }

        var categoryIds = insertView.getCategories();
        if (categoryIds != null) {
            Set<CategoryVod> categoryVods = new HashSet<>();
            for (var id : categoryIds) {
                var category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category", id.toString()));
                CategoryVod categoryVod = new CategoryVod(new CategoryVodId(id, movie.getId()));
                categoryVod.setCategory(category);
                categoryVod.setVod(movie);
                categoryVods.add(categoryVod);
            }
            movie.getCategories().retainAll(categoryVods);
            movie.getCategories().addAll(categoryVods);
        }
    }



    @Mapping(target = "collections", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "sourceLocation", source = "video.sourceLocation")
    @Mapping(target = "sourceServer", source = "video.sourceServer.id")
    public abstract MovieView convertToView(Movie movie);

    @AfterMapping
    public void addRelationshipsToView(final Movie movie, @MappingTarget MovieView movieView) {
        movieView.setCollections(movie.getCollectionAssigns().stream().map(collectionVod -> collectionVod.getCollection().getId()).collect(Collectors.toList()));
        movieView.setCategories(movie.getCategories().stream().map(categoryVod -> categoryVod.getId().getCategoryId()).collect(Collectors.toList()));
        var video = movie.getVideo();
        if (video != null) {
            var servers = emptyIfNull(movie.getVideo().getVideoServers()).stream().map(videoServer -> new ServerIdEncodeStatus(videoServer.getServer().getId(), videoServer.getEncodeStatus())).collect(Collectors.toList());
            movieView.setServers(servers);
            movieView.setSourceAudios(video.getSourceAudios().stream().map(this::toAudioDetails).collect(Collectors.toList()));
            movieView.setSourceSubtitles(video.getSourceSubtitles().stream().map(this::toSubtitleDetails).collect(Collectors.toList()));
            movieView.setTargetVideosInfos(emptyIfNull(video.getTargetVideosInfos()).stream().map(this::toVideoInfoView).collect(Collectors.toList()));
        }
    }

    @Mapping(source = "name", target = "name")
    public abstract MovieListView convertToListView(Movie movie);

    @AfterMapping
    public void assignInfo(final Movie movie, @MappingTarget MovieListView movieListView) {
        movieListView.setDuration(movie.getInfo() != null ? movie.getInfo().getRuntime() : 0);
        if (movie.getVideo() != null) {
            var video = movie.getVideo();
            movieListView.setServers(video.getVideoServers().stream().map(videoServer -> new ServerEncodeStatus(videoServer.getServer().getName(), videoServer.getEncodeStatus())).collect(Collectors.toList()));
            movieListView.setTargetVideos(video.getTargetVideosInfos().stream().map(this::toVideoInfoView).collect(Collectors.toList()));
            var system_line = lineRepository.findByUsername("system_line");
            String link = system_line.map(line -> "http://" + serverAddress + ":" + serverPort + "/api/play/video/" + line.getLineToken() + "/" + video.getToken()).orElse("");
            movieListView.setLink(link);
        }
    }

    public List<Movie> convertToMovieList(MovieImportView importView) {
        var movies = new ArrayList<Movie>();
        for (var movieDetails : importView.getMovies()) {
            var movie = new Movie();
            movie.setName(movieDetails.getName());
            var collections = collectionRepository.findAllById(importView.getCollections());
            movie.setCollectionAssigns(collections.stream().map(collection -> new CollectionVod(collection, movie)).collect(Collectors.toSet()));
            var info = new MovieInfo();
            info.setTmdbId(movieDetails.getTmdbId());
            movie.setInfo(info);
            //@todo add all target servers

            var sourceServer = serverRepository.findById(importView.getSourceServer()).orElseThrow(() -> new EntityNotFoundException("Server", importView.getSourceServer().toString()));
            var video = new Video();
            var sourceVideo = movieDetails.getSourceVideo();
            video.setSourceServer(sourceServer);
            video.setSourceSubtitles(sourceVideo.getSourceSubtitles().stream().map(this::toSubtitle).collect(Collectors.toList()));
            video.setSourceAudios(sourceVideo.getSourceAudios().stream().map(this::toAudio).collect(Collectors.toList()));
            var videoServer = new VideoServer(video, sourceServer);
            videoServer.setTargetDirectoryLocation(sourceVideo.getLocation());
            video.setVideoServers(List.of(videoServer));
            movie.setVideo(video);
            movies.add(movie);
        }
        return movies;
    }

    public abstract MovieInfo convertToMovieInfo(MovieInfoInsertView movieInfo);
}
