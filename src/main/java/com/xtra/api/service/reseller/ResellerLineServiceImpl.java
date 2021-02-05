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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('RESELLER')")
public class ResellerLineServiceImpl extends LineService {
    private final ResellerLineMapper lineMapper;

    @Autowired
    protected ResellerLineServiceImpl(LineRepository repository, ResellerLineMapper lineMapper, LineActivityRepository lineActivityRepository) {
        super(repository, Line.class, lineActivityRepository);
        this.lineMapper = lineMapper;
    }

    public Page<LineView> getAllLines(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return new PageImpl<>(findAll(getLoggedInReseller(), search, pageNo, pageSize, sortBy, sortDir).stream().map(lineMapper::convertToView).collect(Collectors.toList()));
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findLineByOwnerUsernameAndIdOrFail(getLoggedInReseller(), id));
    }

    public LineView createLine(LineCreateView createView) {
        return lineMapper.convertToView(insert(lineMapper.convertToEntity(createView)));
    }


    public LineView updateLine(Long id, LineCreateView createView) {
        return lineMapper.convertToView(updateOrFail(id, lineMapper.convertToEntity(createView)));
    }

    @Override
    public void deleteOrFail(Long id) {
        var owner = getLoggedInReseller();
        if (!repository.existsByOwnerUsernameAndId(owner, id))
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
        repository.deleteByOwnerUsernameAndId(owner, id);
    }

    public void updateLineBlock(Long id, boolean blocked) {
        Line line = findLineByOwnerUsernameAndIdOrFail(getLoggedInReseller(), id);
        line.setBlocked(blocked);
        killAllConnections(id);
        repository.save(line);
    }

    public void updateLineBan(Long id, boolean banned) {
        Line line = findLineByOwnerUsernameAndIdOrFail(getLoggedInReseller(), id);
        line.setBanned(banned);
        killAllConnections(id);
        repository.save(line);
    }

    private Line findLineByOwnerUsernameAndIdOrFail(String username, Long id) {
        var result = repository.findByOwnerUsernameAndId(username, id);
        return result.orElseThrow(() -> new EntityNotFoundException(aClass.getSimpleName(), id.toString()));
    }

    private Page<Line> findAll(String username, String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);
        if (StringUtils.isEmpty(search))
            return repository.findAllByOwnerUsername(username, page);
        else
            return findWithSearch(page, search);
    }

    @Override
    protected Page<Line> findWithSearch(Pageable page, String search) {
        return null;
    }

    private String getLoggedInReseller() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            var principal = auth.getPrincipal();
            if (principal != null) {
                return ((User)principal).getUsername();
            }
        }
        throw new AccessDeniedException("access denied");
    }
}
