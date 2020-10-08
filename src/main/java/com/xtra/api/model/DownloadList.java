package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.springframework.core.annotation.Order;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class DownloadList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean systemDefault;

    @ManyToOne
    private Reseller owner;

    @JsonManagedReference("dl")
    @OneToMany(mappedBy = "downloadList", cascade = CascadeType.ALL)
    @OrderBy("order ASC")
    List<DownloadListCollection> collectionsAssign;

}
