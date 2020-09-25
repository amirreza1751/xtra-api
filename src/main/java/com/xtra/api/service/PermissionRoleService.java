package com.xtra.api.service;

import com.xtra.api.model.PermissionRole;
import com.xtra.api.model.PermissionRoleId;
import com.xtra.api.model.Role;
import com.xtra.api.repository.PermissionRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionRoleService extends CrudService<PermissionRole, PermissionRoleId, PermissionRoleRepository> {

    @Autowired
    protected PermissionRoleService(PermissionRoleRepository repository) {
        super(repository, PermissionRole.class);
    }

    @Override
    protected Page<PermissionRole> findWithSearch(Pageable page, String search) {
        return null;
    }

    public List<PermissionRole> findAllByRole(Role role) {
        return repository.findAllByRole(role);
    }

    public void deleteAll(List<PermissionRole> permissionRoles) {
        repository.deleteAll(permissionRoles);
    }
}
