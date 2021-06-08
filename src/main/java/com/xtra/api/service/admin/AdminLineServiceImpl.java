package com.xtra.api.service.admin;

import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.mapper.admin.AdminLineMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.line.*;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.RoleRepository;
import com.xtra.api.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class AdminLineServiceImpl extends LineService {
    private final AdminLineMapper lineMapper;

    @Autowired
    public AdminLineServiceImpl(LineRepository repository, ConnectionRepository connectionRepository, AdminLineMapper lineMapper
            , BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        super(repository, connectionRepository, bCryptPasswordEncoder, roleRepository);
        this.lineMapper = lineMapper;
    }

    public Page<LineListView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(lineMapper::convertToListView);
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

    public void saveAll(LineBatchInsertView insertView) {
        var lineIds = insertView.getLineIds();

        if (lineIds != null) {
            for (Long lineId : lineIds) {
                Line line = repository.findById(lineId).orElseThrow(() -> new EntityNotFoundException("Line", lineId.toString()));

                if (insertView.getNeverExpire() != null && !insertView.getNeverExpire().equals(""))
                    line.setNeverExpire(Boolean.parseBoolean(insertView.getNeverExpire()));

                if (insertView.getMaxConnections() > 0)
                    line.setMaxConnections(insertView.getMaxConnections());

                if (insertView.getIsTrial() != null && !insertView.getIsTrial().equals(""))
                    line.setTrial(Boolean.parseBoolean(insertView.getIsTrial()));

                if (insertView.getIsBanned() != null && !insertView.getIsBanned().equals(""))
                    line.setBanned(Boolean.parseBoolean(insertView.getIsBanned()));

                if (insertView.getIsBlocked() != null && !insertView.getIsBlocked().equals(""))
                    line.setBlocked(Boolean.parseBoolean(insertView.getIsBlocked()));

                if (insertView.getAdminNotes() != null && !insertView.getAdminNotes().equals(""))
                    line.setAdminNotes(insertView.getAdminNotes());

                if (insertView.getAllowedOutputs().size() > 0)
                    line.setAllowedOutputs(insertView.getAllowedOutputs());

                repository.save(line);
            }
        }
    }

    public void deleteAll(LineBatchDeleteView view) {
        var lineIds = view.getLineIds();
        if (lineIds != null) {
            for (Long lineId : lineIds) {
                deleteOrFail(lineId);
            }
        }
    }

    public void updateLineBlock(Long id, boolean blocked) {
        Line line = findByIdOrFail(id);
        line.setBlocked(blocked);
        repository.save(line);
    }

    public void updateLineBan(Long id, boolean banned) {
        Line line = findByIdOrFail(id);
        line.setBanned(banned);
        repository.save(line);
    }

    @Override
    public Line updateOrFail(Long id, Line newLine) {
        Line oldLine = findByIdOrFail(id);
        copyProperties(newLine, oldLine, "id", "lineToken", "currentConnections", "role");
        return repository.save(oldLine);
    }

    public Optional<Line> findById(Long lineId) {
        return repository.findById(lineId);
    }


    public Line findByTokenOrFail(String lineToken) {
        return repository.findByLineToken(lineToken).orElseThrow(() -> new EntityNotFoundException(entityName, "token", lineToken));
    }

    public ResponseEntity<String> downloadLinePlaylist(Long lineId) {
        return downloadLinePlaylist(findByIdOrFail(lineId));
    }
}
