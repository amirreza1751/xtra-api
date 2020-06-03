package com.xtra.api.repository;

import com.xtra.api.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Package, Long> {
}
