package com.xtra.api.mapper.admin;

import com.google.gson.Gson;
import com.xtra.api.model.category.CategoryStream;
import com.xtra.api.model.category.CategoryStreamId;
import com.xtra.api.model.collection.CollectionStream;
import com.xtra.api.model.collection.CollectionStreamId;
import com.xtra.api.model.epg.EpgChannel;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.stream.*;
import com.xtra.api.projection.admin.channel.*;
import com.xtra.api.projection.admin.epg.EpgDetails;
import com.xtra.api.repository.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

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
    @Autowired
    private CategoryRepository categoryRepository;

    @Mapping(target = "streamServers", ignore = true)
    @Mapping(target = "collectionAssigns", ignore = true)
    @Mapping(target = "categories", ignore = true)
    public abstract Channel convertToEntity(ChannelInsertView channelView);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "streamServers", ignore = true)
    @Mapping(target = "collectionAssigns", ignore = true)
    @Mapping(target = "categories", ignore = true)
    public abstract Channel convertToEntity(ChannelInsertView channelView, @MappingTarget final Channel channel);

    @AfterMapping
    void convertServerIdsAndCollectionIds(final ChannelInsertView channelView, @MappingTarget final Channel channel) {
        if (channelView.getOnDemand() != null && channelView.getCatchUp() != null && channelView.getCatchUp().equals(channelView.getOnDemand())) {
            throw new RuntimeException("The on-demand server and catch-up server must not be the same.");
        }
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
                StreamServer streamServer = new StreamServer(new StreamServerId(channel.getId(), serverId));
                //insert catch-up server
                if (channelView.getCatchUp() != null && server.getId().equals(channelView.getCatchUp())) {
                    streamServer.setCatchUp(true);
                    streamServer.setCatchUpDays(channelView.getCatchUpDays());
                }
                //insert on-demand server
                if (channelView.getOnDemand() != null && server.getId().equals(channelView.getOnDemand())) {
                    streamServer.setOnDemand(true);
                }
                streamServer.setStreamDetails(new StreamDetails("", "", "", "", "", "", "", "", StreamStatus.OFFLINE));
                streamServer.setServer(server);
                streamServer.setStream(channel);
                streamServers.add(streamServer);
            }
            channel.getStreamServers().retainAll(streamServers);
            channel.getStreamServers().addAll(streamServers);
        }

        var collectionIds = channelView.getCollections();
        if (collectionIds != null) {
            Set<CollectionStream> collectionStreams = new HashSet<>();
            for (var id : collectionIds) {
                var collectionStream = new CollectionStream(new CollectionStreamId(id, channel.getId()));
                var orderCount = collectionStreamRepository.countAllByIdCollectionId(id);
                collectionStream.setOrder(orderCount);
                var col = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("collection", id.toString()));
                collectionStream.setCollection(col);
                collectionStream.setStream(channel);
                collectionStreams.add(collectionStream);
            }
            channel.getCollectionAssigns().retainAll(collectionStreams);
            channel.getCollectionAssigns().addAll(collectionStreams);
        }

        var categoryIds = channelView.getCategories();
        if (categoryIds != null) {
            Set<CategoryStream> categoryStreams = new HashSet<>();
            for (var id : categoryIds) {
                var categoryStream = new CategoryStream();
                categoryStream.setId(new CategoryStreamId(id, channel.getId()));
                categoryStream.setStream(channel);
                categoryStream.setCategory(categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category", id)));
                categoryStreams.add(categoryStream);
            }
            channel.getCategories().retainAll(categoryStreams);
            channel.getCategories().addAll(categoryStreams);
        }
    }

    @Mapping(source = "epgChannel", target = "epgDetails")
    @Mapping(target = "collections", ignore = true)
    @Mapping(target = "servers", ignore = true)
    @Mapping(target = "categories", ignore = true)
    public abstract ChannelView convertToView(Channel channel);

    @AfterMapping
    protected void convertAdditionalFieldsForView(Channel channel, @MappingTarget final ChannelView channelView) {
        //@todo optimize query
        channelView.setServers(emptyIfNull(channel.getStreamServers())
                .stream().map(streamServer -> streamServer.getServer().getId()).collect(Collectors.toSet()));

        channelView.setCollections(emptyIfNull(channel.getCollectionAssigns())
                .stream().map(collectionStream -> collectionStream.getCollection().getId()).collect(Collectors.toSet()));

        channelView.setCategories(emptyIfNull(channel.getCategories())
                .stream().map(categoryStream -> categoryStream.getCategory().getId()).collect(Collectors.toSet()));


        var catchUp = emptyIfNull(channel.getStreamServers()).stream().filter(StreamServer::isCatchUp).findFirst();
        if (catchUp.isPresent()) {
            channelView.setCatchUp(catchUp.get().getServer().getId());
            channelView.setCatchUpDays(catchUp.get().getCatchUpDays());
        }
        var onDemand = channel.getStreamServers().stream().filter(StreamServer::isOnDemand).findFirst();
        onDemand.ifPresent(streamServer -> channelView.setOnDemand(streamServer.getServer().getId()));
    }


    public EpgDetails map(EpgChannel epgChannel) {
        if (epgChannel == null)
            return null;
        EpgDetails epgDetails = new EpgDetails();
        epgDetails.setEpgId(epgChannel.getEpgFile().getId());
        epgDetails.setLanguage(epgChannel.getLanguage());
        epgDetails.setName(epgChannel.getName());
        return epgDetails;
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

    public List<ChannelInsertView> addChannels(ChannelImportView importView) {
        List<ChannelInsertView> channelInsertViews = new ArrayList<>();
        try {
            List<String> names = new ArrayList<>();
            List<String> inputs = new ArrayList<>();
            MultipartFile document = importView.getDocument();
            InputStream inputStream = document.getInputStream();
            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines().forEach((line) -> {
                if (line.contains("tvg-name")) {
                    names.add(line.substring(line.indexOf(",") + 1));
                } else {
                    inputs.add(line);
                }
            });


            for (int i = 0; i < names.size(); i++) {
                ChannelInsertView newInsertView = new ChannelInsertView();
                newInsertView.setName(names.get(i));
                newInsertView.setNotes(importView.getNotes());
                Gson gson = new Gson();
                AdvancedStreamOptions aso = gson.fromJson(importView.getAdvancedStreamOptions(), AdvancedStreamOptions.class);
                newInsertView.setDaysToRestart(importView.getDaysToRestart());
                newInsertView.setTimeToRestart(LocalTime.parse(importView.getTimeToRestart()));
                List<String> streamInputs = new ArrayList<>();
                streamInputs.add(inputs.get(i));
                newInsertView.setStreamInputs(streamInputs);
                newInsertView.setServers(importView.getServers());
                newInsertView.setCollections(importView.getCollections());
                newInsertView.setAdvancedStreamOptions(aso);
                newInsertView.setOnDemand(importView.getOnDemand());

                channelInsertViews.add(newInsertView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return channelInsertViews;
    }

}
