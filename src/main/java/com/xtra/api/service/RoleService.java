package com.xtra.api.service;

import com.xtra.api.model.Role;
import com.xtra.api.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends CrudService<Role, Long, RoleRepository> {

    protected RoleService(RoleRepository repository) {
        super(repository, Role.class);
    }

    @Override
    protected Page<Role> findWithSearch(Pageable page, String search) {
        return null;
    }

}
