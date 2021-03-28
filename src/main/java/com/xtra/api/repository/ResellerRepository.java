package com.xtra.api.repository;

import com.xtra.api.model.Collection;
import com.xtra.api.model.Reseller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResellerRepository extends JpaRepository<Reseller, Long> {

    Optional<Reseller> findByUsername(String username);

    Optional<Reseller> findByIdAndOwnerUsername(Long id, String username);

    Boolean existsByIdAndOwner(Long id, Reseller owner);

    Page<Reseller> findAllByNameLike(String name, Pageable pageable);

    void deleteByIdAndOwner(Long id, Reseller owner);

    Page<Reseller> findAllByOwner(Reseller owner, Pageable pageable);
}
