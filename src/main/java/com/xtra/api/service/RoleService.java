package com.xtra.api.service;

import com.xtra.api.model.Role;
import com.xtra.api.repository.PermissionRepository;
import com.xtra.api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends CrudService<Role, Long, RoleRepository> {
    private final PermissionRepository permissionRepository;

    @Autowired
    protected RoleService(RoleRepository repository, PermissionRepository permissionRepository) {
        super(repository, Role.class);
        this.permissionRepository = permissionRepository;
    }

    @Override
    protected Page<Role> findWithSearch(Pageable page, String search) {
        return null;
    }

    public Role updateOrFail(Long id, Role role) {
        role.setId(id);
        return super.updateOrFail(id, role);
    }

    @Override
    public void deleteOrFail(Long id) {
        Role role = findByIdOrFail(id);
        repository.delete(role);
    }
}
