package com.xtra.api.mapper.admin;

import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.model.CollectionVod;
import com.xtra.api.model.Episode;
import com.xtra.api.model.Series;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
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
public abstract class EpisodeMapper {

    public abstract Episode convertToEntity(EpisodeInsertView episodeInsertView);
    public abstract EpisodeInsertView convertToView(Episode episode);

}
