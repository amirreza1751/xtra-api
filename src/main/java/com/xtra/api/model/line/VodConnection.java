package com.xtra.api.model.line;

import com.xtra.api.model.server.Server;
import com.xtra.api.model.vod.Video;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@ToString(exclude = {"line", "video", "server"})
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"line_id", "video_id", "server_id", "user_ip"})
)
@NoArgsConstructor
public class VodConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Line line;

    @ManyToOne
    @NotNull
    private Video video;

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

    public VodConnection(Line line, Video video, Server server, String userIp) {
        this.line = line;
        this.video = video;
        this.server = server;
        this.userIp = userIp;
    }
}
