package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "video", "server"})
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

    public VideoServer(VideoServerId id) {
        this.id = id;
    }

}
