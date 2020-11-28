package com.xtra.api.mapper;

import com.xtra.api.model.Channel;
import com.xtra.api.model.CollectionStream;
import com.xtra.api.model.StreamInfo;
import com.xtra.api.model.StreamServer;
import com.xtra.api.projection.ChannelInfo;
import com.xtra.api.projection.ChannelView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ChannelInfoMapper {



    @Mapping(source = "streamServers", target = "streamInfos")
    public abstract ChannelInfo convertToDto(Channel channel);

    public List<StreamInfo> convertToStreamInfoList(List<StreamServer> streamServers) {
        if (streamServers == null) return null;
        return streamServers.stream().map(StreamServer::getStreamInfo).collect(Collectors.toList());
    }

}
