package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.time.LocalDateTime;

@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LineActivity {

    @EmbeddedId
    private LineActivityId id = new LineActivityId();

    @ManyToOne
    @JsonBackReference("line_id")
    @MapsId("lineId")
    private Line line;

    @ManyToOne
    @JsonBackReference("stream_id")
    @MapsId("streamId")
    private Stream stream;

    @ManyToOne
    @JsonBackReference("server_id")
    @MapsId("serverId")
    private Server server;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime lastRead;

    private boolean hlsEnded;
    private String userAgent;
    private String isp;
    private String country;
    private String city;

}
