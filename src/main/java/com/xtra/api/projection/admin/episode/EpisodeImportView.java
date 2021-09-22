package com.xtra.api.projection.admin.episode;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.projection.admin.movie.MovieImport;
import com.xtra.api.projection.admin.season.SeasonInsertView;
import com.xtra.api.projection.admin.video.VideoInsertView;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EpisodeImportView {
    private Set<EpisodeImport> episodes;
    private Set<Long> servers;
    private String notes;
}
