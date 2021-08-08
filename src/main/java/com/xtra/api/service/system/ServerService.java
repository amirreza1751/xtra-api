package com.xtra.api.service.system;

import com.xtra.api.mapper.admin.ChannelMapper;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.stream.Channel;
import com.xtra.api.model.ChannelList;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.StreamServerId;
import com.xtra.api.model.vod.TeleRecord;
import com.xtra.api.model.vod.Video;
import com.xtra.api.model.vod.VideoServer;
import com.xtra.api.model.vod.VideoServerId;
import com.xtra.api.projection.admin.catchup.CatchupRecordView;
import com.xtra.api.projection.admin.channel.ChannelStart;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.repository.TeleRecordRepository;
import com.xtra.api.service.CrudService;
import com.xtra.api.service.admin.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service("systemServerService")
public class ServerService extends CrudService<Server, Long, ServerRepository> {
    private final ChannelMapper channelMapper;
    private final StreamServerRepository streamServerRepository;
    private final MovieService movieService;
    private final TeleRecordRepository teleRecordRepository;

    protected ServerService(ServerRepository repository, ChannelMapper channelMapper, StreamServerRepository streamServerRepository, MovieService movieService, TeleRecordRepository teleRecordRepository) {
        super(repository, "Server");
        this.channelMapper = channelMapper;
        this.streamServerRepository = streamServerRepository;
        this.movieService = movieService;
        this.teleRecordRepository = teleRecordRepository;
    }

    public ChannelStart getChannelForServer(Long channelId, String token) {
        var server = repository.findByToken(token).orElseThrow(() -> new RuntimeException("server was not found!!!"));
        var optionalStreamServer = server.getStreamServers().stream().filter(streamServer -> streamServer.getStream().getId().equals(channelId)).findFirst();
        if (optionalStreamServer.isPresent()) {
            var streamServer = optionalStreamServer.get();
            return channelMapper.convertToChannelStart((Channel) streamServer.getStream(), streamServer.getSelectedSource());
        } else throw new EntityNotFoundException("Channel", channelId);
    }

    public ChannelList getAllChannelsForServer(String token) {
        var server = repository.findByToken(token).orElseThrow(() -> new RuntimeException("server was not found!!!"));
        return new ChannelList(server.getStreamServers().stream().map(streamServer -> channelMapper.convertToChannelStart((Channel) streamServer.getStream(), streamServer.getSelectedSource())).collect(Collectors.toList()));
    }

    @Override
    protected Page<Server> findWithSearch(String search, Pageable page) {
        return null;
    }

    public void updateRecordingStatus(String token, boolean status, Long streamId, CatchupRecordView catchupRecordView) {
        var server = repository.findByToken(token).orElseThrow(() -> new RuntimeException("server was not found!!!"));
        var streamServer = streamServerRepository.findById(new StreamServerId(streamId, server.getId()));
        streamServer.ifPresent(item -> {

            Video video = new Video();
            movieService.generateToken(video);
            video.setLocation(catchupRecordView.getLocation());

            VideoServer videoServer = new VideoServer(new VideoServerId(null, server.getId()));
            videoServer.setVideo(video);
            videoServer.setServer(server);
            Set<VideoServer> videoServers = new HashSet<>();
            videoServers.add(videoServer);

            video.setVideoServers(videoServers);

            TeleRecord teleRecord = new TeleRecord();
            teleRecord.setTitle(catchupRecordView.getTitle());
            teleRecord.setStart(catchupRecordView.getStart());
            teleRecord.setStop(catchupRecordView.getStop());
            teleRecord.setDuration(Duration.between(catchupRecordView.getStart(), catchupRecordView.getStop()));
            teleRecord.setVideo(video);
            teleRecord.setChannel((Channel) item.getStream());

            item.setRecording(status);
            streamServerRepository.save(item);
            teleRecordRepository.save(teleRecord);
        });
    }
}
