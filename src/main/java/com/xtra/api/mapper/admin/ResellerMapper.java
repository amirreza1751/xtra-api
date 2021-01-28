package com.xtra.api.mapper.admin;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.DownloadList;
import com.xtra.api.model.Reseller;
import com.xtra.api.model.Role;
import com.xtra.api.projection.user.UserSimpleView;
import com.xtra.api.projection.user.reseller.ResellerInsertView;
import com.xtra.api.projection.user.reseller.ResellerView;
import com.xtra.api.repository.ResellerRepository;
import com.xtra.api.service.admin.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT, uses = RoleMapper.class)
public abstract class ResellerMapper {

    @Autowired
    private RoleService roleService;
    @Autowired
    private ResellerRepository repository;

    public abstract UserSimpleView convertToSimpleView(Reseller reseller);

    public abstract ResellerView convertToView(Reseller reseller);

    Set<Long> convertToIdSet(List<DownloadList> downloadLists) {
        if (downloadLists == null)
            return null;
        return downloadLists.stream().map(DownloadList::getId).collect(Collectors.toSet());
    }

    public abstract Reseller convertToEntity(ResellerInsertView insertView);

    Long convertToId(Reseller reseller) {
        if (reseller==null)
            return null;
        return reseller.getId();
    }

    Role convertToId(Long roleId) {
        if (roleId == null)
            return null;//todo throw exception
        return roleService.findByIdOrFail(roleId);
    }

    Reseller convertToReseller(Long resellerId) {
        if (resellerId == null)
            return null;
        return repository.findById(resellerId).orElseThrow(() -> new EntityNotFoundException("Reseller", resellerId.toString()));
    }

}
