package com.xtra.api.service.admin;

import com.google.common.collect.Sets;
import com.xtra.api.model.Role;
import com.xtra.api.repository.PermissionRepository;
import com.xtra.api.repository.RoleRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class RoleService extends CrudService<Role, Long, RoleRepository> {

    @Autowired
    protected RoleService(RoleRepository repository, PermissionRepository permissionRepository) {
        super(repository, "Role");
    }

    @Override
    protected Page<Role> findWithSearch(Pageable page, String search) {
        return null;
    }

    public Role updateOrFail(Long id, Role newRole) {
        newRole.setId(id);
        var oldRole = findByIdOrFail(id);
        copyProperties(newRole, oldRole, "id", "permissions");
        var obsoletePermissions = Sets.difference(oldRole.getPermissions(), newRole.getPermissions()).immutableCopy();
        oldRole.getPermissions().removeAll(obsoletePermissions);
        for (var permission : newRole.getPermissions()) {
            if(!oldRole.getPermissions().contains(permission)){
                oldRole.addPermission(permission);
            }
        }
        return repository.save(oldRole);
    }

    @Override
    public void deleteOrFail(Long id) {
        Role role = findByIdOrFail(id);
        repository.delete(role);
    }
}
