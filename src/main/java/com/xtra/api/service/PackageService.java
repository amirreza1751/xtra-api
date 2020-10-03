package com.xtra.api.service;

import com.xtra.api.model.Package;
import com.xtra.api.repository.PackageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PackageService extends CrudService<Package, Long, PackageRepository> {
    protected PackageService(PackageRepository repository) {
        super(repository, Package.class);
    }

    @Override
    protected Page<Package> findWithSearch(Pageable page, String search) {
        return null;
    }
}
