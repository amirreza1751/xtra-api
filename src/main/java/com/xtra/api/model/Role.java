package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;
    private String color;

    @Enumerated(EnumType.STRING)
    private UserType userType;
}
