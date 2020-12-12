package com.xtra.api.mapper;

import com.xtra.api.model.Channel;
import com.xtra.api.model.StreamServer;
import com.xtra.api.projection.channel.ChannelInfo;
import com.xtra.api.projection.channel.MergedChannelInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class ChannelInfoMapper {


    @Mapping(source = "streamServers", target = "infos")
    public abstract ChannelInfo convertToDto(Channel channel);

    public Set<MergedChannelInfo> convertToInfosMap(Set<StreamServer> streamServers) {
        if (streamServers == null) return null;
        Set<MergedChannelInfo> infos = new HashSet<>();
        streamServers.forEach(streamServer -> infos.add(new MergedChannelInfo(streamServer.getStreamInfo(), streamServer.getProgressInfo())));
        return infos;
    }

}
