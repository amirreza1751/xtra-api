package com.xtra.api.projection.admin.video;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioDetails {
    private String location;
    //@todo use enum?
    private String language;
}
