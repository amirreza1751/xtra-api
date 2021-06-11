package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.AdminMapper;
import com.xtra.api.model.Admin;
import com.xtra.api.model.UserType;
import com.xtra.api.projection.admin.user.admin.AdminInsertView;
import com.xtra.api.projection.admin.user.admin.AdminView;
import com.xtra.api.repository.AdminRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class AdminService extends CrudService<Admin, Long, AdminRepository> {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AdminMapper adminMapper;

    @Autowired
    protected AdminService(AdminRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder, AdminMapper adminMapper) {
        super(repository, "Admin");
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.adminMapper = adminMapper;
    }

    @Override
    protected Page<Admin> findWithSearch(String search, Pageable page) {
        return repository.findAllByUsernameLike(search, page);
    }

    @Override
    public Admin insert(Admin admin) {
        admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
        return super.insert(admin);
    }

    public AdminView save(Long id, AdminInsertView insertView) {
        var existingAdmin = findByIdOrFail(id);
        List<String> toIgnore = new ArrayList<>();
        toIgnore.add("id");
        if (insertView.getRoleId() == null) toIgnore.add("roleId");
        if (insertView.getPassword() == null) toIgnore.add("password");
        else insertView.setPassword(bCryptPasswordEncoder.encode(insertView.getPassword()));
        var admin = adminMapper.convertToEntity(insertView);
        if (admin.getRole().getType() != UserType.ADMIN)
            throw new RuntimeException("user role not suitable");
        copyProperties(admin, existingAdmin, toIgnore.toArray(new String[0]));
        return adminMapper.convertToView(repository.save(existingAdmin));
    }

    public AdminView add(AdminInsertView adminInsertView) {
        return adminMapper.convertToView(insert(adminMapper.convertToEntity(adminInsertView)));
    }
}
