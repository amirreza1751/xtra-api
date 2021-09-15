package com.xtra.api.model.category;

import com.xtra.api.model.MediaType;
import com.xtra.api.model.stream.Stream;
import com.xtra.api.model.vod.Vod;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private MediaType type;

    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean isAdult;

    @Column(name = "`order`")
    private int order;

    @ManyToMany
    List<Vod> vods;

    @ManyToMany
    List<Stream> streams;
}
