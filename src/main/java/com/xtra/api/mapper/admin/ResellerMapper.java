package com.xtra.api.mapper.admin;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.DownloadList;
import com.xtra.api.model.Reseller;
import com.xtra.api.model.Role;
import com.xtra.api.projection.admin.user.UserSimpleView;
import com.xtra.api.projection.admin.user.reseller.ResellerInsertView;
import com.xtra.api.projection.admin.user.reseller.ResellerListView;
import com.xtra.api.projection.admin.user.reseller.ResellerView;
import com.xtra.api.projection.reseller.subreseller.SubresellerCreateView;
import com.xtra.api.projection.reseller.subreseller.SubresellerSimplified;
import com.xtra.api.projection.reseller.subreseller.SubresellerView;
import com.xtra.api.repository.ResellerRepository;
import com.xtra.api.service.admin.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
        uses = RoleMapper.class)
public abstract class ResellerMapper {

    @Autowired
    private RoleService roleService;
    @Autowired
    private ResellerRepository repository;

    public abstract UserSimpleView convertToSimpleView(Reseller reseller);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "customDownloadLists", target = "customDownloadListIds")
    public abstract ResellerView convertToView(Reseller reseller);

    @Mapping(source = "owner.username", target = "ownerUsername")
    public abstract ResellerListView convertToListView(Reseller reseller);

    //Subreseller conversion
    public abstract SubresellerSimplified convertToSimplifiedSubreseller(Reseller reseller);

    public abstract SubresellerView convertToSubresellerView(Reseller reseller);

    Set<Long> convertToIdSet(List<DownloadList> downloadLists) {
        if (downloadLists == null)
            return null;
        return downloadLists.stream().map(DownloadList::getId).collect(Collectors.toSet());
    }

    @Mapping(source = "ownerId", target = "owner")
    public abstract Reseller convertToEntity(ResellerInsertView insertView);

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

    public abstract Reseller convertToEntity(SubresellerCreateView view);

}
