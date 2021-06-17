package com.xtra.api.service;

import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.admin.ChannelService;
import com.xtra.api.service.admin.ConnectionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleService {
    private final ConnectionService connectionService;
    private final ChannelService channelService;

    public ScheduleService(ConnectionService connectionService, ChannelService channelService) {
        this.connectionService = connectionService;
        this.channelService = channelService;
    }

    @Scheduled(fixedDelay = 1000)
    private void connectionCleaner() {
        connectionService.deleteOldConnections();
    }

    @Scheduled(fixedDelay = 10000)
    public void autoStopOnDemandChannels() {
        channelService.autoStopOnDemandChannels();
    }

}
