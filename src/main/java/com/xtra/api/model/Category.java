package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Category {
    @Id
    private String name;
    private MediaType type;

    @Column(name = "`order`")
    private int order;

    public Category(String name, MediaType type, int order) {
        this.name = name;
        this.type = type;
        this.order = order;
    }

    public Category() {
    }
}
