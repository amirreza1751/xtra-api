package com.xtra.api.service;

import com.xtra.api.model.Role;
import com.xtra.api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends CrudService<Role, Long, RoleRepository> {

    @Autowired
    protected RoleService(RoleRepository repository) {
        super(repository, Role.class);
    }

    @Override
    protected Page<Role> findWithSearch(Pageable page, String search) {
        return null;
    }

    public Role updateOrFail(Long id, Role role) {
        role.setId(id);
        return super.updateOrFail(id, role);
    }

}
