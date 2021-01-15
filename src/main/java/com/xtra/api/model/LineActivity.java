package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.time.LocalDateTime;

@Entity
@Data
@ToString(exclude = {"line", "stream", "server"})
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LineActivity {

    @EmbeddedId
    private LineActivityId id = new LineActivityId();

    @ManyToOne
    @MapsId("lineId")
    private Line line;

    @ManyToOne
    @MapsId("streamId")
    private Stream stream;

    @ManyToOne
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
    private String isoCode;

}
