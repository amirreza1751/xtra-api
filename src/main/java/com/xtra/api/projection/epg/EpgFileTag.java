package com.xtra.api.projection.epg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EpgFileTag {

    private String name;
    private String source;
    @JacksonXmlProperty(isAttribute = true, localName = "generator-info-name")
    private String generatorInfoName;
    @JacksonXmlProperty(isAttribute = true, localName = "generator-info-url")
    private String generatorInfoUrl;

    @JacksonXmlProperty(localName = "channel")
    @JacksonXmlCData
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<EpgChannelTag> epgChannelTags;


    @JacksonXmlProperty(localName = "programme")
    @JacksonXmlCData
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ProgramTag> programTags;

}
