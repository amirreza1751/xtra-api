package com.xtra.api.projection.epg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EpgChannelTag {

    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String name;

    @JacksonXmlProperty(localName="display-name")
    private DisplayNameTag displayNameTag;

    @JacksonXmlProperty(localName="iconTag")
    private IconTag iconTag;

    @JacksonXmlProperty(localName="urlTag")
    private UrlTag urlTag;

}
