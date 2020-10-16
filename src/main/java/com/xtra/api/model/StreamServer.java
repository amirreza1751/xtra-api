package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

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

    public StreamServer() {
    }
    public StreamServer(StreamServerId id){
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof StreamServer))
            return false;
        StreamServer streamServer = (StreamServer) obj;
        return streamServer.id.getServerId().equals(this.id.getServerId()) && streamServer.id.getStreamId().equals(this.id.getStreamId());
    }


}
