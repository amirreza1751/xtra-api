package com.xtra.api.service.admin;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.mapper.admin.MappingService;
import com.xtra.api.mapper.admin.ServerMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.server.ServerView;
import com.xtra.api.projection.admin.server.SimpleServerView;
import com.xtra.api.projection.admin.server.resource.ResourceView;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.CrudService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.springframework.beans.BeanUtils.copyProperties;

@Configuration
@EnableScheduling
@Service
public class ServerService extends CrudService<Server, Long, ServerRepository> implements MappingService<Server, ServerView, SimpleServerView> {
    private final LineActivityRepository lineActivityRepository;
    private final StreamServerRepository streamServerRepository;
    private final ServerMapper serverMapper;
    private final WebClient webClient;

    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    @Autowired
    protected ServerService(ServerRepository repository, LineActivityRepository lineActivityRepository, StreamServerRepository streamServerRepository, ServerMapper serverMapper, WebClient.Builder webClientBuilder) {
        super(repository, "Server");
        this.lineActivityRepository = lineActivityRepository;
        this.streamServerRepository = streamServerRepository;
        this.serverMapper = serverMapper;
        TcpClient tcpClient = TcpClient.create()
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(10000, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(10000, TimeUnit.MILLISECONDS));
                })
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
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

    public void sendStartRequest(Long channelId, Server server) {
        Mono<Boolean> result = this.webClient
                .get()
                .uri(URI.create("http://" + server.getIp() + ":" + server.getCorePort()
                        + "/streams/" + channelId + "/start"))
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("4xx Client Error")))
                .bodyToMono(Boolean.class);
        result.subscribe(
                successValue -> System.out.println("success value: " + successValue),
                error -> System.out.println("error value: " + error)
        );
    }

    public void sendRestartRequest(Long channelId, Server server) {
        Mono<Boolean> result = this.webClient
                .get()
                .uri(URI.create("http://" + server.getIp() + ":" + server.getCorePort()
                        + "/streams/" + channelId + "/restart"))
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("4xx Client Error")))
                .bodyToMono(Boolean.class);
        result.subscribe(
                successValue -> System.out.println("success value: " + successValue),
                error -> System.out.println("error value: " + error)
        );
    }

    public void sendStopRequest(Long channelId, Server server) {
        Mono<Boolean> result = this.webClient
                .get()
                .uri(URI.create("http://" + server.getIp() + ":" + server.getCorePort()
                        + "/streams/" + channelId + "/stop"))
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("4xx Client Error")))
                .bodyToMono(Boolean.class);
        result.subscribe(
                successValue -> System.out.println("success value: " + successValue),
                error -> System.out.println("error value: " + error)
        );
    }

    public Optional<Server> findByName(String search) {
        return repository.findByName(search);
    }

    public void sendEncodeRequest(Video video) {
        new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/encode/", video, String.class);
    }

    public VideoInfo getMediaInfo(Movie movie) {
        return new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/info/", movie, VideoInfo.class);
    }

    public String SetAudioRequest(Movie movie) {
        return new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/set_audios/", movie, String.class);
    }


    @Override
    protected Page<Server> findWithSearch(String search, Pageable page) {
        return null;
    }


    public String sendPlayRequest(String stream_token, String line_token, Server server) {
        return new RestTemplate().getForObject(getServerAddress(server) + "/streams?line_token=" + line_token + "&stream_token=" + stream_token + "&extension=m3u8", String.class);
    }

    public Optional<Server> findByIpAndCorePort(String ip, String corePort) {
        return repository.findByIpAndCorePort(ip, corePort);
    }

    public List<Server> findByIdIn(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    public ResourceView getServerResource(Long serverId) {
        var srv = repository.findById(serverId);
        if (srv.isPresent()) {
            return serverMapper.convertResourceToView(srv.get().getResource());
        } else throw new EntityNotFoundException(Server.class.toString(), serverId.toString());
    }

    @Scheduled(fixedDelay = 3000)
    public void updateServersDetails() {
        List<Server> servers = repository.findAll();
        servers.forEach(server -> {
            try {
                if (server.getIp() == null || server.getCorePort() == null)
                    return;
                Resource r = new RestTemplate().getForObject(getServerAddress(server) + "/resources/?interfaceName=" + server.getInterfaceName(), Resource.class);
                if (r != null) {
                    Resource resource = new Resource();
                    if (server.getResource() != null) {
                        resource = server.getResource();
                    }
                    copyProperties(r, resource, "id", "server");
                    resource.setConnections(lineActivityRepository.countAllByIdServerId(server.getId()));
                    server.setResource(resource);
                    repository.save(server);
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

    public Optional<StreamServer> findStreamServerById(StreamServerId streamServerId) {
        return streamServerRepository.findById(streamServerId);
    }

    public StreamServer saveStreamServer(StreamServer streamServer) {
        return streamServerRepository.save(streamServer);
    }

    public Boolean startAllChannelsOnServer(Long serverId) {
        var server = findByIdOrFail(serverId);
        new RestTemplate().getForObject(getServerAddress(server) + "/streams/start", Boolean.class);
        return true;
    }

    public Boolean stopAllChannelsOnServer(Long serverId) {
        var server = findByIdOrFail(serverId);
        new RestTemplate().getForObject(getServerAddress(server) + "/streams/stop", Boolean.class);
        return true;
    }

    public Boolean restartAllChannelsOnServer(Long serverId) {
        var server = findByIdOrFail(serverId);
        new RestTemplate().getForObject(getServerAddress(server) + "/streams/restart", Boolean.class);
        return true;
    }

    @Override
    public Page<SimpleServerView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(serverMapper::convertToSimpleView);
    }

    @Override
    public ServerView getById(Long id) {
        return serverMapper.convertToView(findByIdOrFail(id));
    }

    @Override
    public ServerView add(ServerView server) {
        return serverMapper.convertToView(insert(serverMapper.convertToEntity(server)));
    }

    @Override
    public ServerView save(Long id, ServerView server) {
        return serverMapper.convertToView(updateOrFail(id, serverMapper.convertToEntity(server)));
    }

    private String getServerAddress(Server server) {
        //@todo different protocols - use dns if present
        return "http://" + server.getIp() + ":" + server.getCorePort();
    }


}
