package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Channel extends Stream {
    private String icon;

}
