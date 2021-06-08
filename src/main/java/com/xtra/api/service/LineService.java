package com.xtra.api.service;

import com.xtra.api.model.*;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.xtra.api.util.Utilities.generateRandomString;
import static com.xtra.api.util.Utilities.wrapSearchString;
import static org.springframework.beans.BeanUtils.copyProperties;

public abstract class LineService extends CrudService<Line, Long, LineRepository> {
    protected final ConnectionRepository connectionRepository;
    protected final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;


    protected LineService(LineRepository repository, ConnectionRepository connectionRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        super(repository, "Line");
        this.connectionRepository = connectionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    protected Page<Line> findWithSearch(String search, Pageable page) {
        search = wrapSearchString(search);
        return repository.findByUsernameLikeOrAdminNotesLikeOrResellerNotesLike(search, search, search, page);
    }

    @Override
    public Line insert(Line line) {
        var role = roleRepository.findByTypeAndName(UserType.LINE, "default").orElseThrow(() -> new RuntimeException("default role not found"));
        line.setRole(role);
        if (line.getRole().getType() != UserType.LINE) {
            throw new RuntimeException("role not suitable for line");
        }
        String lineUsername = line.getUsername();
        if (lineUsername == null || StringUtils.isEmpty(lineUsername)) {
            var isUnique = false;
            var username = "";
            while (!isUnique) {
                username = generateRandomString(8, 12, true);
                if (!repository.existsByUsername(username)) {
                    isUnique = true;
                }
            }
            line.setUsername(username);
        } else {
            if (repository.existsByUsername(lineUsername))
                //@todo change exception type
                throw new RuntimeException("line Username already exists");
        }
        line.setPassword(bCryptPasswordEncoder.encode(line.getPassword()));

        String token;
        do {
            token = generateRandomString(8, 12, false);
        }
        while (repository.findByLineToken(token).isPresent());
        line.setLineToken(token);
        return repository.save(line);
    }

    @Override
    public Line updateOrFail(Long id, Line newLine) {
        var line = findByIdOrFail(id);
        newLine.setPassword(bCryptPasswordEncoder.encode(newLine.getPassword()));
        copyProperties(newLine, line, "id", "username");
        return repository.save(line);
    }

    public ResponseEntity<String> downloadLinePlaylist(Line line) {

        StringBuilder playlist = new StringBuilder("#EXTM3U\n");

        DownloadList downloadList = line.getDefaultDownloadList();
        if (downloadList != null) {
            Set<DownloadListCollection> collections = downloadList.getCollectionsAssign();

            for (DownloadListCollection dlCollection : collections) {
                Collection collection = dlCollection.getCollection();

                Set<CollectionStream> streams = collection.getStreams();
                for (CollectionStream cStream : streams) {
                    Stream stream = cStream.getStream();

                    if (stream.getStreamInputs().size() > 0) {
                        playlist.append("#EXTINF:-1 tvg-id=\"\" tvg-name=\"").append(stream.getName()).append("\" group-title=\"Sports\",").append(stream.getName()).append("\n");
                        playlist.append("http://").append(serverAddress).append(":").append(serverPort).append("/api/channels/play/").append(line.getLineToken()).append("/").append(stream.getStreamToken()).append("\n");
                    }

                }
            }
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, "application/x-mpegurl");
        responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(playlist.length()));
        responseHeaders.set(HttpHeaders.CACHE_CONTROL, "no-cache");
        responseHeaders.add(HttpHeaders.CACHE_CONTROL, "no-store");
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + line.getUsername() + ".m3u8\"");

        return ResponseEntity.ok().
                headers(responseHeaders)
                .body(playlist.toString());
    }

    public boolean getIsOnline(Long lineId) {
        return connectionRepository.countAllByLineId(lineId) > 0;
    }

    public long getConnectionsCount(Long lineId) {
        return connectionRepository.countAllByLineId(lineId);
    }
}
