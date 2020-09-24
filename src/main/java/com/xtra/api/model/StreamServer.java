package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
@Getter
@Setter
public class StreamServer {
    @EmbeddedId
    private StreamServerId id;

    @ManyToOne
    @JsonBackReference("stream_id")
    @MapsId("streamId")
    Stream stream;

    @ManyToOne
    @JsonBackReference("server_id")
    @MapsId("serverId")
    Server server;

}
