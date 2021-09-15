package com.xtra.api.model.category;

import com.xtra.api.model.vod.Vod;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoryVod {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private CategoryVodId id;

    @MapsId("vodId")
    @ManyToOne
    private Vod vod;

    @MapsId("categoryId")
    @ManyToOne
    private Category category;

}
