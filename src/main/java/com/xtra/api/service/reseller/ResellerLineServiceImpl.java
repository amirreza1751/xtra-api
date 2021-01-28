package com.xtra.api.service.reseller;

import com.xtra.api.mapper.admin.LineMapper;
import com.xtra.api.model.Line;
import com.xtra.api.projection.line.LineInsertView;
import com.xtra.api.projection.line.LineView;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ResellerLineServiceImpl extends LineService {
    private final LineMapper lineMapper;

    @Autowired
    protected ResellerLineServiceImpl(LineRepository repository, LineMapper lineMapper, LineActivityRepository lineActivityRepository) {
        super(repository, Line.class, lineActivityRepository);
        this.lineMapper = lineMapper;
    }

    public Page<LineView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return new PageImpl<>(findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(lineMapper::convertToView).collect(Collectors.toList()));
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findByIdOrFail(id));
    }

    public LineView add(LineInsertView lineInsertView) {
        return lineMapper.convertToView(insert(lineMapper.convertToEntity(lineInsertView)));
    }

    @Override
    protected Page<Line> findWithSearch(Pageable page, String search) {
        return null;
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

}
