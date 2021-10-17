package com.xtra.api.mapper.admin;

import com.xtra.api.model.category.CategoryVod;
import com.xtra.api.model.category.CategoryVodId;
import com.xtra.api.model.collection.CollectionVod;
import com.xtra.api.model.collection.CollectionVodId;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.vod.Series;
import com.xtra.api.projection.admin.series.SeriesInsertView;
import com.xtra.api.projection.admin.series.SeriesView;
import com.xtra.api.repository.CategoryRepository;
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
    @Autowired
    private CategoryRepository categoryRepository;

    @Mapping(target = "categories", ignore = true)
    public abstract Series convertToEntity(SeriesInsertView seriesInsertView);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "collectionAssigns", ignore = true)
    @Mapping(target = "seasons", ignore = true)
    @Mapping(target = "categories", ignore = true)
    public abstract Series convertToEntity(SeriesInsertView seriesInsertView, @MappingTarget final Series series);

    @AfterMapping
    void addCollectionsAndCategories(final SeriesInsertView insertView, @MappingTarget final Series series) {
        var collectionIds = insertView.getCollections();
        if (collectionIds != null) {
            Set<CollectionVod> collectionVods = new HashSet<>();
            for (var id : collectionIds) {
                var collectionVod = new CollectionVod();
                //Find number of added entries in collection to add new entry to the end of the list
                var orderCount = collectionVodRepository.countAllByIdCollectionId(id);
                collectionVod.setOrder(orderCount);
                var col = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Collection", id.toString()));
                collectionVod.setId(new CollectionVodId(id, series.getId()));
                collectionVod.setCollection(col);
                collectionVod.setVod(series);
                collectionVods.add(collectionVod);
            }
            series.getCollectionAssigns().retainAll(collectionVods);
            series.getCollectionAssigns().addAll(collectionVods);
        }
        var categoryIds = insertView.getCategories();
        if (categoryIds != null) {
            var categoryVods = new HashSet<CategoryVod>();
            for (var id : categoryIds) {
                var category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category", id.toString()));
                CategoryVod categoryVod = new CategoryVod(new CategoryVodId(id, series.getId()));
                categoryVod.setCategory(category);
                categoryVod.setVod(series);
                categoryVods.add(categoryVod);
            }
            series.getCategories().retainAll(categoryVods);
            series.getCategories().addAll(categoryVods);
        }
    }

    @Mapping(target = "collections", ignore = true)
    @Mapping(target = "categories", ignore = true)
    public abstract SeriesView convertToView(Series series);

    @AfterMapping
    protected void convertRelationsToIds(final Series series, @MappingTarget final SeriesView seriesView) {
        seriesView.setCollections(series.getCollectionAssigns().stream().map(collectionVod -> collectionVod.getCollection().getId()).collect(Collectors.toSet()));
        seriesView.setCategories(series.getCategories().stream().map(categoryVod -> categoryVod.getCategory().getId()).collect(Collectors.toSet()));
    }

    public Set<CollectionVod> convertToCollections(Set<Long> ids, Series series) {
        Set<CollectionVod> collectionVodSet = new HashSet<>();

        for (Long id : ids) {
            CollectionVod collectionVod = new CollectionVod(new CollectionVodId(id, series.getId()));
            var collection = collectionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Collection", id.toString()));
            collectionVod.setVod(series);
            collectionVod.setCollection(collection);
            collectionVodSet.add(collectionVod);
        }

        return collectionVodSet;
    }
}
