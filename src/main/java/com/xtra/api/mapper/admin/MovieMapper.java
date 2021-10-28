package com.xtra.api.mapper.admin;

import com.xtra.api.model.category.CategoryVod;
import com.xtra.api.model.category.CategoryVodId;
import com.xtra.api.model.collection.CollectionVod;
import com.xtra.api.model.collection.CollectionVodId;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.vod.*;
import com.xtra.api.projection.admin.movie.*;
import com.xtra.api.projection.admin.video.AudioDetails;
import com.xtra.api.projection.admin.video.SubtitleDetails;
import com.xtra.api.projection.admin.video.VideoInfoView;
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

@Mapper(componentModel = "spring")
public abstract class MovieMapper {

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

    @Mapping(target = "categories", ignore = true)
    public abstract Movie convertToEntity(MovieInsertView movieView);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "video", ignore = true)
    public abstract Movie updateEntityFromInsertView(MovieInsertView movieView, @MappingTarget final Movie movie);

    @AfterMapping
    void addRelationshipsToEntity(final MovieInsertView insertView, @MappingTarget final Movie movie) {
        var video = movie.getVideo();
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


        var collectionIds = insertView.getCollections();
        if (collectionIds != null) {
            Set<CollectionVod> collectionVods = new HashSet<>();
            for (var id : collectionIds) {
                var collectionVod = new CollectionVod(new CollectionVodId(id, movie.getId()));
                var orderCount = collectionVodRepository.countAllByIdCollectionId(id);
                collectionVod.setOrder(orderCount);
                var col = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("collection", id.toString()));
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

    abstract Audio toAudio(AudioDetails audioDetails);

    abstract Subtitle toSubtitle(SubtitleDetails subtitleDetails);

    @Mapping(target = "collections", ignore = true)
    @Mapping(target = "categories", ignore = true)
    public abstract MovieView convertToView(Movie movie);

    @AfterMapping
    public void addRelationshipsToView(final Movie movie, @MappingTarget MovieView movieView) {
        Set<Long> servers = movie.getVideo().getVideoServers().stream().map(videoServer -> videoServer.getServer().getId()).collect(Collectors.toSet());
        movieView.setServers(servers);
        movieView.setCollections(movie.getCollectionAssigns().stream().map(collectionVod -> collectionVod.getCollection().getId()).collect(Collectors.toSet()));
        movieView.setCategories(movie.getCategories().stream().map(categoryVod -> categoryVod.getId().getCategoryId()).collect(Collectors.toSet()));
    }

    @Mapping(source = "movie.video.encodeStatus", target = "encodeStatus")
    @Mapping(source = "name", target = "name")
    public abstract MovieListView convertToListView(Movie movie);

    @AfterMapping
    public void assignInfo(final Movie movie, @MappingTarget MovieListView movieListView) {
        movieListView.setDuration(movie.getInfo() != null ? movie.getInfo().getRuntime() : 0);
        movieListView.setServers(movie.getVideo().getVideoServers().stream().map(videoServer -> videoServer.getServer().getName()).collect(Collectors.toList()));
        movieListView.setTargetVideos(movie.getVideo().getTargetVideosInfos().stream().map(this::toVideoInfoView).collect(Collectors.toList()));

        var system_line = lineRepository.findByUsername("system_line");
        String link = system_line.map(line -> "http://" + serverAddress + ":" + serverPort + "/api/play/video/" + line.getLineToken() + "/" + movie.getVideo().getToken()).orElse("");
        movieListView.setLink(link);
    }

    abstract VideoInfoView toVideoInfoView(VideoInfo videoInfo);

    /*public Set<VideoServer> convertToVideoServers(Set<Long> ids, Movie movie) {
        Set<VideoServer> videoServers = new HashSet<>();
        for (Video video : movie.getVideos()) {
            for (Long serverId : ids) {
                VideoServer videoServer = new VideoServer(new VideoServerId(video.getId(), serverId));
                var server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server", serverId.toString()));
                videoServer.setServer(server);
                videoServer.setVideo(video);
                videoServers.add(videoServer);
            }
        }

        return videoServers;
    }*/

    public Set<CollectionVod> convertToCollections(Set<Long> ids, Movie movie) {
        Set<CollectionVod> collectionVodSet = new HashSet<>();

        for (Long id : ids) {
            CollectionVod collectionVod = new CollectionVod(new CollectionVodId(id, movie.getId()));
            var collection = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Collection", id.toString()));
            collectionVod.setVod(movie);
            collectionVod.setCollection(collection);
            collectionVodSet.add(collectionVod);
        }

        return collectionVodSet;
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
            video.setVideoServers(Set.of(videoServer));
            movie.setVideo(video);
            movies.add(movie);
        }
        return movies;
    }

    public abstract MovieInfo convertToMovieInfo(MovieInfoInsertView movieInfo);
}
