package com.xtra.api.service.reseller;

import com.xtra.api.exceptions.ActionNotAllowedException;
import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.mapper.admin.ResellerMapper;
import com.xtra.api.mapper.reseller.CreditChangeMapper;
import com.xtra.api.model.Reseller;
import com.xtra.api.projection.reseller.subreseller.CreditChangeRequest;
import com.xtra.api.projection.reseller.subreseller.SubresellerCreateView;
import com.xtra.api.projection.reseller.subreseller.SubresellerSimplified;
import com.xtra.api.projection.reseller.subreseller.SubresellerView;
import com.xtra.api.repository.CreditChangeLogRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.ResellerRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.xtra.api.service.system.SystemResellerService.getCurrentReseller;

@Service
public class SubresellerService extends CrudService<Reseller, Long, ResellerRepository> {
    private final ResellerMapper resellerMapper;
    private final LineRepository lineRepository;
    private final CreditChangeMapper creditChangeMapper;
    private final CreditChangeLogRepository creditChangeRepository;

    protected SubresellerService(ResellerRepository repository, ResellerMapper resellerMapper, LineRepository lineRepository, CreditChangeMapper creditChangeMapper, CreditChangeLogRepository creditChangeRepository) {
        super(repository, Reseller.class);
        this.resellerMapper = resellerMapper;
        this.lineRepository = lineRepository;
        this.creditChangeMapper = creditChangeMapper;
        this.creditChangeRepository = creditChangeRepository;
    }

    @Override
    protected Page<Reseller> findWithSearch(Pageable page, String search) {
        return null;
    }

    public Page<SubresellerSimplified> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);
        return repository.findAllByOwner(getCurrentReseller(), page).map(resellerMapper::convertToSimplifiedSubreseller);
    }

    public SubresellerView getReseller(Long id) {
        var subreseller = repository.findByIdAndOwnerUsername(id, getCurrentReseller().getUsername());
        return resellerMapper.convertToSubresellerView(subreseller.orElseThrow(() -> new EntityNotFoundException("Reseller", id.toString())));
    }

    public SubresellerView addSubreseller(SubresellerCreateView view) {
        var subreseller = resellerMapper.convertToEntity(view);
        var currentReseller = getCurrentReseller();
        if (currentReseller.getCredits() < subreseller.getCredits())
            throw new ActionNotAllowedException("Not Enough Credits", "LOW_CREDITS");
        subreseller.setOwner(currentReseller);

        var currentCredits = currentReseller.getCredits();
        currentReseller.setCredits(currentCredits - view.getCredits());
        var res = repository.save(subreseller);
        repository.save(currentReseller);
        return resellerMapper.convertToSubresellerView(res);
    }

    public SubresellerView updateSubreseller(Long id, SubresellerCreateView view) {
        var subreseller = resellerMapper.convertToEntity(view);
        var currentReseller = getCurrentReseller();
        var oldSubreseller = repository.findByIdAndOwnerUsername(id, currentReseller.getUsername()).orElseThrow(() -> new EntityNotFoundException("Reseller", id.toString()));
        oldSubreseller.setEmail(subreseller.getEmail());
        oldSubreseller.setPassword(subreseller.getPassword());
        oldSubreseller.setResellerDns(subreseller.getResellerDns());
        return resellerMapper.convertToSubresellerView(repository.save(oldSubreseller));
    }

    public void changeCredits(Long id, CreditChangeRequest creditChangeRequest) {
        var target = findByIdOrFail(id);
        var credChange = creditChangeMapper.toEntity(creditChangeRequest);
        target.setCredits(target.getCredits() + credChange.getChangeAmount());
        repository.save(target);

        credChange.setTarget(target);
        credChange.setActor(getCurrentReseller());
        credChange.setDate(LocalDateTime.now());
        creditChangeRepository.save(credChange);
    }


    public void deleteSubreseller(Long id) {
        var currentReseller = getCurrentReseller();
        if (repository.existsByIdAndOwner(id, currentReseller)) {
            repository.deleteByIdAndOwner(id, currentReseller);
        } else
            throw new EntityNotFoundException("Reseller", id.toString());
    }

    public void enableResellerLines(Long subresellerId) {
        var subreseller = repository.findByIdAndOwnerUsername(subresellerId, getCurrentReseller().getUsername());
        var lines = subreseller.orElseThrow(() -> new EntityNotFoundException("Reseller", subresellerId.toString())).getLines();
        for (var line : lines) {
            line.setBlocked(false);
        }
        lineRepository.saveAll(lines);
    }

    public void disableResellerLines(Long subresellerId) {
        var subreseller = repository.findByIdAndOwnerUsername(subresellerId, getCurrentReseller().getUsername());
        var lines = subreseller.orElseThrow(() -> new EntityNotFoundException("Reseller", subresellerId.toString())).getLines();
        for (var line : lines) {
            line.setBlocked(true);
        }
        lineRepository.saveAll(lines);
    }


}
