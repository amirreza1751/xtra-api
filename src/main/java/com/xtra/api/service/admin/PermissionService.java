package com.xtra.api.service.admin;

import com.xtra.api.model.Permission;
import com.xtra.api.model.PermissionId;
import com.xtra.api.model.UserType;
import com.xtra.api.repository.PermissionRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService extends CrudService<Permission, PermissionId, PermissionRepository> {

    protected PermissionService(PermissionRepository repository) {
        super(repository, "Permission");
    }

    @Override
    protected Page<Permission> findWithSearch(String search, Pageable page) {
        return null;
    }

    @Override
    protected Pageable getSortingPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable page;
        Sort.Order order;
        if (sortBy != null && !sortBy.equals("")) {
            if (sortDir != null && sortDir.equalsIgnoreCase("desc"))
                order = Sort.Order.desc(sortBy);
            else
                order = Sort.Order.asc(sortBy);
            page = PageRequest.of(pageNo, pageSize, Sort.by(order));
        } else {
            page = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.asc("pKey")));
        }
        return page;
    }

    public List<Permission> getPermissions(UserType userType) {
        if (userType == null)
            return repository.findAll();
        else
            return repository.findAllByIdUserType(userType);
    }
}
