package com.xtra.api.repository;

import com.xtra.api.model.BlockedIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedIpRepository extends JpaRepository<BlockedIp, String> {
}
