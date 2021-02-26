package com.xtra.api.service.admin;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.mapper.admin.ChannelMapper;
import com.xtra.api.mapper.admin.ChannelStartMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.channel.*;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.repository.EpgChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.generateRandomString;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class ChannelService extends StreamService<Channel, ChannelRepository> {
    private final ServerService serverService;
    private final LoadBalancingService loadBalancingService;
    private final ChannelStartMapper channelStartMapper;
    private final ChannelMapper channelMapper;
    private final EpgChannelRepository epgChannelRepository;


    @Autowired
    public ChannelService(ChannelRepository repository, ServerService serverService, LoadBalancingService loadBalancingService, ChannelStartMapper channelStartMapper, ChannelMapper channelMapper, EpgChannelRepository epgChannelRepository) {
        super(repository, Channel.class, serverService);
        this.serverService = serverService;
        this.loadBalancingService = loadBalancingService;
        this.channelStartMapper = channelStartMapper;
        this.channelMapper = channelMapper;
        this.epgChannelRepository = epgChannelRepository;
    }


    public Page<ChannelInfo> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var result = findAll(search, pageNo, pageSize, sortBy, sortDir);
        return new PageImpl<>(result.stream().map(channelMapper::convertToChannelInfo).collect(Collectors.toList()));
    }

    public ChannelView getViewById(Long id) {
        return channelMapper.convertToView(findByIdOrFail(id));
    }

    @Override
    protected Page<Channel> findWithSearch(Pageable page, String search) {
        return repository.findByNameLike(search, page);
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

        savedEntity.updateRelationIds();
        repository.save(savedEntity);
        var serverIds = emptyIfNull(savedEntity.getStreamServers()).stream().map(streamServer -> streamServer.getServer().getId()).collect(Collectors.toSet());
        if (start) {
            //@todo start servers
        }
        return savedEntity;
    }

    public ChannelView save(Long id, ChannelInsertView channelView, boolean restart) {
        return channelMapper.convertToView(update(id, channelMapper.convertToEntity(channelView), restart));
    }

    public void saveAll(ChannelMassInsertView channelMassInsertView, boolean restart) {
        var channelIds = channelMassInsertView.getChannelIds();
        var serverIds = channelMassInsertView.getServerIds();
        if (channelIds != null) {
            for (Long channelId : channelIds) {
                var channel = repository.findById(channelId).orElseThrow(() -> new EntityNotFoundException("Channel", channelId.toString()));

                if (channelMassInsertView.getReadNative() != null)
                    channel.setReadNative(Boolean.parseBoolean(channelMassInsertView.getReadNative()));
                if (channelMassInsertView.getStreamAll() != null)
                    channel.setStreamAll(Boolean.parseBoolean(channelMassInsertView.getStreamAll()));
                if (channelMassInsertView.getDirectSource() != null)
                    channel.setDirectSource(Boolean.parseBoolean(channelMassInsertView.getDirectSource()));
                if (channelMassInsertView.getGenTimestamps() != null)
                    channel.setGenTimestamps(Boolean.parseBoolean(channelMassInsertView.getGenTimestamps()));
                if (channelMassInsertView.getRtmpOutput() != null)
                    channel.setRtmpOutput(Boolean.parseBoolean(channelMassInsertView.getRtmpOutput()));

                Set<StreamServer> streamServers = channelMapper.convertToServers(serverIds, channelId);
                channel.getStreamServers().retainAll(streamServers);
                channel.getStreamServers().addAll(streamServers);
                //channel.updateRelationIds();

                repository.save(channel);
            }
        }
    }

    public void deleteAll(ChannelMassDeleteView channelMassDeleteView) {
        var channelIds = channelMassDeleteView.getChannelIds();
        if (channelIds != null) {
            for (Long channelId : channelIds) {
                deleteOrFail(channelId);
            }
        }
    }

    public Channel update(Long id, Channel channel, boolean restart) {
        //@todo check validation check
        Channel oldChannel = findByIdOrFail(id);
        copyProperties(channel, oldChannel, "id", "currentInput", "currentConnections", "lineActivities", "streamServers", "streamCollections");

        //remove old servers from channel and add new ones
        if (oldChannel.getStreamServers() != null) {
            oldChannel.getStreamServers().clear();
            oldChannel.getStreamServers().addAll(channel.getStreamServers().stream().peek(streamServer -> {
                streamServer.setId(new StreamServerId(oldChannel.getId(), streamServer.getServer().getId()));
                streamServer.setStream(oldChannel);
            }).collect(Collectors.toSet()));
        }
        //remove old collections from channel and add new ones
        if (oldChannel.getCollectionAssigns() != null) {
            oldChannel.getCollectionAssigns().clear();
            oldChannel.getCollectionAssigns().addAll(channel.getCollectionAssigns().stream().peek(collectionStream -> {
                collectionStream.setId(new CollectionStreamId(collectionStream.getCollection().getId(), oldChannel.getId()));
                collectionStream.setStream(oldChannel);
            }).collect(Collectors.toSet()));
        }

        oldChannel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));
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
        return serverService.sendPlayRequest(stream_token, line_token, server);
    }

    public int changeSource(Long streamId, String portNumber, HttpServletRequest request) {
        Optional<Server> srv = serverService.findByIpAndCorePort(request.getRemoteAddr(), portNumber);
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

    public Channel channelInfo(Long channelId) {
        return this.findByIdOrFail(channelId);
    }

    public Channel channelStart(Long channelId) {
        return this.findByIdOrFail(channelId);
    }

    public ChannelList getBatchChannel(List<Long> streamIds) {
        List<Channel> channels = repository.findByIdIn(streamIds);
        List<ChannelStart> channelStarts = new ArrayList<>();
        channels.forEach(channel -> channelStarts.add(channelStartMapper.convertToDto(channel)));
        ChannelList channelList = new ChannelList();
        channelList.setChannelList(channelStarts);
        return channelList;
    }

    public void setEpgRecord(Long id, EpgChannelId epgChannelId) {
        var epgChannel = epgChannelRepository.findById(epgChannelId).orElseThrow(() -> new EntityNotFoundException("Epg channel not found!"));
        var channel = findByIdOrFail(id);
        channel.setEpgChannel(epgChannel);
        repository.save(channel);
    }
}
