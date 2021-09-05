package com.xtra.api.service.admin;

import com.xtra.api.model.role.Role;
import com.xtra.api.model.stream.StreamStatus;
import com.xtra.api.model.user.User;
import com.xtra.api.model.user.UserType;
import com.xtra.api.projection.admin.analytics.AnalyticsData;
import com.xtra.api.projection.admin.analytics.ResellerAnalytics;
import com.xtra.api.repository.*;
import com.xtra.api.service.system.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    final private ResellerRepository resellerRepository;
    final private LineRepository lineRepository;
    final private StreamServerRepository streamServerRepository;
    final private ResourceRepository resourceRepository;
    final private ServerRepository serverRepository;

    @Autowired
    public AnalyticsService(ResellerRepository resellerRepository, ConnectionRepository connectionRepository, LineRepository lineRepository, ChannelRepository channelRepository, ServerService serverService, StreamServerRepository streamServerRepository, ResourceRepository resourceRepository, ServerRepository serverRepository) {
        this.resellerRepository = resellerRepository;
        this.lineRepository = lineRepository;
        this.streamServerRepository = streamServerRepository;
        this.resourceRepository = resourceRepository;
        this.serverRepository = serverRepository;
    }

    public Object getData() {
        Role currentRole = UserAuthService.getCurrentUser().getRole();
        AnalyticsData data = new AnalyticsData();
        if (currentRole.getType().equals(UserType.ADMIN) || currentRole.getType().equals(UserType.SUPER_ADMIN)){
            data.setResellerCount(resellerRepository.count());
            data.setPendingResellerCount(resellerRepository.countByIsVerifiedFalse());
            data.setOnlineUsersCount(lineRepository.countOnlineUsers().getOnlineUsersCount());
            data.setConnectionsCount(lineRepository.countOnlineUsers().getConnectionsCount());
            data.setOnlineChannelsCount(streamServerRepository.countByStreamDetails_StreamStatusIs(StreamStatus.ONLINE));
            data.setOfflineChannelsCount(streamServerRepository.countByStreamDetails_StreamStatusIs(StreamStatus.OFFLINE));
            data.setTotalInput(resourceRepository.networksBytesSum().getNetworkBytesRecv());
            data.setTotalOutput(resourceRepository.networksBytesSum().getNetworkBytesSent());
            data.setServerSummaryList(serverRepository.getServerSummaryList());
            return data;
        } else if (currentRole.getType().equals(UserType.RESELLER)){
            return new ResellerAnalytics();
        } else {
            return null;
        }

    }
}
