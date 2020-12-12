package com.xtra.api.mapper;

import com.xtra.api.model.*;
import com.xtra.api.projection.collection.CollectionView;
import com.xtra.api.projection.collection.CollectionInsertView;
import com.xtra.api.projection.collection.CollectionSimplifiedView;
import org.mapstruct.Mapper;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CollectionMapper {

    /*collection simple display*/

    public CollectionSimplifiedView convertToSimpleDto(Collection collection) {
        CollectionSimplifiedView collectionSimplifiedView = new CollectionSimplifiedView();

        collectionSimplifiedView.setId(collection.getId());
        collectionSimplifiedView.setName(collection.getName());
        collectionSimplifiedView.setType(collection.getType());

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
                i = 0;
                for (var id : input.getChannels()) {
                    CollectionStream collectionStream = new CollectionStream(new CollectionStreamId(input.getId(), id));
                    collectionStream.setOrder(i++);
                    collectionStream.setCollection(collection);
                    collection.addStream(collectionStream);
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
