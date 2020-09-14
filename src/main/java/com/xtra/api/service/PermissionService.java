package com.xtra.api.service;

import com.xtra.api.model.Permission;
import com.xtra.api.repository.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PermissionService extends CrudService<Permission,Long, PermissionRepository> {

    protected PermissionService(PermissionRepository repository) {
        super(repository, Permission.class);
    }

    @Override
    protected Page<Permission> findWithSearch(Pageable page, String search) {
        return null;
    }
}
