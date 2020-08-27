package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@IdClass(LineActivityId.class)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LineActivity {

    @ManyToOne
    @Id
    @MapsId
    private Line line;

    @Column(name = "line_id", insertable = false, updatable = false)
    private Long lineId;

    @ManyToOne
    @Id
    @MapsId
    private Stream stream;

    @Column(name = "stream_id", insertable = false, updatable = false)
    private Long streamId;

    @Id
    private String userIp;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime lastRead;

    private boolean hlsEnded;
    private String userAgent;
    private String isp;
    private String country;
    private String city;

}
