package com.xtra.api.projection.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.Id;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StreamInfoDto {
    @Id
    private Long streamId;
    private String uptime;
    private String currentInput;
    private String resolution;
    private String videoCodec;
    private String audioCodec;

    public StreamInfoDto() {
    }

    public StreamInfoDto(Long streamId) {
        this.streamId = streamId;
    }

}
