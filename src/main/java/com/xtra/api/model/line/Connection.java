package com.xtra.api.model.line;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.line.Line;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.Stream;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
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

    private String userAgent;
    private String isp;
    private String country;
    private String city;
    private String isoCode;

    public Connection(Line line, Stream stream, Server server, String userIp) {
        this.line = line;
        this.stream = stream;
        this.server = server;
        this.userIp = userIp;
    }
}
