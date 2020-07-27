package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;

    private String pKey;

    @Enumerated(EnumType.STRING)
    private UserType userType;
}
