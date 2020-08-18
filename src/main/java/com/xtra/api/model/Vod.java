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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_seq")
    @GenericGenerator(
            name = "movie_seq",
            strategy = "enhanced-sequence",
            parameters = {
                    @Parameter(name = "prefer_sequence_per_entity", value = "true"),
                    @Parameter(name = "increment_size", value = "1")})
    private Long id;

    @NotBlank
    private String name;
    private String location;
    private String token;

    @Enumerated(EnumType.STRING)
    private EncodingStatus encodeStatus;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Subtitle> subtitles = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Audio> audios = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private MediaInfo mediaInfo;

}
