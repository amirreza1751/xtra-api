package com.xtra.api.mapper.line;

import com.xtra.api.model.stream.Channel;
import com.xtra.api.model.stream.StreamServer;
import com.xtra.api.projection.admin.channel.ChannelView;
import com.xtra.api.projection.line.channel.ChannelPlayListView;
import com.xtra.api.projection.line.channel.ChannelPlayView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Value;

import java.util.stream.Collectors;

import static com.xtra.api.service.system.UserAuthService.getCurrentLine;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Mapper(componentModel = "spring")
public abstract class LineChannelMapper {
    @Value("${server.external.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @Mapping(target = "collections", ignore = true)
    @Mapping(target = "categories", ignore = true)
    public abstract ChannelPlayListView convertToPlaylistView(Channel channel);

    @AfterMapping
    protected void convertAdditionalFieldsForView(Channel channel, @MappingTarget final ChannelPlayListView playListView) {

        playListView.setCollections(emptyIfNull(channel.getCollectionAssigns())
                .stream().map(collectionStream -> collectionStream.getCollection().getId()).collect(Collectors.toSet()));

        playListView.setCategories(emptyIfNull(channel.getCategories())
                .stream().map(categoryStream -> categoryStream.getCategory().getId()).collect(Collectors.toSet()));

        var line = getCurrentLine();
        playListView.setLink("http://" + serverAddress + ":" + serverPort + "/api/play/channel/" + line.getLineToken() + "/" + channel.getStreamToken());
    }

    public abstract ChannelPlayView convertToPlayView(Channel channel);

    @AfterMapping
    protected void convertAdditionalFieldsForView(Channel channel, @MappingTarget final ChannelPlayView playView) {
        var line = getCurrentLine();
        playView.setLink("http://" + serverAddress + ":" + serverPort + "/api/play/channel/" + line.getLineToken() + "/" + channel.getStreamToken());
    }

}
