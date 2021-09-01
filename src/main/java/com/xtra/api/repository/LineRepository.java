package com.xtra.api.repository;

import com.xtra.api.model.line.Line;
import com.xtra.api.model.user.Reseller;
import com.xtra.api.projection.admin.analytics.ConnectionsCountResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
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

    @Query(nativeQuery = true,
            value = "SELECT  count(distinct line_id) as online_users_count, count(id) as connections_count\n" +
                    "FROM (\n" +
                    "         select * from vod_connection\n" +
                    "         union\n" +
                    "         select * from connection\n" +
                    "         ) as tbl;")
    List<ConnectionsCountResult> countOnlineUsers();

    List<Line> findAllByUsernameContains(String username);


}
