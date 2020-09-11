package com.xtra.api.service;

import com.xtra.api.model.Channel;
import com.xtra.api.model.Server;
import com.xtra.api.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.*;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class ChannelService extends StreamService<Channel, ChannelRepository> {
    private final ServerService serverService;

    @Autowired
    public ChannelService(ChannelRepository repository, ServerService serverService) {
        super(repository, Channel.class, serverService);
        this.serverService = serverService;
    }


    public Channel addChannel(Channel channel, boolean start) {

        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (repository.existsChannelByStreamToken(token));

        channel.setStreamToken(token);
        channel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));
        Channel ch = repository.save(channel);
        if (start) {
            serverService.sendStartRequest(ch.getId());
        }
        return ch;
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
        Optional<Server> server = serverService.findByName(search);
        if (server.isPresent())
            return repository.findByNameLikeOrCategoryNameLikeOrStreamInfoCurrentInputLikeOrServersContains(search, search, search, server.get(), page);
        else
            return repository.findByNameLikeOrCategoryNameLikeOrStreamInfoCurrentInputLike(search, search, search, page);
    }
}