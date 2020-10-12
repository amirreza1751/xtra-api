package com.xtra.api.service;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ServerService extends CrudService<Server, Long, ServerRepository> {
    private final ServerRepository serverRepository;
    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    @Autowired
    protected ServerService(ServerRepository repository, ServerRepository serverRepository) {
        super(repository, Server.class);
        this.serverRepository = serverRepository;
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
        var result = new RestTemplate().getForObject("http://"+ server.getIp() + ":" + server.getCorePort() + "/streams/start/" + channelId, String.class);
        return true;
    }

    public void sendRestartRequest(Long channelId) {
        new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/restart/" + channelId, String.class);
    }

    public boolean sendStopRequest(Long channelId) {
        var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/stop/" + channelId, String.class);
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

    public MediaInfo getMediaInfo(Movie movie) {
        return new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/info/", movie, MediaInfo.class);
    }

    public String SetAudioRequest(Movie movie) {
        return new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/set_audios/", movie, String.class);
    }


    @Override
    protected Page<Server> findWithSearch(Pageable page, String search) {
        return null;
    }

    public boolean existsAllByIdIn(ArrayList<Long> ids){
        return serverRepository.existsAllByIdIn(ids);
    }

    public String sendPlayRequest(String stream_token, String line_token, Server server){
        return new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/streams?line_token" + line_token + "&stream_token=" + stream_token + "&extension=m3u8", String.class);
    }

    public Resource getResourceUsage(Long serverId){
        Optional<Server> srv = serverRepository.findById(serverId);
        if (srv.isPresent()){
            var server = srv.get();
            return new RestTemplate().getForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/servers/resources/", Resource.class);
        } else throw new EntityNotFoundException(aClass.getSimpleName(), serverId.toString());
    }
}
