package com.xtra.api.schedule;

import com.xtra.api.model.Program;
import com.xtra.api.model.Server;
import com.xtra.api.model.StreamServer;
import com.xtra.api.model.StreamServerId;
import com.xtra.api.projection.admin.catchup.CatchupRecordView;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.admin.ServerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class CatchUpScheduler {
    private final StreamServerRepository streamServerRepository;
    private final ServerService serverService;
    private final ServerRepository serverRepository;
    public CatchUpScheduler(StreamServerRepository streamServerRepository, ServerService serverService, ServerRepository serverRepository) {

        this.streamServerRepository = streamServerRepository;
        this.serverService = serverService;
        this.serverRepository = serverRepository;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkProgramsForRecording() {
        var streamServers = streamServerRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[XXX]");
        if (streamServers.size() > 0){
            for (StreamServer streamServer : streamServers){
                if (streamServer.getIsCatchUp() && !streamServer.getRecording() && streamServer.getStream().getEpgChannel().getPrograms().size() > 0){
                    Optional<Program> result = streamServer.getStream().getEpgChannel().getPrograms().stream().filter(program -> program.getId().getStart().withZoneSameInstant(ZoneOffset.UTC).toString().equals(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).format(formatter))).findAny();
                    result.ifPresent(program -> {
                        //Send record request to server.
                        CatchupRecordView catchupRecordView = new CatchupRecordView(program.getId().getTitle(), program.getId().getStart(), program.getId().getStop(), streamServer.getStream().getStreamInputs().get(streamServer.getSelectedSource()), streamServer.getCatchUpDays());
                        Boolean res = serverService.sendRecordRequest(streamServer.getStream().getId(), streamServer.getServer(), catchupRecordView);
                        if (res){
                            streamServer.setRecording(true);
                            streamServerRepository.save(streamServer);
                        }
                    });
                }
            }
        }
    }

}
