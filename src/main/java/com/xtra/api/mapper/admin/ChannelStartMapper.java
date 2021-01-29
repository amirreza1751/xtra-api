package com.xtra.api.mapper.admin;

import com.xtra.api.model.Channel;
import com.xtra.api.projection.admin.channel.ChannelStart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public abstract class ChannelStartMapper {


    @Mapping(source = "streamServers", target = "streamServers")
    @Mapping(source = "streamInputs", target = "streamInputs")
    public abstract ChannelStart convertToDto(Channel channel);


}
