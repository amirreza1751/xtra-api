package com.xtra.api.service;

import com.xtra.api.model.PermissionRoleId;
import com.xtra.api.model.Role;
import com.xtra.api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends CrudService<Role, Long, RoleRepository> {

    private final PermissionRoleService permissionRoleService;

    @Autowired
    protected RoleService(RoleRepository repository, PermissionRoleService permissionRoleService) {
        super(repository, Role.class);
        this.permissionRoleService = permissionRoleService;
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
        var assignments = permissionRoleService.findAllByRole(findByIdOrFail(id));
        permissionRoleService.deleteAll(assignments);
        super.deleteOrFail(id);
    }
}
