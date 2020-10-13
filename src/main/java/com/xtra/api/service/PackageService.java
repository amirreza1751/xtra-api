package com.xtra.api.service;

import com.xtra.api.model.Package;
import com.xtra.api.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class PackageService extends CrudService<Package, Long, PackageRepository> {
    private final DownloadListService dlService;

    @Autowired
    protected PackageService(PackageRepository repository, DownloadListService dlService) {
        super(repository, Package.class);
        this.dlService = dlService;
    }

    @Override
    protected Page<Package> findWithSearch(Pageable page, String search) {
        return null;
    }

    @Override
    public Package updateOrFail(Long id, Package pack) {
        var oldPack = findByIdOrFail(id);
        pack.setId(id);
        oldPack.setDefaultDownloadList(dlService.findByIdOrFail(pack.getDefaultDownloadList().getId()));
        copyProperties(pack, oldPack, "id","defaultDownloadList");
        return repository.save(oldPack);
    }
}
