package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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
