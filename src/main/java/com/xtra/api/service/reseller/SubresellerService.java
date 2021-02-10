package com.xtra.api.service.reseller;

import com.xtra.api.exceptions.ActionNotAllowedException;
import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.mapper.admin.ResellerMapper;
import com.xtra.api.model.Reseller;
import com.xtra.api.projection.admin.user.reseller.ResellerInsertView;
import com.xtra.api.projection.admin.user.reseller.ResellerView;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.ResellerRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class SubresellerService extends CrudService<Reseller, Long, ResellerRepository> {
    private final ResellerMapper resellerMapper;
    private final LineRepository lineRepository;

    protected SubresellerService(ResellerRepository repository, ResellerMapper resellerMapper, LineRepository lineRepository) {
        super(repository, Reseller.class);
        this.resellerMapper = resellerMapper;
        this.lineRepository = lineRepository;
    }

    @Override
    protected Page<Reseller> findWithSearch(Pageable page, String search) {
        return null;
    }

    public Page<ResellerView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);
        return new PageImpl<>(repository.findAllByOwner(getCurrentReseller(), page).stream().map(resellerMapper::convertToView).collect(Collectors.toList()));

    }

    public ResellerView getReseller(Long id) {
        var subreseller = repository.findByIdAndOwnerUsername(id, getCurrentReseller().getUsername());
        return resellerMapper.convertToView(subreseller.orElseThrow(() -> new EntityNotFoundException("Reseller", id.toString())));
    }

    public ResellerView addSubreseller(ResellerInsertView insertView) {
        var subreseller = resellerMapper.convertToEntity(insertView);
        var currentReseller = getCurrentReseller();
        if (currentReseller.getCredits() < subreseller.getCredits())
            throw new ActionNotAllowedException("Not Enough Credits", "LOW_CREDITS");
        subreseller.setOwner(currentReseller);

        var currentCredits = currentReseller.getCredits();
        currentReseller.setCredits(currentCredits - insertView.getCredits());
        var res = repository.save(subreseller);
        repository.save(currentReseller);
        return resellerMapper.convertToView(res);
    }

    public ResellerView updateSubreseller(Long id, ResellerInsertView insertView) {
        var subreseller = resellerMapper.convertToEntity(insertView);
        var currentReseller = getCurrentReseller();
        var oldSubreseller = repository.findByIdAndOwnerUsername(id, currentReseller.getUsername()).orElseThrow(() -> new EntityNotFoundException("Reseller", id.toString()));
        copyProperties(subreseller, oldSubreseller, ResellerInsertView.class);
        return resellerMapper.convertToView(repository.save(oldSubreseller));
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

    private Reseller getCurrentReseller() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            var principal = auth.getPrincipal();
            if (principal != null && !(principal instanceof String)) {
                return repository.findByUsername(((User) principal).getUsername()).orElseThrow(() -> new AccessDeniedException("access denied"));
            }
        }
        throw new AccessDeniedException("access denied");
    }

}
