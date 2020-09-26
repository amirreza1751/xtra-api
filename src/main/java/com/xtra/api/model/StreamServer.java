package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StreamServer {
    @EmbeddedId
    private StreamServerId id;

    @ManyToOne
    @JsonBackReference("stream_server")
    @MapsId("streamId")
    Stream stream;

    @ManyToOne
    @JsonBackReference("server_id")
    @MapsId("serverId")
    Server server;

    @OneToOne
    StreamInfo streamInfo;

    @OneToOne
    ProgressInfo progressInfo;
}
