package com.xtra.api.service.reseller;

import com.xtra.api.mapper.reseller.ResellerLineMapper;
import com.xtra.api.model.exception.ActionNotAllowedException;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.line.Line;
import com.xtra.api.model.line.Package;
import com.xtra.api.model.user.*;
import com.xtra.api.projection.reseller.line.LineCreateView;
import com.xtra.api.projection.reseller.line.LineUpdateView;
import com.xtra.api.projection.reseller.line.LineView;
import com.xtra.api.repository.*;
import com.xtra.api.service.CreditLogService;
import com.xtra.api.service.LineService;
import com.xtra.api.service.admin.LogService;
import com.xtra.api.service.admin.PackageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static com.xtra.api.model.exception.ErrorCode.RESELLER_CREDIT_LOW;
import static com.xtra.api.service.system.UserAuthService.getCurrentReseller;

@Service
@PreAuthorize("hasRole('RESELLER')")
public class ResellerLineServiceImpl extends LineService {
    private final ResellerLineMapper lineMapper;
    private final PackageService packageService;
    private final ResellerRepository resellerRepository;
    private final CreditLogService creditLogService;
    private final LogService logService;

    @Autowired
    protected ResellerLineServiceImpl(LineRepository repository, ResellerLineMapper lineMapper, ConnectionRepository connectionRepository, PackageService packageService
            , BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository, ResellerRepository resellerRepository, CreditLogService creditLogService, UserRepository userRepository, LogService logService) {
        super(repository, connectionRepository, bCryptPasswordEncoder, roleRepository, userRepository);
        this.lineMapper = lineMapper;
        this.packageService = packageService;
        this.resellerRepository = resellerRepository;
        this.creditLogService = creditLogService;
        this.logService = logService;
    }

    public Page<LineView> getAllLines(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(getCurrentReseller(), search, pageNo, pageSize, sortBy, sortDir).map(lineMapper::convertToView);
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findLineByOwnerAndIdOrFail(getCurrentReseller(), id));
    }

    public LineView createLine(LineCreateView createView) {
        var _package = packageService.findByIdOrFail(createView.getPackageId());
        Line line = lineMapper.convertToEntity(createView);
        var owner = getCurrentReseller();
        var initialCredit = owner.getCredits();
        var packageCredits = _package.getCredits();
        line.setOwner(owner);
        line.setMaxConnections(_package.getMaxConnections());
        line.setExpireDate(LocalDateTime.now().plus(_package.getDuration()));
        if (initialCredit >= packageCredits) {
            owner.setCredits(initialCredit - packageCredits);
            var res = insert(line);
            resellerRepository.save(owner);
            CreditLog creditLog = creditLogService.saveCreditChangeLog(owner, owner, initialCredit, -packageCredits, CreditLogReason.RESELLER_LINE_CREATE_EXTEND, "");
            logService.saveResellerLog(new ResellerLog(owner, line, creditLog, LocalDateTime.now(), ResellerLogAction.NEW_LINE));
            return lineMapper.convertToView(res);
        }
        throw new ActionNotAllowedException("User Credit is Low", RESELLER_CREDIT_LOW);
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
        var initialCredit = owner.getCredits();
        var packageCredits = pack.getCredits();
        if (initialCredit >= packageCredits) {
            owner.setCredits(initialCredit - packageCredits);
            resellerRepository.save(owner);
            CreditLog log = new CreditLog(owner, owner, initialCredit, owner.getCredits(), -1 * packageCredits, LocalDateTime.now()
                    , CreditLogReason.RESELLER_LINE_CREATE_EXTEND, "");
            logService.saveResellerLog(new ResellerLog(owner, line, log, LocalDateTime.now(), ResellerLogAction.EXTEND_LINE));

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

        logService.saveResellerLog(new ResellerLog(owner, findByIdOrFail(id), LocalDateTime.now(), ResellerLogAction.DELETE_LINE));
    }

    public void updateLineBlock(Long id, boolean blocked) {
        Line line = findLineByOwnerAndIdOrFail(getCurrentReseller(), id);
        line.setBlocked(blocked);
        repository.save(line);

        logService.saveResellerLog(new ResellerLog(getCurrentReseller(), line, LocalDateTime.now(), ResellerLogAction.BLOCK_LINE));
    }

    public void updateLineBan(Long id, boolean banned) {
        Line line = findLineByOwnerAndIdOrFail(getCurrentReseller(), id);
        line.setBanned(banned);
        repository.save(line);

        logService.saveResellerLog(new ResellerLog(getCurrentReseller(), line, LocalDateTime.now(), ResellerLogAction.BAN_LINE));
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
