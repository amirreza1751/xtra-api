package com.xtra.api.mapper.admin;

import com.xtra.api.model.*;
import com.xtra.api.model.Package;
import com.xtra.api.projection.admin.downloadlist.DlCollectionView;
import com.xtra.api.projection.admin.package_.PackageInsertView;
import com.xtra.api.projection.admin.package_.PackageView;
import com.xtra.api.service.admin.CollectionService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = DownloadListMapper.class)
public abstract class PackageMapper {
    @Autowired
    private CollectionService collectionService;

    @Mapping(source = "defaultDownloadList", target = "collections")
    public abstract PackageView convertToDto(Package pack);

    Set<Long> convertRolesToIds(Set<Role> roles) {
        if (roles != null)
            return roles.stream().map(Role::getId).collect(Collectors.toSet());
        else return null;
    }

    Set<DlCollectionView> convertCollectionsToView(DownloadList defaultDownloadList) {
        return defaultDownloadList.getCollectionsAssign().stream().map(dlc -> new DlCollectionView(dlc.getCollection().getId(), dlc.getCollection().getName())).collect(Collectors.toSet());
    }

    @Mapping(target = "defaultDownloadList", ignore = true)
    @Mapping(target = "allowedRoles", ignore = true)
    public abstract Package convertToEntity(PackageInsertView packageView);

    @AfterMapping
    void convertCollectionsAndRoles(final PackageInsertView packageView, @MappingTarget final Package pack) {
        if (packageView.getAllowedRoles() != null)
            pack.setAllowedRoles(packageView.getAllowedRoles().stream().map(Role::new).collect(Collectors.toSet()));
        DownloadList dl = new DownloadList();
        dl.setName(packageView.getName() + "_" + "default");
        var collectionIds = packageView.getCollections();
        var dlcSet = new HashSet<DownloadListCollection>();
        int i = 0;
        for (var collectionId : collectionIds) {
            var dlc = new DownloadListCollection(null, collectionId);
            dlc.setCollection(collectionService.findByIdOrFail(collectionId));
            dlc.setDownloadList(dl);
            dlc.setOrder(i++);
            dlcSet.add(dlc);
        }
        dl.setCollectionsAssign(dlcSet);
        pack.setDefaultDownloadList(dl);
    }

}
