package com.xtra.api.facade;

import com.xtra.api.model.Channel;
import com.xtra.api.projection.ChannelDTO;
import com.xtra.api.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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

        return null;
    }

    public ChannelDTO convertToDto(Channel channel) {

        return null;
    }
}
