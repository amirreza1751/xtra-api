package com.xtra.api.service.line;

import com.xtra.api.mapper.admin.DownloadListMapper;
import com.xtra.api.mapper.line.LineLineMapper;
import com.xtra.api.projection.admin.downloadlist.DownloadListView;
import com.xtra.api.projection.line.LineSecurityView;
import com.xtra.api.projection.line.line.LineSecurityUpdateView;
import com.xtra.api.projection.line.line.LineView;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.RoleRepository;
import com.xtra.api.repository.VodConnectionRepository;
import com.xtra.api.service.DownloadListService;
import com.xtra.api.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.xtra.api.service.system.UserAuthService.getCurrentLine;

@Service
@Validated
public class LineLineServiceImpl extends LineService {
    private final LineLineMapper lineMapper;
    private final DownloadListService downloadListService;
    private final DownloadListMapper downloadListMapper;

    @Autowired
    protected LineLineServiceImpl(LineRepository repository, LineLineMapper lineMapper, ConnectionRepository connectionRepository
            , BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository, DownloadListService downloadListService, DownloadListMapper downloadListMapper, VodConnectionRepository vodConnectionRepository) {
        super(repository, connectionRepository, bCryptPasswordEncoder, roleRepository, vodConnectionRepository);
        this.lineMapper = lineMapper;
        this.downloadListService = downloadListService;
        this.downloadListMapper = downloadListMapper;
    }

    public List<Long> updateDownloadList(@NotNull List<Long> collectionsIds) {
        var currentLine = getCurrentLine();
        var id = currentLine.getId();
        var lineDownloadList = currentLine.getDefaultDownloadList();
        var newDownloadList = lineMapper.convertCollectionIdsToDownloadList(lineDownloadList.getId(), collectionsIds);
        downloadListService.updateDownloadList(lineDownloadList.getId(), newDownloadList);
        return collectionsIds;
    }

    public LineView updateProfileSecurity(@Valid LineSecurityUpdateView updateView) {
        var line = getCurrentLine();
        if (!(updateView.getPassword().isBlank() && updateView.getPassword().isEmpty())) {
            line.setPassword(bCryptPasswordEncoder.encode(updateView.getPassword()));
        }
        if (!updateView.getAllowedIps().isEmpty())
            line.setAllowedIps(updateView.getAllowedIps());
        else if (!updateView.getBlockedIps().isEmpty())
            line.setBlockedIps(updateView.getBlockedIps());
        return lineMapper.convertToView(repository.save(line));
    }

    public LineView getProfile() {
        return lineMapper.convertToView(getCurrentLine());
    }

    public ResponseEntity<String> downloadLinePlaylist() {
        return downloadLinePlaylist(getCurrentLine());
    }

    public LineSecurityView getSecurityDetails() {
        return lineMapper.getSecurityDetails(getCurrentLine());
    }

    public DownloadListView getDownloadList() {
        return downloadListMapper.convertToView(getCurrentLine().getDefaultDownloadList());
    }
}
