package com.xtra.api.service.reseller;

import com.xtra.api.mapper.reseller.ResellerLineMapper;
import com.xtra.api.model.exception.ActionNotAllowedException;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.line.Line;
import com.xtra.api.model.line.Package;
import com.xtra.api.model.user.Reseller;
import com.xtra.api.projection.reseller.line.LineCreateView;
import com.xtra.api.projection.reseller.line.LineUpdateView;
import com.xtra.api.projection.reseller.line.LineView;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.RoleRepository;
import com.xtra.api.service.LineService;
import com.xtra.api.service.admin.PackageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.xtra.api.model.exception.ErrorCode.RESELLER_CREDIT_LOW;
import static com.xtra.api.service.system.UserAuthService.getCurrentReseller;

@Service
@PreAuthorize("hasRole('RESELLER')")
public class ResellerLineServiceImpl extends LineService {
    private final ResellerLineMapper lineMapper;
    private final PackageService packageService;

    @Autowired
    protected ResellerLineServiceImpl(LineRepository repository, ResellerLineMapper lineMapper, ConnectionRepository connectionRepository, PackageService packageService
            , BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        super(repository, connectionRepository, bCryptPasswordEncoder, roleRepository);
        this.lineMapper = lineMapper;
        this.packageService = packageService;
    }

    public Page<LineView> getAllLines(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(getCurrentReseller(), search, pageNo, pageSize, sortBy, sortDir).map(lineMapper::convertToView);
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findLineByOwnerAndIdOrFail(getCurrentReseller(), id));
    }

    public LineView createLine(LineCreateView createView) {
        Line line = lineMapper.convertToEntity(createView);
        line.setOwner(getCurrentReseller());
        return lineMapper.convertToView(insert(line));
    }


    public LineView updateLine(Long id, LineUpdateView updateView) {
        var line = lineMapper.convertToEntity(updateView, findByIdOrFail(id));
        return lineMapper.convertToView(repository.save(line));
    }

    public LineView extendLine(Long id, Long packageId) {
        Package pack = packageService.findByIdOrFail(packageId);
        Line line = findByIdOrFail(id);
        line.setExpireDate(line.getExpireDate().plus(pack.getDuration()));
        line.setMaxConnections(pack.getMaxConnections());

        var owner = getCurrentReseller();
        var currentCredits = owner.getCredits();
        var packageCredits = pack.getCredits();
        if (currentCredits >= packageCredits) {
            owner.setCredits(currentCredits - packageCredits);
            return lineMapper.convertToView(repository.save(line));
        }
        throw new ActionNotAllowedException("User Credit is Low", RESELLER_CREDIT_LOW);
    }

    @Override
    @Transactional
    public void deleteOrFail(Long id) {
        var owner = getCurrentReseller();
        if (!repository.existsByOwnerAndId(owner, id))
            entityNotFoundException("id", id);
        repository.deleteLineByOwnerAndId(owner, id);
    }

    public void updateLineBlock(Long id, boolean blocked) {
        Line line = findLineByOwnerAndIdOrFail(getCurrentReseller(), id);
        line.setBlocked(blocked);
        repository.save(line);
    }

    public void updateLineBan(Long id, boolean banned) {
        Line line = findLineByOwnerAndIdOrFail(getCurrentReseller(), id);
        line.setBanned(banned);
        repository.save(line);
    }

    private Line findLineByOwnerAndIdOrFail(Reseller owner, Long id) {
        var result = repository.findByOwnerAndId(owner, id);
        return result.orElseThrow(() -> new EntityNotFoundException(entityName, id));
    }

    private Page<Line> findAll(Reseller owner, String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);
        if (StringUtils.isEmpty(search))
            return repository.findAllByOwner(owner, page);
        else
            return findWithSearch(search, page);
    }

    public ResponseEntity<String> downloadLinePlaylist(Long lineId) {
        return downloadLinePlaylist(findLineByOwnerAndIdOrFail(getCurrentReseller(), lineId));
    }
}
