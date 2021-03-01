package com.xtra.api.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Setting {
    @Id
    private String id;

    @Column(columnDefinition = "text")
    private String value;

}
