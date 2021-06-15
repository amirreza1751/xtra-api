package com.xtra.api.service;

import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.admin.ChannelService;
import com.xtra.api.service.admin.ConnectionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduleService {
    private final ConnectionService connectionService;
    private final ChannelService channelService;
    private final StreamServerRepository streamServerRepository;

    public ScheduleService(ConnectionService connectionService, ChannelService channelService, StreamServerRepository streamServerRepository) {
        this.connectionService = connectionService;
        this.channelService = channelService;
        this.streamServerRepository = streamServerRepository;
    }

    @Scheduled(fixedDelay = 1000)
    private void connectionCleaner() {
        connectionService.deleteOldConnections();
    }

    @Scheduled(fixedDelay = 10000)
    public void autoStopOnDemandChannels() {
        channelService.autoStopOnDemandChannels();
    }

    @Scheduled(fixedDelay = 3000)
    void clearOldStreamDetails() {
        var streamServers = streamServerRepository.findAllByStreamDetailsUpdatedBefore(LocalDateTime.now().minusSeconds(2));
        for (var ss:streamServers){
            ss.setStreamDetails(null);
        }
        streamServerRepository.saveAll(streamServers);
    }

}
