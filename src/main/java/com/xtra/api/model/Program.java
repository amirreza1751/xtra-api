package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Embeddable
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    @Column( length = 100000 )
    private String description;
    private ZonedDateTime start;
    private ZonedDateTime stop;
    private String category;
    private String language;


    @ManyToOne
    @JsonBackReference("epg_channel_id")
    private EpgChannel epgChannel;
}
