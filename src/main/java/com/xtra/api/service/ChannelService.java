package com.xtra.api.service;

import com.xtra.api.model.Channel;
import com.xtra.api.model.Server;
import com.xtra.api.model.StreamServer;
import com.xtra.api.model.StreamServerId;
import com.xtra.api.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.*;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class ChannelService extends StreamService<Channel, ChannelRepository> {
    private final ServerService serverService;
    private final LoadBalancingService loadBalancingService;


    @Autowired
    public ChannelService(ChannelRepository repository, ServerService serverService, LoadBalancingService loadBalancingService) {
        super(repository, Channel.class, serverService);
        this.serverService = serverService;
        this.loadBalancingService = loadBalancingService;
    }


    public Channel addChannel(Channel channel) {

        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (repository.existsChannelByStreamToken(token));

        channel.setStreamToken(token);
        channel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));
        return repository.save(channel);
    }


    public Optional<Channel> updateChannel(Long id, Channel channel, boolean restart) {
        var result = repository.findById(id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        Channel oldChannel = result.get();
        copyProperties(channel, oldChannel, "id", "currentInput", "currentConnections", "lineActivities");
        channel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));
        if (restart)
            serverService.sendRestartRequest(id);
        return Optional.of(repository.save(oldChannel));
    }

    @Override
    protected Page<Channel> findWithSearch(Pageable page, String search) {
            return repository.findByNameLikeOrCategoryNameLikeOrStreamInfoCurrentInputLike(search, search, search, page);
    }

    public Channel add(Channel channel, ArrayList<Long> serverIds, boolean start){
        Channel ch = this.addChannel(channel);
        Long streamId = ch.getId();
        if(!serverService.existsAllByIdIn(serverIds)){
            throw new RuntimeException("at least of one the ids are wrong");
        }
        ArrayList<StreamServer> streamServers = new ArrayList<>();
        for (Long serverId : serverIds){
            StreamServer streamServer = new StreamServer();
            streamServer.setId(new StreamServerId(streamId, serverId));

            var server = serverService.findByIdOrFail(serverId);
            streamServer.setServer(server);
            streamServer.setStream(channel);
            server.addStreamServer(streamServer);

            streamServers.add(streamServer);
            channel.setStreamServers(streamServers);
            ch = repository.save(channel);
            serverService.updateOrFail(server.getId(), server);

            if (start){
                ExecutorService executor = Executors.newFixedThreadPool(2);
                Channel finalCh = ch;
                executor.execute(() -> this.startOrFail(finalCh.getId(), serverId));
            }
        }
        return ch;
    }

    public void updateServersList(Long channel_id, Long[] serverIds){
        //@todo uodate servers list
    }

    public String playChannel(String stream_token, String line_token, HttpServletRequest request){
        ArrayList<Server> servers  = loadBalancingService.findAvailableServers(stream_token);
        Server server  = loadBalancingService.findLeastConnServer(servers);
        return serverService.sendPlayRequest(stream_token, line_token, server);
    }
}
