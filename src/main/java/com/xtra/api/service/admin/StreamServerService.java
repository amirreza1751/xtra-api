package com.xtra.api.service.admin;

import com.xtra.api.model.stream.StreamServer;
import com.xtra.api.projection.admin.ChangingServerPair;
import com.xtra.api.repository.StreamServerRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class StreamServerService {
    final StreamServerRepository repository;
    final StreamService streamService;
    final ServerService serverService;

    public StreamServerService(StreamServerRepository repository, StreamService streamService, ServerService serverService){
        this.repository = repository;
        this.streamService = streamService;
        this.serverService = serverService;
    }
    public void changeServer(ChangingServerPair changingServerPair){
        var oldStreamServerList = repository.findAllByServerId(changingServerPair.getOldServerID());

        for(StreamServer oldStreamServer : oldStreamServerList){
            var stream = streamService.findById(oldStreamServer.getStream().getId());
            var streamServers = stream.getStreamServers();
            Optional<StreamServer> newServer = streamServers.stream().filter(streamServer -> streamServer.getServer().getId().equals(changingServerPair.getNewServerID())).findFirst();
            if(newServer.isPresent()){
                streamServers.removeIf(streamServer -> streamServer.getServer().getId().equals(changingServerPair.getOldServerID()));
            }else {
                Optional<StreamServer> oldServer = streamServers.stream().filter(streamServer -> streamServer.getServer().getId().equals(changingServerPair.getOldServerID())).findFirst();
                if(oldServer.isPresent()){
                    oldServer.get().setServer(serverService.findByIdOrFail(changingServerPair.getNewServerID()));
                }else {
                    throw new EntityNotFoundException();
                }
            }
            repository.delete(oldStreamServer);
            streamService.updateOrFail(stream.getId(),stream);
        }
    }
}
