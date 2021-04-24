package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@ToString(exclude = {"line", "stream", "server"})
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"line_id", "stream_id", "server_id", "user_ip"})
)
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Line line;

    @ManyToOne
    @NotNull
    private Stream stream;

    @ManyToOne
    @NotNull
    private Server server;

    @NotNull
    @Column(name = "user_ip")
    private String userIp;

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
