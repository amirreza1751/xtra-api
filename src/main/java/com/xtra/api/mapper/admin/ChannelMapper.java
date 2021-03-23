package com.xtra.api.mapper.admin;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.channel.ChannelInfo;
import com.xtra.api.projection.admin.channel.ChannelInsertView;
import com.xtra.api.projection.admin.channel.ChannelView;
import com.xtra.api.projection.admin.channel.MergedChannelInfo;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.repository.CollectionStreamRepository;
import com.xtra.api.repository.EpgChannelRepository;
import com.xtra.api.repository.ServerRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ChannelMapper {

    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private CollectionStreamRepository collectionStreamRepository;
    @Autowired
    private CollectionRepository collectionRepository;
    @Autowired
    private EpgChannelRepository epgChannelRepository;

    @Mapping(target = "streamType", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Channel convertToEntity(ChannelInsertView channelView);

    @AfterMapping
    void convertServerIdsAndCollectionIds(final ChannelInsertView channelView, @MappingTarget final Channel channel) {
        var epgDetails = channelView.getEpgDetails();
        if (epgDetails != null) {
            var epgChannel = epgChannelRepository.findById(new EpgChannelId(epgDetails.getName(), epgDetails.getEpgId(), epgDetails.getLanguage())).orElseThrow(() -> new EntityNotFoundException("epg channel"));
            channel.setEpgChannel(epgChannel);
        }
        var serverIds = channelView.getServers();
        if (serverIds != null) {
            Set<StreamServer> streamServers = new HashSet<>();
            for (Long serverId : serverIds) {
                var server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server", serverId.toString()));
                StreamServer streamServer = new StreamServer(new StreamServerId(null, serverId));
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
                var collectionStream = new CollectionStream();
                var orderCount = collectionStreamRepository.countAllByIdCollectionId(id);
                collectionStream.setOrder(orderCount + 1);
                var col = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("collection", id.toString()));
                collectionStream.setCollection(col);
                collectionStream.setStream(channel);
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
        return streamServers.stream().map(streamServer -> streamServer.getServer().getId()).collect(Collectors.toSet());
    }

    public Set<StreamServer> convertToServers(Set<Long> ids, Channel channel) {
        Set<StreamServer> streamServers = new HashSet<>();

        for (Long id : ids) {
            StreamServer streamServer = new StreamServer(new StreamServerId(channel.getId(), id));
            var server = serverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Server", id.toString()));
            streamServer.setStream(channel);
            streamServer.setServer(server);
            streamServers.add(streamServer);
        }

        return streamServers;
    }

    public Set<Long> convertToCollectionIds(Set<CollectionStream> collectionStreams) {
        if (collectionStreams == null) return null;
        return collectionStreams.stream().map(collectionStream -> collectionStream.getCollection().getId()).collect(Collectors.toSet());
    }

    @Mapping(source = "streamServers", target = "channelInfos")
    public abstract ChannelInfo convertToChannelInfo(Channel channel);

    public Set<MergedChannelInfo> convertToInfosMap(Set<StreamServer> streamServers) {
        if (streamServers == null) return null;
        Set<MergedChannelInfo> infos = new HashSet<>();
        streamServers.forEach(streamServer -> infos.add(new MergedChannelInfo(streamServer.getStreamInfo(), streamServer.getProgressInfo())));
        return infos;
    }
}
