package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EpgChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String icon;
    private String url;
    private String language;


    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference("epg_file_id")
    private EpgFile epgFile;


    @OneToMany(mappedBy = "epgChannel", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference("epg_channel_id")
    private Set<Program> programs;

    @JsonBackReference("epgChannel")
    @OneToOne(mappedBy = "epgChannel", cascade = CascadeType.ALL)
    private Stream stream;
}
