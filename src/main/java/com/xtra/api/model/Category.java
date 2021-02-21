package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Category {
    @Id
    private String name;

    @Column(name = "`order`")
    private int order;

    @OneToMany(mappedBy = "category")
    private Set<Collection> collections;

    public Category(String name, int order) {
        this.name = name;

    }

    public Category() {
    }
}
