package com.xtra.api.service.admin;

import com.google.common.collect.Sets;
import com.xtra.api.mapper.admin.RoleMapper;
import com.xtra.api.model.Role;
import com.xtra.api.model.UserType;
import com.xtra.api.projection.admin.role.RoleInsertView;
import com.xtra.api.projection.admin.role.RoleListItem;
import com.xtra.api.projection.admin.role.RoleView;
import com.xtra.api.repository.PermissionRepository;
import com.xtra.api.repository.RoleRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class RoleService extends CrudService<Role, Long, RoleRepository> {

    private final RoleMapper roleMapper;

    @Override
    protected Page<Role> findWithSearch(String search, Pageable page) {
        return null;
    }

    @Autowired
    protected RoleService(RoleRepository repository, RoleMapper roleMapper) {
        super(repository, "Role");
        this.roleMapper = roleMapper;
    }

    public List<RoleListItem> getRoleList(UserType type) {
        if (type == null) {
            return repository.findAll().stream().map(roleMapper::toListItem).collect(Collectors.toList());
        }
        return repository.findAllByType(type).stream().map(roleMapper::toListItem).collect(Collectors.toList());
    }

    public RoleView add(RoleInsertView roleView) {
        return roleMapper.convertToDto(repository.save(roleMapper.convertToEntity(roleView)));
    }


    public RoleView updateOrFail(Long id, RoleInsertView view) {
        Role newRole = roleMapper.convertToEntity(view);
        var oldRole = findByIdOrFail(id);
        copyProperties(newRole, oldRole, "id", "permissions");
        var obsoletePermissions = Sets.difference(oldRole.getPermissions(), newRole.getPermissions()).immutableCopy();
        oldRole.getPermissions().removeAll(obsoletePermissions);
        for (var permission : newRole.getPermissions()) {
            if (!oldRole.getPermissions().contains(permission)) {
                oldRole.addPermission(permission);
            }
        }
        return roleMapper.convertToDto(repository.save(oldRole));
    }

    @Override
    public void deleteOrFail(Long id) {
        Role role = findByIdOrFail(id);
        repository.delete(role);
    }


    public Page<RoleView> getRoles(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(roleMapper::convertToDto);
    }


    public RoleView findById(Long id) {
        return roleMapper.convertToDto(findByIdOrFail(id));
    }
}
