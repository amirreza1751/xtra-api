package com.xtra.api.repository;

import com.xtra.api.model.collection.Collection;
import com.xtra.api.projection.admin.collection.CollectionSimpleView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    boolean existsAllByIdIn(Set<Long> ids);

    Page<Collection> findAllByNameLike(String name, Pageable pageable);

    @Query(value = "select c.id,\n" +
            "       c.name,\n" +
            "       count(IF(s.stream_type = 0, 1, null)) as channelCount,\n" +
            "       count(IF(s.stream_type = 1, 1, null)) as radioCount,\n" +
            "\t     count(IF(v.vod_type = 0, 1, null)) as movieCount,\n" +
            "       count(IF(v.vod_type = 1, 1, null)) as seriesCount\n" +
            "from collection c\n" +
            "         left join collection_stream cs on c.id = cs.collection_id\n" +
            "         left join stream s on s.id = cs.stream_id\n" +
            "         left join collection_vod cv on c.id = cv.collection_id\n" +
            "         left join vod v on v.id = cv.vod_id\n" +
            "group by c.id ",
            nativeQuery = true)
    Page<CollectionSimpleView> listAllCollectionsSimpleView(Pageable page);
}
