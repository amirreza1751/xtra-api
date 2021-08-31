package com.xtra.api.mapper.admin;

import com.xtra.api.model.collection.*;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.projection.admin.MediaPair;
import com.xtra.api.projection.admin.collection.CollectionInsertView;
import com.xtra.api.projection.admin.collection.CollectionSimplifiedView;
import com.xtra.api.projection.admin.collection.CollectionView;
import com.xtra.api.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CollectionMapper {

    @Autowired
    CategoryRepository categoryRepository;

    /*collection simple display*/

    public CollectionSimplifiedView convertToSimpleDto(Collection collection) {
        CollectionSimplifiedView collectionSimplifiedView = new CollectionSimplifiedView();

        collectionSimplifiedView.setId(collection.getId());
        collectionSimplifiedView.setName(collection.getName());
        collectionSimplifiedView.setType(collection.getType());
        collectionSimplifiedView.setCategoryName(collection.getCategory().getName());
        switch (collection.getType()) {
            case SERIES:
                collectionSimplifiedView.setSeries(collection.getVods().size());
                break;
            case MOVIE:
                collectionSimplifiedView.setMovies(collection.getVods().size());
                break;
            case RADIO:
                collectionSimplifiedView.setRadios(collection.getStreams().size());
                break;
            case CHANNEL:
                collectionSimplifiedView.setChannels(collection.getStreams().size());

                break;
        }
        return collectionSimplifiedView;
    }

    /*collection for insert*/

    public Collection convertToEntity(CollectionInsertView input) {
        Collection collection = new Collection();
        collection.setId(input.getId());
        collection.setName(input.getName());
        collection.setType(input.getType());
        collection.setCategory(categoryRepository.findById(input.getCategoryName()).orElseThrow(() -> new EntityNotFoundException("Category", "Name", input.getCategoryName())));
        int i;
        switch (collection.getType()) {
            case SERIES:
                i = 0;
                for (var id : input.getSeries()) {
                    CollectionVod collectionVod = new CollectionVod(new CollectionVodId(input.getId(), id));
                    collectionVod.setOrder(i++);
                    collectionVod.setCollection(collection);
                    collection.addVod(collectionVod);
                }
                break;
            case MOVIE:
                i = 0;
                for (var id : input.getMovies()) {
                    CollectionVod collectionVod = new CollectionVod(new CollectionVodId(input.getId(), id));
                    collectionVod.setOrder(i++);
                    collectionVod.setCollection(collection);
                    collection.addVod(collectionVod);
                }
                break;
            case RADIO:
                i = 0;
                for (var id : input.getRadios()) {
                    CollectionStream collectionStream = new CollectionStream(new CollectionStreamId(input.getId(), id));
                    collectionStream.setOrder(i++);
                    collectionStream.setCollection(collection);
                    collection.addStream(collectionStream);
                }
                break;
            case CHANNEL:
                if (input.getChannels() != null){
                    i = 0;
                    for (var id : input.getChannels()) {
                        CollectionStream collectionStream = new CollectionStream(new CollectionStreamId(input.getId(), id));
                        collectionStream.setOrder(i++);
                        collectionStream.setCollection(collection);
                        collection.addStream(collectionStream);
                    }
                }
                break;
        }
        return collection;
    }


    public CollectionView convertToDto(Collection collection) {
        CollectionView collectionView = new CollectionView();
        collectionView.setId(collection.getId());
        collectionView.setName(collection.getName());
        collectionView.setType(collection.getType());
        collectionView.setCategoryName(collection.getCategory().getName());
        switch (collection.getType()) {
            case SERIES:
                var series = collection.getVods() != null ? collection.getVods().stream().map(vod -> new MediaPair<>(vod.getVod().getId(), vod.getVod().getName())).collect(Collectors.toCollection(LinkedHashSet::new)) : null;
                collectionView.setSeries(series);
                break;
            case MOVIE:
                var movies = collection.getVods() != null ? collection.getVods().stream().map(vod -> new MediaPair<>(vod.getVod().getId(), vod.getVod().getName())).collect(Collectors.toCollection(LinkedHashSet::new)) : null;
                collectionView.setMovies(movies);
                break;
            case RADIO:
                var radios = collection.getStreams() != null ? collection.getStreams().stream().map(stream -> new MediaPair<>(stream.getStream().getId(), stream.getStream().getName())).collect(Collectors.toCollection(LinkedHashSet::new)) : null;
                collectionView.setRadios(radios);
                break;
            case CHANNEL:
                var channels = collection.getStreams() != null ? collection.getStreams().stream().map(stream -> new MediaPair<>(stream.getStream().getId(), stream.getStream().getName())).collect(Collectors.toCollection(LinkedHashSet::new)) : null;
                collectionView.setChannels(channels);
                break;
        }
        return collectionView;
    }

}
