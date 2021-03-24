package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.PackageMapper;
import com.xtra.api.model.Package;
import com.xtra.api.projection.admin.package_.PackageInsertView;
import com.xtra.api.projection.admin.package_.PackageView;
import com.xtra.api.repository.PackageRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class  PackageService extends CrudService<Package, Long, PackageRepository> {
    private final PackageMapper packageMapper;

    @Autowired
    public PackageService(PackageRepository repository, PackageMapper packageMapper) {
        super(repository, "Package");
        this.packageMapper = packageMapper;
    }

    @Override
    protected Page<Package> findWithSearch(Pageable page, String search) {
        return null;
    }

    @Override
    public Package updateOrFail(Long id, Package pack) {
        var oldPack = findByIdOrFail(id);
        pack.setId(id);
        oldPack.setDefaultDownloadList(pack.getDefaultDownloadList());
        copyProperties(pack, oldPack, "id", "defaultDownloadList");
        return repository.save(oldPack);
    }

    public PackageView getViewById(Long id) {
        return packageMapper.convertToDto(findByIdOrFail(id));
    }

    public PackageView add(PackageInsertView packageView) {
        return packageMapper.convertToDto(insert(packageMapper.convertToEntity(packageView)));
    }

    public PackageView save(Long id, PackageInsertView packageView) {
        return packageMapper.convertToDto(updateOrFail(id, packageMapper.convertToEntity(packageView)));
    }

    public Page<PackageView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(packageMapper::convertToDto);
    }
}
