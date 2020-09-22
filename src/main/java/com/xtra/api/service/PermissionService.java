package com.xtra.api.service;

import com.xtra.api.model.Permission;
import com.xtra.api.repository.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PermissionService extends CrudService<Permission,String, PermissionRepository> {

    protected PermissionService(PermissionRepository repository) {
        super(repository, Permission.class);
    }

    @Override
    protected Page<Permission> findWithSearch(Pageable page, String search) {
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

    public boolean existsAllByKeys(Set<String> keys){
        return repository.existsAllBypKeyIn(keys);
    }

}
