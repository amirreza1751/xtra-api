package com.xtra.api.mapper;

import com.xtra.api.model.*;
import com.xtra.api.projection.ChannelInfo;
import com.xtra.api.projection.MergedChannelInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.*;

@Mapper(componentModel = "spring")
public abstract class ChannelInfoMapper {


    @Mapping(source = "streamServers", target = "infos")
    public abstract ChannelInfo convertToDto(Channel channel);

    public List<MergedChannelInfo> convertToInfosMap(List<StreamServer> streamServers){
        if (streamServers == null) return null;
        List<MergedChannelInfo> infos = new ArrayList<>();
        streamServers.forEach(streamServer -> infos.add( new MergedChannelInfo(streamServer.getStreamInfo(), streamServer.getProgressInfo())));
        return infos;
    }

}
