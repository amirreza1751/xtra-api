package com.xtra.api.projection.admin.season;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeasonInsertView {

    @NotBlank(message = "season name can not be empty")
    private String seasonName;

    @NotNull(message = "season number can not be empty")
    @Digits(message = "season number should be a 3 digits number", integer = 3, fraction = 0)
    private int seasonNumber;

    @NotNull(message = "number of Episode can not be empty")
    @Digits(message = "number of Episode should be a 3 digits number", integer = 3, fraction = 0)
    private int NoOfEpisodes;
    private LocalDate airDate;

}
