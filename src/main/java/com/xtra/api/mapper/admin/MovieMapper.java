package com.xtra.api.mapper.admin;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.channel.ChannelInsertView;
import com.xtra.api.projection.admin.channel.ChannelView;
import com.xtra.api.projection.admin.movie.MovieInsertView;
import com.xtra.api.projection.admin.movie.MovieView;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.repository.CollectionStreamRepository;
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

    public abstract Movie convertToEntity(MovieInsertView movieView);

    @Mapping(source = "serverVods", target = "servers")
    @Mapping(source = "collectionAssigns", target = "collections")
    public abstract MovieView convertToView(Movie movie);

    @AfterMapping
    void convertServerIdsAndCollectionIds(final MovieInsertView movieView, @MappingTarget final Movie movie) {
        var serverIds = movieView.getServers();
        if (serverIds != null) {
            Set<ServerVod> serverVods = new HashSet<>();
            for (Long serverId : serverIds) {
                var server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server", serverId.toString()));
                ServerVod serverVod = new ServerVod(new ServerVodId(serverId, null));
                serverVod.setServer(server);
                serverVod.setVod(movie);
                serverVods.add(serverVod);
            }
            movie.setServerVods(serverVods);
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

    public Set<Long> convertToServerIds(Set<ServerVod> serverVods) {
        if (serverVods == null) return null;
        return serverVods.stream().map(serverVod -> serverVod.getServer().getId()).collect(Collectors.toSet());
    }


}
