package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Radio extends Stream {

    @ManyToMany(mappedBy = "radios")
    private List<Collection> collections;

    public Radio() {
    }

    public Radio(Long id) {
        setId(id);
    }
}
