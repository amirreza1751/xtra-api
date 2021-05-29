package com.xtra.api.mapper.admin;

import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.movie.MovieInsertView;
import com.xtra.api.projection.admin.movie.MovieView;
import com.xtra.api.projection.admin.server.ServerView;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.repository.CollectionVodRepository;
import com.xtra.api.repository.ServerRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
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
    private ServerMapper serverMapper;

    public abstract Movie convertToEntity(MovieInsertView movieView);

//    @Mapping(source = "videos.videoServers.serverId", target = "servers")
    @Mapping(source = "collectionAssigns", target = "collections")
    public abstract MovieView convertToView(Movie movie);

    @AfterMapping
    void convertServerIdsAndCollectionIds(final MovieInsertView movieView, @MappingTarget final Movie movie) {
        var serverIds = movieView.getServers();
        if (serverIds != null) {
            Set<VideoServer> videoServers = new HashSet<>();
            for (Video video : movie.getVideos()) {
                for (Long serverId : serverIds) {
                    var server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server", serverId.toString()));
                    VideoServer videoServer = new VideoServer(new VideoServerId(null, serverId));
                    videoServer.setServer(server);
                    videoServer.setVideo(video);
                    videoServers.add(videoServer);
                }
                video.setVideoServers(videoServers);
            }
        }

        var collectionIds = movieView.getCollections();
        if (collectionIds != null) {
            Set<CollectionVod> collectionVods = new HashSet<>();
            for (var id : collectionIds) {
                var collectionVod = new CollectionVod();
                var orderCount = collectionVodRepository.countAllByIdCollectionId(id);
                collectionVod.setOrder(orderCount + 1);
                var col = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("collection", id.toString()));
                collectionVod.setCollection(col);
                collectionVod.setVod(movie);
                collectionVods.add(collectionVod);
            }
            movie.setCollectionAssigns(collectionVods);
        }
    }

    public Set<Long> convertToCollectionIds(Set<CollectionVod> collectionVods) {
        if (collectionVods == null) return null;
        return collectionVods.stream().map(collectionVod -> collectionVod.getCollection().getId()).collect(Collectors.toSet());
    }

//    public Set<Long> convertToServerIds(Set<ServerVod> serverVods) {
//        if (serverVods == null) return null;
//        return serverVods.stream().map(serverVod -> serverVod.getServer().getId()).collect(Collectors.toSet());
//    }

    public Set<VideoServer> convertToVideoServers(Set<Long> ids, Movie movie) {
        Set<VideoServer> videoServers = new HashSet<>();
        for (Video video : movie.getVideos()) {
            for (Long serverId : ids) {
                VideoServer videoServer = new VideoServer(new VideoServerId(video.getId(), serverId));
                var server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server", serverId.toString()));
                videoServer.setServer(server);
                videoServer.setVideo(video);
                videoServers.add(videoServer);
            }
            video.setVideoServers(videoServers);
        }

        return videoServers;
    }

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
    @AfterMapping
    public void convertToServers(final Movie movie, @MappingTarget MovieView movieView){
        Set<Long> servers = new HashSet<>();
        for (Video video : movie.getVideos()){
            servers.addAll(video.getVideoServers().stream().map(videoServer -> videoServer.getServer().getId()).collect(Collectors.toSet()));
        }
        movieView.setServers(servers);
    }

}
