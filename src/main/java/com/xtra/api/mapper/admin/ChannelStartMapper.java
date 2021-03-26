package com.xtra.api.mapper.admin;

import com.xtra.api.model.Channel;
import com.xtra.api.projection.admin.channel.ChannelStart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public abstract class ChannelStartMapper {


    @Mapping(source = "channel.streamInputs", target = "streamInputs")
//    @Mapping(source = "selectedSource", target = "selectedSource")
    public abstract ChannelStart convertToDto(Channel channel, int selectedSource);

}
