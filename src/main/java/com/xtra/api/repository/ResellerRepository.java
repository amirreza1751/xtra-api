package com.xtra.api.repository;

import com.xtra.api.model.user.Reseller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ResellerRepository extends JpaRepository<Reseller, Long> {

    Optional<Reseller> findByUsername(String username);

    Optional<Reseller> findByIdAndOwnerUsername(Long id, String username);

    Boolean existsByIdAndOwner(Long id, Reseller owner);

    Page<Reseller> findAllByUsernameLike(String username, Pageable pageable);

    Page<Reseller> findAllByUsernameContains(String username, Pageable pageable);

    @Transactional
    void deleteByIdAndOwner(Long id, Reseller owner);

    Page<Reseller> findAllByOwner(Reseller owner, Pageable pageable);

    long countByIsVerifiedFalse();
    long countByOwner(Reseller owner);
}
