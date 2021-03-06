package com.xtra.api.model;

import com.xtra.api.model.collection.Collection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"collections"})
public class Category {
    @Id
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<Collection> collections;

    @PreRemove
    private void preRemove() {
        collections.forEach(collection -> collection.setCategory(null));
    }
}
