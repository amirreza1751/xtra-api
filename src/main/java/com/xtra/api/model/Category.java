package com.xtra.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Category {
    @Id
    @NotBlank(message = "Category name must not be empty")
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<Collection> collections;

    @PreRemove
    private void preRemove() {
        collections.forEach(collection -> collection.setCategory(null));
    }
}
