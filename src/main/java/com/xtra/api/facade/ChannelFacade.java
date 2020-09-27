package com.xtra.api.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtra.api.model.Channel;
import com.xtra.api.projection.ChannelDTO;
import com.xtra.api.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        if (channel.getId() == null || channel.getId() == 0) {
            channel.setId(channelService.add(channel).getId());
        }
        Long channelId = channel.getId();
        ArrayList<Long> serverIds = channelDTO.getServerIds();
        if (!serverService.existsAllByIdIn(serverIds)) {
            throw new RuntimeException("at least of one the permission keys are not found");
        }
        return null;
    }

    public ChannelDTO convertToDto(Channel channel) {

        return null;
    }
}
