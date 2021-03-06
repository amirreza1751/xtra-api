package com.xtra.api.model.stream;

import com.xtra.api.model.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Radio extends Stream {
}
