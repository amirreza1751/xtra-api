package com.xtra.api.service.system;

import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.mapper.admin.ChannelStartMapper;
import com.xtra.api.model.Channel;
import com.xtra.api.model.ChannelList;
import com.xtra.api.model.Server;
import com.xtra.api.projection.admin.channel.ChannelStart;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@Service("systemServerService")
public class ServerService extends CrudService<Server, Long, ServerRepository> {
    private final ChannelStartMapper channelStartMapper;

    protected ServerService(ServerRepository repository, ChannelStartMapper channelStartMapper) {
        super(repository, "Server");
        this.channelStartMapper = channelStartMapper;
    }

    public ChannelStart getChannelForServer(HttpServletRequest request, Long channelId, String port) {
        var server = repository.findByIpAndCorePort(request.getRemoteAddr(), port).orElseThrow(() -> new RuntimeException("server was not found!!!"));
        var optionalStreamServer = server.getStreamServers().stream().filter(streamServer -> streamServer.getStream().getId().equals(channelId)).findFirst();
        if (optionalStreamServer.isPresent()) {
            var streamServer = optionalStreamServer.get();
            return channelStartMapper.convertToDto((Channel) streamServer.getStream(), streamServer.getSelectedSource());
        } else throw new EntityNotFoundException("Channel", channelId);
    }

    public ChannelList getAllChannelsForServer(HttpServletRequest request) {
        var server = repository.findByIpAndCorePort(request.getRemoteAddr(), String.valueOf(request.getRemotePort())).orElseThrow(() -> new RuntimeException("server was not found!!!"));
        return new ChannelList(server.getStreamServers().stream().map(streamServer -> channelStartMapper.convertToDto((Channel) streamServer.getStream(), streamServer.getSelectedSource())).collect(Collectors.toList()));
    }

    @Override
    protected Page<Server> findWithSearch(String search, Pageable page) {
        return null;
    }
}
