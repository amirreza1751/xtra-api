package com.xtra.api.service.admin;

import com.xtra.api.model.stream.StreamStatus;
import com.xtra.api.projection.admin.analytics.AnalyticsData;
import com.xtra.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    final private ResellerRepository resellerRepository;
    final private LineRepository lineRepository;
    final private StreamServerRepository streamServerRepository;
    final private ResourceRepository resourceRepository;

    @Autowired
    public AnalyticsService(ResellerRepository resellerRepository, ConnectionRepository connectionRepository, LineRepository lineRepository, ChannelRepository channelRepository, ServerService serverService, StreamServerRepository streamServerRepository, ResourceRepository resourceRepository) {
        this.resellerRepository = resellerRepository;
        this.lineRepository = lineRepository;
        this.streamServerRepository = streamServerRepository;
        this.resourceRepository = resourceRepository;
    }

    public AnalyticsData getData() {
        AnalyticsData data = new AnalyticsData();
        data.setResellerCount(resellerRepository.count());
        data.setPendingResellerCount(resellerRepository.countByIsVerifiedFalse());
        data.setOnlineUsersCount(lineRepository.countOnlineUsers().getOnlineUsersCount());
        data.setConnectionsCount(lineRepository.countOnlineUsers().getConnectionsCount());
        data.setOnlineChannelsCount(streamServerRepository.countByStreamDetails_StreamStatusIs(StreamStatus.ONLINE));
        data.setOfflineChannelsCount(streamServerRepository.countByStreamDetails_StreamStatusIs(StreamStatus.OFFLINE));
        data.setTotalInput(resourceRepository.networksBytesSum().getNetworkBytesRecv());
        data.setTotalOutput(resourceRepository.networksBytesSum().getNetworkBytesSent());
        return data;
    }
}
