package com.xtra.api.model.category;

import com.xtra.api.model.vod.Vod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
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

    public CategoryVod(CategoryVodId id) {
        this.id = id;
    }
}
