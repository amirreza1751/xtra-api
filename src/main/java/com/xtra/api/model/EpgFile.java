package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EpgFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String source;
    @JacksonXmlProperty(isAttribute = true, localName = "generator-info-name")
    private String generatorInfoName;
    @JacksonXmlProperty(isAttribute = true, localName = "generator-info-url")
    private String generatorInfoUrl;

    @OneToMany(mappedBy = "epgFile", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonManagedReference("epg_file_id")
    @JacksonXmlProperty(localName = "channel")
    private Set<EpgChannel> epgChannels;


}
