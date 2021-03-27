package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.AdminLineMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.line.LineInsertView;
import com.xtra.api.projection.admin.line.LineView;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.xtra.api.service.system.UserAuthService.getCurrentLine;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class AdminLineServiceImpl extends LineService {
    private final AdminLineMapper lineMapper;

    @Autowired
    public AdminLineServiceImpl(LineRepository repository, LineActivityRepository lineActivityRepository, AdminLineMapper lineMapper
            , BCryptPasswordEncoder bCryptPasswordEncoder) {
        super(repository, lineActivityRepository, bCryptPasswordEncoder);
        this.lineMapper = lineMapper;
    }

    public Page<LineView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(lineMapper::convertToView);
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findByIdOrFail(id));
    }

    public LineView add(@Valid LineInsertView lineInsertView) {
        return lineMapper.convertToView(insert(lineMapper.convertToEntity(lineInsertView)));
    }

    public LineView save(Long id, LineInsertView lineInsertView) {
        return lineMapper.convertToView(updateOrFail(id, lineMapper.convertToEntity(lineInsertView)));
    }

    public void updateLineBlock(Long id, boolean blocked) {
        Line line = findByIdOrFail(id);
        line.setBlocked(blocked);
        killAllConnections(id);
        repository.save(line);
    }

    public void updateLineBan(Long id, boolean banned) {
        Line line = findByIdOrFail(id);
        line.setBanned(banned);
        killAllConnections(id);
        repository.save(line);
    }

    @Override
    public Line updateOrFail(Long id, Line newLine) {
        Line oldLine = findByIdOrFail(id);
        copyProperties(newLine, oldLine, "id","lineToken","currentConnections");
        return repository.save(oldLine);
    }

    public Optional<Line> findById(Long lineId) {
        return repository.findById(lineId);
    }

    @Override
    protected Page<Line> findWithSearch(Pageable page, String search) {
        return repository.findByUsernameLikeOrAdminNotesLikeOrResellerNotesLike(search, search, search, page);
    }

    public Map<String, String> downloadLine(Long id) {
        Map<String, String> data = new HashMap<>();

        StringBuilder playlist = new StringBuilder("#EXTM3U\n");

        Line line = findByIdOrFail(id);
        DownloadList downloadList = line.getDefaultDownloadList();
        if (downloadList != null) {
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
        }

        data.put("fileName", line.getUsername() + ".m3u8");
        data.put("playlist", playlist.toString());

        return data;
    }

}
