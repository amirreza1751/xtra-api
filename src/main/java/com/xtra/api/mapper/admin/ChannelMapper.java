package com.xtra.api.mapper.admin;

import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.channel.ChannelInfo;
import com.xtra.api.projection.admin.channel.ChannelInsertView;
import com.xtra.api.projection.admin.channel.ChannelServerInfo;
import com.xtra.api.projection.admin.channel.ChannelView;
import com.xtra.api.projection.admin.epg.EpgDetails;
import com.xtra.api.repository.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

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
    @Autowired
    private ConnectionRepository connectionRepository;

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
                collectionStream.setId(new CollectionStreamId(id, null));
                collectionStream.setCollection(col);
                collectionStream.setStream(channel);
                collectionStreams.add(collectionStream);
            }
            channel.setCollectionAssigns(collectionStreams);
        }
    }

    @Mapping(source = "streamServers", target = "servers")
    @Mapping(source = "collectionAssigns", target = "collections")
    @Mapping(source = "epgChannel", target = "epgDetails")
    public abstract ChannelView convertToView(Channel channel);

    public EpgDetails map(EpgChannel epgChannel) {
        if (epgChannel == null)
            return null;
        EpgDetails epgDetails = new EpgDetails();
        epgDetails.setEpgId(epgChannel.getId().getEpgId());
        epgDetails.setLanguage(epgChannel.getId().getLanguage());
        epgDetails.setName(epgChannel.getId().getName());
        return epgDetails;
    }

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

    public Set<CollectionStream> convertToCollections(Set<Long> ids, Channel channel) {
        Set<CollectionStream> collectionStreamSet = new HashSet<>();

        for (Long id : ids) {
            CollectionStream collectionStream = new CollectionStream(new CollectionStreamId(id, channel.getId()));
            var collection = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Collection", id.toString()));
            collectionStream.setStream(channel);
            collectionStream.setCollection(collection);
            collectionStreamSet.add(collectionStream);
        }

        return collectionStreamSet;
    }

    public Set<Long> convertToCollectionIds(Set<CollectionStream> collectionStreams) {
        if (collectionStreams == null) return null;
        return collectionStreams.stream().map(collectionStream -> collectionStream.getCollection().getId()).collect(Collectors.toSet());
    }

    @Mapping(source = "streamServers", target = "channelInfos")
    @Mapping(source = "channel", target = "totalUsers", qualifiedByName = "getTotalUsers")
    @Mapping(target = "epg", expression = "java(channel.getEpgChannel()!=null)")
    public abstract ChannelInfo convertToChannelInfo(Channel channel);

    @Named("getTotalUsers")
    long getTotalUsers(Channel channel) {
        return connectionRepository.countAllByStreamId(channel.getId());
    }

    @Mapping(source = "streamDetails", target = "users", qualifiedByName = "getUsers")
    @Mapping(source = "streamServer.server.name", target = "serverName")
    public abstract ChannelServerInfo toStreamDetailsView(StreamDetails streamDetails);

    @Named("getUsers")
    long getUsers(StreamDetails streamDetails) {
        var streamServer = streamDetails.getStreamServer();
        return connectionRepository.countAllByServerIdAndStreamId(streamServer.getServer().getId(), streamServer.getStream().getId());
    }


    public Set<ChannelServerInfo> convertToInfosMap(Set<StreamServer> streamServers) {
        if (streamServers == null) return null;
        return streamServers.stream().map(streamServer -> toStreamDetailsView(streamServer.getStreamDetails())).collect(Collectors.toSet());
    }
}
