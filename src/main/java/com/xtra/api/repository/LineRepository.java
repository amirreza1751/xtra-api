package com.xtra.api.repository;

import com.xtra.api.model.Line;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
    Page<Line> findByUsernameLikeOrAdminNotesLikeOrResellerNotesLike(String username, String adminNotes, String resellerNotes, Pageable pageable);
}
