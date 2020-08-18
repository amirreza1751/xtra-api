package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.Period;
import java.util.List;

@Entity
@Data
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id ;
    private String name;
    private boolean isTrial;
    private int credits;
    private Period duration;

    @ManyToMany
    private List<Collection> collections;

}
