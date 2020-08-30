package com.xtra.api.service;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.Channel;
import com.xtra.api.model.Server;
import com.xtra.api.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.*;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ServerService serverService;

    @Autowired
    public ChannelService(ChannelRepository channelRepository, ServerService serverService) {
        this.channelRepository = channelRepository;
        this.serverService = serverService;
    }

    public Page<Channel> getChannels(int pageNo, int pageSize
            , String search, String sortBy, String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);

        if (search == null)
            return channelRepository.findAll(page);
        else {
            search = wrapSearchString(search);
            Optional<Server> server = serverService.findByName(search);
            if (server.isPresent())
                return channelRepository.findByNameLikeOrCategoryNameLikeOrStreamInfoCurrentInputLikeOrServersContains(search, search, search, server.get(), page);
            else
                return channelRepository.findByNameLikeOrCategoryNameLikeOrStreamInfoCurrentInputLike(search, search, search, page);
        }
    }

    public Channel addChannel(Channel channel, boolean start) {

        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (channelRepository.existsChannelByStreamToken(token));

        channel.setStreamToken(token);
        channel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));
        Channel ch = channelRepository.save(channel);
        if (start) {
            serverService.sendStartRequest(ch.getId());
        }
        return ch;
    }

    public Optional<Channel> getChannel(Long id) {
        return channelRepository.findById(id);
    }


    public Optional<Channel> updateChannel(Long id, Channel channel, boolean restart) {
        var result = channelRepository.findById(id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        Channel oldChannel = result.get();
        copyProperties(channel, oldChannel, "id", "currentInput", "currentConnections", "lineActivities");
        channel.setStreamInputs(channel.getStreamInputs().stream().distinct().collect(Collectors.toList()));
        if (restart)
            serverService.sendRestartRequest(id);
        return Optional.of(channelRepository.save(oldChannel));
    }

    public void deleteChannel(Long id) {
        // @todo add exception handling
        channelRepository.deleteById(id);
    }

    public boolean start(long id) {
        Optional<Channel> channel = channelRepository.findById(id);
        if (channel.isPresent()) {
            return serverService.sendStartRequest(id);
        } else
            throw new EntityNotFoundException();
    }

    public boolean stop(Long id) {
        Optional<Channel> channelById = channelRepository.findById(id);
        if (channelById.isPresent()) {
            Channel channel = channelById.get();
            if (!serverService.sendStopRequest(channel.getId()))
                return false;
            channel.setStreamInfo(null);
            channel.setProgressInfo(null);
            channelRepository.save(channel);
            return true;
        } else
            throw new EntityNotFoundException();
    }

    public boolean restart(Long id) {
        Optional<Channel> channelById = channelRepository.findById(id);
        if (channelById.isPresent()) {
            Channel channel = channelById.get();
            serverService.sendRestartRequest(channel.getId());
            return true;
        } else
            throw new EntityNotFoundException();
    }
}
