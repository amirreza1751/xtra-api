package com.xtra.api.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtra.api.model.Channel;
import com.xtra.api.model.StreamServer;
import com.xtra.api.model.StreamServerId;
import com.xtra.api.projection.ChannelDTO;
import com.xtra.api.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class ChannelFacade {
    private final ModelMapper modelMapper;
    private final ChannelService channelService;
    private final ServerService serverService;

    @Autowired
    public ChannelFacade(ChannelService channelService, ServerService serverService) {
        this.channelService = channelService;
        this.serverService = serverService;
        this.modelMapper = new ModelMapper();
    }

    public Channel convertToEntity(ChannelDTO channelDTO) {
        Channel channel = modelMapper.map(channelDTO, Channel.class);
        if (channel.getId() != null) {

            ArrayList<Long> serverIds = channelDTO.getStream_servers();
            Long channelId = channel.getId();
            ArrayList<StreamServer> streamServers = new ArrayList<>();

            if (!serverService.existsAllByIdIn(serverIds)) {
                throw new RuntimeException("at least of one the server ids are not found");
            }

            for (Long serverId : serverIds) {
                StreamServer streamServer = new StreamServer();
                streamServer.setId(new StreamServerId(channelId, serverId));

                var server = serverService.findByIdOrFail(serverId);
                streamServer.setServer(server);
                streamServer.setStream(channel);
                server.addStreamServer(streamServer);

                streamServers.add(streamServer);
                serverService.updateOrFail(server.getId(), server);

            }
            channel.setStreamServers(streamServers);
        }
        return channel;
    }

    public ChannelDTO convertToDto(Channel channel) {
        ChannelDTO channelDTO = modelMapper.map(channel, ChannelDTO.class);
        channelDTO.setStream_servers(channel.getStreamServers().stream().map(e -> e.getServer().getId()).collect(Collectors.toCollection(ArrayList::new)));
        return channelDTO;
    }
}
