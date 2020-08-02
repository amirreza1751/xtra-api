package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Vod {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "movie_seq")
    @GenericGenerator(
            name = "movie_seq",
            strategy = "enhanced-sequence",
            parameters = {
                    @Parameter(name = "prefer_sequence_per_entity", value = "true"),
                    @Parameter(name = "increment_size", value = "1") })
    private Long id;

    @NotBlank
    private String name;
    private String location;
    private boolean encoded;

    @OneToMany
    private Set<Subtitle> subtitles;

    @OneToMany
    private Set<Audio> audios;
}
