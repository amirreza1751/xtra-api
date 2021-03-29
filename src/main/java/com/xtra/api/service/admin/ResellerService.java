package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.ResellerMapper;
import com.xtra.api.model.Reseller;
import com.xtra.api.projection.admin.user.reseller.ResellerCreditChangeView;
import com.xtra.api.projection.admin.user.reseller.ResellerInsertView;
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

    @Autowired
    protected ResellerService(ResellerRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder, ResellerMapper resellerMapper) {
        super(repository, "Reseller");
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.resellerMapper = resellerMapper;
    }

    @Override
    protected Page<Reseller> findWithSearch(String search, Pageable page) {
        search = wrapSearchString(search);
        return repository.findAllByUsernameLike(search, page);
    }

    @Override
    public Reseller insert(Reseller reseller) {
        reseller.setPassword(bCryptPasswordEncoder.encode(reseller.getPassword()));
        return super.insert(reseller);
    }


    public ResellerView save(Long id, ResellerInsertView insertView) {
        var existingReseller = findByIdOrFail(id);
        List<String> toIgnore = new ArrayList<>();
        toIgnore.add("id");
        if (insertView.getRole() == null) toIgnore.add("roleId");
        if (insertView.getPassword() == null) toIgnore.add("password");
        else insertView.setPassword(bCryptPasswordEncoder.encode(insertView.getPassword()));
        copyProperties(resellerMapper.convertToEntity(insertView), existingReseller, toIgnore.toArray(new String[0]));
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
}
