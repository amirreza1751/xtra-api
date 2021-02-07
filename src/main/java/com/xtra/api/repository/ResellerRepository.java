package com.xtra.api.repository;

import com.xtra.api.model.Reseller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResellerRepository extends JpaRepository<Reseller, Long> {

    Optional<Reseller> findByUsername(String username);
}
