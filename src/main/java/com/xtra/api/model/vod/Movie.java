package com.xtra.api.model.vod;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Movie extends Vod {

    @OneToOne(cascade = CascadeType.ALL)
    private MovieInfo info;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Video> videos;


    public Movie() {
        setVodType(VodType.MOVIE);
    }

    public Movie(Long id) {
        this();
        setId(id);
    }
}
