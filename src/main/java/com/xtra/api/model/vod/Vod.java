package com.xtra.api.model.vod;

import com.xtra.api.model.category.CategoryVod;
import com.xtra.api.model.collection.CollectionVod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private VodType vodType;

    @OneToMany(mappedBy = "vod", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<CollectionVod> collectionAssigns = new HashSet<>();

    @OneToMany(mappedBy = "vod", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<CategoryVod> categories = new HashSet<>();

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "modified_date")
    @UpdateTimestamp
    private Date modifiedDate;
}
