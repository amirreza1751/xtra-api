package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;
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
    }

    public Movie(Long id) {
        setId(id);
    }
}
