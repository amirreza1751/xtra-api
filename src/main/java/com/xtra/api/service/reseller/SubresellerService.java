package com.xtra.api.service.reseller;

import com.xtra.api.mapper.admin.ResellerMapper;
import com.xtra.api.model.exception.ActionNotAllowedException;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.user.*;
import com.xtra.api.projection.reseller.subreseller.CreditChangeRequest;
import com.xtra.api.projection.reseller.subreseller.SubresellerCreateView;
import com.xtra.api.projection.reseller.subreseller.SubresellerSimplified;
import com.xtra.api.projection.reseller.subreseller.SubresellerView;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.ResellerRepository;
import com.xtra.api.service.CreditLogService;
import com.xtra.api.service.CrudService;
import com.xtra.api.service.admin.LogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.xtra.api.model.exception.ErrorCode.CREDIT_VALUE_INVALID;
import static com.xtra.api.model.exception.ErrorCode.RESELLER_CREDIT_LOW;
import static com.xtra.api.service.system.UserAuthService.getCurrentUser;

@Service
@Validated
public class SubresellerService extends CrudService<Reseller, Long, ResellerRepository> {
    private final ResellerMapper resellerMapper;
    private final LineRepository lineRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CreditLogService creditLogService;
    private final LogService logService;


    protected SubresellerService(ResellerRepository repository, ResellerMapper resellerMapper, LineRepository lineRepository, BCryptPasswordEncoder bCryptPasswordEncoder, CreditLogService creditLogService, LogService logService) {
        super(repository, "Reseller");
        this.resellerMapper = resellerMapper;
        this.lineRepository = lineRepository;
        this.creditLogService = creditLogService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.logService = logService;
    }

    @Override
    protected Page<Reseller> findWithSearch(String search, Pageable page) {
        return repository.findAllByUsernameContains(search, page);
    }

    public Page<SubresellerSimplified> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);
        return repository.findAllByOwner(getCurrentReseller(), page).map(resellerMapper::convertToSimplifiedSubreseller);
    }

    public SubresellerView getReseller(Long id) {
        var subreseller = repository.findByIdAndOwnerUsername(id, getCurrentUser().getUsername());
        return resellerMapper.convertToSubresellerView(subreseller.orElseThrow(() -> new EntityNotFoundException("Reseller", id.toString())));
    }

    public SubresellerView addSubreseller(SubresellerCreateView view) {
        var subreseller = resellerMapper.convertToEntity(view);
        var currentReseller = getCurrentReseller();
        if (currentReseller.getCredits() < subreseller.getCredits())
            throw new ActionNotAllowedException("Not Enough Credits", RESELLER_CREDIT_LOW);
        subreseller.setOwner(currentReseller);
        subreseller.setPassword(bCryptPasswordEncoder.encode(subreseller.getPassword()));
        subreseller.setRole(currentReseller.getRole());
        var creditChange = view.getCredits();
        var actorInitialCredits = currentReseller.getCredits();
        currentReseller.setCredits(actorInitialCredits - creditChange);
        var res = repository.save(subreseller);
        repository.save(currentReseller);

        CreditLog log = creditLogService.saveCreditChangeLog(getCurrentUser(), subreseller, 0, creditChange, CreditLogReason.RESELLER_SUBRESELLER_CREATION, "");
        creditLogService.saveCreditChangeLog(getCurrentUser(), getCurrentReseller(), actorInitialCredits, -creditChange, CreditLogReason.RESELLER_SUBRESELLER_CREATION, "");
        logService.saveResellerLog(new ResellerLog(currentReseller, subreseller, log, LocalDateTime.now(), ResellerLogAction.NEW_SUBRESELLER));
        return resellerMapper.convertToSubresellerView(res);
    }

    public SubresellerView updateSubreseller(Long id, SubresellerCreateView view) {
        var subreseller = resellerMapper.convertToEntity(view);
        var currentReseller = getCurrentUser();
        var oldSubreseller = repository.findByIdAndOwnerUsername(id, currentReseller.getUsername()).orElseThrow(() -> new EntityNotFoundException("Reseller", id.toString()));
        oldSubreseller.setEmail(subreseller.getEmail());
        oldSubreseller.setPassword(bCryptPasswordEncoder.encode(subreseller.getPassword()));
        oldSubreseller.setResellerDns(subreseller.getResellerDns());
        return resellerMapper.convertToSubresellerView(repository.save(oldSubreseller));
    }

    public void changeCredits(Long id, CreditChangeRequest creditChangeRequest) {
        var targetReseller = findByIdOrFail(id);
        var currentReseller = getCurrentReseller();
        var actorInitialCredits = currentReseller.getCredits();
        var targetInitialCredits = targetReseller.getCredits();
        var creditChange = creditChangeRequest.getCredits() - targetInitialCredits;

        if (creditChange <= 0)
            throw new ActionNotAllowedException("credit change must be a positive amount", CREDIT_VALUE_INVALID);
        if (creditChange > currentReseller.getCredits())
            throw new ActionNotAllowedException("credit amount is less than current credit balance", RESELLER_CREDIT_LOW);
        targetReseller.setCredits(targetReseller.getCredits() + creditChange);
        currentReseller.setCredits(currentReseller.getCredits() - creditChange);
        repository.saveAll(Arrays.asList(targetReseller, currentReseller));

        CreditLog log = creditLogService.saveCreditChangeLog(getCurrentUser(), targetReseller, targetInitialCredits, creditChange, CreditLogReason.RESELLER_CREDIT_TRANSFER, creditChangeRequest.getDescription());
        creditLogService.saveCreditChangeLog(getCurrentUser(), getCurrentReseller(), actorInitialCredits, -creditChange, CreditLogReason.RESELLER_CREDIT_TRANSFER, creditChangeRequest.getDescription());

        logService.saveResellerLog(new ResellerLog(currentReseller, targetReseller, log, LocalDateTime.now(), ResellerLogAction.CREDIT_CHANGE_SUBRESELLER));
    }


    public void deleteSubreseller(Long id) {
        var currentReseller = getCurrentReseller();
        if (repository.existsByIdAndOwner(id, currentReseller)) {
            repository.deleteByIdAndOwner(id, currentReseller);
        } else
            throw new EntityNotFoundException("Reseller", id.toString());

        logService.saveResellerLog(new ResellerLog(currentReseller, findByIdOrFail(id), LocalDateTime.now(), ResellerLogAction.DELETE_SUBRESELLER));
    }

    public void enableResellerLines(Long subresellerId) {
        var subreseller = repository.findByIdAndOwnerUsername(subresellerId, getCurrentUser().getUsername());
        var lines = subreseller.orElseThrow(() -> new EntityNotFoundException("Reseller", subresellerId.toString())).getLines();
        for (var line : lines) {
            line.setBlocked(false);
        }
        lineRepository.saveAll(lines);
    }

    public void disableResellerLines(Long subresellerId) {
        var subreseller = repository.findByIdAndOwnerUsername(subresellerId, getCurrentUser().getUsername());
        var lines = subreseller.orElseThrow(() -> new EntityNotFoundException("Reseller", subresellerId.toString())).getLines();
        for (var line : lines) {
            line.setBlocked(true);
        }
        lineRepository.saveAll(lines);
    }

    protected Reseller getCurrentReseller() {
        return (Reseller) getCurrentUser();
    }
}
