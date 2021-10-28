package com.xtra.api.model.vod;

import com.xtra.api.model.server.Server;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@NoArgsConstructor
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"video", "server"})
public class VideoServer {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private VideoServerId id;

    @ManyToOne
    @MapsId("videoId")
    Video video;

    @ManyToOne
    @MapsId("serverId")
    Server server;

    private String targetDirectoryLocation;

    public VideoServer(VideoServerId id) {
        this.id = id;
    }

    public VideoServer(Video video, Server server) {
        this.video = video;
        this.server = server;
    }
}
