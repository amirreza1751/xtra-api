package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    public ProgressInfo() {

    }

    public ProgressInfo(Stream stream) {
        this.stream = stream;
    }

    @OneToOne(mappedBy = "progressInfo", cascade = CascadeType.ALL)
    @JsonBackReference("server_id2")
    private StreamServer streamServer;
}
