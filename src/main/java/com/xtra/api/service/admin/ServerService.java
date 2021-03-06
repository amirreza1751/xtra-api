package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.ChannelMapper;
import com.xtra.api.mapper.admin.ServerMapper;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.server.File;
import com.xtra.api.model.server.Resource;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.StreamServer;
import com.xtra.api.model.vod.Movie;
import com.xtra.api.model.vod.Video;
import com.xtra.api.model.vod.VideoInfo;
import com.xtra.api.projection.EntityListItem;
import com.xtra.api.projection.admin.catchup.CatchupRecordView;
import com.xtra.api.projection.admin.channel.ChannelStart;
import com.xtra.api.projection.admin.server.ServerInfo;
import com.xtra.api.projection.admin.server.ServerInsertView;
import com.xtra.api.projection.admin.server.ServerView;
import com.xtra.api.projection.admin.server.resource.ResourceView;
import com.xtra.api.projection.system.CoreConfiguration;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.CrudService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Configuration
@EnableScheduling
@Service
@Log4j2
public class ServerService extends CrudService<Server, Long, ServerRepository> {
    private final ConnectionRepository connectionRepository;
    private final StreamServerRepository streamServerRepository;
    private final ServerMapper serverMapper;
    private final WebClient webClient;
    private final ChannelMapper channelMapper;

    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    @Autowired
    protected ServerService(ServerRepository repository, ConnectionRepository connectionRepository, StreamServerRepository streamServerRepository, ServerMapper serverMapper, WebClient.Builder webClientBuilder, ChannelMapper channelMapper) {
        super(repository, "Server");
        this.connectionRepository = connectionRepository;
        this.streamServerRepository = streamServerRepository;
        this.serverMapper = serverMapper;
        this.channelMapper = channelMapper;
        TcpClient tcpClient = TcpClient.create()
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(3000, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(3000, TimeUnit.MILLISECONDS));
                })
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
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

    public void sendStartRequest(Server server, ChannelStart channelStartData) {
        this.webClient
                .post()
                .uri(URI.create("http://" + server.getIp() + ":" + server.getCorePort()
                        + "/streams/start"))
                .body(Mono.just(channelStartData), ChannelStart.class)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("4xx Client Error")))
                .bodyToMono(Void.class)
                .block();

    }

    public void sendAsyncStartRequest(Server server, ChannelStart channelStartData) {
        var result = this.webClient
                .post()
                .uri(URI.create("http://" + server.getIp() + ":" + server.getCorePort()
                        + "/streams/start"))
                .body(Mono.just(channelStartData), ChannelStart.class)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("4xx Client Error")))
                .bodyToMono(Void.class);
        result.subscribe(
                successValue -> log.info("success value: " + successValue),
                error -> log.error("error value: " + error)
        );
    }

    public void sendStopRequest(Long channelId, Server server) {
        this.webClient
                .get()
                .uri(URI.create("http://" + server.getIp() + ":" + server.getCorePort()
                        + "/streams/" + channelId + "/stop"))
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("4xx Client Error")))
                .bodyToMono(Void.class)
                .block();
    }

    public void startAllChannelsOnServer(Long serverId) {
        var server = findByIdOrFail(serverId);
        var channelStartList = server.getStreamServers().stream().map(streamServer -> channelMapper.convertToChannelStart(streamServer.getStream(), 0));
        var result = this.webClient
                .post()
                .uri(URI.create("http://" + server.getIp() + ":" + server.getCorePort()
                        + "/streams/start"))
                .body(Mono.just(channelStartList), List.class)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("4xx Client Error")))
                .bodyToMono(Void.class);
        result.subscribe(
                successValue -> log.info("success value: " + successValue),
                error -> log.error("error value: " + error)
        );
    }

    public void stopAllChannelsOnServer(Long serverId) {
        var server = findByIdOrFail(serverId);
        this.webClient
                .get()
                .uri(URI.create("http://" + server.getIp() + ":" + server.getCorePort()
                        + "/streams/stop"))
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("4xx Client Error")))
                .bodyToMono(Void.class)
                .block();
    }

    public Boolean restartAllChannelsOnServer(Long serverId) {
        var server = findByIdOrFail(serverId);
        new RestTemplate().getForObject(getServerAddress(server) + "/streams/restart", Boolean.class);
        return true;
    }

    public Boolean sendUpdateConfigRequest(Server server, CoreConfiguration configuration) {
        return this.webClient
                .post()
                .uri(URI.create("http://" + server.getIp() + ":" + server.getCorePort()
                        + "/config"))
                .body(Mono.just(configuration), CoreConfiguration.class)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("4xx Client Error")))
                .bodyToMono(Boolean.class).block();

    }

    public Optional<Server> findByName(String search) {
        return repository.findByName(search);
    }

    public void sendEncodeRequest(Video video) {
        new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/encode/", video, String.class);
    }

    public List<VideoInfo> getMediaInfo(Server server, List<Video> videoList) {
        return Arrays.asList(Objects.requireNonNull(new RestTemplate().postForObject("http://" + server.getIp() + ":" + server.getCorePort() + "/vod/info/", videoList, VideoInfo[].class)));
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

    public Optional<Server> findByServerToken(String token) {
        return repository.findByToken(token);
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
                Resource r = new RestTemplate().getForObject(getServerAddress(server) + "/resources?interfaceName=" + server.getInterfaceName(), Resource.class);
                if (r != null) {
                    Resource resource = new Resource();
                    if (server.getResource() != null) {
                        resource = server.getResource();
                    }
                    copyProperties(r, resource, "id", "server");
                    resource.setConnections(connectionRepository.countAllByServerId(server.getId()));
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
            return connectionRepository.countAllByServerId(serverId);
        } else throw new RuntimeException("Server Not Found.");
    }

    public StreamServer saveStreamServer(StreamServer streamServer) {
        return streamServerRepository.save(streamServer);
    }


    public Page<ServerInfo> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(serverMapper::convertToSimpleView);
    }

    public ServerView getById(Long id) {
        return serverMapper.convertToView(findByIdOrFail(id));
    }

    public ServerView add(ServerInsertView insertView) {
        var server = serverMapper.convertToEntity(insertView);
        UUID uuid = UUID.randomUUID();
        server.setToken(uuid.toString());
        var config = new CoreConfiguration("token", uuid.toString());
        if (!sendUpdateConfigRequest(server, config)) {
            throw new RuntimeException("could not contact server");
        }
        return serverMapper.convertToView(insert(server));
    }

    public ServerView save(Long id, ServerInsertView insertView) {
        return serverMapper.convertToView(updateOrFail(id, serverMapper.convertToEntity(insertView)));
    }

    private String getServerAddress(Server server) {
        //@todo different protocols - use dns if present
        return "http://" + server.getIp() + ":" + server.getCorePort();
    }

    @Override
    public Server updateOrFail(Long id, Server newServer) {
        Server oldObject = findByIdOrFail(id);
        copyProperties(newServer, oldObject, "id", "token");
        return repository.save(oldObject);
    }

    //Catch-up Methods
    public Boolean sendRecordRequest(Long streamId, Server server, CatchupRecordView catchupRecordView) {
        return this.webClient
                .post()
                .uri(URI.create("http://" + server.getIp() + ":" + server.getCorePort()
                        + "/streams/" + streamId + "/catch-up/record"))
                .body(Mono.just(catchupRecordView), CatchupRecordView.class)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("4xx Client Error")))
                .bodyToMono(Boolean.class).block();
    }

    public <T> T sendPostRequest(String uri, Class<T> tClass, Object data) {
        ResponseEntity<T> result;
        try {
            result = new RestTemplate().postForEntity(uri, data, tClass);
        } catch (HttpClientErrorException | NullPointerException | ResourceAccessException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
        return result.getBody();
    }


    public List<EntityListItem> getServerList(String search) {
        return repository.findAllByNameContains(search).stream().map(serverMapper::convertToEntityListItem).collect(Collectors.toList());
    }
}
