package com.xtra.api.service.reseller;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.mapper.reseller.ResellerLineMapper;
import com.xtra.api.model.Line;
import com.xtra.api.model.Reseller;
import com.xtra.api.projection.reseller.line.LineCreateView;
import com.xtra.api.projection.reseller.line.LineView;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.LineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ResellerLineServiceImpl extends LineService {
    private final ResellerLineMapper lineMapper;
    private Reseller reseller;

    @Autowired
    protected ResellerLineServiceImpl(LineRepository repository, ResellerLineMapper lineMapper, LineActivityRepository lineActivityRepository) {
        super(repository, Line.class, lineActivityRepository);
        this.lineMapper = lineMapper;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            var principal = auth.getPrincipal();
            if (principal != null) {
                reseller = (Reseller) principal;
            }
        }

    }

    public Page<LineView> getAllLines(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return new PageImpl<>(findAll(reseller, search, pageNo, pageSize, sortBy, sortDir).stream().map(lineMapper::convertToView).collect(Collectors.toList()));
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findLineByOwnerAndIdOrFail(reseller, id));
    }

    public LineView createLine(LineCreateView createView) {
        return lineMapper.convertToView(insert(lineMapper.convertToEntity(createView)));
    }


    public LineView updateLine(Long id, LineCreateView createView) {
        return lineMapper.convertToView(updateOrFail(id, lineMapper.convertToEntity(createView)));
    }

    public void deleteOrFail(Reseller owner, Long id) {
        if (!repository.existsByOwnerAndId(owner, id))
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
        repository.deleteByOwnerAndId(owner, id);
    }

    public void updateLineBlock(Long id, boolean blocked) {
        Line line = findLineByOwnerAndIdOrFail(reseller, id);
        line.setBlocked(blocked);
        killAllConnections(id);
        repository.save(line);
    }

    public void updateLineBan(Long id, boolean banned) {
        Line line = findLineByOwnerAndIdOrFail(reseller, id);
        line.setBanned(banned);
        killAllConnections(id);
        repository.save(line);
    }

    private Line findLineByOwnerAndIdOrFail(Reseller reseller, Long id) {
        var result = repository.findByOwnerAndId(reseller, id);
        return result.orElseThrow(() -> new EntityNotFoundException(aClass.getSimpleName(), id.toString()));
    }

    private Page<Line> findAll(Reseller owner, String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);
        if (StringUtils.isEmpty(search))
            return repository.findAllByOwner(owner, page);
        else
            return findWithSearch(page, search);
    }

    @Override
    protected Page<Line> findWithSearch(Pageable page, String search) {
        return null;
    }

}
