package com.xtra.api.service.line;

import com.xtra.api.mapper.line.LineLineMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.line.line.LineInsertView;
import com.xtra.api.projection.line.line.LineView;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.xtra.api.service.system.UserAuthService.getCurrentLine;

@Service
public class LineLineServiceImpl extends LineService {
    private final LineLineMapper lineMapper;

    @Autowired
    protected LineLineServiceImpl(LineRepository repository, LineLineMapper lineMapper, ConnectionRepository connectionRepository
            , BCryptPasswordEncoder bCryptPasswordEncoder) {
        super(repository, connectionRepository, bCryptPasswordEncoder);
        this.lineMapper = lineMapper;
    }

    public Map<String, String> downloadLine() {
        Map<String, String> data = new HashMap<>();

        StringBuilder playlist = new StringBuilder("#EXTM3U\n");

        Line line = getCurrentLine();
        DownloadList downloadList = line.getDefaultDownloadList();
        Set<DownloadListCollection> collections = downloadList.getCollectionsAssign();

        for (DownloadListCollection dlCollection : collections) {
            Collection collection = dlCollection.getCollection();

            Set<CollectionStream> streams = collection.getStreams();
            for (CollectionStream cStream : streams) {
                Stream stream = cStream.getStream();
                if (stream.getStreamInputs().size() > 0) {
                    playlist.append("#EXTINF:-1 tvg-id=\"\" tvg-name=\"").append(stream.getName()).append("\" group-title=\"Sports\",").append(stream.getName()).append("\n");
                    playlist.append("http://95.217.186.119:8082/api/channels/play/").append(line.getLineToken()).append("/").append(stream.getStreamToken()).append("\n");
                }

            }
        }

        data.put("fileName", line.getUsername() + ".m3u8");
        data.put("playlist", playlist.toString());

        return data;
    }

    public LineView updateProfile(LineInsertView insertView) {
        return lineMapper.convertToView(updateOrFail(getCurrentLine().getId(), lineMapper.convertToEntity(insertView)));
    }

    public LineView getProfile() {
        return lineMapper.convertToView(getCurrentLine());
    }
}
