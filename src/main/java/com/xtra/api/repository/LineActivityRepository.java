package com.xtra.api.repository;


import com.xtra.api.model.LineActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LineActivityRepository extends JpaRepository<LineActivity, Long> {
    Optional<LineActivity> findByLineIdAndUserIpAndStreamId(Long lineId, String userIp, Long streamId);

    void deleteByLineIdAndUserIpAndStreamId(Long lineId, String userIp, Long streamId);
}
