package com.xtra.api.service.admin;

import com.xtra.api.mapper.system.StreamMapper;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.stream.Stream;
import com.xtra.api.model.stream.StreamDetails;
import com.xtra.api.model.stream.StreamStatus;
import com.xtra.api.projection.EntityListItem;
import com.xtra.api.projection.system.StreamDetailsView;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class StreamService extends CrudService<Stream, Long, StreamRepository> {
    private final StreamMapper streamMapper;
    private final ServerService serverService;

    protected StreamService(StreamRepository repository, StreamMapper streamMapper, ServerService serverService) {
        super(repository, "Stream");
        this.streamMapper = streamMapper;
        this.serverService = serverService;
    }

    @Override
    protected Page<Stream> findWithSearch(String search, Pageable page) {
        return null;
    }

    public Stream findById(Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void updateStreamStatuses(String token, List<StreamDetailsView> statuses) {
        serverService.findByServerToken(token).ifPresent(server -> {
            for (var streamServer : server.getStreamServers()) {
                var status = statuses.stream().filter(details -> details.getStreamId().equals(streamServer.getId().getStreamId())).findFirst();
                if (status.isPresent()) {
                    copyProperties(streamMapper.convertToEntity(status.get()), streamServer.getStreamDetails(), "id");
                    streamServer.getStreamDetails().setStreamStatus(StreamStatus.ONLINE);
                } else {
                    streamServer.setStreamDetails(new StreamDetails());
                    streamServer.getStreamDetails().setStreamStatus(StreamStatus.OFFLINE);
                }
                serverService.saveStreamServer(streamServer);
            }
        });
    }

    public List<EntityListItem> getStreamList(String name) {
        return repository.findAllByNameContains(name).stream().map(streamMapper::convertToEntityListItem).collect(Collectors.toList());
    }
}
