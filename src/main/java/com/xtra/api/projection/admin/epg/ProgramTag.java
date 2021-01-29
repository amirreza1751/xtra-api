package com.xtra.api.projection.admin.epg;

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
public class ProgramTag {

    @JacksonXmlProperty(isAttribute = true, localName = "channel")
    private String channelId;
    @JacksonXmlProperty(isAttribute = true, localName = "start")
    private String start;
    @JacksonXmlProperty(isAttribute = true, localName = "stop")
    private String stop;


    @JacksonXmlProperty(localName="title")
    private TitleTag titleTag;
    @JacksonXmlProperty(localName="desc")
    private DescriptionTag descriptionTag;


    @JacksonXmlProperty(localName = "category")
    @JacksonXmlCData
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CategoryTag> categories ;


}
