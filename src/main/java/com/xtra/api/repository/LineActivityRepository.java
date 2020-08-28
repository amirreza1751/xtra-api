package com.xtra.api.repository;


import com.xtra.api.model.LineActivity;
import com.xtra.api.model.LineActivityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LineActivityRepository extends JpaRepository<LineActivity, LineActivityId> {
    Optional<LineActivity> findById(LineActivityId id);

    void deleteById(LineActivityId id);

}
