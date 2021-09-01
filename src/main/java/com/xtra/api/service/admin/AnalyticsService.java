package com.xtra.api.service.admin;

import com.xtra.api.projection.admin.analytics.AnalyticsData;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.ResellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    final private ResellerRepository resellerRepository;
    final private ConnectionRepository connectionRepository;
    final private LineRepository lineRepository;
    final private ChannelRepository channelRepository;
    final private ServerService serverService;

    @Autowired
    public AnalyticsService(ResellerRepository resellerRepository, ConnectionRepository connectionRepository, LineRepository lineRepository, ChannelRepository channelRepository, ServerService serverService) {
        this.resellerRepository = resellerRepository;
        this.connectionRepository = connectionRepository;
        this.lineRepository = lineRepository;
        this.channelRepository = channelRepository;
        this.serverService = serverService;
    }

    public AnalyticsData getData() {
        AnalyticsData data = new AnalyticsData();
        data.setResellerCount(resellerRepository.count());
        data.setPendingResellerCount(resellerRepository.countByIsVerifiedFalse());
        data.setOnlineUsersCount(lineRepository.countOnlineUsers().onlineUsersCount());
        data.setConnectionsCount(lineRepository.countOnlineUsers().connectionsCount());
        data.setOnlineChannelsCount(0);
        return data;
    }
}
