package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StreamServer {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private StreamServerId id;
    private int selectedSource = 0;

    @ManyToOne
    @MapsId("streamId")
    Stream stream;

    @ManyToOne
    @MapsId("serverId")
    Server server;

    @OneToOne(cascade = CascadeType.ALL)
    StreamInfo streamInfo;

    @OneToOne(cascade = CascadeType.ALL)
    ProgressInfo progressInfo;

    public StreamServer() {
    }

    public StreamServer(StreamServerId id) {
        this.id = id;
    }

}
