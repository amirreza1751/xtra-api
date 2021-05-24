package com.xtra.api.service.admin;

import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.mapper.admin.ChannelMapper;
import com.xtra.api.mapper.admin.ChannelStartMapper;
import com.xtra.api.mapper.system.StreamMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.StreamInputPair;
import com.xtra.api.projection.admin.channel.*;
import com.xtra.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.generateRandomString;
import static com.xtra.api.util.Utilities.wrapSearchString;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class ChannelService extends StreamService<Channel, ChannelRepository> {
    private final ServerService serverService;
    private final LoadBalancingService loadBalancingService;
    private final ChannelMapper channelMapper;
    private final EpgChannelRepository epgChannelRepository;
    private final StreamInputRepository streamInputRepository;
    private final ServerRepository serverRepository;
    private final ConnectionRepository connectionRepository;


    @Autowired
    public ChannelService(ChannelRepository repository, ServerService serverService, LoadBalancingService loadBalancingService, ChannelStartMapper channelStartMapper, ChannelMapper channelMapper, EpgChannelRepository epgChannelRepository, StreamInputRepository streamInputRepository, StreamMapper streamMapper, ServerRepository serverRepository, ConnectionRepository connectionRepository) {
        super(repository, "Channel", serverService, streamMapper);
        this.serverService = serverService;
        this.loadBalancingService = loadBalancingService;
        this.channelMapper = channelMapper;
        this.epgChannelRepository = epgChannelRepository;
        this.streamInputRepository = streamInputRepository;
        this.serverRepository = serverRepository;
        this.connectionRepository = connectionRepository;
    }


    public Page<ChannelInfo> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(channelMapper::convertToChannelInfo);
    }

    public ChannelView getViewById(Long id) {
        return channelMapper.convertToView(findByIdOrFail(id));
    }

    @Override
    protected Page<Channel> findWithSearch(String search, Pageable page) {
        search = wrapSearchString(search);
        return repository.findByNameLikeOrNotesLike(search, search, page);
    }

    public ChannelView add(ChannelInsertView insertView, boolean start) {
        return channelMapper.convertToView(insert(channelMapper.convertToEntity(insertView), start));
    }

    public Channel insert(Channel channel, boolean start) {
        //@todo check validation check
        String token;
        do {
            token = generateRandomString(10, 16, false);
        } while (repository.existsChannelByStreamToken(token));

        channel.setStreamToken(token);
        channel.setStreamInputs(emptyIfNull(channel.getStreamInputs()).stream().distinct().collect(Collectors.toList()));
        var savedEntity = repository.save(channel);
        var serverIds = emptyIfNull(savedEntity.getStreamServers()).stream().map(streamServer -> streamServer.getServer().getId()).collect(Collectors.toSet());
        if (start) {
            //@todo start servers
        }
        return savedEntity;
    }

    public ChannelView save(Long id, ChannelInsertView channelView, boolean restart) {
        return channelMapper.convertToView(update(id, channelMapper.convertToEntity(channelView), restart));
    }

    public void saveAll(ChannelBatchInsertView channelBatchInsertView, boolean restart) {
        var channelIds = channelBatchInsertView.getChannelIds();
        var serverIds = channelBatchInsertView.getServerIds();
        var collectionIds = channelBatchInsertView.getCollectionIds();

        if (channelIds != null) {
            for (Long channelId : channelIds) {
                var channel = repository.findById(channelId).orElseThrow(() -> new EntityNotFoundException("Channel", channelId.toString()));

//                if (channelBatchInsertView.getReadNative() != null)
//                    channel.setReadNative(Boolean.parseBoolean(channelBatchInsertView.getReadNative()));
//                if (channelBatchInsertView.getStreamAll() != null)
//                    channel.setStreamAll(Boolean.parseBoolean(channelBatchInsertView.getStreamAll()));
//                if (channelBatchInsertView.getDirectSource() != null)
//                    channel.setDirectSource(Boolean.parseBoolean(channelBatchInsertView.getDirectSource()));
//                if (channelBatchInsertView.getGenTimestamps() != null)
//                    channel.setGenTimestamps(Boolean.parseBoolean(channelBatchInsertView.getGenTimestamps()));
//                if (channelBatchInsertView.getRtmpOutput() != null)
//                    channel.setRtmpOutput(Boolean.parseBoolean(channelBatchInsertView.getRtmpOutput()));

                if (collectionIds.size() > 0) {
                    Set<CollectionStream> collectionStreamSet = channelMapper.convertToCollections(collectionIds, channel);
                    if (!channelBatchInsertView.getKeepCollections())
                        channel.getCollectionAssigns().retainAll(collectionStreamSet);
                    channel.getCollectionAssigns().addAll(collectionStreamSet);
                }

                if (serverIds.size() > 0) {
                    Set<StreamServer> streamServers = channelMapper.convertToServers(serverIds, channel);
                    if (!channelBatchInsertView.getKeepServers())
                        channel.getStreamServers().retainAll(streamServers);
                    channel.getStreamServers().addAll(streamServers);
                }

                repository.save(channel);
            }
        }
    }

    public void deleteAll(ChannelBatchDeleteView channelBatchDeleteView) {
        var channelIds = channelBatchDeleteView.getChannelIds();
        if (channelIds != null) {
            for (Long channelId : channelIds) {
                deleteOrFail(channelId);
            }
        }
    }

    public Channel update(Long id, Channel channel, boolean restart) {
        //@todo check validation check
        Channel oldChannel = findByIdOrFail(id);
        copyProperties(channel, oldChannel, "id", "currentInput", "currentConnections", "collections", "lineActivities", "streamServers", "collectionAssigns", "advancedStreamOptions");

        //remove old servers from channel and add new ones
        if (channel.getStreamServers() != null) {
            oldChannel.getStreamServers().clear();
            oldChannel.getStreamServers().addAll(channel.getStreamServers().stream().peek(streamServer -> {
                streamServer.setId(new StreamServerId(oldChannel.getId(), streamServer.getServer().getId()));
                streamServer.setStream(oldChannel);
            }).collect(Collectors.toSet()));
        }
//        //remove old collections from channel and add new ones
        if (channel.getCollectionAssigns() != null) {
            oldChannel.getCollectionAssigns().clear();
            oldChannel.getCollectionAssigns().addAll(channel.getCollectionAssigns().stream().peek(collectionStream -> {
                collectionStream.setId(new CollectionStreamId(collectionStream.getCollection().getId(), oldChannel.getId()));
                collectionStream.setStream(oldChannel);
            }).collect(Collectors.toSet()));
        }

        if (channel.getStreamInputs() != null) {
            oldChannel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));
        }

        if (channel.getAdvancedStreamOptions() != null) {
            var oldOptions = oldChannel.getAdvancedStreamOptions();
            copyProperties(channel.getAdvancedStreamOptions(), oldOptions, "id");
            oldChannel.setAdvancedStreamOptions(oldOptions);
        }
        var savedEntity = repository.save(oldChannel);
        if (savedEntity.getStreamServers() != null) {
            var serverIds = savedEntity.getStreamServers().stream().map(streamServer -> streamServer.getServer().getId()).collect(Collectors.toSet());
            if (restart) {
                //@todo call stream restart on servers
            }
        }
        return savedEntity;
    }

    public void updateServersList(Long channel_id, Long[] serverIds) {
        //@todo update servers list
    }

    public String playChannel(String stream_token, String line_token, HttpServletRequest request) {
        ArrayList<Server> servers = loadBalancingService.findAvailableServers(stream_token);
        Server server = loadBalancingService.findLeastConnServer(servers);

        /*This forEach below checks that if the requested stream
        is on-demand and offline at the same time, a start request will be sent to the related server.*/
        server.getStreamServers().forEach(streamServer -> {
            if (checkOnDemandStatus(stream_token, streamServer)){
                serverService.sendStartRequest(streamServer.getId().getStreamId(), server);
            }
        });
        return "http://" + server.getIp() + ":" + server.getCorePort() + "/live/" + line_token + "/" + stream_token + "/m3u8";
    }

    public boolean checkOnDemandStatus(String stream_token, StreamServer streamServer) {
        if (streamServer.getIsOnDemand() == null){
            return false;
        }
        if (streamServer.getStreamDetails()!= null){
            boolean status = streamServer.getStream().getStreamToken().equals(stream_token) &&
                    streamServer.getIsOnDemand() &&
                    streamServer.getStreamDetails().getStreamStatus() == null ||
                    !streamServer.getStreamDetails().getStreamStatus().equals(StreamStatus.ONLINE);
            return status;
        } else return streamServer.getStream().getStreamToken().equals(stream_token) && streamServer.getIsOnDemand();
    }

    public int changeSource(Long streamId, String token) {
        Optional<Server> srv = serverService.findByServerToken(token);
        if (srv.isEmpty()) {
            throw new RuntimeException("Server is invalid. Check your ip and port.");
        }
        Server server = srv.get();
        Long serverId = server.getId();

        Optional<Channel> ch = this.repository.findById(streamId);
        if (ch.isEmpty()) {
            throw new RuntimeException("Channel not found");
        }
        Channel channel = ch.get();
        StreamServer streamServer = new StreamServer(new StreamServerId(streamId, serverId));
        Set<StreamServer> streamServers = channel.getStreamServers();
        int nextSource = 0;
        for (StreamServer item : streamServers) {
            if (item.equals(streamServer)) {
                nextSource = (item.getSelectedSource() + 1) % channel.getStreamInputs().size();
                item.setSelectedSource(nextSource);
                break;
            }
        }
        channel.setStreamServers(streamServers);
        repository.save(channel);
        this.restartOrFail(streamId, Collections.singletonList(serverId));
        return nextSource;
    }

    public ChannelInfo getChannelInfo(Long channelId) {
        return channelMapper.convertToChannelInfo(findByIdOrFail(channelId));
    }

    public Channel channelStart(Long channelId) {
        return this.findByIdOrFail(channelId);
    }

    public void setEpgRecord(Long id, EpgChannelId epgChannelId) {
        var epgChannel = epgChannelRepository.findById(epgChannelId).orElseThrow(() -> new EntityNotFoundException("Epg channel", epgChannelId));
        var channel = findByIdOrFail(id);
        channel.setEpgChannel(epgChannel);
        repository.save(channel);
    }

    public void changeDns(StreamInputPair streamInputPair) {
        List<StreamInput> streamInputs = streamInputRepository.findAllByUrl(streamInputPair.getOldDns());
//        if (streamInputs.isPresent()){
//            streamInputs.ifPresent(streamInput -> {
//                streamInput.setUrl(streamInputPair.getNewDns());
//                streamInputRepository.save(streamInput);
//            });
//        }
        if (!streamInputs.isEmpty())
            for (StreamInput streamInput : streamInputs) {
                streamInput.setUrl(streamInputPair.getNewDns());
                streamInputRepository.save(streamInput);
            }
    }

    public void autoStopOnDemandChannels(){
        List<Server> servers = serverRepository.findAll();
        List<Long> streamIds = new ArrayList<>();
        if (servers.size() > 0){
            for (Server server : servers){
                checkOnDemandConnections(streamIds, server);
                if (streamIds.size() > 0){
                    serverService.sendPostRequest("http://" + server.getIp() + ":" + server.getCorePort() + "/streams/batch-stop", String.class, streamIds);
                }
                streamIds.clear();
            }
        }
    }

    private void checkOnDemandConnections(List<Long> streamIds, Server server) {
        int connections;
        for (StreamServer streamServer : server.getStreamServers()){
            connections = connectionRepository.countAllByServerIdAndStreamId(server.getId(), streamServer.getStream().getId());
            if (streamServer.getIsOnDemand() != null && streamServer.getStreamDetails() != null){
                if (streamServer.getIsOnDemand() && connections == 0 && streamServer.getStreamDetails().getStreamStatus().equals(StreamStatus.ONLINE)){
                    streamIds.add(streamServer.getStream().getId());
                }
            }
        }
    }
}
