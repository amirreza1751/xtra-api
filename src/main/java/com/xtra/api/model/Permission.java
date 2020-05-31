package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String key;

    @Enumerated(EnumType.STRING)
    private UserType userType;
}
