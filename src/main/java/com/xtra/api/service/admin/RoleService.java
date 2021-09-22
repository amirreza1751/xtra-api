package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.RoleMapper;
import com.xtra.api.model.role.Role;
import com.xtra.api.model.user.UserType;
import com.xtra.api.projection.admin.role.RoleInsertView;
import com.xtra.api.projection.admin.role.RoleListItem;
import com.xtra.api.projection.admin.role.RoleView;
import com.xtra.api.repository.RoleRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
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


    public RoleView updateOrFail(Long id, @Validated RoleInsertView view) {
        var oldRole = findByIdOrFail(id);
        oldRole = roleMapper.updateEntity(view, oldRole);
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
