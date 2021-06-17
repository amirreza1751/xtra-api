package com.xtra.api.mapper.admin;

import com.xtra.api.model.*;
import com.xtra.api.projection.admin.channel.*;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.repository.CollectionStreamRepository;
import com.xtra.api.repository.EpgChannelRepository;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.model.collection.CollectionStream;
import com.xtra.api.model.collection.CollectionStreamId;
import com.xtra.api.model.epg.EpgChannel;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.stream.*;
import com.xtra.api.projection.admin.epg.EpgDetails;
import com.xtra.api.repository.*;
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
    @Autowired
    private ConnectionRepository connectionRepository;

    public abstract Channel convertToEntity(ChannelInsertView channelView);

    @AfterMapping
    void convertServerIdsAndCollectionIds(final ChannelInsertView channelView, @MappingTarget final Channel channel) {
        var epgDetails = channelView.getEpgDetails();
        if (epgDetails != null && epgDetails.getEpgId() != null) {
            var epgChannel = epgChannelRepository.findByNameAndLanguageAndEpgFile_Id(epgDetails.getName(), epgDetails.getLanguage(), epgDetails.getEpgId())
                    .orElseThrow(() -> new EntityNotFoundException("epg channel"));
            channel.setEpgChannel(epgChannel);
        }
        var serverIds = channelView.getServers();
        if (serverIds != null) {
            Set<StreamServer> streamServers = new HashSet<>();
            for (Long serverId : serverIds) {
                var server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server", serverId.toString()));
                StreamServer streamServer = new StreamServer(new StreamServerId(null, serverId));
                streamServer.setStreamDetails(new StreamDetails("", "", "", "", "", "", "", "", StreamStatus.OFFLINE));
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
        epgDetails.setEpgId(epgChannel.getEpgFile().getId());
        epgDetails.setLanguage(epgChannel.getLanguage());
        epgDetails.setName(epgChannel.getName());
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

    public AdvancedStreamOptions setASO(AdvancedStreamOptions aso, ChannelBatchInsertView channelBatchInsertView) {
        if (channelBatchInsertView.getNativeFrames() != null)
            aso.setNativeFrames(Boolean.parseBoolean(channelBatchInsertView.getNativeFrames()));

        if (channelBatchInsertView.getGeneratePts() != null)
            aso.setGeneratePts(Boolean.parseBoolean(channelBatchInsertView.getGeneratePts()));

        if (channelBatchInsertView.getStreamAllCodecs() != null)
            aso.setStreamAllCodecs(Boolean.parseBoolean(channelBatchInsertView.getStreamAllCodecs()));

        if (channelBatchInsertView.getAllowRecording() != null)
            aso.setAllowRecording(Boolean.parseBoolean(channelBatchInsertView.getAllowRecording()));

        if (channelBatchInsertView.getOutputRTMP() != null)
            aso.setOutputRTMP(Boolean.parseBoolean(channelBatchInsertView.getOutputRTMP()));

        if (channelBatchInsertView.getDirectSource() != null)
            aso.setDirectSource(Boolean.parseBoolean(channelBatchInsertView.getDirectSource()));

        if (channelBatchInsertView.getCustomChannelSID() != null)
            aso.setCustomChannelSID(channelBatchInsertView.getCustomChannelSID());

        if (channelBatchInsertView.getMinuteDelay() != null)
            aso.setMinuteDelay(channelBatchInsertView.getMinuteDelay());

        if (channelBatchInsertView.getUserAgent() != null)
            aso.setUserAgent(channelBatchInsertView.getUserAgent());

        if (channelBatchInsertView.getHttpProxy() != null)
            aso.setHttpProxy(channelBatchInsertView.getHttpProxy());

        if (channelBatchInsertView.getCookie() != null)
            aso.setCookie(channelBatchInsertView.getCookie());

        if (channelBatchInsertView.getHeaders() != null)
            aso.setHeaders(channelBatchInsertView.getHeaders());

        if (channelBatchInsertView.getTranscodingProfile() != null)
            aso.setTranscodingProfile(channelBatchInsertView.getTranscodingProfile());

        return aso;
    }

    public abstract ChannelStart convertToChannelStart(Channel channel, int selectedSource);

    public abstract ChannelStart convertToChannelStart(Stream stream, int selectedSource);

    @AfterMapping
    ChannelStart setChannelInput(Channel channel, @MappingTarget ChannelStart channelStart, int selectedSource) {
        channelStart.setStreamInput(channel.getStreamInputs().get(selectedSource));
        return channelStart;
    }

}
