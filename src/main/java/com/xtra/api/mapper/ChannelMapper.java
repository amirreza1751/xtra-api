package com.xtra.api.mapper;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.projection.ChannelInsertView;
import com.xtra.api.projection.ChannelView;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.repository.CollectionStreamRepository;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.service.CollectionService;
import com.xtra.api.service.ServerService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ChannelMapper {

    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private CollectionStreamRepository collectionStreamRepository;
    @Autowired
    private CollectionRepository collectionRepository;

    public abstract Channel convertToEntity(ChannelInsertView channelView);

    @AfterMapping
    void convertServerIdsAndCollectionIds(final ChannelInsertView channelView, @MappingTarget final Channel channel) {
        var serverIds = channelView.getServers();
        if (serverIds != null) {
            Set<StreamServer> streamServers = new HashSet<>();
            for (Long serverId : serverIds) {
                StreamServer streamServer = new StreamServer();
                streamServer.setId(new StreamServerId(channel.getId(), serverId));

                var server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server", serverId.toString()));
                streamServer.setServer(server);
                streamServer.setStream(channel);
                streamServers.add(streamServer);
            }
            channel.setStreamServers(streamServers);
        }

        var collectionIds = channelView.getCollections();
        if (collectionIds != null) {
            Set<CollectionStream> collectionStreams = new HashSet<>();
            for (var id : collectionIds) {
                var collectionStream = new CollectionStream(new CollectionStreamId(id, channel.getId()));
                var orderCount = collectionStreamRepository.countAllByIdCollectionId(id);
                collectionStream.setOrder(orderCount + 1);
                collectionStream.setStream(channel);
                var col = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("collection", id.toString()));
                collectionStream.setCollection(col);
                collectionStreams.add(collectionStream);
            }
            channel.setCollectionAssigns(collectionStreams);
        }
    }

    @Mapping(source = "streamServers", target = "servers")
    @Mapping(source = "collectionAssigns", target = "collections")
    public abstract ChannelView convertToView(Channel channel);

    public Set<Long> convertToServerIds(Set<StreamServer> streamServers) {
        if (streamServers == null) return null;
        return streamServers.stream().map(streamServer -> streamServer.getId().getServerId()).collect(Collectors.toSet());
    }

    public Set<Long> convertToCollectionIds(Set<CollectionStream> collectionStreams) {
        if (collectionStreams == null) return null;
        return collectionStreams.stream().map(collectionStream -> collectionStream.getCollection().getId()).collect(Collectors.toSet());
    }
}
