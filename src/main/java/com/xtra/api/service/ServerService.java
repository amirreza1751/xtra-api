package com.xtra.api.service;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.ResourceRepository;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.repository.StreamServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.beans.BeanUtils.copyProperties;

@Configuration
@EnableScheduling
@Service
public class ServerService extends CrudService<Server, Long, ServerRepository> {
    private final ServerRepository serverRepository;
    private final ResourceRepository resourceRepository;
    private final LineActivityRepository lineActivityRepository;
    private final StreamServerRepository streamServerRepository;
    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    @Autowired
    protected ServerService(ServerRepository repository, ServerRepository serverRepository, ResourceRepository resourceRepository, LineActivityRepository lineActivityRepository, StreamServerRepository streamServerRepository) {
        super(repository, Server.class);
        this.serverRepository = serverRepository;
        this.resourceRepository = resourceRepository;
        this.lineActivityRepository = lineActivityRepository;
        this.streamServerRepository = streamServerRepository;
    }

    public List<File> getFiles(Long id, String path) {
        List<File> result = null;
        try {
            result = new RestTemplate().getForObject(corePath + ":" + corePort + "/file/list_files?path=" + path, List.class);
        } catch (HttpClientErrorException exception) {
            System.out.println(exception.getMessage());
        }
        return result;
    }

    public boolean sendStartRequest(Long channelId, Server server) {
        var result = new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/streams/start/" + channelId, String.class);
        return true;
    }

    public void sendRestartRequest(Long channelId, Server server) {
        new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/streams/restart/" + channelId, String.class);
    }

    public boolean sendStopRequest(Long channelId, Server server) {
        var result = new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/streams/stop/" + channelId, String.class);
        return true;
    }

    public void sendKillAllConnectionRequest(Long lineId) {
        var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/lines/kill_connections/" + lineId, Boolean.class);
    }

    public Optional<Server> findByName(String search) {
        return repository.findByName(search);
    }

    public void sendEncodeRequest(Movie movie) {
        new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/encode/", movie, String.class);
    }

    public VideoInfo getMediaInfo(Movie movie) {
        return new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/info/", movie, VideoInfo.class);
    }

    public String SetAudioRequest(Movie movie) {
        return new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/set_audios/", movie, String.class);
    }


    @Override
    protected Page<Server> findWithSearch(Pageable page, String search) {
        return null;
    }

    public boolean existsAllByIdIn(Set<Long> ids) {
        return serverRepository.existsAllByIdIn(ids);
    }

    public String sendPlayRequest(String stream_token, String line_token, Server server) {
        return new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/streams?line_token=" + line_token + "&stream_token=" + stream_token + "&extension=m3u8", String.class);
    }

    public Optional<Server> findByIpAndCorePort(String ip, String corePort){
        return repository.findByIpAndCorePort(ip, corePort);
    }

    public Resource getResourceUsage(Long serverId, String interfaceName) {
        Optional<Server> srv = serverRepository.findById(serverId);
        if (srv.isPresent()) {
            var server = srv.get();
            Resource r = new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/servers/resources/?interfaceName=" + interfaceName, Resource.class);
            if (r != null) {
                Resource resource = resourceRepository.findByServerId(serverId).orElseGet(Resource::new);
                copyProperties(r, resource, "id", "server");
                resource.setServer(server);
                return resourceRepository.save(resource);
            } else
                throw new RuntimeException("Error in fetching resource");
        } else throw new EntityNotFoundException(aClass.getSimpleName(), serverId.toString());
    }

    public List<Server> details() {
        return serverRepository.findAll();
    }

    public Resource getRes(Long serverId) {
        return resourceRepository.findByServerId(serverId).get();
    }

    @Scheduled(fixedDelay = 3000)
    public void updateServersDetails() {
        List<Server> servers = serverRepository.findAll();
        servers.forEach(server -> {
            try {
                Resource r = new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/servers/resources/?interfaceName=" + server.getInterfaceName(), Resource.class);
                if (r != null) {
                    Resource resource = resourceRepository.findByServerId(server.getId()).orElseGet(Resource::new);
                    copyProperties(r, resource, "id", "server");
                    resource.setServer(server);
                    resource.setConnections(this.getServerConnectionsCount(server.getId()));
                    resourceRepository.save(resource);
                    System.out.println("test");
                } else
                    throw new RuntimeException("Error in fetching resource");
            } catch (RestClientException e) {
                System.out.println(e.getMostSpecificCause().getMessage());
            }
        });
    }

    public int getServerConnectionsCount(Long serverId) {
        if (this.existsById(serverId)) {
            return lineActivityRepository.countAllByIdServerId(serverId);
        } else throw new RuntimeException("Server Not Found.");
    }

    public Optional<StreamServer> findStreamServerById(StreamServerId streamServerId){
        return streamServerRepository.findById(streamServerId);
    }
    public StreamServer saveStreamServer(StreamServer streamServer){
        return streamServerRepository.save(streamServer);
    }
}
