package com.xtra.api.mapper;

import com.xtra.api.model.*;
import com.xtra.api.projection.ChannelView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ChannelMapper {


    /*@Mapping(source = "collections", target = "collectionAssigns")
    @Mapping(source = "servers", target = "streamServers")*/
    public abstract Channel convertToEntity(ChannelView channelView);

    /*public Set<CollectionStream> addCollectionRelations(List<Long> collectionIds) {
        if (collectionIds == null) return null;
        var collectionAssigns = new HashSet<CollectionStream>();
        for (var id : collectionIds) {
            collectionAssigns.add(new CollectionStream(new CollectionStreamId(id, null)));
        }
        return collectionAssigns;
    }

    public List<StreamServer> convertServerIds(Set<Long> ids) {
        if (ids == null) return null;
        List<StreamServer> servers = new ArrayList<>();
        for (var id : ids) {
            servers.add(new StreamServer(new StreamServerId(null, id)));
        }
        return servers;
    }*/

    @Mapping(source = "streamServers", target = "servers")
    @Mapping(source = "collectionAssigns", target = "collections")
    public abstract ChannelView convertToDto(Channel channel);

    public Set<Long> convertToServerIds(List<StreamServer> streamServers) {
        if (streamServers == null) return null;
        return streamServers.stream().map(streamServer -> streamServer.getId().getServerId()).collect(Collectors.toSet());
    }

    public Set<Long> convertToCollectionIds(Set<CollectionStream> collectionStreams) {
        if (collectionStreams == null) return null;
        return collectionStreams.stream().map(collectionStream -> collectionStream.getCollection().getId()).collect(Collectors.toSet());
    }
}
