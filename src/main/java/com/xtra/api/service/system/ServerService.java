package com.xtra.api.service.system;

import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.mapper.admin.ChannelStartMapper;
import com.xtra.api.model.stream.Channel;
import com.xtra.api.model.ChannelList;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.StreamServerId;
import com.xtra.api.projection.admin.channel.ChannelStart;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service("systemServerService")
public class ServerService extends CrudService<Server, Long, ServerRepository> {
    private final ChannelStartMapper channelStartMapper;
    private final StreamServerRepository streamServerRepository;

    protected ServerService(ServerRepository repository, ChannelStartMapper channelStartMapper, StreamServerRepository streamServerRepository) {
        super(repository, "Server");
        this.channelStartMapper = channelStartMapper;
        this.streamServerRepository = streamServerRepository;
    }

    public ChannelStart getChannelForServer(Long channelId, String token) {
        var server = repository.findByToken(token).orElseThrow(() -> new RuntimeException("server was not found!!!"));
        var optionalStreamServer = server.getStreamServers().stream().filter(streamServer -> streamServer.getStream().getId().equals(channelId)).findFirst();
        if (optionalStreamServer.isPresent()) {
            var streamServer = optionalStreamServer.get();
            return channelStartMapper.convertToDto((Channel) streamServer.getStream(), streamServer.getSelectedSource());
        } else throw new EntityNotFoundException("Channel", channelId);
    }

    public ChannelList getAllChannelsForServer(String token) {
        var server = repository.findByToken(token).orElseThrow(() -> new RuntimeException("server was not found!!!"));
        return new ChannelList(server.getStreamServers().stream().map(streamServer -> channelStartMapper.convertToDto((Channel) streamServer.getStream(), streamServer.getSelectedSource())).collect(Collectors.toList()));
    }

    @Override
    protected Page<Server> findWithSearch(String search, Pageable page) {
        return null;
    }

    public void updateRecordingStatus(String token, boolean status, Long streamId){
        var server = repository.findByToken(token).orElseThrow(() -> new RuntimeException("server was not found!!!"));
        var streamServer = streamServerRepository.findById(new StreamServerId(streamId, server.getId()));
        streamServer.ifPresent(item -> {
            item.setRecording(status);
            streamServerRepository.save(item);
        });
    }
}
