package com.xtra.api.service;

import com.xtra.api.model.Channel;
import com.xtra.api.model.Server;
import com.xtra.api.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.xtra.api.util.Utilities.*;

@Service
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ServerService serverService;
    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

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

    public Channel addChannel(Channel channel, boolean restart) {

        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (channelRepository.existsChannelByStreamToken(token));

        channel.setStreamToken(token);
        Channel ch = channelRepository.save(channel);
        if (restart) {
            restartChannel(ch.getId());
        }
        return ch;
    }

    private void restartChannel(Long channelId) {
        var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/start/" + channelId, String.class);
    }

}
