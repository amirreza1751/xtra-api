package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.ResellerMapper;
import com.xtra.api.model.exception.ActionNotAllowedException;
import com.xtra.api.model.role.Role;
import com.xtra.api.model.user.CreditLogReason;
import com.xtra.api.model.user.Reseller;
import com.xtra.api.model.user.UserType;
import com.xtra.api.projection.admin.user.UserSimpleView;
import com.xtra.api.projection.admin.user.reseller.*;
import com.xtra.api.projection.reseller.ResellerProfile;
import com.xtra.api.repository.CreditLogRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.ResellerRepository;
import com.xtra.api.repository.RoleRepository;
import com.xtra.api.service.CreditLogService;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.xtra.api.model.exception.ErrorCode.CREDIT_VALUE_INVALID;
import static com.xtra.api.service.system.UserAuthService.getCurrentUser;
import static com.xtra.api.util.Utilities.wrapSearchString;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class ResellerService extends CrudService<Reseller, Long, ResellerRepository> {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResellerMapper resellerMapper;
    private final RoleRepository roleRepository;
    private final LineRepository lineRepository;
    private final CreditLogService creditLogService;

    @Autowired
    protected ResellerService(ResellerRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder, ResellerMapper resellerMapper, RoleRepository roleRepository, LineRepository lineRepository, CreditLogRepository creditChangeRepository, CreditLogService creditLogService) {
        super(repository, "Reseller");
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.resellerMapper = resellerMapper;
        this.roleRepository = roleRepository;
        this.lineRepository = lineRepository;
        this.creditLogService = creditLogService;
    }

    public Page<UserSimpleView> getSimpleList(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return super.findAll(search, pageNo, pageSize, sortBy, sortDir).map(resellerMapper::convertToSimpleView);
    }

    public Page<ResellerListView> getList(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return super.findAll(search, pageNo, pageSize, sortBy, sortDir).map(resellerMapper::convertToListView);
    }

    public ResellerView getReseller(Long id) {
        return resellerMapper.convertToView(findByIdOrFail(id));
    }

    @Override
    protected Page<Reseller> findWithSearch(String search, Pageable page) {
        search = wrapSearchString(search);
        return repository.findAllByUsernameLike(search, page);
    }

    @Override
    public Reseller insert(Reseller reseller) {
        if (reseller.getRole().getType() != UserType.RESELLER)
            throw new RuntimeException("user role not suitable");
        reseller.setPassword(bCryptPasswordEncoder.encode(reseller.getPassword()));
        return super.insert(reseller);
    }


    public ResellerView save(Long id, ResellerInsertView insertView) {
        var existingReseller = findByIdOrFail(id);
        List<String> toIgnore = new ArrayList<>();
        toIgnore.add("id");
        toIgnore.add("username");
        if (insertView.getRoleId() == null) toIgnore.add("roleId");
        if (insertView.getPassword() == null) toIgnore.add("password");
        else insertView.setPassword(bCryptPasswordEncoder.encode(insertView.getPassword()));
        var reseller = resellerMapper.convertToEntity(insertView);
        if (reseller.getRole().getType() != UserType.RESELLER)
            throw new RuntimeException("user role not suitable");
        copyProperties(reseller, existingReseller, toIgnore.toArray(new String[0]));
        return resellerMapper.convertToView(repository.save(existingReseller));
    }

    public void saveAll(ResellerBatchView resellerBatchView) {
        Set<Long> ids = resellerBatchView.getIds();

        Reseller owner = null;
        if (resellerBatchView.getOwnerId() != null)
            owner = findByIdOrFail(resellerBatchView.getOwnerId());
        Role role = null;
        if (resellerBatchView.getRoleId() != null)
            role = roleRepository.findById(resellerBatchView.getRoleId()).orElseThrow(() -> new RuntimeException("role not found"));

        for(Long id : ids) {
            Reseller reseller = findByIdOrFail(id);
            if(resellerBatchView.getCredits() != null)
                reseller.setCredits(Integer.parseInt(resellerBatchView.getCredits()));
            if (resellerBatchView.getResellerDns() != null)
                reseller.setResellerDns(resellerBatchView.getResellerDns());
            if (resellerBatchView.getNotes() != null)
                reseller.setNotes(resellerBatchView.getNotes());
            if(resellerBatchView.getLang() != null)
                reseller.setLang(resellerBatchView.getLang());
            if (owner != null) {
                reseller.setOwner(owner);
            }
            if (resellerBatchView.getIsBanned() != null)
                reseller.setBanned(Boolean.parseBoolean(resellerBatchView.getIsBanned()));

            if (role != null)
                reseller.setRole(role);

            repository.save(reseller);
        }
    }

    public ResellerView add(ResellerInsertView resellerInsertView) {
        return resellerMapper.convertToView(insert(resellerMapper.convertToEntity(resellerInsertView)));
    }

    public void updateCredits(Long id, ResellerCreditChangeView creditChange) {
        if (creditChange.getCredits() < 0) {
            throw new ActionNotAllowedException("credit change must be a positive amount", CREDIT_VALUE_INVALID);
        }
        var existingReseller = findByIdOrFail(id);
        var initialCredits = existingReseller.getCredits();
        var finalCredits = creditChange.getCredits();
        var changeAmount = finalCredits - initialCredits;
        existingReseller.setCredits(finalCredits);
        creditLogService.saveCreditChangeLog(getCurrentUser(), existingReseller, initialCredits, changeAmount, CreditLogReason.ADMIN_MANUAL_CHANGE, creditChange.getDescription());
        repository.save(existingReseller);
    }

    public void signUp(ResellerSignUpView resellerSignUpView) {
        var reseller = resellerMapper.convertToEntity(resellerSignUpView);
        var defaultRole = roleRepository.findByTypeAndName(UserType.RESELLER, "default").orElseThrow(() -> new RuntimeException("default role not found"));
        reseller.setRole(defaultRole);
        insert(reseller);
    }

    public ResellerProfile getResellerProfile() {
        return resellerMapper.convertToProfile((Reseller) getCurrentUser());
    }

    public void deleteReseller(Long resellerId, Long newOwnerId) {
        var reseller = findByIdOrFail(resellerId);
        var substituteReseller = findByIdOrFail(newOwnerId);
        var lines = reseller.getLines();
        for (var line : lines) {
            line.setOwner(substituteReseller);
        }
        lineRepository.saveAll(lines);
        super.deleteOrFail(resellerId);
    }

    public void deleteAll(ResellerBatchDeleteView deleteView) {
        for (Long id : deleteView.getIds()) {
            var reseller = findByIdOrFail(id);
            var substituteReseller = findByIdOrFail(deleteView.getNewOwnerId());
            var lines = reseller.getLines();
            for (var line : lines) {
                line.setOwner(substituteReseller);
            }
            lineRepository.saveAll(lines);
            super.deleteOrFail(id);
        }
    }
}
