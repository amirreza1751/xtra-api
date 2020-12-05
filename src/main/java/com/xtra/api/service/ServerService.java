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
        var result = new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/streams/start/" + channelId + "/?serverId=" + server.getId(), String.class);
        return true;
    }

    public void sendRestartRequest(Long channelId, Server server) {
        new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/streams/restart/" + channelId + "/?serverId=" + server.getId(), String.class);
    }

    public boolean sendStopRequest(Long channelId, Server server) {
        var result = new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/streams/stop/" + channelId, String.class);
        return true;
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

    public Resource getResource(Long serverId) {
        var srv = repository.findById(serverId);
        if (srv.isPresent()){
            return srv.get().getResource();
        } else throw new EntityNotFoundException(Server.class.toString(), serverId.toString());
    }

    @Scheduled(fixedDelay = 3000)
    public void updateServersDetails() {
        List<Server> servers = serverRepository.findAll();
        servers.forEach(server -> {
            try {
                Resource r = new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/servers/resources/?interfaceName=" + server.getInterfaceName(), Resource.class);
                if (r != null) {
                    Resource resource = new Resource();
                     if (server.getResource() != null){
                         resource = server.getResource();
                     }
                    copyProperties(r, resource, "id", "server");
                     server.setResource(resource);
                     serverRepository.save(server);
                } else
                    throw new RuntimeException("Error in fetching resource");
            } catch (RestClientException e) {
                //System.out.println(e.getMostSpecificCause().getMessage());
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
