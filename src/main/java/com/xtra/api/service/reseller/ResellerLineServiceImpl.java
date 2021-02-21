package com.xtra.api.service.reseller;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.exceptions.ActionNotAllowedException;
import com.xtra.api.mapper.reseller.ResellerLineMapper;
import com.xtra.api.model.Line;
import com.xtra.api.model.Package;
import com.xtra.api.projection.reseller.line.LineCreateView;
import com.xtra.api.projection.reseller.line.LineView;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.ResellerRepository;
import com.xtra.api.service.LineService;
import com.xtra.api.service.admin.PackageService;
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

import javax.transaction.Transactional;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@PreAuthorize("hasRole('RESELLER')")
public class ResellerLineServiceImpl extends LineService {
    private final ResellerLineMapper lineMapper;
    private final PackageService packageService;
    private final ResellerRepository resellerRepository;

    @Autowired
    protected ResellerLineServiceImpl(LineRepository repository, ResellerLineMapper lineMapper, LineActivityRepository lineActivityRepository, PackageService packageService, ResellerRepository resellerRepository) {
        super(repository, Line.class, lineActivityRepository);
        this.lineMapper = lineMapper;
        this.packageService = packageService;
        this.resellerRepository = resellerRepository;
    }

    public Page<LineView> getAllLines(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return new PageImpl<>(findAll(getLoggedInUsername(), search, pageNo, pageSize, sortBy, sortDir).stream().map(lineMapper::convertToView).collect(Collectors.toList()));
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findLineByOwnerUsernameAndIdOrFail(getLoggedInUsername(), id));
    }

    public LineView createLine(LineCreateView createView) {
        Line line = lineMapper.convertToEntity(createView);
        line.setOwner(resellerRepository.findByUsername(getLoggedInUsername()).orElseThrow());
        return lineMapper.convertToView(insert(line));
    }


    public LineView updateLine(Long id, LineCreateView createView) {
        return lineMapper.convertToView(updateOrFail(id, lineMapper.convertToEntity(createView)));
    }

    public LineView extendLine(Long id, Long packageId) {
        Package pack = packageService.findByIdOrFail(packageId);
        Line line = findByIdOrFail(id);
        line.setExpireDate(line.getExpireDate().plus(pack.getDuration()));
        line.setMaxConnections(pack.getMaxConnections());

        var owner = resellerRepository.findByUsername(getLoggedInUsername()).orElseThrow(() -> new AccessDeniedException("User Does not Exist"));
        var currentCredits = owner.getCredits();
        var packageCredits = pack.getCredits();
        if (currentCredits >= packageCredits) {
            owner.setCredits(currentCredits - packageCredits);
            return lineMapper.convertToView(repository.save(line));
        }
        throw new ActionNotAllowedException("LOW_CREDIT", "User Credit is Low");
    }

    @Override
    @Transactional
    public void deleteOrFail(Long id) {
        var owner = getLoggedInUsername();
        if (!repository.existsByOwnerUsernameAndId(owner, id))
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
        repository.deleteLineByOwnerUsernameAndId(owner, id);
    }

    public void updateLineBlock(Long id, boolean blocked) {
        Line line = findLineByOwnerUsernameAndIdOrFail(getLoggedInUsername(), id);
        line.setBlocked(blocked);
        killAllConnections(id);
        repository.save(line);
    }

    public void updateLineBan(Long id, boolean banned) {
        Line line = findLineByOwnerUsernameAndIdOrFail(getLoggedInUsername(), id);
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

    private String getLoggedInUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            var principal = auth.getPrincipal();
            if (principal != null) {
                return ((User) principal).getUsername();
            }
        }
        throw new AccessDeniedException("access denied");
    }


}
