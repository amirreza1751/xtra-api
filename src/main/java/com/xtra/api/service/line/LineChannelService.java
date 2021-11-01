package com.xtra.api.service.line;

import com.xtra.api.mapper.line.LineChannelMapper;
import com.xtra.api.mapper.system.StreamMapper;
import com.xtra.api.model.stream.Channel;
import com.xtra.api.projection.line.channel.ChannelPlayListView;
import com.xtra.api.projection.line.channel.ChannelPlayView;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.admin.ServerService;
import com.xtra.api.service.admin.StreamBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class LineChannelService extends StreamBaseService<Channel, ChannelRepository> {

    private final LineChannelMapper channelMapper;

    @Autowired
    protected LineChannelService(ChannelRepository repository, ServerService serverService, LineChannelMapper channelMapper, StreamMapper streamMapper, StreamServerRepository streamServerRepository) {
        super(repository, "Channel", serverService, streamMapper, streamServerRepository);
        this.channelMapper = channelMapper;
    }

    public Page<ChannelPlayListView> getChannelPlaylist(int pageNo, String search, String sortBy, Long categoryId) {
        return repository.findAll(getSortingPageable(pageNo, 50, sortBy, "desc")).map(channelMapper::convertToPlaylistView);
    }

    public List<ChannelPlayListView> getHot10ChannelsPlaylist() {
        return repository.Top10Channels().stream().map(channelMapper::convertToPlaylistView).collect(Collectors.toList());
    }

    public ChannelPlayView getChannel(Long id) {
        return this.channelMapper.convertToPlayView(findByIdOrFail(id));
    }

    @Override
    protected Page<Channel> findWithSearch(String search, Pageable page) {
        return null;
    }
}
