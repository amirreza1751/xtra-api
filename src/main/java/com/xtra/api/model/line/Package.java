package com.xtra.api.model.line;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.download_list.DownloadList;
import com.xtra.api.model.role.Role;
import com.xtra.api.model.stream.StreamProtocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Period;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean isTrial = false;
    private int credits;
    private Period duration;
    private int maxConnections;
    private boolean canRestream = false;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<StreamProtocol> allowedOutputs;

    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private DownloadList defaultDownloadList;

    @EqualsAndHashCode.Exclude
    @ManyToMany
    private Set<Role> allowedRoles;
}
