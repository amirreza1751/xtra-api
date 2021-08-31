package com.xtra.api.repository;

import com.xtra.api.model.line.Line;
import com.xtra.api.model.user.Reseller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    Page<Line> findByUsernameLikeOrAdminNotesLikeOrResellerNotesLike(String username, String adminNotes, String resellerNotes, Pageable pageable);

    Page<Line> findAllByOwner(Reseller owner, Pageable page);

    Optional<Line> findByOwnerAndId(Reseller owner, Long id);

    Optional<Line> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Line> findByLineToken(String lineToken);

    boolean existsLineById(Long id);

    boolean existsByOwnerAndId(Reseller owner, Long id);

    void deleteLineByOwnerAndId(Reseller owner, Long id);

    long countLineByCurrentConnectionsIsNot(long count);

    List<Line> findAllByUsernameContains(String username);
}
