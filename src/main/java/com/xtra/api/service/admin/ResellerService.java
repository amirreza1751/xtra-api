package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.ResellerMapper;
import com.xtra.api.model.Reseller;
import com.xtra.api.model.UserType;
import com.xtra.api.projection.admin.user.UserSimpleView;
import com.xtra.api.projection.admin.user.reseller.ResellerCreditChangeView;
import com.xtra.api.projection.admin.user.reseller.ResellerInsertView;
import com.xtra.api.projection.admin.user.reseller.ResellerListView;
import com.xtra.api.projection.admin.user.reseller.ResellerSignUpView;
import com.xtra.api.projection.admin.user.reseller.ResellerView;
import com.xtra.api.repository.ResellerRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.xtra.api.util.Utilities.wrapSearchString;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class ResellerService extends CrudService<Reseller, Long, ResellerRepository> {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResellerMapper resellerMapper;
    private final RoleService roleService;

    @Autowired
    protected ResellerService(ResellerRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder, ResellerMapper resellerMapper, RoleService roleService) {
        super(repository, "Reseller");
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.resellerMapper = resellerMapper;
        this.roleService = roleService;
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
        if (insertView.getRoleId() == null) toIgnore.add("roleId");
        if (insertView.getPassword() == null) toIgnore.add("password");
        else insertView.setPassword(bCryptPasswordEncoder.encode(insertView.getPassword()));
        var reseller = resellerMapper.convertToEntity(insertView);
        if (reseller.getRole().getType() != UserType.RESELLER)
            throw new RuntimeException("user role not suitable");
        copyProperties(reseller, existingReseller, toIgnore.toArray(new String[0]));
        return resellerMapper.convertToView(repository.save(existingReseller));
    }

    public ResellerView add(ResellerInsertView resellerInsertView) {
        return resellerMapper.convertToView(insert(resellerMapper.convertToEntity(resellerInsertView)));
    }

    public void updateCredits(Long id, ResellerCreditChangeView credits) {
        if (credits.getCredits() < 0)
            //@todo throw bad request exception
            return;
        var existingReseller = findByIdOrFail(id);
        existingReseller.setCredits(credits.getCredits());
        //@todo log the credit change with reason
        repository.save(existingReseller);
    }

    public void signUp(ResellerSignUpView resellerSignUpView){
        Reseller reseller = resellerMapper.convertToEntity(resellerSignUpView);
        reseller.setRole(roleService.getRoleByNameAndType("default", UserType.RESELLER));
        insert(reseller);
    }
}
