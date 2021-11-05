package com.xtra.api.model.vod;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Series extends Vod {

    private String year;
    private LocalDate lastUpdated;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @OrderBy("seasonNumber")
    private List<Season> seasons;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private SeriesInfo info;

    public Series() {
        setVodType(VodType.SERIES);
    }

    public Series(Long id) {
        this();
        setId(id);
    }
}
