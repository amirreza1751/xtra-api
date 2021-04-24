package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Series extends Vod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String year;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Season> seasons;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private SeriesInfo info;
}
