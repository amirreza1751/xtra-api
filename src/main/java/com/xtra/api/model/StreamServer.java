package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StreamServer {
    @EmbeddedId
    private StreamServerId id;
    private int selectedSource = 0;

    @ManyToOne
    @JsonBackReference("stream_server")
    @MapsId("streamId")
    Stream stream;

    @ManyToOne
    @JsonBackReference("server_id")
    @MapsId("serverId")
    Server server;


    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference("server_id1")
    StreamInfo streamInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference("server_id2")
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
