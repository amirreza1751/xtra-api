package com.xtra.api.mapper;

import com.xtra.api.model.Channel;
import com.xtra.api.projection.channel.ChannelStart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public abstract class ChannelStartMapper {


    @Mapping(source = "streamServers", target = "streamServers")
    @Mapping(source = "streamInputs", target = "streamInputs")
    public abstract ChannelStart convertToDto(Channel channel);


}