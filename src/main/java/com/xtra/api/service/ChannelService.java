package com.xtra.api.service;

import com.xtra.api.model.*;
import com.xtra.api.projection.ChannelInfo;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.repository.CollectionStreamRepository;
import com.xtra.api.repository.StreamServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.*;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class ChannelService extends StreamService<Channel, ChannelRepository> {
    private final ServerService serverService;
    private final LoadBalancingService loadBalancingService;
    private final CollectionStreamRepository collectionStreamRepository;
    private final CollectionRepository collectionRepository;
    private final StreamServerRepository streamServerRepository;


    @Autowired
    public ChannelService(ChannelRepository repository, ServerService serverService, LoadBalancingService loadBalancingService, CollectionStreamRepository collectionStreamRepository, CollectionRepository collectionRepository, StreamServerRepository streamServerRepository) {
        super(repository, Channel.class, serverService);
        this.serverService = serverService;
        this.loadBalancingService = loadBalancingService;
        this.collectionStreamRepository = collectionStreamRepository;
        this.collectionRepository = collectionRepository;
        this.streamServerRepository = streamServerRepository;
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


    public Optional<Channel> updateChannel(Long id, Channel channel, Set<Long> serverIds, boolean restart) {
        var result = repository.findById(id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        Channel oldChannel = result.get();
        copyProperties(channel, oldChannel, "id", "currentInput", "currentConnections", "lineActivities");
        oldChannel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));

        oldChannel.getStreamServers().forEach(streamServerRepository::delete);
        //@todo should be introduced as a method
        if (serverIds != null) {
            ArrayList<StreamServer> streamServers = new ArrayList<>();
            for (Long serverId : serverIds) {
                StreamServer streamServer = new StreamServer();
                streamServer.setId(new StreamServerId(id, serverId));

                var server = serverService.findByIdOrFail(serverId);
                streamServer.setServer(server);
                streamServer.setStream(oldChannel);

                streamServers.add(streamServer);
            }
            oldChannel.setStreamServers(streamServers);
        }
        //@todo should be introduced as a method

        if (restart) {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            oldChannel.getStreamServers().forEach(streamServer -> executor.execute(() -> this.restartOrFail(oldChannel.getId(), streamServer.getId().getServerId())));
        }
        return Optional.of(repository.save(oldChannel));
    }

    @Override
    protected Page<Channel> findWithSearch(Pageable page, String search) {
        return repository.findByNameLikeOrCategoryNameLike(search, search, search, page);
    }

    public Channel add(Channel channel, Set<Long> serverIds, Set<Long> collectionIds, boolean start) {
        Channel ch = this.addChannel(channel);
        if (collectionIds != null) {
            for (var id : collectionIds) {
                var existing = collectionStreamRepository.findById(new CollectionStreamId(id, ch.getId()));
                if (existing.isEmpty()) {
                    var collection = new CollectionStream(new CollectionStreamId(id, ch.getId()));
                    var orderCount = collectionStreamRepository.countAllByIdCollectionId(id);
                    collection.setOrder(orderCount + 1);
                    collection.setStream(ch);
                    var col = collectionRepository.findById(id);
                    if (col.isPresent()) {
                        collection.setCollection(col.get());
                        col.get().addStream(collection);
                        collectionRepository.save(col.get());
                    }
                }
            }
        }
        Long streamId = ch.getId();
        if (!serverService.existsAllByIdIn(serverIds)) {
            throw new RuntimeException("at least of one the ids are wrong");
        }
        if (serverIds != null) {
            ArrayList<StreamServer> streamServers = new ArrayList<>();
            for (Long serverId : serverIds) {
                StreamServer streamServer = new StreamServer();
                streamServer.setId(new StreamServerId(streamId, serverId));

                var server = serverService.findByIdOrFail(serverId);
                streamServer.setServer(server);
                streamServer.setStream(channel);
                server.addStreamServer(streamServer);

                streamServers.add(streamServer);
                serverService.updateOrFail(server.getId(), server);

            }
            channel.setStreamServers(streamServers);
            if (start) {
                ExecutorService executor = Executors.newFixedThreadPool(2);
                channel.getStreamServers().forEach(streamServer -> executor.execute(() -> this.restartOrFail(channel.getId(), streamServer.getId().getServerId())));
            }
        }

        return repository.save(channel);
    }

    public void updateServersList(Long channel_id, Long[] serverIds) {
        //@todo update servers list
    }

    public String playChannel(String stream_token, String line_token, HttpServletRequest request) {
        ArrayList<Server> servers = loadBalancingService.findAvailableServers(stream_token);
        Server server = loadBalancingService.findLeastConnServer(servers);
        return serverService.sendPlayRequest(stream_token, line_token, server);
    }

    public int changeSource(Long streamId, String portNumber, HttpServletRequest request){
        Optional<Server> srv = serverService.findByIpAndCorePort(request.getRemoteAddr(), portNumber);
        if (srv.isEmpty()){
            throw new RuntimeException("Server is invalid. Check your ip and port.");
        }
        Server server = srv.get();
        Long serverId = server.getId();

        Optional<Channel> ch = this.repository.findById(streamId);
        if (ch.isEmpty()){
            throw new RuntimeException("Channel not found");
        }
        Channel channel = ch.get();
        StreamServer streamServer = new StreamServer(new StreamServerId(streamId, serverId));
        List<StreamServer> streamServers = channel.getStreamServers();
        if (!streamServers.contains(streamServer)){
            throw new RuntimeException("There is a problem with the relation between channel and the server.");
        }
        int nextSource = 0;
        for (StreamServer item : streamServers){
            if (item.equals(streamServer)){
                nextSource = (item.getSelectedSource() + 1)%channel.getStreamInputs().size();
                item.setSelectedSource(nextSource);
                break;
            }
        }
        channel.setStreamServers(streamServers);
        repository.save(channel);
        this.restartOrFail(streamId, serverId);
        return nextSource;
    }
    public Channel channelInfo(Long channelId){
        return this.findByIdOrFail(channelId);
    }

    public Channel channelStart(Long channelId){
        return this.findByIdOrFail(channelId);
    }
}
