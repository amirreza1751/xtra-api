package com.xtra.api.service;

import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.admin.ChannelService;
import com.xtra.api.service.admin.ConnectionService;
import com.xtra.api.service.admin.VodConnectionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleService {
    private final ConnectionService connectionService;
    private final ChannelService channelService;
    private final VodConnectionService vodConnectionService;

    public ScheduleService(ConnectionService connectionService, ChannelService channelService, VodConnectionService vodConnectionService) {
        this.connectionService = connectionService;
        this.channelService = channelService;
        this.vodConnectionService = vodConnectionService;
    }

    @Scheduled(fixedDelay = 1000)
    private void connectionCleaner() {
        connectionService.deleteOldConnections();
    }

    //Removes old vod connections.
    @Scheduled(fixedDelay = 1000)
    private void vodConnectionCleaner() {
        vodConnectionService.deleteOldConnections();
    }

    @Scheduled(fixedDelay = 10000)
    public void autoStopOnDemandChannels() {
        channelService.autoStopOnDemandChannels();
    }

}
