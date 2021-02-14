package com.xtra.api.mapper.admin;

import com.xtra.api.model.Channel;
import com.xtra.api.model.StreamServer;
import com.xtra.api.projection.admin.channel.ChannelInfo;
import com.xtra.api.projection.admin.channel.MergedChannelInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class ChannelInfoMapper {


    @Mapping(source = "streamServers", target = "channelInfos")
    public abstract ChannelInfo convertToDto(Channel channel);

    public Set<MergedChannelInfo> convertToInfosMap(Set<StreamServer> streamServers) {
        if (streamServers == null) return null;
        Set<MergedChannelInfo> infos = new HashSet<>();
        streamServers.forEach(streamServer -> {
            var mergedInfo = new MergedChannelInfo(streamServer.getStreamInfo(), streamServer.getProgressInfo());
            mergedInfo.setServerName(streamServer.getServer().getName());
            infos.add(mergedInfo);
        });
        return infos;
    }

}
