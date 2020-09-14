package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Vod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;
    private String location;
    private String token;

    @Enumerated(EnumType.STRING)
    private EncodeStatus encodeStatus;

    @ManyToMany
    private List<Collection> collections;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Subtitle> subtitles = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Audio> audios = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private MediaInfo mediaInfo;

}
