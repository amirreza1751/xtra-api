package com.xtra.api.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
@Data
public class Radio extends Stream {
    @ManyToMany(mappedBy = "radios")
    private List<Collection> collections;
}
