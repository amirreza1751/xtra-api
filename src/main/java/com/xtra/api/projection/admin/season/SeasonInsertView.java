package com.xtra.api.projection.admin.season;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeasonInsertView {

    @NotBlank(message = "season name can not be empty")
    private String seasonName;

    @NotNull(message = "season number can not be empty")
    @Positive(message = "season number should be a positive number")
    private int seasonNumber;

    @NotNull(message = "number of Episode can not be empty")
    @Positive(message = "number of episodes should be a positive number")
    private int NoOfEpisodes;
    private LocalDate airDate;

}
