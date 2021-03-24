package com.xtra.api.unit.service.admin;

import com.xtra.api.mapper.admin.PackageMapper;
import com.xtra.api.model.*;
import com.xtra.api.model.Package;
import com.xtra.api.projection.admin.downloadlist.DlCollectionView;
import com.xtra.api.projection.admin.package_.PackageInsertView;
import com.xtra.api.projection.admin.package_.PackageView;
import com.xtra.api.repository.PackageRepository;
import com.xtra.api.service.admin.PackageService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Period;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class PackageServiceUnitTest {

    @Mock
    private PackageRepository packageRepository;
    @Mock
    private PackageMapper packageMapper;

    PackageService packageService;

    @Captor
    private ArgumentCaptor<Package> packageArgumentCaptor;


    @BeforeEach
    public void setup(){
        packageService = new PackageService(packageRepository, packageMapper);
    }

    @Test
    @DisplayName("the given package with specified Id should be saved to test be passed")
    void shouldUpdateAGivenPackage() {
        Package aPackage = new Package(123L, "package new",  false, 123, Period.ZERO, 123, false, Collections.<StreamProtocol>emptyList(), new DownloadList(), Collections.<Role>emptySet());
        Package anOldPackage = new Package(321L, "package old",  true, 321, Period.ofDays(20), 321, true, Collections.<StreamProtocol>emptyList(), new DownloadList(), Collections.<Role>emptySet());
        PackageService packageService1 = Mockito.spy(packageService);
        Mockito.doReturn(anOldPackage).when(packageService1).findByIdOrFail(321L);

        packageService1.updateOrFail(321L, aPackage);

        Mockito.verify(packageRepository, Mockito.times(1)).save(packageArgumentCaptor.capture());

        Assertions.assertThat(packageArgumentCaptor.getValue().getId()).isEqualTo(321L);
        Assertions.assertThat(packageArgumentCaptor.getValue().getName()).isEqualTo("package new");
        Assertions.assertThat(packageArgumentCaptor.getValue().isTrial()).isEqualTo(false);
    }


    @Test
    @DisplayName("getViewByID method return a packageView when get existing ID")
    void returnPackageWithIDTrue() {
        Package aPackage = new Package(123L, "package name",  false, 123, Period.ZERO, 123, false, Collections.<StreamProtocol>emptyList(), new DownloadList(), Collections.<Role>emptySet());
        PackageView packageView = new PackageView(123L, "package name", false, 123, Period.ZERO, 123, false, Collections.<StreamProtocol>emptyList(), Collections.<DlCollectionView>emptySet(), Collections.<Long>emptySet());
        PackageService packageService1 = Mockito.spy(packageService);
        Mockito.doReturn(aPackage).when(packageService1).findByIdOrFail(123L);
        Mockito.when(packageMapper.convertToDto(Mockito.any(Package.class))).thenReturn(packageView);

        PackageView actualPackageView = packageService1.getViewById(123L);

        Assertions.assertThat(actualPackageView.getName()).isEqualTo(packageView.getName());
    }


    @Test
    @DisplayName("add a package with a given packageView")
    void addAPackageWithGivenPacgeView() {
        Package aPackage = new Package(123L, "package name",  false, 123, Period.ZERO, 123, false, Collections.<StreamProtocol>emptyList(), new DownloadList(), Collections.<Role>emptySet());
        PackageView packageView = new PackageView(123L, "package name", false, 123, Period.ZERO, 123, false, Collections.<StreamProtocol>emptyList(), Collections.<DlCollectionView>emptySet(), Collections.<Long>emptySet());
        PackageInsertView packageInsertView = new PackageInsertView("package name", false, 123, Period.ZERO, 123, false, Collections.<StreamProtocol>emptyList(), new LinkedHashSet<>(), Collections.<Long>emptySet());
        PackageService packageService1 = Mockito.spy(packageService);
        Mockito.doReturn(aPackage).when(packageService1).insert(aPackage);
        Mockito.when(packageMapper.convertToDto(Mockito.any(Package.class))).thenReturn(packageView);
        Mockito.when(packageMapper.convertToEntity(packageInsertView)).thenReturn(aPackage);

        PackageView actualPackageView = packageService1.add(packageInsertView);

        Assertions.assertThat(actualPackageView.getName()).isEqualTo(packageInsertView.getName());

    }

    @Test
    @DisplayName("pass a pageable of PackageViews for a given search")
    void getAll() {
        Package aPackage = new Package(123L, "package name",  false, 123, Period.ZERO, 123, false, Collections.<StreamProtocol>emptyList(), new DownloadList(), Collections.<Role>emptySet());
        Page<Package> packagePage = new PageImpl<Package>(Arrays.asList(new Package[]{aPackage}));
        PackageView packageView = new PackageView(123L, "package name", false, 123, Period.ZERO, 123, false, Collections.<StreamProtocol>emptyList(), Collections.<DlCollectionView>emptySet(), Collections.<Long>emptySet());
        PackageService packageService1 = Mockito.spy(packageService);
        Mockito.doReturn(packagePage).when(packageService1).findAll("package name", 1, 10, "id", "asc");
        Mockito.when(packageMapper.convertToDto(aPackage)).thenReturn(packageView);

        Page<PackageView> packageViewPages = packageService1.getAll("package name", 1, 10, "id", "asc");

        Assertions.assertThat(packageViewPages.iterator().next().getName()).isEqualTo(packageView.getName());
        Assertions.assertThat(packageViewPages.getTotalElements()).isEqualTo(1L);
    }
}