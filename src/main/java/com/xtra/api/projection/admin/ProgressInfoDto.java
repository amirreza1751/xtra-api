package com.xtra.api.projection.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.Id;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProgressInfoDto {
    @Id
    private Long streamId;
    private String speed;
    private String frameRate;
    private String bitrate;

    public ProgressInfoDto() {

    }

    public ProgressInfoDto(Long streamId) {
        this.streamId = streamId;
    }
}
