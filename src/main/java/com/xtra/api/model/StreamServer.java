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
    private boolean isCatchUp = false;
    private int catchUpDays = 0;
    private boolean recording = false;

    @ManyToOne
    @MapsId("streamId")
    Stream stream;

    @ManyToOne
    @MapsId("serverId")
    Server server;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    StreamDetails streamDetails;

    private Boolean isOnDemand = false;

    public StreamServer() {
    }

    public StreamServer(StreamServerId id) {
        this.id = id;
    }


}
