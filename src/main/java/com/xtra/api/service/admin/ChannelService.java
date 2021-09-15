package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.ChannelMapper;
import com.xtra.api.mapper.system.StreamMapper;
import com.xtra.api.model.collection.CollectionStream;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.*;
import com.xtra.api.model.user.QCreditLog;
import com.xtra.api.projection.admin.StreamInputPair;
import com.xtra.api.projection.admin.channel.*;
import com.xtra.api.projection.admin.epg.EpgDetails;
import com.xtra.api.repository.*;
import com.xtra.api.repository.filter.ChannelFilter;
import com.xtra.api.util.OptionalBooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.generateRandomString;
import static com.xtra.api.util.Utilities.wrapSearchString;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class ChannelService extends StreamBaseService<Channel, ChannelRepository> {
    private final ServerService serverService;
    private final LoadBalancingService loadBalancingService;
    private final ChannelMapper channelMapper;
    private final EpgChannelRepository epgChannelRepository;
    private final StreamInputRepository streamInputRepository;
    private final ServerRepository serverRepository;
    private final ConnectionRepository connectionRepository;
    private final QChannel channel = QChannel.channel;



    @Autowired
    public ChannelService(ChannelRepository repository, ServerService serverService, LoadBalancingService loadBalancingService,
                          ChannelMapper channelMapper, EpgChannelRepository epgChannelRepository,
                          StreamInputRepository streamInputRepository, StreamMapper streamMapper,
                          ServerRepository serverRepository, ConnectionRepository connectionRepository,
                          StreamServerRepository streamServerRepository) {
        super(repository, "Channel", serverService, streamMapper, streamServerRepository);
        this.serverService = serverService;
        this.loadBalancingService = loadBalancingService;
        this.channelMapper = channelMapper;
        this.epgChannelRepository = epgChannelRepository;
        this.streamInputRepository = streamInputRepository;
        this.serverRepository = serverRepository;
        this.connectionRepository = connectionRepository;
    }


    public Page<ChannelInfo> getAll(int pageNo, int pageSize, String sortBy, String sortDir, ChannelFilter filter) {
        var predicate = new OptionalBooleanBuilder(channel.isNotNull())
                .notNullAnd(channel.name::contains, filter.getName())
                .build();
        var search = filter.getSearch();
        if (search != null) {
            predicate = predicate.andAnyOf(
                    channel.name.containsIgnoreCase(search),
                    channel.notes.containsIgnoreCase(search),
                    channel.streamInputs.any().containsIgnoreCase(search)
            );
        }
        return repository.findAll(predicate, getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(channelMapper::convertToChannelInfo);
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
        String token;
        do {
            token = generateRandomString(10, 16, false);
        } while (repository.existsChannelByStreamToken(token));
        channel.setStreamToken(token);
        channel.setStreamInputs(emptyIfNull(channel.getStreamInputs()).stream().distinct().collect(Collectors.toList()));
        var savedEntity = repository.save(channel);
        var serverIds = emptyIfNull(savedEntity.getStreamServers()).stream().map(streamServer -> streamServer.getServer().getId()).collect(Collectors.toSet());
        if (start) {
            startStreamOnServers(channel, serverIds);
        }
        return savedEntity;
    }

    public ChannelView save(Long id, ChannelInsertView channelView, boolean restart) {
        return channelMapper.convertToView(update(id, channelMapper.convertToEntity(channelView, findByIdOrFail(id)), restart));
    }

    public Channel update(Long id, Channel channel, boolean restart) {
        if (channel.getStreamInputs() != null) {
            channel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));
        }

        if (channel.getAdvancedStreamOptions() != null) {
            var oldOptions = channel.getAdvancedStreamOptions();
            copyProperties(channel.getAdvancedStreamOptions(), oldOptions, "id");
            channel.setAdvancedStreamOptions(oldOptions);
        }
        var savedEntity = repository.save(channel);
        if (savedEntity.getStreamServers() != null) {
            var serverIds = savedEntity.getStreamServers().stream().map(streamServer -> streamServer.getServer().getId()).collect(Collectors.toSet());
            if (restart) {
                startStreamOnServers(channel, serverIds);
            }
        }
        return savedEntity;
    }

    public void startStreamOnServers(Long id, Set<Long> serversIds) {
        startStreamOnServers(findByIdOrFail(id), serversIds);
    }

    public void startStreamOnServers(Channel channel, Set<Long> serverIds) {
        for (Server server : super.getServersForStream(channel, serverIds)) {
            serverService.sendAsyncStartRequest(server, channelMapper.convertToChannelStart(channel, 0));
        }
    }

    public void saveAll(ChannelBatchInsertView channelBatchInsertView, boolean restart) {
        var channelIds = channelBatchInsertView.getChannelIds();
        var serverIds = channelBatchInsertView.getServerIds();
        var collectionIds = channelBatchInsertView.getCollectionIds();

        if (channelIds != null) {
            for (Long channelId : channelIds) {
                var channel = repository.findById(channelId).orElseThrow(() -> new EntityNotFoundException("Channel", channelId.toString()));
                AdvancedStreamOptions aso = channel.getAdvancedStreamOptions();

                channel.setAdvancedStreamOptions(channelMapper.setASO(aso, channelBatchInsertView));

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
                if (restart) {
                    startStreamOnServers(channel, serverIds);
                }
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

    public void importChannels(ChannelImportView importView) {
        List<ChannelInsertView> insertViews = channelMapper.addChannels(importView);
        for (ChannelInsertView insertView : insertViews) {
            if (!repository.existsByName(insertView.getName()))
                insert(channelMapper.convertToEntity(insertView), false);

        }
    }


    public void updateServersList(Long channel_id, Long[] serverIds) {
        //@todo update servers list
    }

    public String playChannel(String stream_token, String line_token, HttpServletRequest request) {
        Channel channel = repository.findByStreamToken(stream_token).orElseThrow();
        ArrayList<Server> servers = loadBalancingService.findAvailableServers(channel);
        Server server = loadBalancingService.findLeastConnServer(servers);

        /*This code below checks that if the requested stream
        is on-demand and offline at the same time, a start request will be sent to the related server.*/
        var streamServer = server.getStreamServers().stream().filter(item -> item.getStream().getStreamToken().equals(stream_token)).findFirst();
        streamServer.ifPresent(item -> {
            if (checkOnDemandStatus(item)) {
                serverService.sendStartRequest(server, channelMapper.convertToChannelStart(channel, streamServer.get().getSelectedSource()));
            }
        });
        return "http://" + server.getIp() + ":" + server.getCorePort() + "/live/" + line_token + "/" + stream_token + "/m3u8";
    }

    public boolean checkOnDemandStatus(StreamServer streamServer) {
        if (streamServer.getStreamDetails() != null) {
            return streamServer.isOnDemand() &&
                    streamServer.getStreamDetails().getStreamStatus() == null ||
                    !streamServer.getStreamDetails().getStreamStatus().equals(StreamStatus.ONLINE);
        } else return streamServer.isOnDemand();
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
        this.startStreamOnServers(streamId, Collections.singleton(serverId));
        return nextSource;
    }

    public ChannelInfo getChannelInfo(Long channelId) {
        return channelMapper.convertToChannelInfo(findByIdOrFail(channelId));
    }

    public void setEpgRecord(Long id, EpgDetails epgDetails) {
        var epgChannel = epgChannelRepository.findByNameAndLanguageAndEpgFile_Id(epgDetails.getName(), epgDetails.getLanguage(), epgDetails.getEpgId())
                .orElseThrow(() -> new EntityNotFoundException("Epg channel", epgDetails));
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

    public void autoStopOnDemandChannels() {
        List<Server> servers = serverRepository.findAll();
        List<Long> streamIds = new ArrayList<>();
        if (servers.size() > 0) {
            for (Server server : servers) {
                checkOnDemandConnections(streamIds, server);
                if (streamIds.size() > 0) {
                    serverService.sendPostRequest("http://" + server.getIp() + ":" + server.getCorePort() + "/streams/batch-stop", String.class, streamIds);
                }
                streamIds.clear();
            }
        }
    }

    public void checkOnDemandConnections(List<Long> streamIds, Server server) {
        int connections;
        for (StreamServer streamServer : server.getStreamServers()) {
            connections = connectionRepository.countAllByServerIdAndStreamId(server.getId(), streamServer.getStream().getId());
            if (streamServer.getStreamDetails() != null) {
                if (streamServer.isOnDemand() && connections == 0 && streamServer.getStreamDetails().getStreamStatus().equals(StreamStatus.ONLINE)) {
                    streamIds.add(streamServer.getStream().getId());
                }
            }
        }
    }
}
