package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProgressInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String speed;
    private String frameRate;
    private String bitrate;

    @OneToOne(mappedBy = "progressInfo")
    @JsonBackReference
    private Stream stream;

    @Column(name = "stream_id")
    private Long streamId;

    public ProgressInfo() {

    }

    public ProgressInfo(Stream stream) {
        this.stream = stream;
    }

    @OneToOne(mappedBy = "progressInfo", cascade = CascadeType.ALL)
    @JsonBackReference("server_id2")
    private StreamServer streamServer;
}
