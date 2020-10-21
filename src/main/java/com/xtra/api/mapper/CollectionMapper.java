package com.xtra.api.mapper;

import com.xtra.api.model.*;
import com.xtra.api.projection.CollectionDto;
import com.xtra.api.projection.CollectionInsertDto;
import com.xtra.api.projection.CollectionSimplifiedDto;
import org.mapstruct.Mapper;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring")
public abstract class CollectionMapper {

    /*collection simple display*/

    public CollectionSimplifiedDto convertToSimpleDto(Collection collection) {
        CollectionSimplifiedDto collectionSimplifiedDto = new CollectionSimplifiedDto();

        collectionSimplifiedDto.setId(collection.getId());
        collectionSimplifiedDto.setName(collection.getName());
        collectionSimplifiedDto.setType(collection.getType());

        switch (collection.getType()) {
            case SERIES:
                collectionSimplifiedDto.setSeries(collection.getVods().size());
                break;
            case MOVIE:
                collectionSimplifiedDto.setMovies(collection.getVods().size());
                break;
            case RADIO:
                collectionSimplifiedDto.setRadios(collection.getStreams().size());
                break;
            case CHANNEL:
                collectionSimplifiedDto.setChannels(collection.getStreams().size());

                break;
        }
        return collectionSimplifiedDto;
    }

    /*collection for insert*/

    public Collection convertToEntity(CollectionInsertDto input) {
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


    public CollectionDto convertToDto(Collection collection) {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collection.getId());
        collectionDto.setName(collection.getName());
        collectionDto.setType(collection.getType());

        switch (collection.getType()) {
            case SERIES:
                var series = collection.getVods() != null ? collection.getVods().stream().map(vod -> new MediaPair<>(vod.getVod().getId(), vod.getVod().getName())).collect(Collectors.toCollection(LinkedHashSet::new)) : null;
                collectionDto.setSeries(series);
                break;
            case MOVIE:
                var movies = collection.getVods() != null ? collection.getVods().stream().map(vod -> new MediaPair<>(vod.getVod().getId(), vod.getVod().getName())).collect(Collectors.toCollection(LinkedHashSet::new)) : null;
                collectionDto.setMovies(movies);
                break;
            case RADIO:
                var radios = collection.getStreams() != null ? collection.getStreams().stream().map(stream -> new MediaPair<>(stream.getStream().getId(), stream.getStream().getName())).collect(Collectors.toCollection(LinkedHashSet::new)) : null;
                collectionDto.setRadios(radios);
                break;
            case CHANNEL:
                var channels = collection.getStreams() != null ? collection.getStreams().stream().map(stream -> new MediaPair<>(stream.getStream().getId(), stream.getStream().getName())).collect(Collectors.toCollection(LinkedHashSet::new)) : null;
                collectionDto.setChannels(channels);
                break;
        }
        return collectionDto;
    }

}
