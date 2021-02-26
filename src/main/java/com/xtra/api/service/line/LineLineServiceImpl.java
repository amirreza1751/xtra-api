package com.xtra.api.service.line;

import com.xtra.api.mapper.line.LineLineMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.line.line.LineInsertView;
import com.xtra.api.projection.line.line.LineView;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class LineLineServiceImpl extends LineService {
    private final LineLineMapper lineMapper;

    @Autowired
    protected LineLineServiceImpl(LineRepository repository, LineLineMapper lineMapper, LineActivityRepository lineActivityRepository) {
        super(repository, Line.class, lineActivityRepository);
        this.lineMapper = lineMapper;
    }

    @Override
    protected Page<Line> findWithSearch(Pageable page, String search) {
        return null;
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findByIdOrFail(id));
    }

    public LineView save(Long id, LineInsertView lineInsertView) {
        return lineMapper.convertToView(updateOrFail(id, lineMapper.convertToEntity(lineInsertView)));
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

                //#EXTINF:-1 tvg-id="" tvg-name="Sport-DE: Eurosport 1 FHD (NULL)" tvg-logo="" group-title="Sports",Sport-DE: Eurosport 1 FHD (NULL)
                //http://portal.unblkservice1.xyz:8080/mamad1234/mamad123/48876
                //http://95.217.186.119:8082/api/channels/play/WjHQKChfpjHSc2t/XBSSK1UUenD
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

    private Line getCurrentLine() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            var principal = auth.getPrincipal();
            if (principal != null) {
                return findByUsernameOrFail(((User) principal).getUsername());
            }
        }
        throw new AccessDeniedException("access denied");
    }
}
