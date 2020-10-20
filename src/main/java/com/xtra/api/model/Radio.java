package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Radio extends Stream {

    public Radio() {
    }

    public Radio(Long id) {
        setId(id);
    }
}
