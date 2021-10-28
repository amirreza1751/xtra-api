package com.xtra.api.projection.admin.episode;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.projection.admin.season.SeasonInsertView;
import com.xtra.api.projection.admin.video.VideoInsertView;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EpisodeImport {
    @NotNull(message = "Episode number can not be empty")
    @Positive(message = "Episode number should be a positive number")
    private int episodeNumber;
    @NotBlank(message = "Episode name can not be empty")
    private String episodeName;
    private int tmdbId;

    @NotNull(message = "season can not be empty")
    private SeasonInsertView season;
    private VideoInsertView video;
}
