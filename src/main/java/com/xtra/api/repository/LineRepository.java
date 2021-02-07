package com.xtra.api.repository;

import com.xtra.api.model.Line;
import com.xtra.api.model.Reseller;
import com.xtra.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    Page<Line> findByUsernameLikeOrAdminNotesLikeOrResellerNotesLike(String username, String adminNotes, String resellerNotes, Pageable pageable);

    Page<Line> findAllByOwnerUsername(String username, Pageable page);

    Optional<Line> findByOwnerUsernameAndId(String username, Long id);

    Optional<Line> findByUsername(String username);

    Optional<Line> findByLineToken(String lineToken);

    boolean existsLineById(Long id);

    boolean existsByOwnerUsernameAndId(String username, Long id);

    void deleteByOwnerUsernameAndId(String username, Long id);
}
