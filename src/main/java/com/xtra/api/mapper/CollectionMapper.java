package com.xtra.api.mapper;

import com.xtra.api.model.*;
import com.xtra.api.projection.CollectionDto;
import com.xtra.api.projection.CollectionInsertDto;
import com.xtra.api.projection.CollectionSimplifiedDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring")
public abstract class CollectionMapper {
    /*collection simple display*/

    @Mapping(target = "channels", expression = "java( collection.getChannels().size() )")
    @Mapping(target = "movies", expression = "java( collection.getMovies().size() )")
    @Mapping(target = "series", expression = "java( collection.getSeries().size() )")
    @Mapping(target = "radios", expression = "java( collection.getRadios().size() )")
    public abstract CollectionSimplifiedDto convertToSimpleDto(Collection collection);

    /*collection for insert*/

    public abstract Collection convertToEntity(CollectionInsertDto collectionInsertDto);

    /*collection with details*/

    Set<Channel> convertIdsToChannels(List<Long> ids) {
        return ids != null ? ids.stream().map(Channel::new).collect(toSet()) : null;
    }

    Set<Movie> convertIdsToMovies(List<Long> ids) {

        return ids != null ? ids.stream().map(Movie::new).collect(toSet()) : null;
    }

    Set<Series> convertIdsToSeries(List<Long> ids) {
        return ids != null ? ids.stream().map(Series::new).collect(toSet()) : null;
    }

    Set<Radio> convertIdsToRadios(List<Long> ids) {
        return ids != null ? ids.stream().map(Radio::new).collect(toSet()) : null;
    }

    public abstract CollectionDto convertToDto(Collection collection);

    List<MediaPair<Long, String>> convertChannelsToIds(Set<Channel> channels) {
        if (channels == null)
            return null;
        return channels.stream().map(channel -> new MediaPair<>(channel.getId(), channel.getName())).collect(toList());
    }

    List<MediaPair<Long, String>> convertMoviesToIds(Set<Movie> movies) {
        if (movies == null)
            return null;
        return movies.stream().map(movie -> new MediaPair<>(movie.getId(), movie.getName())).collect(toList());
    }

    List<MediaPair<Long, String>> convertSeriesToIds(Set<Series> seriesList) {
        if (seriesList == null)
            return null;
        return seriesList.stream().map(series -> new MediaPair<>(series.getId(), series.getName())).collect(toList());
    }

    List<MediaPair<Long, String>> convertRadiosToIds(Set<Radio> radios) {
        if (radios == null)
            return null;
        return radios.stream().map(radio -> new MediaPair<>(radio.getId(), radio.getName())).collect(toList());
    }
}
