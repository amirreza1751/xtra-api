package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StreamInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uptime;
    private String currentInput;
    private String resolution;
    private String videoCodec;
    private String audioCodec;

//    @OneToOne(mappedBy = "streamInfo")
//    @JsonBackReference
//    private Stream stream;
//
//    @Column(name = "stream_id")
//    private Long streamId;

    public StreamInfo() {
    }

//    public StreamInfo(Stream stream) {
//        this.stream = stream;
//    }

    @OneToOne(mappedBy = "streamInfo", cascade = CascadeType.ALL)
    @JsonBackReference("server_id1")
    private StreamServer streamServer;
}


