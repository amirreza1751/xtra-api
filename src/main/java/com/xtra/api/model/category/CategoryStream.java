package com.xtra.api.model.category;

import com.xtra.api.model.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoryStream {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private CategoryStreamId id;

    @MapsId("streamId")
    @ManyToOne
    private Stream stream;

    @MapsId("categoryId")
    @ManyToOne
    private Category category;

}
