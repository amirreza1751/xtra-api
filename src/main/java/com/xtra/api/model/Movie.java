package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Movie extends Vod {
    @OneToOne(cascade = CascadeType.ALL)
    private MovieInfo info;

    @ManyToMany(mappedBy = "movies")
    private List<Collection> collections;
}
