package com.xtra.api.projection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.Id;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
