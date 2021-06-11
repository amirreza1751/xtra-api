package com.xtra.api.mapper.admin;

import com.xtra.api.model.collection.CollectionVod;
import com.xtra.api.model.collection.CollectionVodId;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.vod.Series;
import com.xtra.api.projection.admin.series.SeriesInsertView;
import com.xtra.api.projection.admin.series.SeriesView;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.repository.CollectionVodRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class SeriesMapper {

    @Autowired
    private CollectionVodRepository collectionVodRepository;
    @Autowired
    private CollectionRepository collectionRepository;

    public abstract Series convertToEntity(SeriesInsertView seriesInsertView);

    @Mapping(source = "collectionAssigns", target = "collections")
    public abstract SeriesView convertToView(Series series);

    @AfterMapping
    void convertCollectionIds(final SeriesInsertView seriesInsertView, @MappingTarget final Series series) {
        var collectionIds = seriesInsertView.getCollections();
        if (collectionIds != null) {
            Set<CollectionVod> collectionVods = new HashSet<>();
            for (var id : collectionIds) {
                var collectionVod = new CollectionVod();
                var orderCount = collectionVodRepository.countAllByIdCollectionId(id);
                collectionVod.setOrder(orderCount + 1);
                var col = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("collection", id.toString()));
                collectionVod.setId(new CollectionVodId(id, null));
                collectionVod.setCollection(col);
                collectionVod.setVod(series);
                collectionVods.add(collectionVod);
            }
            series.setCollectionAssigns(collectionVods);
        }
    }

    public Set<Long> convertToCollectionIds(Set<CollectionVod> collectionVods) {
        if (collectionVods == null) return null;
        return collectionVods.stream().map(collectionVod -> collectionVod.getCollection().getId()).collect(Collectors.toSet());
    }
}
