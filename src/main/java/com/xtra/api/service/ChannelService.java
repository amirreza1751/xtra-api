package com.xtra.api.service;

import com.xtra.api.mapper.ChannelMapper;
import com.xtra.api.model.Channel;
import com.xtra.api.model.Server;
import com.xtra.api.model.StreamServer;
import com.xtra.api.model.StreamServerId;
import com.xtra.api.projection.ChannelInsertView;
import com.xtra.api.projection.ChannelView;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.repository.StreamServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.generateRandomString;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class ChannelService extends StreamService<Channel, ChannelRepository> {
    private final ServerService serverService;
    private final LoadBalancingService loadBalancingService;
    private final StreamServerRepository streamServerRepository;
    private final ChannelMapper channelMapper;


    @Autowired
    public ChannelService(ChannelRepository repository, ServerService serverService, LoadBalancingService loadBalancingService, StreamServerRepository streamServerRepository, ChannelMapper channelMapper) {
        super(repository, Channel.class, serverService);
        this.serverService = serverService;
        this.loadBalancingService = loadBalancingService;
        this.streamServerRepository = streamServerRepository;
        this.channelMapper = channelMapper;
    }


    public Page<ChannelView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var result = findAll(search, pageNo, pageSize, sortBy, sortDir);
        return new PageImpl<>(result.stream().map(channelMapper::convertToView).collect(Collectors.toList()));
    }

    public ChannelView getViewById(Long id) {
        return channelMapper.convertToView(findByIdOrFail(id));
    }

    @Override
    protected Page<Channel> findWithSearch(Pageable page, String search) {
        return repository.findByNameLikeOrCategoryNameLike(search, search, search, page);
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
        channel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));
        var savedEntity = repository.save(channel);

        var serverIds = savedEntity.getStreamServers().stream().map(streamServer -> streamServer.getServer().getId()).collect(Collectors.toSet());
        if (start) {
            //@todo start servers
        }
        return savedEntity;
    }

    public ChannelView save(Long id, ChannelInsertView channelView, boolean restart) {
        return channelMapper.convertToView(update(id, channelMapper.convertToEntity(channelView), restart));
    }

    public Channel update(Long id, Channel channel, boolean restart) {

        Channel oldChannel = findByIdOrFail(id);
        copyProperties(channel, oldChannel, "id", "currentInput", "currentConnections", "lineActivities");
        //oldChannel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));

        //oldChannel.getStreamServers().forEach(streamServerRepository::delete);
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
        if (!streamServers.contains(streamServer)) {
            throw new RuntimeException("There is a problem with the relation between channel and the server.");
        }
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
        this.restartOrFail(streamId, serverId);
        return nextSource;
    }

    public Channel channelInfo(Long channelId) {
        return this.findByIdOrFail(channelId);
    }


}
