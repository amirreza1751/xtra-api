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
    private final ServerService serverService;

    @Autowired
    public ChannelFacade(ChannelService channelService, ServerService serverService) {
        this.serverService = serverService;
        this.modelMapper = new ModelMapper();
    }

    public Channel convertToEntity(ChannelDTO channelDTO) {
        return modelMapper.map(channelDTO, Channel.class);
    }

    public ChannelDTO convertToDto(Channel channel) {
        ChannelDTO channelDTO = modelMapper.map(channel, ChannelDTO.class);
        channelDTO.setStream_servers(channel.getStreamServers().stream().map(e -> e.getServer().getId()).collect(Collectors.toCollection(ArrayList::new)));
        return channelDTO;
    }
}
