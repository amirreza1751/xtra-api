package com.xtra.api.model.vod;

import lombok.Getter;

@Getter
public enum Availability {
    AVAILABLE("Available"),
    NOT_AVAILABLE("Not Available");
    private final String text;

    Availability(String text) {
        this.text = text;
    }
}
