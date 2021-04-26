package com.xtra.api.mapper.admin;

import com.xtra.api.model.Episode;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class EpisodeMapper {

    public abstract Episode convertToEntity(EpisodeInsertView episodeInsertView);
    public abstract EpisodeInsertView convertToView(Episode episode);

}
